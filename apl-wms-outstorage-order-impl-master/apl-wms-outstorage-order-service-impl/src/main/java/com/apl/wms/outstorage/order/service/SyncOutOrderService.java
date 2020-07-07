package com.apl.wms.outstorage.order.service;

import com.apl.lib.pojo.dto.PageDto;
import com.apl.lib.utils.ResultUtil;
import com.apl.wms.outstorage.order.pojo.dto.SyncOutOrderKeyDto;
import com.apl.wms.outstorage.order.pojo.dto.SyncOutOrderSaveDto;
import com.apl.wms.outstorage.order.pojo.vo.SyncOutOrderInfoVo;
import com.apl.wms.outstorage.order.pojo.vo.SyncOutOrderListVo;
import com.apl.wms.outstorage.order.pojo.po.SyncOutOrderPo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 同步平台订单 service接口
 * </p>
 *
 * @author arran
 * @since 2019-12-25
 */
public interface SyncOutOrderService extends IService<SyncOutOrderPo> {


        /**
         * @Desc: 添加一个SyncOrderPo实体
         * @author arran
         * @since 2019-12-25
         */
        ResultUtil<Integer> add(SyncOutOrderSaveDto syncOrder);


        /**
         * @Desc: 根据id 更新一个SyncOrderPo 实体
         * @author arran
         * @since 2019-12-25
         */
        ResultUtil<Boolean> updById(SyncOutOrderSaveDto syncOrder, Long customerId);


        /**
         * @Desc: 根据id 更新状态
         * @author arran
         * @since 2019-12-25
         */
        ResultUtil<Boolean> updStatus(Long id, Integer status, Long customerId);


        /**
         * @Desc: 根据id 查找一个SyncOrderPo 实体
         * @author arran
         * @since 2019-12-25
         */
        ResultUtil<Boolean> delById(Long id, Long customerId);


        /**
         * @Desc: 根据id 查找一个 SyncOutOrderPo 实体
         * @author arran
         * @since 2019-12-25
         */
        ResultUtil<SyncOutOrderInfoVo> selectById(Long id, Long customerId, Integer isShowCustomer);


        /**
         * @Desc: 分页查找 SyncOutOrderPo 列表
         * @author arran
         * @since 2019-12-25
         */
        ResultUtil<Page<SyncOutOrderListVo>>getList(PageDto pageDto, SyncOutOrderKeyDto keyDto, Integer isShowCustomer)  throws Exception ;


        /**
         * @Desc: 启动任务
         * @author arran
         * @since 2020-01-07
         */
        ResultUtil<Boolean> bootTask(Long id, Long customerId);


        /**
         * @Desc: 获取任务状态
         * @author arran
         * @since 2020-01-15
         */
        ResultUtil<Integer> getStatus(Long id);

}
