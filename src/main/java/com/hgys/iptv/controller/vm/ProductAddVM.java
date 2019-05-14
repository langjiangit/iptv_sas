package com.hgys.iptv.controller.vm;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
@Data
@ApiModel(value = "产品新增VM")
public class ProductAddVM {
    @ApiModelProperty("主键，新增时填写无效")
    private Integer id;
    @ApiModelProperty("名称") @NotBlank(message = "不能为空")
    private String name;

    @ApiModelProperty("价格")
    private Integer price;

    @ApiModelProperty("状态")@NotBlank(message = "不能为空")
    private Integer status;

    @ApiModelProperty(value = "产品关联的cp集合id字符串")//dataType = "List"
    private String cpids;

    @ApiModelProperty(value = "产品关联的业务集合id字符串")//dataType = "List"
    private String bids;
}
