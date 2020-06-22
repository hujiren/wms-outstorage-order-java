package com.apl.wms.outstorage.order.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 出库订单商品 持久化对象
 * </p>
 *
 * @author cy
 * @since 2020-01-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("out_order_commodity_item")
@ApiModel(value="出库订单商品 持久化对象", description="出库订单商品 持久化对象")
public class OutOrderCommodityItemPo extends Model<OutOrderCommodityItemPo> {


    @TableId(value = "id", type = IdType.INPUT)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    //订单id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long orderId;

    //商品id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long commodityId;

    //sku
    private String commoditySku;

    //商品名称
    private String commodityName;

    //商品英文名称
    private String commodityNameEn;

    //商品规格
    private String commoditySpec;

    //商品下单数量
    private Integer orderQty;

    //已打包数量
    private Integer packQty;

    private static final long serialVersionUID=1L;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
