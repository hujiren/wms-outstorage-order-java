package com.apl.wms.outstorage.operator.service.impl;

import com.apl.lib.constants.CommonStatusCode;
import com.apl.lib.exception.AplException;
import com.apl.lib.join.JoinUtil;
import com.apl.lib.pojo.dto.PageDto;
import com.apl.lib.utils.ResultUtil;
import com.apl.lib.utils.SnowflakeIdWorker;
import com.apl.wms.outstorage.order.pojo.po.OutOrderPo;
import com.apl.wms.outstorage.order.service.OutOrderService;
import com.apl.wms.outstorage.operator.dao.PullMaterialsMapper;
import com.apl.wms.outstorage.operator.pojo.dto.PackOrderSubmitDto;
import com.apl.wms.outstorage.operator.pojo.dto.PullMaterialsKeyDto;
import com.apl.wms.outstorage.operator.pojo.po.PullMaterialsPo;
import com.apl.wms.outstorage.operator.pojo.vo.PullMaterialsInfoVo;
import com.apl.wms.outstorage.operator.pojo.vo.PullMaterialsListVo;
import com.apl.wms.outstorage.operator.service.PullMaterialsService;
import com.apl.wms.outstorage.operator.service.PullPackItemService;
import com.apl.wms.outstorage.order.lib.enumwms.OutStorageOrderStatusEnum;
import com.apl.wms.outstorage.order.lib.enumwms.PullStatusType;
import com.apl.wms.warehouse.lib.pojo.bo.PackagingMaterialsCountBo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>
 * 出库订单包装材料 service实现类
 * </p>
 *
 * @author cy
 * @since 2020-06-13
 */
@Service
@Slf4j
public class PullMaterialsServiceImpl extends ServiceImpl<PullMaterialsMapper, PullMaterialsPo> implements PullMaterialsService {

//状态code枚举
    enum PullMaterialsServiceCode {

        PACK_DATA_ERROR("PACK_DATA_ERROR" , "打包数据异常"),
        MATERIALS_COUNT_ERROR("MATERIALS_COUNT_ERROR" , "包装材料数量有误"),
        OUT_ORDER_NOT_EXIST("OUT_ORDER_NOT_EXIST" , "出库订单不存在"),
        OUT_ORDER_NOT_ALLOW_PACK("OUT_ORDER_NOT_ALLOW_PACK" , "该订单不能进行打包"),
        ;

        private String code;
        private String msg;

        PullMaterialsServiceCode(String code, String msg) {
             this.code = code;
             this.msg = msg;
        }
    }

    @Autowired
    RedisTemplate redisTemplate;


    @Autowired
    PullPackItemService pullPackItemService;

    @Autowired
    OutOrderService outOrderService;


    @Override
    public ResultUtil<Integer> add(PullMaterialsPo pullMaterials){


        Integer flag = baseMapper.insert(pullMaterials);
        if(flag.equals(1)){
        return ResultUtil.APPRESULT(CommonStatusCode.SAVE_SUCCESS , pullMaterials.getId());
        }

        return ResultUtil.APPRESULT(CommonStatusCode.SAVE_FAIL , null);
        }


    @Override
    public ResultUtil<Boolean> updById(PullMaterialsPo pullMaterials){


        Integer flag = baseMapper.updateById(pullMaterials);
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
    public ResultUtil<PullMaterialsInfoVo> selectById(Long id){

        PullMaterialsInfoVo pullMaterialsInfoVo = baseMapper.getById(id);

        return ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS, pullMaterialsInfoVo);
    }


    @Override
    public ResultUtil<Page<PullMaterialsListVo>> getList(PageDto pageDto, PullMaterialsKeyDto keyDto){

        Page<PullMaterialsListVo> page = new Page();
        page.setCurrent(pageDto.getPageIndex());
        page.setSize(pageDto.getPageSize());

        List<PullMaterialsListVo> list = baseMapper.getList(page , keyDto);
        page.setRecords(list);

        return ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS , page);
        }

    @Override
    @Transactional
    public ResultUtil submitPackMsg(PackOrderSubmitDto packOrderSubmit) throws Exception {

        //判断订单状态
        OutOrderPo outOrderPo = outOrderService.getById(packOrderSubmit.getOrderId());
        if(outOrderPo == null){
            throw new AplException(PullMaterialsServiceCode.OUT_ORDER_NOT_EXIST.code , PullMaterialsServiceCode.OUT_ORDER_NOT_EXIST.msg);
        }
        if(!outOrderPo.getOrderStatus().equals(OutStorageOrderStatusEnum.CREATE.getStatus()) || !outOrderPo.getPullStatus().equals(PullStatusType.HAS_BEEN_SORTED.getStatus())){
            throw new AplException(PullMaterialsServiceCode.OUT_ORDER_NOT_ALLOW_PACK.code , PullMaterialsServiceCode.OUT_ORDER_NOT_ALLOW_PACK.msg);
        }

        validatePackMsg(packOrderSubmit);

        for (PackOrderSubmitDto.PackCount packCount : packOrderSubmit.getPackCounts()) {
            PullMaterialsPo pullMaterialsPo = new PullMaterialsPo();
            pullMaterialsPo.setId(SnowflakeIdWorker.generateId());
            pullMaterialsPo.setMaterialsId(packCount.getMaterialsId());
            pullMaterialsPo.setOutOrderId(packOrderSubmit.getOrderId());
            pullMaterialsPo.setQty(packCount.getCount());
            baseMapper.insert(pullMaterialsPo);

        }

        pullPackItemService.batchAddPullPackItem(packOrderSubmit.getOrderId() , packOrderSubmit.getPackMsgs());

        //更新订单状态
        outOrderPo.setPullStatus(PullStatusType.HAS_BEEN_PACKED.getStatus());
        outOrderService.updateById(outOrderPo);

        return ResultUtil.APPRESULT(CommonStatusCode.SAVE_SUCCESS , null);
    }

    private void validatePackMsg(PackOrderSubmitDto packOrderSubmit) throws Exception {

        Map<String , List<PackagingMaterialsCountBo>> commodityPackMap = (Map<String , List<PackagingMaterialsCountBo>>) redisTemplate.opsForValue().get("packaging:count:" + packOrderSubmit.getOrderId());

        Map<Long, PackOrderSubmitDto.PackCount> packCountMap = packOrderSubmit.getPackCounts().stream().collect(Collectors.toMap(PackOrderSubmitDto.PackCount::getMaterialsId, packCount -> packCount));

        if(CollectionUtils.isEmpty(commodityPackMap)){
            throw new AplException(PullMaterialsServiceCode.PACK_DATA_ERROR.code , PullMaterialsServiceCode.PACK_DATA_ERROR.msg);
        }
        List<PackagingMaterialsCountBo> packagingMaterialsCountBoList = new ArrayList<>();
        for (Map.Entry<String, List<PackagingMaterialsCountBo>> packagingMaterialsCountEntry : commodityPackMap.entrySet()) {
            packagingMaterialsCountBoList.addAll(packagingMaterialsCountEntry.getValue());
        }

        Map<String, List<PackagingMaterialsCountBo>> materialsMap = JoinUtil.listGrouping(packagingMaterialsCountBoList , "id");

        for (Map.Entry<String, List<PackagingMaterialsCountBo>> packagingMaterialsEntry : materialsMap.entrySet()) {

            Integer materialsCount = 0;
            for (PackagingMaterialsCountBo packagingMaterialsCountBo : packagingMaterialsEntry.getValue()) {
                materialsCount = materialsCount + packagingMaterialsCountBo.getCount();
            }
            PackOrderSubmitDto.PackCount packCount = packCountMap.get(Long.parseLong(packagingMaterialsEntry.getKey()));

            if(!packCount.getCount().equals(materialsCount)){
                throw new AplException(PullMaterialsServiceCode.MATERIALS_COUNT_ERROR.code , PullMaterialsServiceCode.MATERIALS_COUNT_ERROR.msg);
            }

        }

    }


}