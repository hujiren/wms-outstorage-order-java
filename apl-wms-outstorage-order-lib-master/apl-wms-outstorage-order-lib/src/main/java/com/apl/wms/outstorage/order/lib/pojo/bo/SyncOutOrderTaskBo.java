package com.apl.wms.outstorage.order.lib.pojo.bo;


import com.apl.lib.security.SecurityUser;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.sql.Timestamp;

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
public class SyncOutOrderTaskBo implements Serializable {


private static final long serialVersionUID=1L;

    //任务id
    private Long id;

    //客户id
    private Long customerId;

    // 店铺id
    private Long storeId;

    //电商平台
    private String ecPlatformCode;

    // 订单起始时间
    private Timestamp orderStartTime;

    // 订单截止时间
    private Timestamp orderEndTime;

    // 状态  1等待同步  2正在同步  3已完成   4暂停  5异常  6取消
    private Integer status;

    //API参数(字符)
    private String apiConfig;


    //租户id
    private Long innerOrgId;

    //安全用户
    private  SecurityUser securityUser;
}
