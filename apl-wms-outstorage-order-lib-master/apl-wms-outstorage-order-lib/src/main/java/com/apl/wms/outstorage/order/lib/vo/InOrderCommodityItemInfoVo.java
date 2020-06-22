package com.apl.wms.outstorage.order.lib.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 入库订单商品明细 详细实体
 * </p>
 *
 * @author cy
 * @since 2019-12-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class InOrderCommodityItemInfoVo implements Serializable {


private static final long serialVersionUID=1L;

    //子订单id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long orderItemId;


    // 被拆分的id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long splitId;



    // 订单的id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long orderId;



    // 商品id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long commodityId;



    // 总箱数
    private Integer orderCtns;



    // 已完结箱数
    private Integer doneCtns;



    // 单箱商品数量
    private Integer unitCntCommodityQty;



    // 商品总数量
    private Integer commodityAllQty;

    //已上架的数量
    private Integer putQty;

    // 商品sku
    private String sku;



    // 商品名称
    private String commodityName;



    // 商品英文名称
    private String commodityNameEn;


    // 商品规格
    private String commoditySpec;


    // 商品图片
    private String imgUrl;


    // 行描述
    private String remark;


    //是否混装 1是  0否
    private Integer isMixed;


}
