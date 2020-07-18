package com.apl.wms.outstorage.operator.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.List;

@Data
public class PullAllocationItemMsgVo {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long commodityId;

    private String commodityName;

    private String img;

    private String sku;

    private List<StorageLocalMsg> storageLocalMsgList;

    @Data
    public static class StorageLocalMsg {

        private Long storageLocalId;

        private String storageName;

        private Integer count;

    }

}
