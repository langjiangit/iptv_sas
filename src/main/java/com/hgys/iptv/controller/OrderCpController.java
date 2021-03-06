package com.hgys.iptv.controller;

import com.hgys.iptv.aop.SystemControllerLog;
import com.hgys.iptv.controller.vm.*;
import com.hgys.iptv.model.OrderCp;
import com.hgys.iptv.model.OrderCpWithCp;
import com.hgys.iptv.model.vo.ResultVO;
import com.hgys.iptv.service.CpService;
import com.hgys.iptv.service.OrderCpService;
import com.hgys.iptv.util.CodeUtil;
import com.hgys.iptv.util.ResultVOUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;


@RestController
@RequestMapping("/ordercp")
@Api(value = "ordercp",tags = "结算类型-CP定比例Api接口")
public class OrderCpController {

    @Autowired
    private OrderCpService ordercpService;

    @Autowired
    private CpService cpService;

    private static final String target="CP定比例结算";
  /*  @GetMapping("/selectById")
    @ApiOperation(value = "通过id查询",notes = "返回json数据类型")
    public ResultVO<?> findById(@ApiParam(value = "用户ID",required = true) @RequestParam("id")String id){
        if (StringUtils.isBlank(id)){
            return ResultVOUtil.error("1","id不能为空");
        }
        OrderCp oc = ordercpService.findById(Integer.valueOf(id.trim()));
        if (null == oc){
            return ResultVOUtil.error("1","未查询到id为：" + id + "的信息");
        }
        return ResultVOUtil.success(oc);
    }*/


    @GetMapping("/selectById")
    @ApiOperation(value = "通过结算组合Id编码查询",notes = "返回json数据")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(value = "hasPermission('OrderCp', 'view')")
    public ResultVO<?> findById(@ApiParam(value = "结算组合Id编码",required = true) @RequestParam("id")String id){
        if (StringUtils.isBlank(id)){
            new IllegalArgumentException(" 不能为空");
        }
        OrderCPWithCPListVM byId = ordercpService.findById(id);
        return ResultVOUtil.success(byId);
    }










     @DeleteMapping("/batchDeleteoc")
    @ApiOperation(value = "通过Id批量逻辑删除",notes = "返回处理结果，false或true")
    @ResponseStatus(HttpStatus.OK)
     @PreAuthorize(value = "hasPermission('OrderCp', 'remove')")
     @SystemControllerLog(target = target,methodName = "OrderCpController.Delete",type = "删除")
    public ResultVO<?> batchDeleteoc(@ApiParam(value = "名称",required = true) @RequestParam("ids")String ids){

        if (StringUtils.isBlank(ids)){
            return ResultVOUtil.error("1","ids不能为空");
        }

        return ordercpService.batchDeleteoc(ids);
    }



    @PostMapping("/addOrderCp")
    @ApiOperation(value = "新增结算类型-订购量",notes = "返回处理结果，false或true")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(value = "hasPermission('OrderCp', 'add')")
    @SystemControllerLog(target = target,methodName = "OrderCpController.save",type = "新增")
    public ResultVO<?> addOrderCp(@ApiParam(value = "新增结算类型-订购量VM") @RequestBody() OrderCPAddVM vo){

        return ordercpService.addOrderCp(vo);
    }



    @GetMapping("/getOrderCp")
    @ApiOperation(value = "通过编码查询",notes = "返回json数据")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(value = "hasPermission('OrderCp', 'view')")
    public OrderCPWithCPListVM getOrderCp(@ApiParam(value = "编码",required = true) @RequestParam("code")String code){
        if (StringUtils.isBlank(code)){
            new IllegalArgumentException("Code不能为空");
        }

        return ordercpService.getOrderCp(code);
    }

    @GetMapping("/findByConditions")
    @ApiOperation(value = "通过条件，分页查询",notes = "返回处理结果，false或true")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(value = "hasPermission('OrderCp', 'view')")
    public Page<OrderCPWithCPListVM> findByConditions(@ApiParam(value = "结算单维度名称") @RequestParam(value = "name",required = false )String name,
                                                                                   @ApiParam(value = "编码") @RequestParam(value = "code",required = false)String code,
                                                                                   @ApiParam(value = "状态") @RequestParam(value = "status",required = false)String status,
                                                                                   @ApiParam(value = "结算方式") @RequestParam(value = "settleaccounts",required = false)String settleaccounts,
                                                                                   @ApiParam(value = "当前页",required = true,example = "1") @RequestParam(value = "pageNum")String pageNum,
                                                                                   @ApiParam(value = "当前页数量",required = true,example = "10") @RequestParam(value = "pageSize")String pageSize){

        Sort sort = new Sort(Sort.Direction.DESC,"inputTime");
        Pageable pageable = PageRequest.of(Integer.parseInt(pageNum) -1 ,Integer.parseInt(pageSize),sort);
        Page<OrderCPWithCPListVM> byConditions = ordercpService.findByConditions(name, code, status, settleaccounts,pageable);
        return byConditions;
    }








    @PostMapping("/updateOrderCp")
    @ApiOperation(value = "修改结算类型-CP定比例",notes = "返回处理结果，false或true")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(value = "hasPermission('OrderCp', 'update')")
    @SystemControllerLog(target = target,methodName = "OrderCpController.update",type = "修改")
    public ResultVO<?> updateOrderCp(@ApiParam(value = "结算类型-CP定比例VM") @RequestBody() OrderCPAddVM vo){
        return ordercpService.updateOrderCp(vo);
    }



    @GetMapping("/queryCPList")
    @ApiOperation(value = "查询CP列表")
    @PreAuthorize(value = "hasPermission('OrderCp', 'view')")
    public ResultVO<?> queryCPList(){
        ResultVO<?> all = cpService.findcplist();
        return all;
    }

}
