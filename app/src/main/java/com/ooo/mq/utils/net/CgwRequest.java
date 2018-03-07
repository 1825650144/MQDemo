package com.ooo.mq.utils.net;

import com.ooo.mq.api.CgwApi;
import com.ooo.mq.base.BaseApplication;
import com.ooo.mq.base.BaseConfig;
import com.ooo.mq.utils.net.ssl.TrustAllCertsManager;
import com.ooo.mq.utils.net.ssl.VerifyEverythingHostnameVerifier;
import com.socks.library.KLog;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.PersistentCookieStore;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * App端同Web端进行数据交互基层对象处理
 * dongdd on 2017/10/11 16:12
 */

public class CgwRequest {
    private static final Object monitor = new Object();
    private static CgwApi sCgwApi = null;

    /**
     * 定义普通的网络访问对象
     */
    private static final int DEFAULT_TIMEOUT = 10;  // 链接超时为5分钟
    private static OkHttpClient clientNormal = new OkHttpClient.Builder()
            .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .build();


    /**
     * 让Android系统信任自签的CA证书,设置可访问所有的https网站(不安全)
     *
     * @return
     */
    public static OkHttpClient getOkHttpClientNoCertifit() {
        //  创建信任管理器
        TrustManager[] trustManager = new TrustManager[]{new TrustAllCertsManager()};
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustManager, new SecureRandom());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        // 持久化Cookie处理
        CookieJarImpl cookieJar = new CookieJarImpl(new PersistentCookieStore(BaseApplication.context()));

        if (cookieJar.getCookieStore().getCookies() != null && cookieJar.getCookieStore().getCookies().size() > 0) {
            KLog.d(BaseConfig.LOG, "cookie:" + cookieJar.getCookieStore().getCookies().get(0).value());
        } else {
            KLog.d(BaseConfig.LOG, "cookie为空");
        }

        return new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request()
                                .newBuilder()
                                .addHeader("phoneEquipmentNum", BaseApplication.phoneEquipmentNum)
                                .build();
                        return chain.proceed(request);
                    }
                })
                .sslSocketFactory(sslContext.getSocketFactory())
                .hostnameVerifier(new VerifyEverythingHostnameVerifier())
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .cookieJar(cookieJar)
                //其他配置
                .build();
    }


    /**
     * 定义全局单例模式的网络访问对象
     *
     * @return
     */
    public static final CgwApi getCgwApi() {
        synchronized (monitor) {
            if (sCgwApi == null) {
                sCgwApi = new Retrofit.Builder()
                        .client(getOkHttpClientNoCertifit())
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .baseUrl(BaseConfig.SERVICE)
                        .build()
                        .create(CgwApi.class);
            }
            return sCgwApi;
        }
    }


    /**
     * 终端访问服务处理
     *
     * @param observable 指定网络请求的具体接口
     * @param subscriber 指定网络响应的具体处理业务
     */
    public static void RequestServer(Observable observable, Subscriber subscriber) {
        Subscription subscription = observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        new CompositeSubscription().add(subscription);
    }


}
