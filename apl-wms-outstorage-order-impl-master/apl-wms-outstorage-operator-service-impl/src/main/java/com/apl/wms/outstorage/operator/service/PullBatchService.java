package com.apl.wms.outstorage.operator.service;
import com.apl.lib.pojo.dto.PageDto;
import com.apl.lib.utils.ResultUtil;
import com.apl.wms.outstorage.order.pojo.vo.OrderItemListVo;
import com.apl.wms.outstorage.operator.pojo.dto.PullBatchKeyDto;
import com.apl.wms.outstorage.operator.pojo.po.PullBatchPo;
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
         * @Desc: 根据条件获取拣货列表
         * @author cy
         * @since 2020-06-08
         */
        ResultUtil listPullBatch(Integer pullStatus, String keyword, Long batchTime);

        /**
         * @Desc: 根据批次id 获取拣货信息
         * @Author: CY
         * @Date: 2020/6/10 11:59
         */
        ResultUtil<List<OrderItemListVo>> getPickMsgSortByOrder(Long batchId) throws Exception;

        /**
         * @Desc: 根据批次id 获取拣货信息
         * @Author: CY
         * @Date: 2020/6/10 11:59
         */
        ResultUtil<List<PullAllocationItemMsgVo>> getPickMsgSortByCommodity(Long batchId) throws Exception;

        /**
         * @Desc: 创建收货批次
         * @Author: CY
         * @Date: 2020/6/8 18:18
         */
        ResultUtil<String> createPullBatch(List<Long> ids) throws Exception;

        /**
         * @Desc: 根据id 查找一个PullBatchPo 实体
         * @author cy
         * @since 2020-06-08
         */
        ResultUtil<Boolean> delById(Long id);


        /**
         * @Desc: 分页查找 PullBatchPo 列表
         * @author cy
         * @since 2020-06-08
         */
        ResultUtil<Page<PullBatchListVo>>getList(PageDto pageDto, PullBatchKeyDto keyDto);


}