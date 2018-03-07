package com.ooo.mq.utils.net;

import com.ooo.mq.base.BaseConfig;
import com.socks.library.KLog;

import rx.Subscriber;

/**
 * 网络框架响应处理
 * dongdd on 2017/12/17 21:18
 */

public abstract class SubscriberSi<T> extends Subscriber<T> {
    /**
     * 屏蔽网络请求成功后的完成方法
     */
    @Override
    public void onCompleted() {
        KLog.d(BaseConfig.LOG, "onCompleted()");
    }
}
