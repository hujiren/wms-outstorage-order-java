package com.apl.wms.outstorage.order.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * <p>
 * 出库订单 详细实体
 * </p>
 *
 * @author cy
 * @since 2020-01-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class OutOrderInfoVo implements Serializable {


    private static final long serialVersionUID=1L;


    //
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;


    // 客户id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long customerId;

    // 客户名称
    private String customerName;

    //是否投保
    private Integer isInsure;

    //投保金额
    private BigDecimal insureAmount;



    //捡货状态
    private Integer pullStatus;


    // 出库订单号
    private String orderSn;


    // 出库订单状态  1创建中  2创建异常  3新建  4已发货  5完成   6取消
    private Integer orderStatus;



    // 订单来自那  1电商   2手动下单
    private Integer orderFrom;


    // 电商平台代码
    //private String ecCode;
    private String ecPlatformCode;



    // 参考单号
    private String referenceSn;



    // 店铺id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long storeId;

    // 店铺id
    private String storeName;



    // 仓库id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long whId;


    // 仓库名称
    private String whName;



    // 拣货员id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long pullOperatorId;

    // 拣货员名称
    private String pullOperatorName;



    // 打包员id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long packOperatorId;



    // 出库总毛重
    private BigDecimal outGw;



    // 出库总体积
    private BigDecimal outVolume;

    // 备注
    private String remark;

    //是否问题件 1:正常订单 2：问题订单
    private Integer isWrong;

    // 订单创建时间
    private Timestamp crTime;




}
