package com.apl.wms.outstorage.operator.service;

import com.apl.lib.pojo.dto.PageDto;
import com.apl.lib.utils.ResultUtil;
import com.apl.wms.outstorage.operator.pojo.dto.PullAllocationItemKeyDto;
import com.apl.wms.outstorage.operator.pojo.po.PullAllocationItemPo;
import com.apl.wms.outstorage.operator.pojo.vo.PullAllocationItemInfoVo;
import com.apl.wms.outstorage.operator.pojo.vo.PullAllocationItemListVo;
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
public interface PullItemService extends IService<PullAllocationItemPo> {


        /**
         * @Desc: 根据批次id 获取下架项，并且根据商品id 进行分组
         * @Author: CY
         * @Date: 2020/6/10 14:18
         */
        List<PullAllocationItemInfoVo> listPullItemByBatchId(Long batchId) throws Exception;
        /**
         * @Desc: 添加一个PullItemPo实体
         * @author cy
         * @since 2020-06-09
         */
        ResultUtil<Integer> add(PullAllocationItemPo pullItem);


        /**
         * @Desc: 根据id 更新一个PullItemPo 实体
         * @author cy
         * @since 2020-06-09
         */
        ResultUtil<Boolean> updById(PullAllocationItemPo pullItem);


        /**
         * @Desc: 根据id 查找一个PullItemPo 实体
         * @author cy
         * @since 2020-06-09
         */
        ResultUtil<Boolean> delById(Long id);


        /**
         * @Desc: 根据id 查找一个 PullItemPo 实体
         * @author cy
         * @since 2020-06-09
         */
        ResultUtil<PullAllocationItemInfoVo> selectById(Long id);


        /**
         * @Desc: 分页查找 PullItemPo 列表
         * @author cy
         * @since 2020-06-09
         */
        ResultUtil<Page<PullAllocationItemListVo>>getList(PageDto pageDto, PullAllocationItemKeyDto keyDto);

        /**
         * @Desc: 商品下架
         * @Author: CY
         * @Date: 2020/6/9 11:24
         */
        void pullCommodity(Long batchId, List<PullBatchOrderItemBo> pullBatchOrderItems);


}