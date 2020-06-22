package com.apl.wms.outstorage.operator.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 出库订单打包明细 查询参数
 * </p>
 *
 * @author cy
 * @since 2020-06-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="出库订单打包明细 查询参数", description="出库订单打包明细 查询参数")
public class PullPackItemKeyDto implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(name = "keyword", value = "关键词")
    private String keyword;

    public String getKeyword() {
        if (keyword != null && keyword.trim().equals(""))
            keyword = null;

        return keyword;
    }
}
