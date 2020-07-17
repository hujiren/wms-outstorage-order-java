package com.apl.wms.outstorage.operator.service.impl;
import com.apl.lib.constants.CommonStatusCode;
import com.apl.lib.exception.AplException;
import com.apl.lib.join.JoinBase;
import com.apl.lib.join.JoinFieldInfo;
import com.apl.lib.join.JoinUtil;
import com.apl.lib.pojo.dto.PageDto;
import com.apl.lib.utils.ResultUtil;
import com.apl.sys.lib.cache.JoinCustomer;
import com.apl.sys.lib.feign.InnerFeign;
import com.apl.wms.outstorage.operator.dao.PickMapper;
import com.apl.wms.outstorage.operator.pojo.dto.PullOrderKeyDto;
import com.apl.wms.outstorage.operator.pojo.vo.OutOrderPickListVo;
import com.apl.wms.outstorage.operator.service.PickService;
import com.apl.wms.outstorage.order.pojo.vo.OutOrderListVo;
import com.apl.wms.warehouse.lib.cache.OperatorCacheBo;
import com.apl.wms.warehouse.lib.feign.WarehouseFeign;
import com.apl.wms.warehouse.lib.utils.WmsWarehouseUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
        ORDER_INFO_IS_NULL_BY_QUERY("ORDER_INFO_IS_NULL_BY_QUERY", "查询出来的订单信息为空"),
        ORDER_STATUS_IS_WRONG("ORDER_STATUS_IS_WRONG", "该订单尚未被分配"),
        ;

        private String code;
        private String msg;

        AllocationWarehouseServiceCode(String code, String msg) {
            this.code = code;
            this.msg = msg;
        }
    }

    static JoinFieldInfo joinCustomerFieldInfo = null; //跨项目跨库关联 客户表 反射字段缓存

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    WarehouseFeign warehouseFeign;

    @Autowired
    InnerFeign innerFeign;


    @Override
    public ResultUtil<OutOrderPickListVo> allocationPickingMember(List<String> orderSns) throws Exception {

        OperatorCacheBo operatorCacheBo = WmsWarehouseUtils.checkOperator(warehouseFeign, redisTemplate);

        List<OutOrderPickListVo> outOrderPickListVo = baseMapper.getListByOrderSns(orderSns);

        if(outOrderPickListVo.isEmpty()){

            return ResultUtil.APPRESULT(AllocationWarehouseServiceCode.ORDER_INFO_IS_NULL_BY_QUERY.code,
                    AllocationWarehouseServiceCode.ORDER_INFO_IS_NULL_BY_QUERY.msg, null);

        }

        //订单列表
        List<Long> orderIds = new ArrayList<>();

        for (OutOrderPickListVo vo : outOrderPickListVo) {

            if(vo.getOrderStatus() == 6){

                // 订单已取消状态
                return ResultUtil.APPRESULT(AllocationWarehouseServiceCode.ORDER_STATUS_IS_CANCEL.code,
                        AllocationWarehouseServiceCode.ORDER_STATUS_IS_CANCEL.msg
                                + ", orderSn:" + vo.getOrderSn(), null);

            }else if(vo.getOrderStatus() != 3){

                // 订单不是已提交状态
                return ResultUtil.APPRESULT(AllocationWarehouseServiceCode.ORDER_STATUS_IS_NOT_COMMIT.code,
                        AllocationWarehouseServiceCode.ORDER_STATUS_IS_NOT_COMMIT.msg
                                + ", orderSn:" + vo.getOrderSn(), null);

            }

            orderIds.add(vo.getOrderId());

        }

        //批量更新订单拣货员信息和订单状态
        Integer integer =  baseMapper.updateOrderPickingMember(operatorCacheBo.getId(), orderIds);

        if(integer == 0){

            throw new AplException(CommonStatusCode.SAVE_FAIL.code, CommonStatusCode.SAVE_FAIL.msg, integer);
        }

        //跨项目跨库关联表数组
        List<JoinBase> joinTabs = new ArrayList<>();

        //关联客户表字段信息
        JoinCustomer joinCustomer = new JoinCustomer(1, innerFeign, redisTemplate);

        if (null != joinCustomerFieldInfo) {

            joinCustomer.setJoinFieldInfo(joinCustomerFieldInfo);

        } else {

            joinCustomer.addField("customerId", Long.class, "customerName", String.class);

            joinCustomerFieldInfo = joinCustomer.getJoinFieldInfo();

        }
        joinTabs.add(joinCustomer);

        //执行跨项目跨库关联
        JoinUtil.join(outOrderPickListVo, joinTabs);

        return ResultUtil.APPRESULT(CommonStatusCode.SYSTEM_SUCCESS.code, CommonStatusCode.SYSTEM_SUCCESS.msg, outOrderPickListVo);

    }



    @Override
    public ResultUtil<Page<OutOrderPickListVo>> pickManage(PageDto pageDto, PullOrderKeyDto keyDto) throws Exception {

        if(keyDto.getPullStatus() < 3){

            return ResultUtil.APPRESULT(AllocationWarehouseServiceCode.ORDER_STATUS_IS_WRONG.code,
                    AllocationWarehouseServiceCode.ORDER_STATUS_IS_WRONG.msg, null);

        }else if(null == keyDto.getPullStatus()){

            keyDto.setPullStatus(3);
        }

        OperatorCacheBo operatorCacheBo = WmsWarehouseUtils.checkOperator(warehouseFeign, redisTemplate);

        Long whId = operatorCacheBo.getWhId();

        if(null != whId && whId != 0){
            keyDto.setWhId(whId);
        }


        List<OutOrderPickListVo> outOrderInfo;

        Page page = null;
        if (pageDto != null) {
            page = new Page();
            page.setCurrent(pageDto.getPageIndex());
            page.setSize(pageDto.getPageSize());
        }

        outOrderInfo = baseMapper.queryOrderPickInfoByPage(page, keyDto);

        //跨项目跨库关联表数组
        List<JoinBase> joinTabs = new ArrayList<>();

        //关联客户表字段信息
        JoinCustomer joinCustomer = new JoinCustomer(1, innerFeign, redisTemplate);

        if (null != joinCustomerFieldInfo) {

            joinCustomer.setJoinFieldInfo(joinCustomerFieldInfo);

        } else {

            joinCustomer.addField("customerId", Long.class, "customerName", String.class);

            joinCustomerFieldInfo = joinCustomer.getJoinFieldInfo();

        }

        joinTabs.add(joinCustomer);

        //执行跨项目跨库关联
        JoinUtil.join(outOrderInfo, joinTabs);
        //填充仓库
//        fullOutOrderMsg(outOrderInfo);

        page.setRecords(outOrderInfo);

        ResultUtil result = ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS, page);

        return result;
    }

}
