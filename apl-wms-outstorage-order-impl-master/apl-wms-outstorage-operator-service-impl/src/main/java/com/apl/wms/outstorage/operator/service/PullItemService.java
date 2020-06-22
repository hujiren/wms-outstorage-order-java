package com.apl.wms.outstorage.operator.service;

import com.apl.lib.pojo.dto.PageDto;
import com.apl.lib.utils.ResultUtils;
import com.apl.wms.outstorage.operator.pojo.dto.PullItemKeyDto;
import com.apl.wms.outstorage.operator.pojo.po.PullItemPo;
import com.apl.wms.outstorage.operator.pojo.vo.PullItemInfoVo;
import com.apl.wms.outstorage.operator.pojo.vo.PullItemListVo;
import com.apl.wms.warehouse.lib.pojo.bo.PullBatchOrderItemBo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 商品下架 service接口
 * </p>
 *
 * @author cy
 * @since 2020-06-09
 */
public interface PullItemService extends IService<PullItemPo> {


        /**
         * @Desc: 根据批次id 获取下架项，并且根据商品id 进行分组
         * @Author: CY
         * @Date: 2020/6/10 14:18
         */
        List<PullItemInfoVo> listPullItemByBatchId(Long batchId) throws Exception;
        /**
         * @Desc: 添加一个PullItemPo实体
         * @author cy
         * @since 2020-06-09
         */
        ResultUtils<Integer> add(PullItemPo pullItem);


        /**
         * @Desc: 根据id 更新一个PullItemPo 实体
         * @author cy
         * @since 2020-06-09
         */
        ResultUtils<Boolean> updById(PullItemPo pullItem);


        /**
         * @Desc: 根据id 查找一个PullItemPo 实体
         * @author cy
         * @since 2020-06-09
         */
        ResultUtils<Boolean> delById(Long id);


        /**
         * @Desc: 根据id 查找一个 PullItemPo 实体
         * @author cy
         * @since 2020-06-09
         */
        ResultUtils<PullItemInfoVo> selectById(Long id);


        /**
         * @Desc: 分页查找 PullItemPo 列表
         * @author cy
         * @since 2020-06-09
         */
        ResultUtils<Page<PullItemListVo>>getList(PageDto pageDto, PullItemKeyDto keyDto);

        /**
         * @Desc: 商品下架
         * @Author: CY
         * @Date: 2020/6/9 11:24
         */
        void pullCommodity(Long batchId, List<PullBatchOrderItemBo> pullBatchOrderItems);


}