package com.apl.wms.outstorage.order.lib.feign;

import com.apl.lib.utils.ResultUtil;
import com.apl.wms.outstorage.order.lib.feign.impl.OutstorageOrderBusinessFeignImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "apl-wms-outstorage-order-business-app", path = "wms-outstorage-order-business",fallback = OutstorageOrderBusinessFeignImpl.class)
@Component
public interface OutstorageOrderBusinessFeign {


    @PostMapping(value = "/out-order/create/call-back")
    ResultUtil outStorageOrderCreateCallback(@RequestParam("orderId")Long orderId,
                                              @RequestParam("status")Integer status);



}
