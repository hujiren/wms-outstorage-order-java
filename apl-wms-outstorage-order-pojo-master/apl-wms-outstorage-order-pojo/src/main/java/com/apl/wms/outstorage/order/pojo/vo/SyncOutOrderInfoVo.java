package com.apl.wms.outstorage.order.pojo.vo;


import java.sql.Timestamp;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 同步平台订单 详细实体
 * </p>
 *
 * @author arran
 * @since 2019-12-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SyncOutOrderInfoVo implements Serializable {


private static final long serialVersionUID=1L;

    //
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    //客户id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long customerId;

    //客户名称
    private String customerName;


    // 店铺id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long storeId;

    //电商平台
    private String ecPlatformCode;

    //店铺代码
    //private String storeCode;

    //店铺名称
    private String storeName;

    //店铺英文名称
    private String storeNameEn;


    // 订单起始时间
    private Timestamp orderStartTime;


    // 订单截止时间
    private Timestamp orderEndTime;


    // 已同步订单数
    //private Integer syncCount;


    // 状态  1等待同步  2正在同步  3已完成   4暂停  5异常  6取消
    private Integer status;


    // 创建时间
    private Timestamp crTime;


}
