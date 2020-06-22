package com.apl.wms.outstorage.order.service;

import com.apl.wms.outstorage.order.pojo.po.OutOrderDestPo;
import com.apl.wms.outstorage.order.pojo.vo.OutOrderDestVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 出库订单其他信息 service接口
 * </p>
 *
 * @author cy
 * @since 2020-01-07
 */
public interface OutOrderDestService extends IService<OutOrderDestPo> {


        /**
         * @Desc: 根据id 更新一个OutOrderAttachmentPo 实体
         * @author cy
         * @since 2020-01-07
         */
        Long saveDest(OutOrderDestPo outOrderAttachment);


        /**
         * @Desc: 根据id 查找一个OutOrderAttachmentPo 实体
         * @author cy
         * @since 2020-01-07
         */
        Boolean delById(Long id , Long customerId);


        /**
         * @Desc: 根据id 查找一个 OutOrderDestPo 实体
         * @author cy
         * @since 2020-01-07
         */
        OutOrderDestVo selectById(Long id);

}
