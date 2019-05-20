package com.hgys.iptv.service.impl;

import com.hgys.iptv.controller.vm.SysMenuListVM;
import com.hgys.iptv.model.*;
import com.hgys.iptv.model.dto.SysMenuDTO;
import com.hgys.iptv.model.dto.SysRoleDTO;
import com.hgys.iptv.model.enums.ResultEnum;
import com.hgys.iptv.model.vo.ResultVO;
import com.hgys.iptv.repository.PermissionRepository;
import com.hgys.iptv.repository.SysMenuPermissionRepository;
import com.hgys.iptv.repository.SysMenuRepository;
import com.hgys.iptv.service.SysMenuService;
import com.hgys.iptv.util.ResultVOUtil;
import com.hgys.iptv.util.UpdateTool;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @ClassName SysMenuServiceImpl
 * @Auther: wangz
 * @Date: 2019/5/19 14:47
 * @Description: TODO
 */
@Service
public class SysMenuServiceImpl implements SysMenuService {
    @Autowired
    private SysMenuRepository sysMenuRepository;
    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private SysMenuPermissionRepository sysMenuPermissionRepository;

    @Override
    public ResultVO loadMenuTree() {
        ArrayList<SysMenuListVM> sysMenuListVMs = new ArrayList<>();
        // 加载菜单
        List<SysMenu> sysMenus = sysMenuRepository.findAll();
        // 关联子菜单权限列表
        //1.遍历;按(子)菜单id 查中间表 SysMenuPermission 的所有权限 id
        //2.按权限id获取权限对象list
        sysMenus.forEach(sysMenu->{
            SysMenuListVM sysMenuListVM = new SysMenuListVM();
            BeanUtils.copyProperties(sysMenu,sysMenuListVM);
            Set<Integer> allPermId = sysMenuPermissionRepository.findAllPermId(sysMenu.getId());
            List<Permission> perms = permissionRepository.findAllById(allPermId);
            sysMenuListVM.setList(perms);
            sysMenuListVMs.add(sysMenuListVM);
        });
        return ResultVOUtil.success(sysMenuListVMs);
    }

    @Override
    public ResultVO loadPermByMenuId(Integer id) {
        Set<Integer> allPermId = sysMenuPermissionRepository.findAllPermId(id);
        List<Permission> perms = permissionRepository.findAllById(allPermId);
        return ResultVOUtil.success(perms);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO createMenuNode(SysMenuDTO sysMenuDTO) {
        // 添加菜单--关联权限
        try {
            SysMenu menu = new SysMenu();
            // 状态0:启用，1：禁用--默认新增时就启用
            if(sysMenuDTO.getStatus()==null || sysMenuDTO.getStatus()!=1)
                menu.setStatus(0);
            BeanUtils.copyProperties(sysMenuDTO,menu);
            SysMenu menu_add = sysMenuRepository.save(menu);
//处理中间表
            handleRelation(sysMenuDTO,menu_add.getId());
        }catch (Exception e){
            e.printStackTrace();
            return ResultVOUtil.error("1","新增菜单结点异常！");
        }
        return ResultVOUtil.success(Boolean.TRUE);
    }





    /**
     * 处理中间表
     * @param sysMenuDTO
     * @param id
     */
    @Transactional(rollbackFor = Exception.class)
    protected void handleRelation(SysMenuDTO sysMenuDTO, Integer id){
        try{
            if(sysMenuDTO.getPids()==null)//没有关联关系直接
                return;
            List<String> ids = Arrays.asList(StringUtils.split(sysMenuDTO.getPids(), ","));
            //2.插中间表
            List<SysMenuPermission> relationList =new ArrayList<>();
            ids.forEach(pid->{
                SysMenuPermission relation = new SysMenuPermission();
                relation.setMenuId(id);
                relation.setPermId(Integer.parseInt(pid));
                relationList.add(relation);
            });
            sysMenuPermissionRepository.saveAll(relationList);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO updateMenuNode(SysMenuDTO sysMenuDTO) {
        if(!(sysMenuDTO.getId()!=null && sysMenuDTO.getId()>0)){
            return ResultVOUtil.error("1","用户id不能为空！");
        }
        try{
            SysMenu menu = new SysMenu();
            BeanUtils.copyProperties(sysMenuDTO,menu);
            //处理 null值
            SysMenu byId = sysMenuRepository.findById(sysMenuDTO.getId()).get();
            if(byId==null)
                return ResultVOUtil.error("1","菜单结点已不存在！");
            UpdateTool.copyNullProperties(byId,menu);
            sysMenuRepository.saveAndFlush(menu);
            // 先删除后插入
            if(StringUtils.isNotBlank(sysMenuDTO.getPids()))
                sysMenuPermissionRepository.deleteAllByMenuId(menu.getId());
            handleRelation(sysMenuDTO,menu.getId());
        }catch (Exception e){
            e.printStackTrace();
            return ResultVOUtil.error(ResultEnum.SYSTEM_INTERNAL_ERROR);
        }
        return ResultVOUtil.success(Boolean.TRUE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO deleteMenuNodeById(Integer id) {

        //删除用户，同时删除关联的角色关系，不是删除角色本身\
        try {
            sysMenuRepository.deleteById(id);
            sysMenuPermissionRepository.deleteAllByMenuId(id);
        }catch (Exception e){
            e.printStackTrace();
            return ResultVOUtil.error(ResultEnum.SYSTEM_INTERNAL_ERROR);
        }
        return ResultVOUtil.success(Boolean.TRUE);
    }




}
