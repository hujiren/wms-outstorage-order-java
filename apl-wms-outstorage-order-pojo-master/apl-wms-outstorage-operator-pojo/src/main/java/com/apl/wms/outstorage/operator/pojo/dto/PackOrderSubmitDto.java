package com.apl.wms.outstorage.operator.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(value="出库订单打包明细 持久化对象", description="出库订单打包明细 持久化对象")
public class PackOrderSubmitDto {

    @ApiModelProperty(name = "orderId" , value = "订单id")
    private Long orderId;

    List<PackCount> packCounts;

    List<PackMsg> packMsgs;

    /**
     * @Desc: 包装材料数量
     * @Author: CY
     * @Date: 2020/6/15 11:55
     */
    @Data
    public static class PackCount {

        @ApiModelProperty(name = "materialsId" , value = "包装材料id")
        private Long materialsId;

        @ApiModelProperty(name = "count" , value = "包装材料 数量")
        private Integer count;

    }

    /**
     * @Desc: 打包信息
     * @Author: CY
     * @Date: 2020/6/15 11:56
     */
    @Data
    public static class PackMsg {

        @ApiModelProperty(name = "subSn" , value = "子单号" , required = true)
        @NotEmpty(message = "子单号不能为空")
        private String subSn;

        @ApiModelProperty(name = "gw" , value = "毛重" , required = true)
        @NotNull(message = "毛重不能为空")
        @Min(value = 0 , message = "毛重不合法")
        private BigDecimal gw;

        @ApiModelProperty(name = "sizeLength" , value = "长" , required = true)
        @NotNull(message = "长不能为空")
        @Min(value = 0 , message = "长不合法")
        private BigDecimal sizeLength;

        @ApiModelProperty(name = "sizeWidth" , value = "宽" , required = true)
        @NotNull(message = "宽不能为空")
        @Min(value = 0 , message = "宽不合法")
        private BigDecimal sizeWidth;

        @ApiModelProperty(name = "sizeHeight" , value = "高" , required = true)
        @NotNull(message = "高不能为空")
        @Min(value = 0 , message = "高不合法")
        private BigDecimal sizeHeight;

        @ApiModelProperty(name = "volume" , value = "体积" , required = true)
        @NotNull(message = "体积不能为空")
        @Min(value = 0 , message = "体积不合法")
        private BigDecimal volume;


    }

}
