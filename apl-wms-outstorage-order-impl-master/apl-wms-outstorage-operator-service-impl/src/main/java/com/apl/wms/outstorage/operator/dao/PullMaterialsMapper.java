package com.apl.wms.outstorage.operator.dao;


import com.apl.wms.outstorage.operator.pojo.dto.PullMaterialsKeyDto;
import com.apl.wms.outstorage.operator.pojo.po.PullMaterialsPo;
import com.apl.wms.outstorage.operator.pojo.vo.PullMaterialsInfoVo;
import com.apl.wms.outstorage.operator.pojo.vo.PullMaterialsListVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 出库订单包装材料 Mapper 接口
 * </p>
 *
 * @author cy
 * @since 2020-06-13
 */
public interface PullMaterialsMapper extends BaseMapper<PullMaterialsPo> {

    /**
     * @Desc: 根据id 查找详情
     * @Author: ${cfg.author}
     * @Date: 2020-06-13
     */
    public PullMaterialsInfoVo getById(@Param("id") Long id);

    /**
     * @Desc: 查找列表
     * @Author: ${cfg.author}
     * @Date: 2020-06-13
     */
    List<PullMaterialsListVo> getList(Page page, @Param("kd") PullMaterialsKeyDto keyDto);


}