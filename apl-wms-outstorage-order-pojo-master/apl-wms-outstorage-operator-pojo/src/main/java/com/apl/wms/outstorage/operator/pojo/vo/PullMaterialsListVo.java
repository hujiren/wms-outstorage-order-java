package com.apl.wms.outstorage.operator.pojo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 出库订单包装材料
 * </p>
 *
 * @author cy
 * @since 2020-06-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PullMaterialsListVo implements Serializable {


    @TableId(value = "id", type = IdType.UUID)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long outOrderId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long materialsId;

    private Integer qty;

    private static final long serialVersionUID = 1L;


}