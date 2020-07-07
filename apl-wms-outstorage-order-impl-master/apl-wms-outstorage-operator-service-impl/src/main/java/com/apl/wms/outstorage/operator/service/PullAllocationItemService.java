package com.apl.wms.outstorage.operator.service;

import com.apl.lib.utils.ResultUtils;
import com.apl.wms.outstorage.operator.pojo.po.PullAllocationItemPo;
import com.apl.wms.warehouse.lib.pojo.bo.CompareStorageLocalStocksBo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author hjr start
 * @date 2020/7/7 - 11:04
 */
public interface PullAllocationItemService extends IService<PullAllocationItemPo> {

    ResultUtils<Integer> insertAllocationItem(Long outOrderId, List<CompareStorageLocalStocksBo> compareStorageLocalStocksBos);
}
