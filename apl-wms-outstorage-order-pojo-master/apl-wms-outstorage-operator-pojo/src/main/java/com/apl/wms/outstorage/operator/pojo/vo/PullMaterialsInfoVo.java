package com.apl.wms.outstorage.operator.pojo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 出库订单包装材料 持久化对象
 * </p>
 *
 * @author cy
 * @since 2020-06-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("pull_materials")
@ApiModel(value = "出库订单包装材料 持久化对象", description = "出库订单包装材料 持久化对象")
public class PullMaterialsInfoVo implements Serializable {


    @TableId(value = "id", type = IdType.UUID)
    private Long id;

    private Long outOrderId;

    private Long materialsId;

    private Integer qty;

    private static final long serialVersionUID = 1L;


}
