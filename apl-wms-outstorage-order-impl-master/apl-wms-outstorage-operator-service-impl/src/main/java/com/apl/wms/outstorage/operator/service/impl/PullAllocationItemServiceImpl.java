package com.apl.wms.outstorage.operator.service.impl;

import com.apl.lib.constants.CommonStatusCode;
import com.apl.lib.utils.ResultUtils;
import com.apl.wms.outstorage.operator.mapper.PullAllocationItemMapper;
import com.apl.wms.outstorage.operator.pojo.po.PullAllocationItemPo;
import com.apl.wms.outstorage.operator.service.PullAllocationItemService;
import com.apl.wms.warehouse.lib.pojo.bo.CompareStorageLocalStocksBo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hjr start
 * @date 2020/7/7 - 11:05
 */

@Service
@Slf4j
public class PullAllocationItemServiceImpl extends ServiceImpl<PullAllocationItemMapper, PullAllocationItemPo> implements PullAllocationItemService {


    @Override
    public ResultUtils<Integer> insertAllocationItem(Long outOrderId, List<CompareStorageLocalStocksBo> compareStorageLocalStocksBos) {

        List<PullAllocationItemPo> itemPoList = new ArrayList<>();

        for (CompareStorageLocalStocksBo stock : compareStorageLocalStocksBos) {
            PullAllocationItemPo po = new PullAllocationItemPo();
            po.setAllocationQty(stock.getAllocationQty());
            po.setOutOrderId(outOrderId);
            po.setCommodityId(stock.getCommodityId());
            po.setStorageLocalId(stock.getStorageLocalId());
            itemPoList.add(po);
        }

        Integer integer = baseMapper.insertPullAllocationItem(itemPoList);

        return ResultUtils.APPRESULT(CommonStatusCode.SAVE_SUCCESS, integer);
    }
}
