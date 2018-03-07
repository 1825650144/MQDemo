package com.ooo.mq.mode.entity;

/**
 * 消息实体类
 * <p>
 * dongtengfei on 2018/2/27 15:10
 */

public class ItemMsg {
    private String person; //  发件人姓名
    private String body; // 内容
    private int typeBody; // 内容类型 1:字符串，2：文件
    private Long dateStamp; // 发件日期 时间戳
    private int type; // 类型，1为左侧，2为右侧

    public ItemMsg(String person, int typeBody ,String body, Long dateStamp, int type) {
        this.person = person;
        this.typeBody = typeBody;
        this.body = body;
        this.dateStamp = dateStamp;
        this.type = type;
    }

    public ItemMsg copy() {
        ItemMsg entity = new ItemMsg();
        entity.setPerson(person);
        entity.setTypeBody(typeBody);
        entity.setBody(body);
        entity.setDateStamp(dateStamp);
        entity.setType(type);
        return entity;
    }

    public ItemMsg() {
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }


    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Long getDateStamp() {
        return dateStamp;
    }

    public void setDateStamp(Long dateStamp) {
        this.dateStamp = dateStamp;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTypeBody() {
        return typeBody;
    }

    public void setTypeBody(int typeBody) {
        this.typeBody = typeBody;
    }
}
