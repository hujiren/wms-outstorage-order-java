package com.apl.wms.outstorage.operator.pojo.bo;

import com.apl.lib.security.SecurityUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockUpdListBo implements Serializable {


    private SecurityUser securityUser;

    private List<StockUpdBo> stockUpdBo;

}
