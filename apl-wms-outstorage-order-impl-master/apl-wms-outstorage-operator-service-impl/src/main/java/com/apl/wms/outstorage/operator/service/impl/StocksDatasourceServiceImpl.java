package com.apl.wms.outstorage.operator.service.impl;

import com.apl.abatis.AbatisExecutor;
import com.apl.cache.AplCacheUtil;
import com.apl.db.adb.AdbContext;
import com.apl.db.adb.AdbPersistent;
import com.apl.db.adb.AdbQuery;
import com.apl.lib.constants.CommonStatusCode;
import com.apl.lib.utils.ResultUtil;
import com.apl.wms.warehouse.lib.pojo.vo.StorageLocalInfoVo;
import com.apl.wms.warehouse.po.StocksPo;
import com.apl.wms.warehouse.po.StorageLocalPo;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hjr start
 * @date 2020/7/31 - 19:52
 */
@Component
public class StocksDatasourceServiceImpl {

    @Autowired
    AplCacheUtil aplCacheUtil;

    public AdbContext connectDb(){

        SqlSession sqlSession = AbatisExecutor.sqlSessionFactory.openSession("wms_stocks_history", aplCacheUtil);

        // 创建数据库上下文
        AdbContext adbContext = new AdbContext("wms_wh", aplCacheUtil);

        return adbContext;
    }


    //批量更新库位实际库存和总库存实际库存
    public ResultUtil<Integer> batchUpdateStorageLocal(AdbContext adbContext, List<StorageLocalPo> newStorageLocalList,
                                                    List<StocksPo> newStocksPoList) throws Exception {

        //批量更新库位和总库存
        Integer integer = AdbPersistent.updateBatch(adbContext, newStorageLocalList, "storage_local", "id");

        Integer integer1 = AdbPersistent.updateBatch(adbContext, newStocksPoList, "stocks", "id");

        return ResultUtil.APPRESULT(CommonStatusCode.SAVE_SUCCESS.getCode() , CommonStatusCode.SAVE_SUCCESS.getMsg() , integer + integer1);
    }

    //根据库位ids查询库位对应实际库存
    public ResultUtil<List<StorageLocalInfoVo>> getStorageLocalMap(AdbContext adbContext , String ids) throws Exception{

        Map<String, Object> paramMaps = new HashMap();
        paramMaps.put("ids", ids);
        List<StorageLocalInfoVo> storageLocalList = AdbQuery.queryList(adbContext,
                "select id, reality_count from storage_local where id in (:ids)",
                paramMaps,
                StorageLocalInfoVo.class);

        return ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS.code, CommonStatusCode.GET_SUCCESS.msg, storageLocalList);
    }


}

