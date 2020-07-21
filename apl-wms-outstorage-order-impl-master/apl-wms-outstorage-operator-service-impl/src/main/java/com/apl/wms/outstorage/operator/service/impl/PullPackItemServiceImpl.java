package com.apl.wms.outstorage.operator.service.impl;
import com.apl.lib.constants.CommonStatusCode;
import com.apl.lib.pojo.dto.PageDto;
import com.apl.lib.utils.ResultUtil;
import com.apl.lib.utils.SnowflakeIdWorker;
import com.apl.wms.outstorage.operator.dao.PullPackItemMapper;
import com.apl.wms.outstorage.operator.pojo.dto.PackOrderSubmitDto;
import com.apl.wms.outstorage.operator.pojo.dto.PullPackItemKeyDto;
import com.apl.wms.outstorage.operator.pojo.po.PullPackItemPo;
import com.apl.wms.outstorage.operator.pojo.vo.PullPackItemInfoVo;
import com.apl.wms.outstorage.operator.pojo.vo.PullPackItemListVo;
import com.apl.wms.outstorage.operator.service.PullPackItemService;
import com.apl.wms.warehouse.lib.feign.WarehouseFeign;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


/**
 * <p>
 * 出库订单打包明细 service实现类
 * </p>
 *
 * @author cy
 * @since 2020-06-13
 */
@Service
@Slf4j
public class PullPackItemServiceImpl extends ServiceImpl<PullPackItemMapper, PullPackItemPo> implements PullPackItemService {



   @Autowired
    WarehouseFeign warehouseFeign;

    @Override
    public ResultUtil<Integer> add(PullPackItemPo pullPackItem){


        Integer flag = baseMapper.insert(pullPackItem);
        if(flag.equals(1)){
        return ResultUtil.APPRESULT(CommonStatusCode.SAVE_SUCCESS , pullPackItem.getId());
        }

        return ResultUtil.APPRESULT(CommonStatusCode.SAVE_FAIL , null);
        }


    @Override
    public ResultUtil<Boolean> updById(PullPackItemPo pullPackItem){


        Integer flag = baseMapper.updateById(pullPackItem);
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
    public ResultUtil<PullPackItemInfoVo> selectById(Long id){

    PullPackItemInfoVo pullPackItemInfoVo = baseMapper.getById(id);

        return ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS, pullPackItemInfoVo);
        }


    @Override
    public ResultUtil<Page<PullPackItemListVo>> getList(PageDto pageDto, PullPackItemKeyDto keyDto){

        Page<PullPackItemListVo> page = new Page();
        page.setCurrent(pageDto.getPageIndex());
        page.setSize(pageDto.getPageSize());

        List<PullPackItemListVo> list = baseMapper.getList(page , keyDto);
        page.setRecords(list);

        return ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS , page);
        }

    @Override
    public void batchAddPullPackItem(Long orderId , List<PackOrderSubmitDto.PackMsg> packMsgs) {

        for (PackOrderSubmitDto.PackMsg packMsg : packMsgs) {

            PullPackItemPo pullPackItemPo = new PullPackItemPo();
            pullPackItemPo.setId(SnowflakeIdWorker.generateId());
            pullPackItemPo.setOutOrderId(orderId);
            pullPackItemPo.setSubSn(packMsg.getSubSn());
            pullPackItemPo.setGw(packMsg.getGw());
            pullPackItemPo.setSizeLength(packMsg.getSizeLength());
            pullPackItemPo.setSizeWidth(packMsg.getSizeWidth());
            pullPackItemPo.setSizeHeight(packMsg.getSizeHeight());
            pullPackItemPo.setVolume(packMsg.getVolume());

            baseMapper.insert(pullPackItemPo);
        }

    }


}