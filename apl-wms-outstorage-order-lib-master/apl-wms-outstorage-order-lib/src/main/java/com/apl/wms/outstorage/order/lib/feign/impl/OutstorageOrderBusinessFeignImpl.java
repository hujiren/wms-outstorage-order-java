package com.apl.wms.outstorage.order.lib.feign.impl;


import com.apl.lib.constants.CommonStatusCode;
import com.apl.lib.utils.ResultUtils;
import com.apl.wms.outstorage.order.lib.feign.OutstorageOrderBusinessFeign;

public class OutstorageOrderBusinessFeignImpl implements OutstorageOrderBusinessFeign {
    @Override
    public ResultUtils outStorageOrderCreateCallback(Long id, Integer status) {
        return ResultUtils.APPRESULT(CommonStatusCode.SERVER_INVOKE_FAIL.getCode() , CommonStatusCode.SERVER_INVOKE_FAIL.getMsg() , null);
    }
}
