package com.apl.wms.outstorage.order.lib.feign.impl;


import com.apl.wms.warehouse.lib.feign.OutstorageOrderBusinessFeign;

public class OutstorageOrderBusinessFeignImpl implements OutstorageOrderBusinessFeign {
    @Override
    public void outStorageOrderCreateCallback(Long id, Integer status) {
        System.out.println("server invoke fail");
    }
}
