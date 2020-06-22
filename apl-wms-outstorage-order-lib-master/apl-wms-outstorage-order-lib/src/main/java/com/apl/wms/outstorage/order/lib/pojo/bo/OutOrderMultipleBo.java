package com.apl.wms.outstorage.order.lib.pojo.bo;

import com.apl.lib.security.SecurityUser;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OutOrderMultipleBo implements Serializable {

    //private String token;

    private SecurityUser securityUser;

    //任务id
    private Long trskId;

    //客户id
    private Long customerId;

    //订单来源   1自动同步电商平台订单   2手动下单
    private  Integer orderFrom;

    //多个订单
    private List<SyncOutOrderBo> orders;

}
