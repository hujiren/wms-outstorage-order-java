package com.apl.wms.outstorage.operator.pojo.vo;

import lombok.Data;

import java.util.List;

@Data
public class PullAllocationItemMsgVo {


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
