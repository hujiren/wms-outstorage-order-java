package com.apl.wms.outstorage.operator.service;
import com.apl.lib.utils.ResultUtil;
import com.apl.wms.outstorage.operator.pojo.vo.OutOrderPickListVo;
import com.apl.wms.outstorage.order.pojo.vo.OutOrderListVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author hjr start
 * @date 2020/7/13 - 15:41
 */
public interface PickService extends IService<OutOrderListVo> {

    ResultUtil<OutOrderPickListVo> allocationPickingMember(List<String> orderSns) throws Exception;
}
