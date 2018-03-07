package com.ooo.mq.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.ooo.mq.utils.ToastUtils;


/**
 * 项目基础视图
 * dongdd on 2017/5/19 09:48
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    /**
     * toast提示框
     *
     * @param msg
     */
    protected void showToast(String msg) {
        ToastUtils.showToast(BaseApplication.context(), msg);
    }

    protected void showToast(int msg) {
        ToastUtils.showToast(BaseApplication.context(), getResources().getString(msg));
    }





}
