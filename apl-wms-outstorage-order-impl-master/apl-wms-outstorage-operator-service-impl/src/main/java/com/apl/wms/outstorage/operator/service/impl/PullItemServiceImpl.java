package com.apl.wms.outstorage.operator.service.impl;

import com.apl.lib.constants.CommonStatusCode;
import com.apl.lib.exception.AplException;
import com.apl.lib.pojo.dto.PageDto;
import com.apl.lib.utils.ResultUtil;
import com.apl.lib.utils.SnowflakeIdWorker;
import com.apl.wms.outstorage.order.dao.PullAllocationItemMapper;
import com.apl.wms.outstorage.operator.pojo.dto.PullAllocationItemKeyDto;
import com.apl.wms.outstorage.operator.pojo.po.PullAllocationItemPo;
import com.apl.wms.outstorage.operator.pojo.vo.PullAllocationItemInfoVo;
import com.apl.wms.outstorage.operator.pojo.vo.PullAllocationItemListVo;
import com.apl.wms.outstorage.operator.service.PullItemService;
import com.apl.wms.warehouse.lib.feign.WarehouseFeign;
import com.apl.wms.warehouse.lib.pojo.bo.PullBatchOrderItemBo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 商品下架 service实现类
 * </p>
 *
 * @author cy
 * @since 2020-06-09
 */
@Service
@Slf4j
public class PullItemServiceImpl extends ServiceImpl<PullAllocationItemMapper, PullAllocationItemPo> implements PullItemService {

//状态code枚举
/*enum PullItemServiceCode {

        ;

        private String code;
        private String msg;

        PullItemServiceCode(String code, String msg) {
             this.code = code;
             this.msg = msg;
        }
    }*/

    @Autowired
    WarehouseFeign warehouseFeign;

    @Override
    public List<PullAllocationItemInfoVo> listPullItemByBatchId(Long batchId) throws Exception {

        return baseMapper.listPullItemByBatchId(batchId);


    }

    @Override
    public ResultUtil<Integer> add(PullAllocationItemPo pullItem){


        Integer flag = baseMapper.insert(pullItem);
        if(flag.equals(1)){
        return ResultUtil.APPRESULT(CommonStatusCode.SAVE_SUCCESS , pullItem.getId());
        }

        return ResultUtil.APPRESULT(CommonStatusCode.SAVE_FAIL , null);
        }


    @Override
    public ResultUtil<Boolean> updById(PullAllocationItemPo pullItem){


        Integer flag = baseMapper.updateById(pullItem);
        if(flag.equals(1)){
        return ResultUtil.APPRESULT(CommonStatusCode.SAVE_SUCCESS , true);
        }

        return ResultUtil.APPRESULT(CommonStatusCode.SAVE_FAIL , false);
        }


    @Override
    public ResultUtil<Boolean> delById(Long id){

        boolean flag = removeById(id);
        if(flag){
        return ResultUtil.APPRESULT(CommonStatusCode.DEL_SUCCESS , true);
        }

        return ResultUtil.APPRESULT(CommonStatusCode.DEL_FAIL , false);
        }


    @Override
    public ResultUtil<PullAllocationItemInfoVo> selectById(Long id){

    PullAllocationItemInfoVo pullAllocationItemInfoVo = baseMapper.getById(id);

        return ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS, pullAllocationItemInfoVo);
        }


    @Override
    public ResultUtil<Page<PullAllocationItemListVo>> getList(PageDto pageDto, PullAllocationItemKeyDto keyDto){

        Page<PullAllocationItemListVo> page = new Page();
        page.setCurrent(pageDto.getPageIndex());
        page.setSize(pageDto.getPageSize());

        List<PullAllocationItemListVo> list = baseMapper.getList(page , keyDto);
        page.setRecords(list);

        return ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS , page);
        }

    @Override
    public void pullCommodity(Long batchId, List<PullBatchOrderItemBo> pullBatchOrderItems) {

        //冻结库位库存
        ResultUtil<Map<String, List<PullBatchOrderItemBo>>> invokeResult = warehouseFeign.lockStorageLocal(pullBatchOrderItems);

        if(invokeResult.getCode().equals(CommonStatusCode.SERVER_INVOKE_FAIL)){
            throw new AplException(CommonStatusCode.SERVER_INVOKE_FAIL);
        }

        Map<String, List<PullBatchOrderItemBo>> commodityOrders = invokeResult.getData();
        for (Map.Entry<String, List<PullBatchOrderItemBo>> entry : commodityOrders.entrySet()) {

            List<PullBatchOrderItemBo> orderItems = entry.getValue();
            for (PullBatchOrderItemBo orderItem : orderItems) {

                List<PullBatchOrderItemBo.StorageCount> storageCounts = orderItem.getStorageCounts();

                for (PullBatchOrderItemBo.StorageCount storageCount : storageCounts) {
                    PullAllocationItemPo pullAllocationItemPo = new PullAllocationItemPo();
                    pullAllocationItemPo.setId(SnowflakeIdWorker.generateId());
                    pullAllocationItemPo.setCommodityId(orderItem.getCommodityId());
                    pullAllocationItemPo.setOutOrderId(orderItem.getOrderId());
                    pullAllocationItemPo.setAllocationQty(storageCount.getCount());
                    pullAllocationItemPo.setStorageLocalId(storageCount.getStorageId());

                    baseMapper.insert(pullAllocationItemPo);
                }

            }
        }



    }


}