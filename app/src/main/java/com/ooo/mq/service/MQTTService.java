package com.ooo.mq.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.ooo.mq.base.BaseApplication;
import com.ooo.mq.base.BaseConfig;
import com.ooo.mq.mode.entity.MsgEntity;
import com.ooo.mq.utils.DataFormatUtils;
import com.ooo.mq.utils.ToastUtils;
import com.google.gson.reflect.TypeToken;
import com.socks.library.KLog;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import xiaofei.library.hermeseventbus.HermesEventBus;

/**
 * mqtt模式的消息通讯服务
 * dongdd on 2018/3/2 11:26
 */
@SuppressWarnings("all")
public class MQTTService extends Service {

    //    public static final String ACTION = MQTTService.class.getSimpleName();
    public static final String ACTION = "com.cgw.mq.service.MQTTService";

    private static MqttAndroidClient client;
    private MqttConnectOptions conOpt;


    private static String myTopic = "topic2";
    private static String sendTopic = "topic1";
    private String clientId = "a";

    public static MQTTService instance;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        instance = this;
        init();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        try {
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    /**
     * 初始化mqtt消息服务
     */
    private void init() {
        // 服务器地址（协议+地址+端口号）
        client = new MqttAndroidClient(this, BaseConfig.HOST, clientId);
        // 设置MQTT监听并且接受消息
        client.setCallback(mqttCallback);

        conOpt = new MqttConnectOptions();
        // 清除缓存
        conOpt.setCleanSession(true);
        // 设置超时时间，单位：秒
        conOpt.setConnectionTimeout(BaseConfig.CONNECTIONTIMEOUT);
        // 心跳包发送间隔，单位：秒
        conOpt.setKeepAliveInterval(BaseConfig.KEEPALIVEINTERVAL);
        // 用户名
        conOpt.setUserName(BaseConfig.USERNAME);
        // 密码
        conOpt.setPassword(BaseConfig.PASSWORD.toCharArray());

        // last will message
        boolean doConnect = true;
        String message = "{\"terminal_uid\":\"" + clientId + "\"}";
        String topic = myTopic;
        Integer qos = 0;
        Boolean retained = false;
        if ((!message.equals("")) || (!topic.equals(""))) {
            try {
                //setWill方法，如果项目中需要知道客户端是否掉线可以调用该方法。设置最终端口的通知消息
                conOpt.setWill(topic, message.getBytes(), qos.intValue(), retained.booleanValue());
            } catch (Exception e) {
                KLog.d(BaseConfig.LOG, "Exception Occured:" + e.getMessage());
                doConnect = false;
                iMqttActionListener.onFailure(null, e);
            }
        }

        if (doConnect) {
            KLog.d(BaseConfig.LOG, "连接apollo服务");
            doClientConnection();
        }
    }


    /**
     * 发送消息
     *
     * @param msgStr
     */
    public void publish(String msgStr) {
        boolean isSendOk = false;
        try {
            if (client != null && client.isConnected()) {
                KLog.d(BaseConfig.LOG, "发送消息：" + msgStr);
//                MqttMessage mqttMessage = new MqttMessage(msgStr.getBytes());
//                mqttMessage.setQos(BaseConfig.MQTT_QOS);
//                client.publish(sendTopic, mqttMessage);
                client.publish(sendTopic, msgStr.getBytes(), BaseConfig.MQTT_QOS, false);
            } else {
                KLog.d(BaseConfig.LOG, "连接中断");
                ToastUtils.showToast(BaseApplication.context(), "连接中断,请稍后重试");
            }
        } catch (MqttException e) {
            e.printStackTrace();
            KLog.d(BaseConfig.LOG, "发送消息失败：" + e.getMessage());
        }
    }


    /**
     * 连接MQTT服务器
     */
    private void doClientConnection() {
        if (!client.isConnected() && isConnectIsNomarl()) {
            try {
                client.connect(conOpt, null, iMqttActionListener);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

    }

    // MQTT是否连接成功
    private IMqttActionListener iMqttActionListener = new IMqttActionListener() {

        @Override
        public void onSuccess(IMqttToken arg0) {
            KLog.d(BaseConfig.LOG, "连接成功");
            try {
                // 订阅myTopic话题
                client.subscribe(myTopic, 1);
                KLog.d(BaseConfig.LOG, "订阅主题：" + myTopic + " 成功");
            } catch (MqttException e) {
                KLog.d(BaseConfig.LOG, "订阅失败：" + e.getMessage());
            }
        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            arg1.printStackTrace();
            // 连接失败，重连
        }
    };

    /**
     * mqtt监听并接受消息
     */
    private MqttCallback mqttCallback = new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {
            KLog.d(BaseConfig.LOG, "connectionLost 失去连接，重连 cause:" + cause.getMessage());
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            String msg = new String(message.getPayload());
            KLog.d(BaseConfig.LOG, "messageArrived 收到消息 topic:" + topic + "；内容：" + msg);
            if (msg.length() > 0) {
                MsgEntity msgData = DataFormatUtils.jsonToObj(msg, new TypeToken<MsgEntity>() {
                }.getType());
                msgData.setGoBack(false);
                HermesEventBus.getDefault().post(msgData);
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            try {
                String msg = new String(token.getMessage().getPayload());
                KLog.d(BaseConfig.LOG, "deliveryComplete 发送结果  token:" + token.isComplete() + ";内容:" + new String(token.getMessage().getPayload()));
                if (msg.length() > 0) {
                    MsgEntity msgData = DataFormatUtils.jsonToObj(msg, new TypeToken<MsgEntity>() {
                    }.getType());
                    KLog.d(BaseConfig.LOG, "更新消息");
                    msgData.setGoBack(true);
                    HermesEventBus.getDefault().post(msgData);
                }
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    };


    /**
     * 判断网络是否连接
     */
    private boolean isConnectIsNomarl() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            String name = info.getTypeName();
            KLog.d(BaseConfig.LOG, "MQTT当前网络名称：" + name);
            return true;
        } else {
            KLog.d(BaseConfig.LOG, "MQTT 没有可用网络");
            return false;
        }
    }


}
