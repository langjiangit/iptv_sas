package com.hgys.iptv.controller.vm;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import java.sql.Timestamp;
import java.util.List;

@ApiModel(value = "结算类型-CP定比例新增VM")
public class OrderCPAddVM {
    @ApiModelProperty("ID")
    private Integer id;
    /** 结算类型-CP定比例名称 */
    @ApiModelProperty("结算类型-CP定比例名称")
    @NotBlank(message = "结算类型-CP定比例名称不能为空")
    private String name;

    @ApiModelProperty("备注")
    private String note;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("集合")
    private List<SmallCPOrderVM> list;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setSettleaccounts(Integer settleaccounts) {
        this.settleaccounts = settleaccounts;
    }

    public Integer getSettleaccounts() {
        return settleaccounts;
    }

    /** 结算方式 */
    @ApiModelProperty("settleaccounts")
    private Integer settleaccounts;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<SmallCPOrderVM> getList() {
        return list;
    }

    public void setList(List<SmallCPOrderVM> list) {
        this.list = list;
    }
}
