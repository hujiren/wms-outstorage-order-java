package com.apl.wms.outstorage.order.lib.feign.impl;
import com.apl.lib.constants.CommonStatusCode;
import com.apl.lib.utils.ResultUtil;
import com.apl.wms.outstorage.order.lib.feign.OutStorageOrderOperatorFeign;
import com.apl.wms.outstorage.order.lib.pojo.bo.AllocationWarehouseOutOrderBo;
import com.apl.wms.warehouse.lib.pojo.bo.CompareStorageLocalStocksBo;
import java.util.List;

/**
 * @author hjr start
 * @date 2020/7/10 - 15:50
 */
public class OutStorageOrderOperatorFeignImpl implements OutStorageOrderOperatorFeign {

    @Override
    public ResultUtil<AllocationWarehouseOutOrderBo> getOrderByAllocationWarehouseManual(Long outOrderId) {
        return ResultUtil.APPRESULT(CommonStatusCode.GET_FAIL.getCode() , CommonStatusCode.GET_FAIL.getMsg() , null);
    }

    @Override
    public ResultUtil<Integer> AllocOutOrderStockCallBack(String tranId, Long outOrderId, Integer pullStatus, List<CompareStorageLocalStocksBo> compareStorageLocalStocksBos) {
        return ResultUtil.APPRESULT(CommonStatusCode.SAVE_FAIL.getCode(), CommonStatusCode.SAVE_FAIL.getMsg(), null);
    }

}
