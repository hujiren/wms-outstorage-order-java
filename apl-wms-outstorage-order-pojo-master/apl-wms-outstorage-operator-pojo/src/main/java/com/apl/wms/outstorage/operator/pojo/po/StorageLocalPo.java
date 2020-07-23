package com.apl.wms.outstorage.operator.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 库位
 * </p>
 *
 * @author cy
 * @since 2019-12-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("storage_local")
@ApiModel(value="StorageLocalPo对象", description="库位")
public class StorageLocalPo extends Model<StorageLocalPo> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @ApiModelProperty(name = "storageLocalSn" , value = "库位编号" , required = true)
    @NotEmpty(message = "库位编号不能为空")
    @Length(min=6, max=30,  message = "库位编号长度必须是6-30位，且后3位必须为纯数字")
    private String storageLocalSn;

    @ApiModelProperty(name = "storageLayer" , value = "所在层数" , required = true)
    @Range(min = 1, max = 10, message = "所在层数必须是1-10")
    @NotNull(message = "所在层数不能为空")
    private Integer storageLayer;

    @ApiModelProperty(name = "sizeLength" , value = "长" , required = true)
    @Min(value = 0 , message = "长不能小于0")
    @NotNull(message = "长不能为空")
    private BigDecimal sizeLength;

    @ApiModelProperty(name = "sizeWidth" , value = "宽" , required = true)
    @Min(value = 0 , message = "宽不能小于0")
    @NotNull(message = "宽不能为空")
    private BigDecimal sizeWidth;

    @ApiModelProperty(name = "sizeHeight" , value = "高" , required = true)
    @Min(value = 0 , message = "高不能小于0")
    @NotNull(message = "高不能为空")
    private BigDecimal sizeHeight;

    @ApiModelProperty(name = "volume" , value = "体积" ,hidden = true)
    private BigDecimal volume;

    @ApiModelProperty(name = "supportWeight" , value = "承量" , required = true)
    @Min(value = 0 , message = "承量不能小于0")
    @NotNull(message = "承量不能为空")
    private Double supportWeight;

    @ApiModelProperty(name = "remark" , value = "库位描述" , required = true)
    @NotEmpty(message = "库位描述不能为空")
    private String remark;

    @ApiModelProperty(name = "storageStatus" , value = "库位状态" , hidden = true)
    private Integer storageStatus;

    @ApiModelProperty(name = "shelvesId" , value = "货架id" , required = true)
    @Min(value = 0 , message = "货架id不能小于1")
    @NotNull(message = "货架id不能为空")
    private Long shelvesId;

    @ApiModelProperty(value = "商品ID", hidden = true)
    private Long commodityId;

    @ApiModelProperty(value = "可售库存", hidden = true)
    private Integer availableCount;

    @ApiModelProperty(value = "可以存放最大的商品数量", hidden = true)
    private Integer thresholdCount;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}