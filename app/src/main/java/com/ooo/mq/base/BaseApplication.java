package com.ooo.mq.base;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.ooo.mq.BuildConfig;
import com.ooo.mq.utils.DisplayUtils;
import com.socks.library.KLog;

import java.util.List;

import xiaofei.library.hermeseventbus.HermesEventBus;


/**
 * 项目系统配置和初始化
 * dongdd on 2017/5/19 09:48
 */
public class BaseApplication extends Application {

    /**
     * 系统上下文对象
     */
    private static Context context;
    /**
     * 终端设备号
     */
    public static String phoneEquipmentNum;


    @Override
    public void onCreate() {
        super.onCreate();
        int pid = android.os.Process.myPid();
        KLog.d(BaseConfig.LOG, "pid:" + pid);
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();

        if (runningApps != null && !runningApps.isEmpty()) {
            for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
                if (procInfo.pid == pid) {
                    if (procInfo.processName.equals(BaseConfig.PACKAGENAME_MAIN)) {
                        context = getApplicationContext();
                        // 初始化日志
                        KLog.init(BuildConfig.DEBUG);
                        BaseApplication.phoneEquipmentNum = DisplayUtils.phoneEquipmentNum(BaseApplication.context());
                        //初始化HermesEventBus用来做通信
                        HermesEventBus.getDefault().init(this);
                    } else if (procInfo.processName.equals("xxx")) {

                    }
                }
            }
        }

    }


    /**
     * 定义全局单例模式的系统对象
     *
     * @return
     */
    public static synchronized BaseApplication context() {
        return (BaseApplication) context;
    }


}
