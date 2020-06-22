package com.apl.wms.outstorage.operator.service.impl;

import com.apl.lib.constants.CommonStatusCode;
import com.apl.lib.exception.AplException;
import com.apl.lib.pojo.dto.PageDto;
import com.apl.lib.utils.ResultUtils;
import com.apl.lib.utils.SnowflakeIdWorker;
import com.apl.wms.outstorage.operator.mapper.PullItemMapper;
import com.apl.wms.outstorage.operator.pojo.dto.PullItemKeyDto;
import com.apl.wms.outstorage.operator.pojo.po.PullItemPo;
import com.apl.wms.outstorage.operator.pojo.vo.PullItemInfoVo;
import com.apl.wms.outstorage.operator.pojo.vo.PullItemListVo;
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
public class PullItemServiceImpl extends ServiceImpl<PullItemMapper, PullItemPo> implements PullItemService {

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
    public List<PullItemInfoVo> listPullItemByBatchId(Long batchId) throws Exception {

        return baseMapper.listPullItemByBatchId(batchId);


    }

    @Override
    public ResultUtils<Integer> add(PullItemPo pullItem){


        Integer flag = baseMapper.insert(pullItem);
        if(flag.equals(1)){
        return ResultUtils.APPRESULT(CommonStatusCode.SAVE_SUCCESS , pullItem.getId());
        }

        return ResultUtils.APPRESULT(CommonStatusCode.SAVE_FAIL , null);
        }


    @Override
    public ResultUtils<Boolean> updById(PullItemPo pullItem){


        Integer flag = baseMapper.updateById(pullItem);
        if(flag.equals(1)){
        return ResultUtils.APPRESULT(CommonStatusCode.SAVE_SUCCESS , true);
        }

        return ResultUtils.APPRESULT(CommonStatusCode.SAVE_FAIL , false);
        }


    @Override
    public ResultUtils<Boolean> delById(Long id){

        boolean flag = removeById(id);
        if(flag){
        return ResultUtils.APPRESULT(CommonStatusCode.DEL_SUCCESS , true);
        }

        return ResultUtils.APPRESULT(CommonStatusCode.DEL_FAIL , false);
        }


    @Override
    public ResultUtils<PullItemInfoVo> selectById(Long id){

    PullItemInfoVo pullItemInfoVo = baseMapper.getById(id);

        return ResultUtils.APPRESULT(CommonStatusCode.GET_SUCCESS, pullItemInfoVo);
        }


    @Override
    public ResultUtils<Page<PullItemListVo>> getList(PageDto pageDto, PullItemKeyDto keyDto){

        Page<PullItemListVo> page = new Page();
        page.setCurrent(pageDto.getPageIndex());
        page.setSize(pageDto.getPageSize());

        List<PullItemListVo> list = baseMapper.getList(page , keyDto);
        page.setRecords(list);

        return ResultUtils.APPRESULT(CommonStatusCode.GET_SUCCESS , page);
        }

    @Override
    public void pullCommodity(Long batchId, List<PullBatchOrderItemBo> pullBatchOrderItems) {

        //冻结库位库存
        ResultUtils<Map<String, List<PullBatchOrderItemBo>>> invokeResult = warehouseFeign.lockStorageLocal(pullBatchOrderItems);

        if(invokeResult.getCode().equals(CommonStatusCode.SERVER_INVOKE_FAIL)){
            throw new AplException(CommonStatusCode.SERVER_INVOKE_FAIL);
        }

        Map<String, List<PullBatchOrderItemBo>> commodityOrders = invokeResult.getData();
        for (Map.Entry<String, List<PullBatchOrderItemBo>> entry : commodityOrders.entrySet()) {

            List<PullBatchOrderItemBo> orderItems = entry.getValue();
            for (PullBatchOrderItemBo orderItem : orderItems) {

                List<PullBatchOrderItemBo.StorageCount> storageCounts = orderItem.getStorageCounts();

                for (PullBatchOrderItemBo.StorageCount storageCount : storageCounts) {
                    PullItemPo pullItemPo = new PullItemPo();
                    pullItemPo.setId(SnowflakeIdWorker.generateId());
                    pullItemPo.setBatchId(batchId);
                    pullItemPo.setCommodityId(orderItem.getCommodityId());
                    pullItemPo.setOutOrderId(orderItem.getOrderId());
                    pullItemPo.setPullQty(storageCount.getCount());
                    pullItemPo.setStorageLocalId(storageCount.getStorageId());

                    baseMapper.insert(pullItemPo);
                }

            }
        }



    }


}