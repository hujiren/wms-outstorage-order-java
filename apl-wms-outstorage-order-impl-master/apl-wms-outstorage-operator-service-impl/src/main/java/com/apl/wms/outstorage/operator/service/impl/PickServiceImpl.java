package com.apl.wms.outstorage.operator.service.impl;
import com.apl.lib.constants.CommonStatusCode;
import com.apl.lib.exception.AplException;
import com.apl.lib.join.JoinUtil;
import com.apl.lib.utils.ResultUtil;
import com.apl.wms.outstorage.operator.dao.PickMapper;
import com.apl.wms.outstorage.operator.service.PickService;
import com.apl.wms.outstorage.order.pojo.vo.OutOrderListVo;
import com.apl.wms.warehouse.lib.cache.OperatorCacheBo;
import com.apl.wms.warehouse.lib.feign.WarehouseFeign;
import com.apl.wms.warehouse.lib.utils.WmsWarehouseUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.SqlReturnUpdateCount;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hjr start
 * @date 2020/7/13 - 15:41
 */
@Service
@Slf4j
public class PickServiceImpl extends ServiceImpl<PickMapper, OutOrderListVo> implements PickService {

    //状态code枚举
    enum AllocationWarehouseServiceCode {
        ORDER_STATUS_IS_CANCEL("ORDER_STATUS_IS_CANCEL" ,"该订单状态为取消状态"),
        ORDER_STATUS_IS_NOT_COMMIT("ORDER_STATUS_IS_NOT_COMMIT", "该订单不是已提交状态"),
        THE_ORDER_HAS_BEEN_ASSIGNED_TO_A_PICKER("THE_ORDER_HAS_BEEN_ASSIGNED_TO_A_PICKER", "该订单已经分配拣货员")
        ;

        private String code;
        private String msg;

        AllocationWarehouseServiceCode(String code, String msg) {
            this.code = code;
            this.msg = msg;
        }
    }

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    WarehouseFeign warehouseFeign;

    @Override
    public ResultUtil<Boolean> allocationPickingMember(List<String> orderSns) {

        OperatorCacheBo operatorCacheBo = WmsWarehouseUtils.checkOperator(warehouseFeign, redisTemplate);

        List<OutOrderListVo> outOrderListVo = baseMapper.getListByOrderSns(orderSns);

        if(outOrderListVo.isEmpty()){
            return ResultUtil.APPRESULT(CommonStatusCode.NULL_POINT_ERROR.code, CommonStatusCode.NULL_POINT_ERROR.msg, outOrderListVo);
        }

        List<Long> ids = new ArrayList<>();
        for (OutOrderListVo vo : outOrderListVo) {
            if(vo.getOrderStatus() == 6){
                // 订单已取消状态
                return ResultUtil.APPRESULT(AllocationWarehouseServiceCode.ORDER_STATUS_IS_CANCEL.code,
                        AllocationWarehouseServiceCode.ORDER_STATUS_IS_CANCEL.msg + ", orderSn:" + vo.getOrderSn(), null);
            }else if(vo.getOrderStatus() != 3){
                // 订单不是已提交状态
                return ResultUtil.APPRESULT(AllocationWarehouseServiceCode.ORDER_STATUS_IS_NOT_COMMIT.code,
                        AllocationWarehouseServiceCode.ORDER_STATUS_IS_NOT_COMMIT.msg + ", orderSn:" + vo.getOrderSn(), null);
            }

            ids.add(vo.getId());
        }

        Integer integer =  baseMapper.updateOrderPickingMember(operatorCacheBo.getId(), ids);
        if(integer == 0){
            throw new AplException(CommonStatusCode.SAVE_FAIL.code, CommonStatusCode.SAVE_FAIL.msg, integer);
        }

        ResultUtil<Boolean> booleanResult = ResultUtil.APPRESULT(CommonStatusCode.SYSTEM_SUCCESS,true);
        return booleanResult;
    }
}
