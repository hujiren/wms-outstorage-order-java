package com.apl.wms.outstorage.operator.service;
import com.apl.lib.pojo.dto.PageDto;
import com.apl.lib.utils.ResultUtil;
import com.apl.wms.outstorage.operator.pojo.dto.PullOrderKeyDto;
import com.apl.wms.outstorage.operator.pojo.dto.SubmitPickItemDto;
import com.apl.wms.outstorage.operator.pojo.vo.OutOrderPickListVo;
import com.apl.wms.outstorage.order.pojo.vo.OutOrderListVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author hjr start
 * @date 2020/7/13 - 15:41
 */
public interface PickService extends IService<OutOrderListVo> {

    /**
     * @Desc: 拣货管理
     * @Author: hjr
     * @Date: 2020.7.17
     */
    ResultUtil<Page<OutOrderPickListVo>> pickManage(PageDto pageDto, PullOrderKeyDto keyDto) throws Exception;


    /**
     * 分配拣货员
     * @param orderSns
     * @return
     * @throws Exception
     */
    ResultUtil<OutOrderPickListVo> allocationPickingMember(List<String> orderSns) throws Exception;


    /**
     * 提交拣货数据
     * @param submitPickItemDtoList
     * @return
     */
    ResultUtil<Boolean> submitPick(Long batchId, List<SubmitPickItemDto> submitPickItemDtoList) throws Exception;
}
