package com.apl.wms.outstorage.order.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 出库订单商品
 * </p>
 *
 * @author cy
 * @since 2020-01-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class OutOrderCommodityItemInfoVo implements Serializable {


private static final long serialVersionUID=1L;

    //
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    // 订单id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long orderId;

    // 商品id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long commodityId;

    // 商品sku
    private String commoditySku;

    //是否带电
    private Integer isCorrespondence;

    // 商品 url
    private String imgUrl;

    // 商品 总数
    private Integer orderQty;

    // 商品名称
    private String commodityName;

    // 商品英文名称
    private String commodityNameEn;

    // 商品规格
    private String commoditySpec;

}