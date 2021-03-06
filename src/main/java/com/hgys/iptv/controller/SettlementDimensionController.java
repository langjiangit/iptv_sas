package com.hgys.iptv.controller;

import com.hgys.iptv.aop.SystemControllerLog;
import com.hgys.iptv.controller.vm.SettlementDimensionAddVM;
import com.hgys.iptv.controller.vm.SettlementDimensionControllerListVM;
import com.hgys.iptv.controller.vm.SettlementDimensionControllerUpdateVM;
import com.hgys.iptv.model.SettlementDimension;
import com.hgys.iptv.model.vo.ResultVO;
import com.hgys.iptv.service.SettlementDimensionService;
import com.hgys.iptv.util.ResultVOUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author yangpeng
 */
@RestController
@RequestMapping("/settlementDimensionController")
@Api(value = "settlementDimensionController",tags = "结算单维度Api接口")
public class SettlementDimensionController {

    @Autowired
    private SettlementDimensionService settlementDimensionService;

    @GetMapping("/selectByCode")
    @ApiOperation(value = "通过code查询",notes = "返回json数据类型")
    @ResponseStatus(HttpStatus.OK)
    public ResultVO<?> findByCode(@ApiParam(value = "结算单维度code",required = true) @RequestParam("code")String code){

        if (StringUtils.isBlank(code)){
            return ResultVOUtil.error("1","结算单维度code不能为空");
        }
        SettlementDimension vo = settlementDimensionService.findByCode(code.trim()).orElseThrow(()-> new IllegalArgumentException("未查询到code为：" + code + "的信息"));

        return ResultVOUtil.success(vo);
    }

    @GetMapping("/selectById")
    @ApiOperation(value = "通过Id查询",notes = "返回json数据类型")
    @ResponseStatus(HttpStatus.OK)
    public ResultVO<?> findById(@ApiParam(value = "结算单维度Id",required = true) @RequestParam("id")String id){

        if (StringUtils.isBlank(id)){
            return ResultVOUtil.error("1","结算单维度id不能为空");
        }
        SettlementDimension vo = settlementDimensionService.findById(id.trim()).orElseThrow(()-> new IllegalArgumentException("未查询到code为：" + id + "的信息"));

        return ResultVOUtil.success(vo);
    }

    @PostMapping("/addSettlementDimension")
    @ApiOperation(value = "新增结算维度",notes = "返回处理结果，false或true")
    @ResponseStatus(HttpStatus.CREATED)
    @SystemControllerLog(target = "结算单维度",methodName = "SettlementDimensionController.addSettlementDimension",type = "新增")
    public ResultVO<?> addSettlementDimension(@ApiParam(value = "结算单维度名称") @RequestBody SettlementDimensionAddVM vo){

        if (StringUtils.isBlank(vo.getName())){
            return ResultVOUtil.error("1","结算单维度name不能为空");
        }

        if (StringUtils.isBlank(vo.getStatus())){
            return ResultVOUtil.error("1","结算单维度status不能为空");
        }

        return settlementDimensionService.insterSettlementDimension(vo);
    }

    @DeleteMapping("/batchLogicDelete")
    @ApiOperation(value = "通过Id批量逻辑删除",notes = "返回处理结果，false或true")
    @ResponseStatus(HttpStatus.OK)
    @SystemControllerLog(target = "结算单维度",methodName = "SettlementDimensionController.batchLogicDelete",type = "删除")
    public ResultVO batchLogicDelete(@ApiParam(value = "结算单维度ids",required = true) @RequestParam("ids")String ids){

        if (StringUtils.isBlank(ids)){
            return ResultVOUtil.error("1","结算单维度ids不能为空");
        }

        ResultVO<?> resultVO = settlementDimensionService.batchLogicDelete(ids);
        return resultVO;
    }

    @GetMapping("/findByConditions")
    @ApiOperation(value = "通过条件，分页查询",notes = "返回处理结果，false或true")
    @ResponseStatus(HttpStatus.OK)
    public Page<SettlementDimensionControllerListVM> findByConditions(@ApiParam(value = "结算单维度名称") @RequestParam(value = "name",required = false )String name,
                                                                    @ApiParam(value = "结算单维度编码") @RequestParam(value = "code",required = false)String code,
                                                                    @ApiParam(value = "状态") @RequestParam(value = "status",required = false)String status,
                                                                    @ApiParam(value = "当前页",required = true,example = "1") @RequestParam(value = "pageNum")String pageNum,
                                                                    @ApiParam(value = "当前页数量",required = true,example = "10") @RequestParam(value = "pageSize")String pageSize){

        Sort sort = new Sort(Sort.Direction.DESC,"inputTime");
        Pageable pageable = PageRequest.of(Integer.parseInt(pageNum) -1 ,Integer.parseInt(pageSize),sort);
        Page<SettlementDimensionControllerListVM> byConditions = settlementDimensionService.findByConditions(name, code, status, pageable);
        return byConditions;
    }


    @PostMapping("/updateSettlementDimension")
    @ApiOperation(value = "结算单维度修改",notes = "返回处理结果，false或true")
    @ResponseStatus(HttpStatus.CREATED)
    @SystemControllerLog(target = "结算单维度",methodName = "SettlementDimensionController.updateSettlementDimension",type = "修改")
    public ResultVO<?> updateSettlementDimension(@ApiParam(value = "结算单维度修改VM") @RequestBody() SettlementDimensionControllerUpdateVM vo){
        SettlementDimension settlementDimension = new SettlementDimension();
        BeanUtils.copyProperties(vo,settlementDimension);
        ResultVO<?> resultVO = settlementDimensionService.updateSettlementDimension(settlementDimension);
        return resultVO;
    }
}
