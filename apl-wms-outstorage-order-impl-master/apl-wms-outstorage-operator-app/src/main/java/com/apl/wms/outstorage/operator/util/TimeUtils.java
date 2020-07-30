package com.apl.wms.outstorage.operator.util;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

    /**
     * @Desc: 获取前一个月的时间戳
     * @Author: CY
     * @Date: 2020/4/6 17:27
     * month : 表示前后几个月 1 后一个月 -1 前一个月
     */
    public static Timestamp getPreOrAfterMonthMillis(Integer month){

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH , month);

        return new Timestamp(calendar.getTimeInMillis());
    }

    /**
     * @Desc  获取前XX天的时间戳
     * @Author WGF
     * @Date 2020/7/29
     */
    public static Timestamp getPreOrAfterDayMillis(Integer days){

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE , days);

        return new Timestamp(calendar.getTimeInMillis());
    }

    public static Timestamp now(){

        return new Timestamp(System.currentTimeMillis());
    }

}
