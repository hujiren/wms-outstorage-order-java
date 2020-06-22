package com.apl.wms.outstorage.order.lib.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * <p>
 * 入库订单 详细实体
 * </p>
 *
 * @author cy
 * @since 2019-12-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class InOrderInfoVo implements Serializable {


private static final long serialVersionUID=1L;

    private Long id;

    private Integer orderType;

    private String orderSn;

    private Long customerId;

    private String customerName;

    // 货物渠道来源 1临时入库    2供应商发货    3库存
    private Integer goodsFrom;

    private Long supplierId;

    private String supplierName;

    private Long startWhId;

    private String startWhName;

    private Integer startWhOperator;


    private Long destWhId;

    //目的地仓库名称
    private String destWhName;

    //注定但id
    private Long mainId;

    //创建时间
    private Timestamp crTime;

    //订单状态
    private Integer orderStatus;


    //备注
    private String remark;

}
