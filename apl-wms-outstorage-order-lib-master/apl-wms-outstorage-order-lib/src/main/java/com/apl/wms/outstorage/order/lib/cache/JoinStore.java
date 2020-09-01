package com.apl.wms.outstorage.order.lib.cache;
import com.apl.lib.cachebase.BaseCacheUtil;
import com.apl.lib.constants.CommonStatusCode;
import com.apl.lib.join.JoinBase;
import com.apl.lib.utils.ResultUtil;
import com.apl.tenant.AplTenantConfig;
import com.apl.wms.warehouse.lib.cache.bo.StoreCacheBo;
import com.apl.wms.warehouse.lib.feign.WarehouseFeign;

public class JoinStore extends JoinBase<StoreCacheBo> {

    public WarehouseFeign warehouseFeign;

    public JoinStore(int joinStyle, WarehouseFeign warehouseFeign, BaseCacheUtil cacheUtil){
        this.warehouseFeign = warehouseFeign;
        this.cacheUtil = cacheUtil;
        this.tabName = "store";
        this.joinStyle = joinStyle;

        this.innerOrgId = AplTenantConfig.tenantIdContextHolder.get();
        this.cacheKeyNamePrefix = "JOIN_CACHE:"+this.tabName+"_"+this.innerOrgId.toString()+"_";
    }


    @Override
    public Boolean addCache(String keys, Long minKey, Long maxKey){

        ResultUtil<Boolean> result = warehouseFeign.addStoreCache(keys, minKey, maxKey);
        if(result.getCode().equals(CommonStatusCode.SYSTEM_SUCCESS.code))
            return true;

        return false;
    }

}
