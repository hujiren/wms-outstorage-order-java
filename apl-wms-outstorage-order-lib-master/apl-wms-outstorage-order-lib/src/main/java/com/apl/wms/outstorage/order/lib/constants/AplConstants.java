package com.apl.wms.outstorage.order.lib.constants;

/**
 * @author CY
 * @version 1.0.0
 * @ClassName PgsSystemConstants.java
 * @createTime 2019年07月24日 14:16:00
 */
public interface AplConstants {

    Long ZERO = 0l;
    Long DEFAULT_ID = 0L;

    //锁定
    Integer LOCK = 1;
    //没有锁定
    Integer UN_LOCK = 0;

    Integer SUCCESS = 1;

    //系统访问token
    String TOKEN_FLAG = "token";
    //上家类型订单
    Integer PUT_AWAY_OPERATOR_TYPE = 1;


    Integer WH_PUT_AWAY_OPERATOR = 1;

    /**
     * @Desc: 入库订单状态： 1创建中   2创建异常   3新建   4供应商发货中    5起运仓已入库   6仓库操作中   7转运中   8目的仓已入库     9目的仓操作中   10完成   11取消
     * @Author: CY
     * @Date: 2020/3/20 10:03
     */
    Integer START_WH_ENTER = 5;
    Integer DEST_WH_ENTER = 8;


    //库位状态.  1空   2未满     3满   4占用中
    Integer STORAGE_EMTITY_STATUS = 1;
    Integer STORAGE_UN_FULL_STATUS = 2;
    Integer STORAGE_FULL_STATUS = 3;
    Integer STORAGE_USEING_STATUS = 4;

    //上架is_submit   0:未操作 1： 已同步 2： 以提交
    Integer NOT_OPERATION = 0;
    Integer ASYNC_OPERATION = 1;
    Integer ALREADY_SUBMIT = 2;


}
