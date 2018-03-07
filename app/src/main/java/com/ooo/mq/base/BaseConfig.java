package com.ooo.mq.base;

/**
 * 项目配置
 * dongdd on 2017/5/19 09:49
 */
public class BaseConfig {

    /**
     * 系统配置参数
     */
    public static final String LOG = "cgw"; // 日志target
    public static final String PACKAGENAME_MAIN = "com.ooo.mqtt"; // 主进程名称
    public static final String AUTHORITIES = PACKAGENAME_MAIN + ".fileprovider"; // Android7.0系统打开文件权限处理

    /**
     * 网络访问配置
     */
    public static String SERVICE = "https://192.168.1.125:8010/";


    /**
     * 消息通讯配置
     */
    public static String MESSAGE_ADDRESS = "192.168.1.127"; // 张高峰消息服务器ip地址


    public static final int sGETFILE = 1;//打开系统文件系统，获取到选中文件

    public static final String FileTempName = "comcgwmq_maven_FileTempName";//默认保存附件的名称

    public static final int REQUESTCODE_WRITE_EXTERNAL_STORAGE = 1002; // 写出权限



    //    private String host = "tcp://192.168.14.206:1883";
    public static String HOST = "tcp://192.168.1.122:61613";
    public static String USERNAME = "admin";
    public static String PASSWORD = "password";
    public static Integer CONNECTIONTIMEOUT = 10;
    // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
    public static Integer KEEPALIVEINTERVAL= 20;
    public static int MQTT_QOS = 2;


}
