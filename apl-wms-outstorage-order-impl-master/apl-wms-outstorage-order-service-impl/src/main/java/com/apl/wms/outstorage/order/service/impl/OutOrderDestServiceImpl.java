package com.apl.wms.outstorage.order.service.impl;

import com.apl.wms.outstorage.order.mapper.OutOrderAttachmentMapper;
import com.apl.wms.outstorage.order.pojo.po.OutOrderDestPo;
import com.apl.wms.outstorage.order.pojo.vo.OutOrderDestVo;
import com.apl.wms.outstorage.order.service.OutOrderDestService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;



/**
 * <p>
 * 出库订单其他信息 service实现类
 * </p>
 *
 * @author cy
 * @since 2020-01-07
 */
@Service
@Slf4j
public class OutOrderDestServiceImpl extends ServiceImpl<OutOrderAttachmentMapper, OutOrderDestPo> implements OutOrderDestService {

    //状态code枚举
    /*enum OutOrderAttachmentServiceCode {

        ;

        private String code;
        private String msg;

        OutOrderAttachmentServiceCode(String code, String msg) {
             this.code = code;
             this.msg = msg;
        }
    }*/


    @Override
    public Long saveDest(OutOrderDestPo outOrderAttachment){

        Integer flag =0;
        if(this.exists(outOrderAttachment.getOrderId())) {
            flag = baseMapper.updateById(outOrderAttachment);
        }
        else {
            flag = baseMapper.insert(outOrderAttachment);
        }

        if(flag.equals(1))
            return outOrderAttachment.getOrderId();

        return 0l;
    }


    @Override
    public Boolean delById(Long id , Long customerId){

        return baseMapper.delOutOrderItem(id , customerId);
    }

    @Override
    public OutOrderDestVo selectById(Long id){

        OutOrderDestVo outOrderAttachmentInfoVo = baseMapper.getById(id);

        return outOrderAttachmentInfoVo;
    }



    Boolean exists(Long orderId) {

        return baseMapper.exists(orderId) !=null ? true : false;
    }


}
