package com.ooo.mq.utils;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.ooo.mq.base.BaseConfig;
import com.socks.library.KLog;

import java.util.UUID;


/**
 * 设备屏幕尺寸和像素信息获取工具类
 * dongdd on 2016/12/14 17:19
 */
public class DisplayUtils {

    private static TelephonyManager telephonyManager;

    /**
     * 获取设备屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取设备屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 得到的设备的密度
     *
     * @param context
     * @return
     */
    public static float getScreenDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }


    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = getScreenDensity(context);
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * 获取终端设备的IMEI信息
     *
     * @param context
     * @return
     */
    public static String getSysIMEI(Context context) {
        String imei;
        try {
            imei = getSysDeviceid(context);
            if (imei == null || imei.length() == 0) {
                imei = getSysIMEIPad(context);
            }
        } catch (Exception e) {
            imei = getSysIMEIPad(context);
        }
        return imei;
    }


    /**
     * 获取DEVICE_ID(设备编号)
     * 必须添加：android.permission.READ_PHONE_STATE
     */
    public static String getSysDeviceid(Context context) {
        if (telephonyManager == null) {
            telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        }
        String deviceId = "";
        try {
            deviceId = telephonyManager.getDeviceId();
        } catch (Exception e) {
            KLog.e(BaseConfig.LOG, "deviceId获取异常:" + e.getMessage());
        }
        KLog.e(BaseConfig.LOG, "deviceId:" + deviceId);
        return deviceId;
    }


    /**
     * 发现Android Pad没有IMEI,用此方法获取设备获取ANDROID_ID：
     *
     * @param context
     * @return
     */
    public static String getSysIMEIPad(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }


    /**
     * 获取sim卡iccid SIM的序列号
     * ICCID为IC卡的唯一识别号码，共有20位数字组成，其编码格式为：XXXXXX 0MFSS YYGXX XXXXX。
     * 前六位运营商代码：中国移动的为：898600；898602 ，中国联通的为：898601，中国电信898603
     *
     * @return
     */
    public static String getIccid(Context context) {
        if (telephonyManager == null) {
            telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        }
        String iccid = "";
        try {
            iccid = telephonyManager.getSimSerialNumber();
        } catch (Exception e) {
            KLog.e(BaseConfig.LOG, "获取sim卡iccid异常：" + e.getMessage());
        }
        KLog.d(BaseConfig.LOG, "SIM卡信息iccid:" + iccid);
        KLog.e(BaseConfig.LOG, "SIM卡信息iccid:" + iccid);

        if (iccid != null && iccid.length() > 0) {
            return iccid;
        }
        return "";
    }

    /**
     * 获取电话号码
     *
     * @return
     */
    public static String getNativePhoneNumber(Context context) {
        if (telephonyManager == null) {
            telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        }
        return telephonyManager.getLine1Number();
    }

    /**
     * 获取手机sim卡信息 icci号码:手机号码
     *
     * @param context
     * @return
     */
    public static String getSimCardNo(Context context) {
        StringBuffer simCardNo = new StringBuffer();

        String iccid = getIccid(context);
        if (iccid != null && iccid.length() > 0) {
            simCardNo.append(iccid);
        }

        String phoneNo = getNativePhoneNumber(context);
        if (phoneNo != null && phoneNo.length() > 0) {
            simCardNo.append(":" + phoneNo);
        }
        KLog.d(BaseConfig.LOG, "Sim卡信息:" + simCardNo.toString());
        return simCardNo.toString();
    }


    /**
     * 获取网络信息
     * 网络信息:连接到一个网络时有效，如果是CDMA则不一定有效
     *
     * @param context
     * @return
     */
    private static String getNetworkInfo(Context context) {
        StringBuffer networkInfo = new StringBuffer();
        if (telephonyManager == null) {
            telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        }

        //国家ISO代码
        String networkCountry = telephonyManager.getNetworkCountryIso();
        networkInfo.append("国家ISO代码:" + networkCountry).append("\n");
        //运营商信息
        String networkOperatorId = telephonyManager.getNetworkOperator();
        networkInfo.append("运营商编号:" + networkOperatorId).append("\n");
        String networkOperatorName = telephonyManager.getNetworkOperatorName();
        networkInfo.append("运营商名称:" + networkOperatorName).append("\n");
        //网络连接状态
        int networktype = telephonyManager.getNetworkType();
        networkInfo.append("网络类型:" + networktype).append("\n");

        KLog.d(BaseConfig.LOG, "网络信息:\n" + networkInfo.toString());
        return networkInfo.toString();
    }

    /**
     * 获取sim卡信息
     *
     * @param context
     * @return
     */
    private static String getSimCardInfo(Context context) {
        StringBuffer simInfo = new StringBuffer();
        if (telephonyManager == null) {
            telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        }
        int simState = telephonyManager.getSimState();
        switch (simState) {
            case TelephonyManager.SIM_STATE_READY:
                //SIM卡的ISO国家代码
                String simISO = telephonyManager.getSimCountryIso();
                simInfo.append("SIM国家ISO:" + simISO).append("\n");
                //SIM卡的运营商代码
                String simOperator = telephonyManager.getSimOperator();
                simInfo.append("SIM运营商编号:" + simOperator).append("\n");
                //SIM的运营商名称
                String simOperatorName = telephonyManager.getSimOperatorName();
                simInfo.append("SIM运营商名称:" + simOperatorName).append("\n");
                //SIM的序列号‘
                String number = telephonyManager.getSimSerialNumber();
                simInfo.append("SIM序列号:" + number).append("\n");
                break;
            default:
                break;
        }
        KLog.d(BaseConfig.LOG, "SIM卡信息:\n" + simInfo.toString());
        return simInfo.toString();
    }

    /**
     * 获取设备信息
     *
     * @param context
     * @return
     */
    public static String getDeviceInfo(Context context) {
        StringBuffer deviceInfo = new StringBuffer();
        if (telephonyManager == null) {
            telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        }

        String phoneTypeStr = "未知";
        int phoneType = telephonyManager.getPhoneType();
        switch (phoneType) {
            case TelephonyManager.PHONE_TYPE_CDMA:
                phoneTypeStr = "CDMA";
                break;
            case TelephonyManager.PHONE_TYPE_GSM:
                phoneTypeStr = "GSM";
                break;
            case TelephonyManager.PHONE_TYPE_SIP:
                phoneTypeStr = "SIP";
                break;
            case TelephonyManager.PHONE_TYPE_NONE:
                phoneTypeStr = "None";
                break;
            default:
                break;
        }
        deviceInfo.append("电话类型:" + phoneTypeStr).append("\n");
        //GSM--IMEI  CDMA--MEID
        String deviceId = telephonyManager.getDeviceId();
        deviceInfo.append("设备编号:" + deviceId).append("\n");
        //软件版本编号
        String softVersion = telephonyManager.getDeviceSoftwareVersion();
        deviceInfo.append("软件版本:" + softVersion).append("\n");
        //手机号码(不一定能获取到)
        String phoneNumber = telephonyManager.getLine1Number();
        deviceInfo.append("手机号码:" + phoneNumber).append("\n");

        KLog.d(BaseConfig.LOG, "设备信息:\n" + deviceInfo.toString());
        return deviceInfo.toString();
    }

    /**
     * 获取UUID,使用deviceId、androidId和获取SimSerialNumber作为参数
     *
     * @param context
     * @return
     */

    public static String phoneEquipmentNum(Context context) {
//        getDeviceInfo(context);
//        getNetworkInfo(context);
//        getSimCardInfo(context);

        final String imei, tmSerial, androidId;
        imei = getSysIMEI(context);
        androidId = "" + getSysIMEIPad(context);
        //UUID uuid = new UUID(androidId.hashCode(),((long) imei.hashCode() << 32) | tmSerial.hashCode());
        UUID uuid = new UUID(androidId.hashCode(), ((long) imei.hashCode() << 32));
        String res = uuid.toString().replace("-", "");
        KLog.d(BaseConfig.LOG, "终端唯一id:" + res);
        KLog.e(BaseConfig.LOG, "终端唯一id:" + res);
        return res;
    }
}
