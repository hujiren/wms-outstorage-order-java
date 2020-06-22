package com.apl.wms.outstorage.operator.pojo.bo;

public class TestBo {


    /**
     * accessKeyId : LTAI4FxCvcXw2ZpynhsU6kib
     * accessKeySecret : Q6Q3WtGtFmcq8nP91za8Q0TTr4uc2I
     * templateCode : SMS_174986264
     * signName : APL系统
     * connectTimeout : 10000
     * readTimeout : 10000
     */

    private String accessKeyId;
    private String accessKeySecret;
    private String templateCode;
    private String signName;
    private String connectTimeout;
    private String readTimeout;

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getSignName() {
        return signName;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }

    public String getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(String connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public String getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(String readTimeout) {
        this.readTimeout = readTimeout;
    }
}
