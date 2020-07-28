package com.apl.wms.outstorage.operator.service.impl;

import com.apl.lib.utils.ResultUtil;
import com.apl.wms.outstorage.operator.dao.PackMapper;
import com.apl.wms.outstorage.operator.pojo.vo.PackingInfo;
import com.apl.wms.outstorage.operator.service.PackService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author hjr start
 * @date 2020/7/28 - 14:57
 */
@Service
@Slf4j
public class PackServiceImpl extends ServiceImpl<PackMapper, PackingInfo> implements PackService {

    /**
     * 根据订单号查询订单信息, 商品信息, 包装材料信息
     * @param orderSn
     * @return
     */
    @Override
    public ResultUtil<PackingInfo> getPackInfo(String orderSn) {


        return null;
    }
}
