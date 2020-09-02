package com.apl.wms.outstorage.operator.mapper;


import com.apl.wms.outstorage.operator.pojo.dto.PullPackItemKeyDto;
import com.apl.wms.outstorage.operator.pojo.po.PullPackItemPo;
import com.apl.wms.outstorage.operator.pojo.vo.PullPackItemInfoVo;
import com.apl.wms.outstorage.operator.pojo.vo.PullPackItemListVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 出库订单打包明细 Mapper 接口
 * </p>
 *
 * @author cy
 * @since 2020-06-13
 */
public interface PullPackItemMapper extends BaseMapper<PullPackItemPo> {

    /**
     * @Desc: 根据id 查找详情
     * @Author: ${cfg.author}
     * @Date: 2020-06-13
     */
    public PullPackItemInfoVo getById(@Param("id") Long id);

    /**
     * @Desc: 查找列表
     * @Author: ${cfg.author}
     * @Date: 2020-06-13
     */
    List<PullPackItemListVo> getList(Page page, @Param("kd") PullPackItemKeyDto keyDto);

}