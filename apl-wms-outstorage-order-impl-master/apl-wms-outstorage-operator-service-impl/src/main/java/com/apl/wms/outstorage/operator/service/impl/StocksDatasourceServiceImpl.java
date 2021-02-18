package com.apl.wms.outstorage.operator.service.impl;

import com.apl.cache.AplCacheHelper;
import com.apl.db.adb.AdbHelper;
import com.apl.lib.constants.CommonStatusCode;
import com.apl.lib.utils.ResultUtil;
import com.apl.wms.warehouse.lib.pojo.vo.StorageLocalInfoVo;
import com.apl.wms.warehouse.po.StocksPo;
import com.apl.wms.warehouse.po.StorageLocalPo;
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
    AplCacheHelper AplCacheHelper;

    @Autowired
    AdbHelper adbHelper;


    //批量更新库位实际库存和总库存实际库存
    public ResultUtil<Integer> batchUpdateStorageLocal(List<StorageLocalPo> newStorageLocalList,
                                                    List<StocksPo> newStocksPoList) throws Exception {

        //批量更新库位和总库存
        adbHelper.updateBatch(newStorageLocalList, "storage_local", "id");

        adbHelper.updateBatch(newStocksPoList, "stocks", "id");

        return ResultUtil.APPRESULT(CommonStatusCode.SAVE_SUCCESS.getCode() , CommonStatusCode.SAVE_SUCCESS.getMsg() , newStorageLocalList.size());

    }

    //根据库位ids查询库位对应实际库存
    public ResultUtil<List<StorageLocalInfoVo>> getStorageLocalMap(String ids) throws Exception{

        Map<String, Object> paramMaps = new HashMap();
        paramMaps.put("ids", ids);
        List<StorageLocalInfoVo> storageLocalList = adbHelper.queryList(
                "select id, reality_count from storage_local where id in (:ids)",
                paramMaps,
                StorageLocalInfoVo.class);

        return ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS.code, CommonStatusCode.GET_SUCCESS.msg, storageLocalList);

    }


}

