package com.ooo.mq.utils.net;

import android.support.annotation.NonNull;

import com.ooo.mq.api.CgwApi;
import com.ooo.mq.base.BaseApplication;
import com.ooo.mq.base.BaseConfig;
import com.ooo.mq.utils.FileUtils;
import com.ooo.mq.utils.net.ssl.TrustAllCertsManager;
import com.ooo.mq.utils.net.ssl.VerifyEverythingHostnameVerifier;
import com.socks.library.KLog;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.PersistentCookieStore;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 文件下载API处理
 * dongdd on 2017/10/29 18:35
 */

public class DownloadAPI {
    private static final int DEFAULT_TIMEOUT = 15;
    public Retrofit retrofit;

    /**
     * 初始化网络访问客户端
     *
     * @param url
     */
    public DownloadAPI(String url) {

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

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request()
                        .newBuilder()
                        .addHeader("phoneEquipmentNum", BaseApplication.phoneEquipmentNum)
                        .build();

                // 设置文件下载拦截器
                Response originalResponse = chain.proceed(request);
                return originalResponse.newBuilder()
                        .body(new DownloadProgressResponseBody(originalResponse.body()))
                        .build();
            }
        }).sslSocketFactory(sslContext.getSocketFactory())
                .hostnameVerifier(new VerifyEverythingHostnameVerifier())
                .retryOnConnectionFailure(true) // 链接失败重连
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .cookieJar(cookieJar)
                //其他配置
                .build();


        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }


    /**
     * 文件下载
     *
     * @param url        文件下载绝对url
     * @param file       文件存储位置
     * @param subscriber 下载处理
     */
    public void downloadFile(@NonNull String url, final File file, Subscriber subscriber) {
        retrofit.create(CgwApi.class)
                .download(url)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .map(new Func1<ResponseBody, InputStream>() {
                    @Override
                    public InputStream call(ResponseBody responseBody) {
                        return responseBody.byteStream();
                    }
                })
                .observeOn(Schedulers.computation())
                .doOnNext(new Action1<InputStream>() {
                    @Override
                    public void call(InputStream inputStream) {
                        try {
                            FileUtils.writeFile(inputStream, file);
                        } catch (IOException e) {
                            e.printStackTrace();
                            KLog.d(BaseConfig.LOG, "下载异常：" + e.getMessage());
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

}
