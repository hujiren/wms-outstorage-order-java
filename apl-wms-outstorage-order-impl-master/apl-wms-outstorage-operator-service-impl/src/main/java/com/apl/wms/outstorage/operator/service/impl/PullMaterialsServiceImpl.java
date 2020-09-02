package com.apl.wms.outstorage.operator.service.impl;

import com.apl.cache.AplCacheUtil;
import com.apl.lib.constants.CommonStatusCode;
import com.apl.lib.pojo.dto.PageDto;
import com.apl.lib.utils.ResultUtil;
import com.apl.wms.outstorage.order.service.OutOrderService;
import com.apl.wms.outstorage.operator.mapper.PullMaterialsMapper;
import com.apl.wms.outstorage.operator.pojo.dto.PullMaterialsKeyDto;
import com.apl.wms.outstorage.operator.pojo.po.PullMaterialsPo;
import com.apl.wms.outstorage.operator.pojo.vo.PullMaterialsInfoVo;
import com.apl.wms.outstorage.operator.pojo.vo.PullMaterialsListVo;
import com.apl.wms.outstorage.operator.service.PullMaterialsService;
import com.apl.wms.outstorage.operator.service.PullPackItemService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * <p>
 * 出库订单包装材料 service实现类
 * </p>
 *
 * @author cy
 * @since 2020-06-13
 */
@Service
@Slf4j
public class PullMaterialsServiceImpl extends ServiceImpl<PullMaterialsMapper, PullMaterialsPo> implements PullMaterialsService {

//状态code枚举
    enum PullMaterialsServiceCode {

        PACK_DATA_ERROR("PACK_DATA_ERROR" , "打包数据异常"),
        MATERIALS_COUNT_ERROR("MATERIALS_COUNT_ERROR" , "包装材料数量有误"),
        OUT_ORDER_NOT_EXIST("OUT_ORDER_NOT_EXIST" , "出库订单不存在"),
        OUT_ORDER_NOT_ALLOW_PACK("OUT_ORDER_NOT_ALLOW_PACK" , "该订单不能进行打包"),
        ;

        private String code;
        private String msg;

        PullMaterialsServiceCode(String code, String msg) {
             this.code = code;
             this.msg = msg;
        }
    }

    @Autowired
    AplCacheUtil redisTemplate;


    @Autowired
    PullPackItemService pullPackItemService;

    @Autowired
    OutOrderService outOrderService;


    @Override
    public ResultUtil<Integer> add(PullMaterialsPo pullMaterials){


        Integer flag = baseMapper.insert(pullMaterials);
        if(flag.equals(1)){
        return ResultUtil.APPRESULT(CommonStatusCode.SAVE_SUCCESS , pullMaterials.getId());
        }

        return ResultUtil.APPRESULT(CommonStatusCode.SAVE_FAIL , null);
        }


    @Override
    public ResultUtil<Boolean> updById(PullMaterialsPo pullMaterials){


        Integer flag = baseMapper.updateById(pullMaterials);
        if(flag.equals(1)){
        return ResultUtil.APPRESULT(CommonStatusCode.SAVE_SUCCESS , true);
        }

        return ResultUtil.APPRESULT(CommonStatusCode.SAVE_FAIL , false);
        }


    @Override
    public ResultUtil<Boolean> delById(Long id){

        boolean flag = removeById(id);
        if(flag){
        return ResultUtil.APPRESULT(CommonStatusCode.DEL_SUCCESS , true);
        }

        return ResultUtil.APPRESULT(CommonStatusCode.DEL_FAIL , false);
        }


    @Override
    public ResultUtil<PullMaterialsInfoVo> selectById(Long id){

        PullMaterialsInfoVo pullMaterialsInfoVo = baseMapper.getById(id);

        return ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS, pullMaterialsInfoVo);
    }


    @Override
    public ResultUtil<Page<PullMaterialsListVo>> getList(PageDto pageDto, PullMaterialsKeyDto keyDto){

        Page<PullMaterialsListVo> page = new Page();
        page.setCurrent(pageDto.getPageIndex());
        page.setSize(pageDto.getPageSize());

        List<PullMaterialsListVo> list = baseMapper.getList(page , keyDto);
        page.setRecords(list);

        return ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS , page);
        }






}