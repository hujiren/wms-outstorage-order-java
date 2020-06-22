package com.apl.wms.outstorage.order.lib.constants;

public enum WmsCommonStatusCode {

    YOURE_NOT_AN_WAREHOUSE_OPERATOR("YOURE_NOT_AN_WAREHOUSE_OPERATOR", "你不是仓库操作员"),
    CUSTOMER_NOT_EXIST("CUSTOMER_NOT_EXIST", "客户不存在"),

    STOCK_NOT_ENOUGH("STOCK_NOT_ENOUGH","库存不足"),

    ;

    private String code;
    private String msg;


    WmsCommonStatusCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
