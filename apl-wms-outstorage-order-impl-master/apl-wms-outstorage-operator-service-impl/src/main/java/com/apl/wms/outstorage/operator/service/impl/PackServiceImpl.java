package com.apl.wms.outstorage.operator.service.impl;

import com.apl.cache.AplCacheUtil;
import com.apl.lib.constants.CommonStatusCode;
import com.apl.lib.exception.AplException;
import com.apl.lib.join.JoinBase;
import com.apl.lib.join.JoinFieldInfo;
import com.apl.lib.join.JoinUtil;
import com.apl.lib.utils.ResultUtil;
import com.apl.lib.utils.SnowflakeIdWorker;
import com.apl.lib.utils.StringUtil;
import com.apl.wms.outstorage.operator.dao.PackMapper;
import com.apl.wms.outstorage.operator.pojo.dto.PullMaterialsDto;
import com.apl.wms.outstorage.operator.pojo.dto.PullPackItemDto;
import com.apl.wms.outstorage.operator.pojo.vo.OutOrderAttachInfoVo;
import com.apl.wms.outstorage.operator.pojo.vo.PackCommodityInfoVo;
import com.apl.wms.outstorage.operator.pojo.vo.PackingInfo;
import com.apl.wms.outstorage.operator.service.PackService;
import com.apl.wms.warehouse.lib.cache.JoinCommodity;
import com.apl.wms.warehouse.lib.feign.WarehouseFeign;
import com.apl.wms.warehouse.lib.pojo.vo.PackagingMaterialsInfoVo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.plaf.basic.BasicSeparatorUI;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hjr start
 * @date 2020/7/28 - 14:57
 */
@Service
@Slf4j
public class PackServiceImpl extends ServiceImpl<PackMapper, PackingInfo> implements PackService {

    //状态code枚举
    enum PickServiceCode {
        NO_DATA_WAS_FOUND("NO_DATA_WAS_FOUND", "没有找到对应数据!!"),
        REMOTE_PROCEDURE_CALL_FAILED("REMOTE_PROCEDURE_CALL_FAILED", "远程调用失败"),
        ORDER_ID_IS_WRONG("ORDER_ID_IS_WRONG", "传入的订单id不正确"),
        THIS_ORDER_PULL_STATUS_IS_WRONG("THIS_ORDER_PULL_STATUS_IS_WRONG", "该订单拣货状态错误!!")
        ;

        private String code;
        private String msg;

        PickServiceCode(String code, String msg) {
            this.code = code;
            this.msg = msg;
        }
    }

    @Autowired
    WarehouseFeign warehouseFeign;

    @Autowired
    AplCacheUtil aplCacheUtil;

    static JoinFieldInfo joinCommodityFieldInfo = null;

    /**
     * 根据订单号查询订单信息, 商品信息, 包装材料信息
     * @param orderSn
     * @return
     */
    @Override
    public ResultUtil<PackingInfo> getPackInfo(String orderSn) throws Exception {

            //通过订单号查询订单和物流详情
            OutOrderAttachInfoVo outOrderAttachInfoVo = baseMapper.getOrderAttachInfoByOrderSn(orderSn);

            if(outOrderAttachInfoVo == null){
                return ResultUtil.APPRESULT(PickServiceCode.NO_DATA_WAS_FOUND.code, PickServiceCode.NO_DATA_WAS_FOUND.msg, null);
            }

            //通过订单号查询商品信息
            List<PackCommodityInfoVo> commodityInfoList = baseMapper.getCommodityInfoByOrderSn(orderSn);

            //构建商品Id列表
            List<Long> commodityIds = new ArrayList<>();

            for (PackCommodityInfoVo packCommodityInfoVo : commodityInfoList) {
                commodityIds.add(packCommodityInfoVo.getCommodityId());
            }

            //关联商品图片
            JoinCommodity joinCommodity = new JoinCommodity(1, warehouseFeign, aplCacheUtil);

            //跨项目跨库关联表数组
            List<JoinBase> joinTabs = new ArrayList<>();
            if (null != joinCommodityFieldInfo) {
                joinCommodity.setJoinFieldInfo(joinCommodityFieldInfo);
            } else {
                joinCommodity.addField("commodityId", Long.class, "imgUrl", String.class);//添加缓存字段
                joinCommodityFieldInfo = joinCommodity.getJoinFieldInfo();
            }

            joinTabs.add(joinCommodity);
            //执行跨项目跨库关联商品图片
            JoinUtil.join(commodityInfoList, joinTabs);

        //跨库查询包装材料列表
        ResultUtil<List<PackagingMaterialsInfoVo>> packagingMaterialsResult = null;

        try {

            //生成一个唯一的事务id , 用来校验远程调用是否成功
            String tranId ="tranId:"+ StringUtil.generateUuid();
            packagingMaterialsResult = warehouseFeign.getPackingMaterialsByCommodityIds(tranId, commodityIds);
            if(!aplCacheUtil.hasKey(tranId)){
                //如果远程调用失败, redis中key(事务id)将为空
                throw new AplException(PickServiceCode.REMOTE_PROCEDURE_CALL_FAILED.code, PickServiceCode.REMOTE_PROCEDURE_CALL_FAILED.msg);
            }

            aplCacheUtil.delete(tranId);


        } catch (Exception e) {
            e.printStackTrace();
            throw new AplException(PickServiceCode.REMOTE_PROCEDURE_CALL_FAILED.code, PickServiceCode.REMOTE_PROCEDURE_CALL_FAILED.msg, null);
        }

        //远程调用包装材料返回的列表
        List<PackagingMaterialsInfoVo> list = packagingMaterialsResult.getData();
        ObjectMapper objectMapper = new ObjectMapper();
        String s = objectMapper.writeValueAsString(list);
        List<PackagingMaterialsInfoVo> packagingMaterialsInfoList = objectMapper.readValue(s, new TypeReference<List<PackagingMaterialsInfoVo>>() {
        });


        //set所需包装材料数量
        for (PackagingMaterialsInfoVo packagingMaterialsInfoVo : packagingMaterialsInfoList) {
            for (PackCommodityInfoVo packCommodityInfoVo : commodityInfoList) {
                if(packCommodityInfoVo.getCommodityId() == packCommodityInfoVo.getCommodityId()){
                    packagingMaterialsInfoVo.setCount(packCommodityInfoVo.getOrderQty() / packagingMaterialsInfoVo.getCapacity());
                }
            }
        }


        //组装返回前端对象
        PackingInfo packingInfo = new PackingInfo();
        packingInfo.setOutOrderAttachInfoVo(outOrderAttachInfoVo);
        packingInfo.setPackCommodityInfoVoList(commodityInfoList);
        packingInfo.setPackagingMaterialsInfoVo(packagingMaterialsInfoList);

        return ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS, packingInfo);
    }


    /**
     * 提交打包信息
     * @param
     * @return
     */
    @Override
    public ResultUtil<Boolean> submitPackInfo(List<PullMaterialsDto> pullMaterialsDtoList) {

        List<Long> orderIds = new ArrayList<>();

        //校验 遍历取订单Ids, 同时使用雪花算法生成id
        for (PullMaterialsDto pullMaterialsDto : pullMaterialsDtoList) {
            pullMaterialsDto.setId(SnowflakeIdWorker.generateId());
            orderIds.add(pullMaterialsDto.getOutOrderId());
        }

        //通过订单ids获取订单拣货状态
        List<Integer> pullStatusList = baseMapper.getPullStatusByIds(orderIds);

        if(pullStatusList.size() == 0){
            return ResultUtil.APPRESULT(PickServiceCode.ORDER_ID_IS_WRONG.code, PickServiceCode.ORDER_ID_IS_WRONG.msg, false);
        }

        //必须为已拣货状态才能打包
        for (Integer integer : pullStatusList) {
            if(integer != 7){
                return ResultUtil.APPRESULT(PickServiceCode.THIS_ORDER_PULL_STATUS_IS_WRONG.code,
                        PickServiceCode.THIS_ORDER_PULL_STATUS_IS_WRONG.msg, false);
            }
        }

        //向pull_materials表插入打包数据
        Integer integer = baseMapper.insertPullMaterials(pullMaterialsDtoList);

        //批量修改订单状态为"8", 已打包状态
        Integer integer1 = baseMapper.updatePullStatusByIds(orderIds);

        return ResultUtil.APPRESULT(CommonStatusCode.SAVE_SUCCESS, true);
    }


    /**
     * 提交打包尺寸
     * @param pullPackItemList
     * @return
     */
    @Override
    public ResultUtil<Boolean> submitPackSize(List<PullPackItemDto> pullPackItemList) {

        List<Long> orderIds = new ArrayList<>();

        //校验 遍历取订单Ids, 同时使用雪花算法生成id
        for (PullPackItemDto pullPackItemDto : pullPackItemList) {
            pullPackItemDto.setId(SnowflakeIdWorker.generateId());
            orderIds.add(pullPackItemDto.getOutOrderId());
        }

        //通过订单ids获取订单拣货状态
        List<Integer> pullStatusList = baseMapper.getPullStatusByIds(orderIds);

        if(pullStatusList.size() == 0){
            return ResultUtil.APPRESULT(PickServiceCode.ORDER_ID_IS_WRONG.code, PickServiceCode.ORDER_ID_IS_WRONG.msg, false);
        }

        //必须为已打包状态才能入库物流子订单
        for (Integer integer : pullStatusList) {
            if(integer != 8){
                return ResultUtil.APPRESULT(PickServiceCode.THIS_ORDER_PULL_STATUS_IS_WRONG.code,
                        PickServiceCode.THIS_ORDER_PULL_STATUS_IS_WRONG.msg, false);
            }
        }

        //向pull_pack_item插入打包尺寸数据
        Integer integer = baseMapper.insertPullPackItem(pullPackItemList);

        return ResultUtil.APPRESULT(CommonStatusCode.SAVE_SUCCESS, true);
    }

}
