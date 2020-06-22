package com.apl.wms.outstorage.operator.mapper;


import com.apl.wms.outstorage.operator.pojo.dto.PullItemKeyDto;
import com.apl.wms.outstorage.operator.pojo.po.PullItemPo;
import com.apl.wms.outstorage.operator.pojo.vo.PullItemInfoVo;
import com.apl.wms.outstorage.operator.pojo.vo.PullItemListVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 商品下架 Mapper 接口
 * </p>
 *
 * @author cy
 * @since 2020-06-09
 */
public interface PullItemMapper extends BaseMapper<PullItemPo> {

    /**
     * @Desc: 根据批次id ，获取下架项列表
     * @Author: CY
     * @Date: 2020/6/10 14:24
     */
    List<PullItemInfoVo> listPullItemByBatchId(@Param("batchId") Long batchId);

    /**
     * @Desc: 根据id 查找详情
     * @Author: ${cfg.author}
     * @Date: 2020-06-09
     */
    public PullItemInfoVo getById(@Param("id") Long id);

        /**
         * @Desc: 查找列表
         * @Author: ${cfg.author}
         * @Date: 2020-06-09
         */
        List<PullItemListVo> getList(Page page, @Param("kd") PullItemKeyDto keyDto);



}