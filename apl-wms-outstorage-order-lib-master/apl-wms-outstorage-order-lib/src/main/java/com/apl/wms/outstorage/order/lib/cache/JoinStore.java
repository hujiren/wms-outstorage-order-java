package com.apl.wms.outstorage.order.lib.cache;

import com.apl.lib.constants.CommonStatusCode;
import com.apl.lib.datasource.DataSourceContextHolder;
import com.apl.lib.join.JoinBase;
import com.apl.lib.utils.ResultUtils;
import com.apl.wms.warehouse.lib.cache.StoreCacheBo;
import com.apl.wms.warehouse.lib.feign.WarehouseFeign;
import org.springframework.data.redis.core.RedisTemplate;

public class JoinStore extends JoinBase<StoreCacheBo> {

    public WarehouseFeign warehouseFeign;

    public JoinStore(int joinStyle, WarehouseFeign warehouseFeign, RedisTemplate redisTemplate){
        this.warehouseFeign = warehouseFeign;
        this.redisTemplate = redisTemplate;
        this.tabName = "store";
        this.joinStyle = joinStyle;

        this.innerOrgId = DataSourceContextHolder.getInnerOrgId();
        this.cacheKeyNamePrefix = "JOIN_CACHE:"+this.tabName+"_"+this.innerOrgId.toString()+"_";
    }


    @Override
    public Boolean addCache(String keys, Long minKey, Long maxKey){

        ResultUtils<Boolean> result = warehouseFeign.addStoreCache(keys, minKey, maxKey);
        if(result.getCode().equals(CommonStatusCode.SYSTEM_SUCCESS.code))
            return true;

        return false;
    }

}
