package com.ooo.mq.utils.net.ssl;

import com.ooo.mq.base.BaseConfig;
import com.socks.library.KLog;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;


/**
 * TrustAllCertsManager修改HttpsURLConnection类的默认SSL socket factory。
 * 访问自签名证书的网站，Android直接会throw SSLHandshakeException，原因就是12306的数字证书不被Android系统的信任。想解决这个问题，有如下几种方法。让HttpsURLConnection信任所有的CA证书
 * dongdd on 2016/11/8 10:25
 */
public class TrustAllCertsManager implements X509TrustManager {

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        // Do nothing -> accept any certificates
        KLog.d(BaseConfig.LOG, "checkClientTrusted 验证客户端");
    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        // Do nothing -> accept any certificates
        KLog.d(BaseConfig.LOG, "checkServerTrusted 验证服务端");

    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        KLog.d(BaseConfig.LOG, "getAcceptedIssuers 返回受验证同位体信任的认证中心的数组。");
        return new X509Certificate[0];
    }
}
