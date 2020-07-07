package com.apl.wms.outstorage.order.lib.feign.impl;

import com.apl.lib.constants.CommonStatusCode;
import com.apl.lib.utils.ResultUtil;
import com.apl.wms.outstorage.order.lib.feign.OutStorageOrderOperatorFeign;
import com.apl.wms.outstorage.order.lib.pojo.bo.AllocationWarehouseOutOrderBo;
import com.apl.wms.warehouse.lib.pojo.bo.CompareStorageLocalStocksBo;

import java.util.List;

/**
 * @author hjr start
 * @date 2020/7/6 - 14:48
 */
public class OutStorageOrderOperatorFeignImpl implements OutStorageOrderOperatorFeign {

    @Override
    public ResultUtil<List<AllocationWarehouseOutOrderBo>> getOrdersByAllocationWarehouse(String orderIds) {
        return ResultUtil.APPRESULT(CommonStatusCode.SERVER_INVOKE_FAIL.getCode() , CommonStatusCode.SERVER_INVOKE_FAIL.getMsg() , null);
    }

    @Override
    public ResultUtil<Integer> insertAllocationItem(Long outOrderId, List<CompareStorageLocalStocksBo> compareStorageLocalStocksBos) {
        return ResultUtil.APPRESULT(CommonStatusCode.SAVE_FAIL.getCode(), CommonStatusCode.SAVE_FAIL.getMsg(), null);
    }
}
