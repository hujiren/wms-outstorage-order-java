package com.apl.wms.outstorage.operator.service;

import com.apl.lib.pojo.dto.PageDto;
import com.apl.lib.utils.ResultUtils;
import com.apl.wms.outstorage.order.pojo.vo.OrderItemListVo;
import com.apl.wms.outstorage.operator.pojo.dto.PullBatchKeyDto;
import com.apl.wms.outstorage.operator.pojo.dto.PullBatchSubmitDto;
import com.apl.wms.outstorage.operator.pojo.dto.SortOrderSubmitDto;
import com.apl.wms.outstorage.operator.pojo.po.PullBatchPo;
import com.apl.wms.outstorage.operator.pojo.vo.PackOrderItemListVo;
import com.apl.wms.outstorage.operator.pojo.vo.PullBatchListVo;
import com.apl.wms.outstorage.operator.pojo.vo.PullAllocationItemMsgVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 拣货批次 service接口
 * </p>
 *
 * @author cy
 * @since 2020-06-08
 */
public interface PullBatchService extends IService<PullBatchPo> {


        /**
         * @Desc: 根据订单id 获取打包信息
         * @Author: CY
         * @Date: 2020/6/12 11:25
         */
        ResultUtils<PackOrderItemListVo> getSortMsg(Long orderId) throws Exception;


        /**
         * @Desc: 根据状态获取拣货列表
         * @author cy
         * @since 2020-06-08
         */
        ResultUtils listPullBatch(Integer pullStatus, String keyword, Long batchTime);

        /**
         * @Desc: 根据批次id 获取拣货信息
         * @Author: CY
         * @Date: 2020/6/10 11:59
         */
        ResultUtils<List<OrderItemListVo>> getPickMsgSortByOrder(Long batchId) throws Exception;

        /**
         * @Desc: 根据批次id 获取拣货信息
         * @Author: CY
         * @Date: 2020/6/10 11:59
         */
        ResultUtils<List<PullAllocationItemMsgVo>> getPickMsgSortByCommodity(Long batchId) throws Exception;

        /**
         * @Desc: 创建收货批次
         * @Author: CY
         * @Date: 2020/6/8 18:18
         */
        ResultUtils<String> createPullBatch(String ids);

        /**
         * @Desc: 根据id 查找一个PullBatchPo 实体
         * @author cy
         * @since 2020-06-08
         */
        ResultUtils<Boolean> delById(Long id);


        /**
         * @Desc: 分页查找 PullBatchPo 列表
         * @author cy
         * @since 2020-06-08
         */
        ResultUtils<Page<PullBatchListVo>>getList(PageDto pageDto, PullBatchKeyDto keyDto);

        /**
         * @Desc: 创建一个捡货批次，返回批次号
         * @Author: CY
         * @Date: 2020/6/9 9:25
         */
        Long createBatch();


        /**
         * @Desc: 提交拣货信息
         * @Author: CY
         * @Date: 2020/6/10 16:42
         */
        ResultUtils submitPullBatch(PullBatchSubmitDto pullBatchSubmit) throws Exception;


        /**
         * @Desc: 分拣数据提交
         * @Author: CY
         * @Date: 2020/6/12 16:12
         */
        ResultUtils submitSortMsg(SortOrderSubmitDto sortOrderSubmitDto) throws Exception;
}