package com.apl.wms.outstorage.operator.pojo.dto;

import com.apl.lib.validate.TypeValidator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "分拣信息分页查询对象" , description = "分拣信息分页查询对象")
public class StockManageKeyDto implements Serializable {

    //拣货状态  1未分配拣货员  2已分配拣货员  3已分配批次    4拣货中  5已拣货    6分拣中   7已分拣
    //拣货状态  1库存未锁定 2库存已锁定   4已分配拣货员  5开始拣货  7已分拣   8已打包

    @ApiModelProperty(name = "pullStatus", value = "捡货状态")
    @TypeValidator(value = {"1","2","3"} , message = "捡货状态错误")
    private Integer pullStatus;

    @ApiModelProperty(name = "customerId", value = "客户id")
    private Long customerId;

    @ApiModelProperty(name = "orderSn", value = "订单号")
    private String orderSn;

    @ApiModelProperty(name = "whId", value = "仓库id")
    private Long whId;


    public String getOrderSn() {
        if (orderSn == null || orderSn.trim().equals("")){
            return null;
        }
        return orderSn;
    }

}
