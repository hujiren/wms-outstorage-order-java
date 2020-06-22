package com.apl.wms.outstorage.order.lib.enumwms;

public enum OrderTypeEnum {


    IN_ORDER_TYPE(1,"IN_ORDER_TYPE" , "入库订单"),
    OPERATOR_TYPE(2,"OPERATOR_TYPE" , "物流订单"),
    CHECK_ORDER_TYPE(3,"CHECK_ORDER_TYPE" , "盘点订单");


    private Integer status;
    private String code;
    private String msg;


    OrderTypeEnum(Integer status , String code , String msg){
        this.status = status;
        this.code = code;
        this.msg = msg;
    }

    public static OrderTypeEnum getOrderEnum(Integer status) {
        for (OrderTypeEnum orderStatusEnum : values()) {
            if (orderStatusEnum.status.equals(status)) {
                return orderStatusEnum;
            }
        }
        return null;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}