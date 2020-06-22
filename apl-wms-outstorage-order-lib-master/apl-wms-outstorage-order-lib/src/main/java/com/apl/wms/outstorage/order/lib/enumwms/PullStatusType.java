package com.apl.wms.outstorage.order.lib.enumwms;

public enum PullStatusType {

    STOCK_UN_LOCK(1,"STOCK_UN_LOCK" , "库存未锁定"),
    STOCK_LOCK(2,"STOCK__LOCK" , "库存锁定"),
    UN_ALLOCATION(3,"UN_ALLOCATION" , "未分配拣货员"),
    ALREADY_ALLOCATION(4,"ALREADY_ALLOCATION" , "已分配拣货员"),
    START_PULL(5,"START_PULL" , "开始拣货"),
    PULL_DONE(6,"PULL_DONE" , "已拣货"),
    SORT_DONE(7,"SORT_DONE" , "已分拣"),
    PACK_DONE(8,"PACK_DONE" , "已打包");


    private Integer status;
    private String code;
    private String msg;


    PullStatusType(Integer status , String code , String msg){
        this.status = status;
        this.code = code;
        this.msg = msg;
    }

    public static PullStatusType getOrderEnum(Integer status) {
        for (PullStatusType pullStatusType : values()) {
            if (pullStatusType.status.equals(status)) {
                return pullStatusType;
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
