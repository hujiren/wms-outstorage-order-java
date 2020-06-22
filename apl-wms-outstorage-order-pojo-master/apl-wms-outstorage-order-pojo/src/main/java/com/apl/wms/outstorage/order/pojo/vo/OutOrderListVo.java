package com.apl.wms.outstorage.order.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * <p>
 * 出库订单
 * </p>
 *
 * @author cy
 * @since 2020-01-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class OutOrderListVo implements Serializable {


private static final long serialVersionUID=1L;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    // 客户id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long customerId;

    // 客户名称
    private String customerName;

    // 出库订单号
    private String orderSn;

    // 出库订单状态  1创建中  2创建异常  3新建  4已发货  5完成   6取消
    private Integer orderStatus;

    //拣货状态
    private Integer pullStatus;

    // 订单来自那  1电商   2手动下单
    private Integer orderFrom;

    // 电商平台代码
    private String ecPlatformCode;

    // 参考单号
    private String referenceSn;

    // 店铺id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long storeId;

    // 店铺名称
    private String storeName;

    //收件联系人
    private String destContact;

    //收件人 国家简码
    private String destCountryCode;

    // 拣货员id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long pullOperatorId;

    // 仓库id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long whId;

    // 仓库名称
    private String whName;

    // 备注
    private String remark;

    // 订单创建时间
    private Timestamp crTime;






}
