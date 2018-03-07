package com.ooo.mq.mode.entity;

/**
 * 消息通信实体类
 * dongdd on 2017/10/16 16:13
 */

public class MsgEntity {

    private String clientId;  // 客户端id(用户设备号)
    private int messageType;  // 消息类型 -1:无（客户端链接）;  0:报告 ; 1:通知,2:文件
    private String data;  // 发送的数据
    private String filePathUri; // 附件路径
    private boolean isGoBack; // true:发消息；false:接收消息


    public MsgEntity(String clientId, int messageType, String data) {
        this.clientId = clientId;
        this.messageType = messageType;
        this.data = data;
    }

    public MsgEntity( String clientId, int messageType, String data,String filePathUri) {
        this.clientId = clientId;
        this.messageType = messageType;
        this.data = data;
        this.filePathUri = filePathUri;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getFilePathUri() {
        return filePathUri;
    }

    public void setFilePathUri(String filePathUri) {
        this.filePathUri = filePathUri;
    }

    public boolean isGoBack() {
        return isGoBack;
    }

    public void setGoBack(boolean goBack) {
        isGoBack = goBack;
    }
}
