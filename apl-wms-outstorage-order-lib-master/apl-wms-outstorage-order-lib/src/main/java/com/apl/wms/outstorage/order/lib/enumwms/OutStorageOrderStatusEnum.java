package com.apl.wms.outstorage.order.lib.enumwms;

public enum OutStorageOrderStatusEnum {


    CREATE_ING(1,"CREATE_ING" , "创建中"),
    ERROR(2,"ERROR" , "创建异常"),
    CREATE(3, "CREATE" , "已提交"),
    SENDING(4,"SENDING" , "发货中"),
    FINISH(5,"FINISH" , "完成"),
    CANCEL(6, "CANCEL" , "取消"),
    WRONG(7, "WRONG" , "出问题");

    private Integer status;
    private String code;
    private String msg;


    OutStorageOrderStatusEnum(Integer status , String code , String msg){
        this.status = status;
        this.code = code;
        this.msg = msg;
    }

    public static OutStorageOrderStatusEnum getOrderEnum(Integer status) {
        for (OutStorageOrderStatusEnum orderStatusEnum : values()) {
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