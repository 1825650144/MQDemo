package com.ooo.mq.mode.listener;


import com.ooo.mq.mode.entity.MsgEntity;

/**
 * msg通讯服务端消息响应
 * dongdd on 2018/2/27 11:14
 */

public interface IResponseDataListener {
    /**
     * 接收服务端响应的结果值
     * @param msg
     */
    void onRespnse(String msg);

    void onRespnse(MsgEntity msg);
}
