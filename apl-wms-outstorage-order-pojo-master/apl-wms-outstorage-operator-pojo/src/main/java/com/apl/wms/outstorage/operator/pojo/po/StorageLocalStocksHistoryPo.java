package com.apl.wms.outstorage.operator.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * <p>
 * 库存记录
 * </p>
 *
 * @author cy
 * @since 2020-07-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="StorageLocalStocksHistory对象", description="库位库存记录")
public class StorageLocalStocksHistoryPo implements Serializable { //extends Model<StocksHistoryPo>

    //private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @ApiModelProperty(value = "订单id")
    private Long orderId;

    @ApiModelProperty(value = "订单类型 1入库订单   2出库订单  3盘点订单")
    private Integer orderType;

    @ApiModelProperty(value = "库存类型 可售库存  2实际库存")
    private Integer stocksType;

    @ApiModelProperty(value = "订单号")
    private String orderSn;

    @ApiModelProperty(value = "仓库id")
    private Long whId;

    @ApiModelProperty(value = "库位id")
    private Long storageLocalId;

    @ApiModelProperty(value = "商品id")
    private Long commodityId;

    @ApiModelProperty(value = "商品入库数量")
    private Integer inQty;

    @ApiModelProperty(value = "商品出库数量")
    private Integer outQty;

    @ApiModelProperty(value = "库存剩余数量")
    private Integer stocksQty;

    @ApiModelProperty(value = "操作时间")
    private Timestamp operatorTime;


    //@Override
    //protected Serializable pkVal() {
     //   return this.id;
    //}


}
