package com.hgys.iptv.controller;

import com.hgys.iptv.controller.vm.OrderQuantityControllerListVM;
import com.hgys.iptv.controller.vm.OrderQuantityControllerUpdateVM;
import com.hgys.iptv.model.OrderQuantity;
import com.hgys.iptv.model.vo.ResultVO;
import com.hgys.iptv.service.OrderQuantityService;
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
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;


@RestController
@RequestMapping("/orderquantity")
@Api(value = "orderquantity",tags = "新增结算类型-订购量Api接口")
public class OrderQuantityController {
    @Autowired
    private OrderQuantityService orderquantityService;

    /**
     * 根据ID查询结算类型-订购量
     * @param id
     * @return
     */
    @GetMapping("/selectById")
    @ApiOperation(value="通过id查询", notes="返回json数据类型..")
    @ResponseStatus(HttpStatus.OK)
    public ResultVO<?> findById(@ApiParam(value = "用户ID",required = true) @RequestParam("id")String id){
        if (StringUtils.isBlank(id)){
            return ResultVOUtil.error("1","id不能为空");
        }
        OrderQuantity or = orderquantityService.findById(Integer.valueOf(id.trim()));
        if (null == or){
            return ResultVOUtil.error("1","未查询到id为：" + id + "的信息");
        }
        return ResultVOUtil.success(or);

    }


    @PostMapping("/addOrderQuantity")
    @ApiOperation(value = "新增结算类型-订购量",notes = "返回处理结果，false或true")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultVO<?> addOrderQuantity(@ApiParam(value = "名称",required = true) @RequestParam("name")String name,
                                        @ApiParam(value = "状态(0:启用;1:禁用;默认启用)",required = true) @RequestParam("status")String status,
                                        @ApiParam(value = "备注",required = false) @RequestParam("note")String note) {

        if (StringUtils.isBlank(name)){
            return ResultVOUtil.error("1","结算类型-订购量name不能为空");
        }

        if (StringUtils.isBlank(status)){
            return ResultVOUtil.error("1","结算类型-订购量status不能为空");
        }

        return orderquantityService.insterOrderQuantity(name,status,note);
    }


    @PostMapping("/batchDelete")
    @ApiOperation(value = "通过Id批量逻辑删除",notes = "返回处理结果，false或true")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResultVO<?> batchDelete(@ApiParam(value = "名称",required = true) @RequestParam("ids")String ids){

        if (StringUtils.isBlank(ids)){
            return ResultVOUtil.error("1","ids不能为空");
        }

        return orderquantityService.batchDelete(ids);
    }


    @GetMapping("/findByConditions")
    @ApiOperation(value = "通过条件，分页查询",notes = "返回处理结果，false或true")
    @ResponseStatus(HttpStatus.OK)
    public Page<OrderQuantityControllerListVM> findByConditions(@ApiParam(value = "名称") @RequestParam(value = "name",required = false )String name,
                                                                @ApiParam(value = "编码") @RequestParam(value = "code",required = false)String code,
                                                                @ApiParam(value = "状态") @RequestParam(value = "status",required = false)String status,
                                                                @ApiParam(value = "当前页",required = true,example = "1") @RequestParam(value = "pageNum")String pageNum,
                                                                @ApiParam(value = "当前页数量",required = true,example = "10") @RequestParam(value = "pageSize")String pageSize){

        OrderQuantity vo = new OrderQuantity();
        vo.setCode(CodeUtil.getOnlyCode("SDS",5));
        vo.setInputTime(new Timestamp(System.currentTimeMillis()));
        vo.setName(name);

        Sort sort = new Sort(Sort.Direction.DESC,"inputTime");
        Pageable pageable = PageRequest.of(Integer.parseInt(pageNum) -1 ,Integer.parseInt(pageNum),sort);
        Page<OrderQuantityControllerListVM> byConditions = orderquantityService.findByConditions(name, code, status, pageable);
        return byConditions;
    }

    @PutMapping("/updateSettlementDimension")
    @ApiOperation(value = "修改",notes = "返回处理结果，false或true")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultVO<?> updateOrderQuantity(@ApiParam(value = "结算单维度名称") @RequestBody() OrderQuantityControllerUpdateVM vo){

        return ResultVOUtil.success(null);
    }

}