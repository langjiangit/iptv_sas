package com.hgys.iptv.controller.vm;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@ApiModel(value = "修改VM")
@Data
public class OrderBusinessControllerUpdateVM {

    /** 名称 */
    @ApiModelProperty("名称")
    private String name;

    /** 编码 */
    @ApiModelProperty("编码")
    @NotBlank(message = "编码不能为空")
    private String code;

    /** 状态 */
    @ApiModelProperty("状态")
    @NotBlank(message = "状态不能为空")
    private Integer status;

    /** 备注 */
    @ApiModelProperty("备注")
    @NotBlank(message = "备注不能为空")
    private String note;

}
