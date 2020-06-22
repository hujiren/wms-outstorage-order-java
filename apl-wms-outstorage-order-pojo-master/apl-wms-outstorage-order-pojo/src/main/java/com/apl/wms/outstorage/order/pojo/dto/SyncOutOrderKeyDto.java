package com.apl.wms.outstorage.order.pojo.dto;

import com.apl.lib.pojo.dto.TspDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import java.io.Serializable;


/**
 * <p>
 * 同步平台订单 查询参数
 * </p>
 *
 * @author arran
 * @since 2019-12-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="同步平台订单查询参数", description="同步平台订单查询参数")
public class SyncOutOrderKeyDto extends TspDto implements Serializable {

    private static final long serialVersionUID=1L;


    @ApiModelProperty(name = "customerId" , value = "客户id")
    private Long customerId;

    @ApiModelProperty(name = "ecPlatformCode" , value = "电商平台代码")
    private String ecPlatformCode;

    @ApiModelProperty(name = "storeId" , value = "店铺id")
    @Min(value = 0 , message = "店铺id不合法")
    private Integer storeId;

    @ApiModelProperty(name = "status" , value = "状态  0全部  1等待同步  2正在同步  3已完成同步   4同步异常   5暂停同步   6作废")
    private Integer status;

    /*@ApiModelProperty(name = "keyword", value = "关键词")
    private String keyword;

    public String getKeyword() {
        if (keyword != null && keyword.trim().equals(""))
            keyword = null;

        return keyword;
    }*/
}
