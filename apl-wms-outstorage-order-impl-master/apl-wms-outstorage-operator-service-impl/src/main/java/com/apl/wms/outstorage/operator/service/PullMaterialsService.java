package com.apl.wms.outstorage.operator.service;

import com.apl.lib.pojo.dto.PageDto;
import com.apl.lib.utils.ResultUtil;
import com.apl.wms.outstorage.operator.pojo.dto.PackOrderSubmitDto;
import com.apl.wms.outstorage.operator.pojo.dto.PullMaterialsKeyDto;
import com.apl.wms.outstorage.operator.pojo.po.PullMaterialsPo;
import com.apl.wms.outstorage.operator.pojo.vo.PullMaterialsInfoVo;
import com.apl.wms.outstorage.operator.pojo.vo.PullMaterialsListVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 出库订单包装材料 service接口
 * </p>
 *
 * @author cy
 * @since 2020-06-13
 */
public interface PullMaterialsService extends IService<PullMaterialsPo> {

        /**
         * @Desc: 添加一个PullMaterialsPo实体
         * @author cy
         * @since 2020-06-13
         */
        ResultUtil<Integer> add(PullMaterialsPo pullMaterials);


        /**
         * @Desc: 根据id 更新一个PullMaterialsPo 实体
         * @author cy
         * @since 2020-06-13
         */
        ResultUtil<Boolean> updById(PullMaterialsPo pullMaterials);


        /**
         * @Desc: 根据id 查找一个PullMaterialsPo 实体
         * @author cy
         * @since 2020-06-13
         */
        ResultUtil<Boolean> delById(Long id);


        /**
         * @Desc: 根据id 查找一个 PullMaterialsPo 实体
         * @author cy
         * @since 2020-06-13
         */
        ResultUtil<PullMaterialsInfoVo> selectById(Long id);


        /**
         * @Desc: 分页查找 PullMaterialsPo 列表
         * @author cy
         * @since 2020-06-13
         */
        ResultUtil<Page<PullMaterialsListVo>> getList(PageDto pageDto, PullMaterialsKeyDto keyDto);

}