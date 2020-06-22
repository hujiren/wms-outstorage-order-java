package com.apl.wms.outstorage.order.lib.enumwms;

public enum OrderStatusEnum {


    CREATE_ING(1,"CREATE_ING" , "创建中"),
    ERROR(2,"ERROR" , "创建异常"),
    CREATE(3, "CREATE" , "新建"),
    SENDING(4,"SENDING" , "供应商发货中"),
    START_IN_WH(5,"START_IN_WH" , "起运仓已入库"),
    OPERATOR_ING(6, "OPERATOR_ING" , "仓库操作中"),
    RE_SEND_ING(7, "RE_SEND_ING" , "转运中"),
    DEST_IN_WH(8,"DEST_IN_WH" , "目的仓已入库"),
    DEST_OPERATOR_ING(9, "DEST_OPERATOR_ING" , "目的地操作中"),
    FINISH(10,"FINISH" , "完成"),
    CANCEL(11, "CANCEL" , "取消"),

    CHECK_ORDER_CREATE(21, "CHECK_ORDER_CREATE" , "新建盘点"),
    ORDER_CHECK_ING(22 ,"ORDER_CHECK_ING" , "盘点中"),
    ORDER_CHECK_FINISH(23,"ORDER_CHECK_FINISH" , "盘点完成"),
    CHECK_REDO(24, "CHECK_REDO" , "盘点撤销"),
    CHECK_CANCEL(25, "CHECK_CANCEL" , "盘点取消"),
    CHECK_ERROR(25, "CHECK_ERROR" , "盘点异常");


    private Integer status;
    private String code;
    private String msg;


    OrderStatusEnum(Integer status , String code , String msg){
        this.status = status;
        this.code = code;
        this.msg = msg;
    }

    public static OrderStatusEnum getOrderEnum(Integer status) {
        for (OrderStatusEnum orderStatusEnum : values()) {
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