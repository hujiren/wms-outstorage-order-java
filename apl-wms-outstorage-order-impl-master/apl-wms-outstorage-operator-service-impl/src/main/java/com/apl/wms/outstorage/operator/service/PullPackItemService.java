package com.apl.wms.outstorage.operator.service;

import com.apl.lib.pojo.dto.PageDto;
import com.apl.lib.utils.ResultUtil;
import com.apl.wms.outstorage.operator.pojo.dto.PackOrderSubmitDto;
import com.apl.wms.outstorage.operator.pojo.dto.PullPackItemKeyDto;
import com.apl.wms.outstorage.operator.pojo.po.PullPackItemPo;
import com.apl.wms.outstorage.operator.pojo.vo.PullPackItemInfoVo;
import com.apl.wms.outstorage.operator.pojo.vo.PullPackItemListVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 出库订单打包明细 service接口
 * </p>
 *
 * @author cy
 * @since 2020-06-13
 */
public interface PullPackItemService extends IService<PullPackItemPo> {

    /**
     * @Desc: 添加一个PullPackItemPo实体
     * @author cy
     * @since 2020-06-13
     */
    ResultUtil<Integer> add(PullPackItemPo pullPackItem);


    /**
     * @Desc: 根据id 更新一个PullPackItemPo 实体
     * @author cy
     * @since 2020-06-13
     */
    ResultUtil<Boolean> updById(PullPackItemPo pullPackItem);


    /**
     * @Desc: 根据id 查找一个PullPackItemPo 实体
     * @author cy
     * @since 2020-06-13
     */
    ResultUtil<Boolean> delById(Long id);


    /**
     * @Desc: 根据id 查找一个 PullPackItemPo 实体
     * @author cy
     * @since 2020-06-13
     */
    ResultUtil<PullPackItemInfoVo> selectById(Long id);


    /**
     * @Desc: 分页查找 PullPackItemPo 列表
     * @author cy
     * @since 2020-06-13
     */
    ResultUtil<Page<PullPackItemListVo>> getList(PageDto pageDto, PullPackItemKeyDto keyDto);



}