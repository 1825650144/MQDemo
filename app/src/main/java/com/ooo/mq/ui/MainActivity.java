package com.ooo.mq.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ooo.mq.R;
import com.ooo.mq.base.BaseActivity;
import com.ooo.mq.base.BaseApplication;
import com.ooo.mq.base.BaseConfig;
import com.ooo.mq.mode.adapter.MsgAdapter;
import com.ooo.mq.mode.entity.ItemMsg;
import com.ooo.mq.mode.entity.JsonFileList;
import com.ooo.mq.mode.entity.MsgEntity;
import com.ooo.mq.mode.entity.ResultBean;
import com.ooo.mq.service.MQTTService;
import com.ooo.mq.utils.DataFormatUtils;
import com.ooo.mq.utils.FileUtils;
import com.ooo.mq.utils.net.CgwRequest;
import com.ooo.mq.utils.net.DownloadAPI;
import com.ooo.mq.utils.net.SubscriberSi;
import com.socks.library.KLog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.EasyPermissions;
import rx.Subscriber;
import xiaofei.library.hermeseventbus.HermesEventBus;

public class MainActivity extends BaseActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private EditText etMsg;
    private Button btnSend;
    private TextView tvFile;


    private RecyclerView recyclerView;
    private MsgAdapter adapterMsg;
    private List<ItemMsg> msgList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etMsg = (EditText) findViewById(R.id.et_msg);
        btnSend = (Button) findViewById(R.id.btn_send);
        recyclerView = (RecyclerView) findViewById(R.id.rv_list);
        tvFile = (TextView) findViewById(R.id.tv_file);
        btnSend.setOnClickListener(this);
        tvFile.setOnClickListener(this);
        HermesEventBus.getDefault().register(this);
        initRecyclerView();
        initMsg();
    }

    /**
     * 初始化信息
     */
    private void initMsg() {
        KLog.d(BaseConfig.LOG, "启动mqtt服务");
        // 开启接收消息并处理业务线程
        Intent msgService = new Intent(BaseApplication.context(), MQTTService.class);
        msgService.setAction(MQTTService.ACTION);
        startService(msgService);
    }

    /**
     * 初始化列表控件
     */
    private void initRecyclerView() {
        //禁用下拉刷新，下拉加载
        adapterMsg = new MsgAdapter(msgList, this);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapterMsg);
        // 点击item打开文件
        adapterMsg.setOnItemClickListener(new MsgAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String filePath) {
                KLog.d(BaseConfig.LOG, "打开图片：" + filePath);
                //提示获取读写权限
                if (EasyPermissions.hasPermissions(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    FileUtils.openFile(MainActivity.this, filePath, 1);
                } else {
                    EasyPermissions.requestPermissions(MainActivity.this, getString(R.string.permission_write_external_storage),
                            1, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            }
        });
        recyclerView.scrollToPosition(msgList.size() - 1);
    }

    /**
     * 关闭消息通道
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        HermesEventBus.getDefault().unregister(this);
    }


    /**
     * 打开系统文件选择器
     */
    private void getFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "请选择一个要上传的文件"), BaseConfig.sGETFILE);
        } catch (Exception e) {
            Toast.makeText(this, "请安装文件管理器", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 接收获取到的文件路径
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BaseConfig.sGETFILE:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        Uri uri = data.getData();
                        String filePath = FileUtils.getFilePath(MainActivity.this, uri);
                        //提示获取读写权限
                        if (EasyPermissions.hasPermissions(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            uploadFile(filePath);
                        } else {
                            EasyPermissions.requestPermissions(MainActivity.this, getString(R.string.permission_write_external_storage),
                                    4, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        KLog.e(BaseConfig.LOG, e.getMessage());
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 点击事件监听处理
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                // 发送消息监听事件
                String msg = etMsg.getText().toString().trim();
                if (msg.length() == 0) {
                    showToast("发送消息不能为空");
                    return;
                }
                MQTTService.instance.publish(DataFormatUtils.GsonString(new MsgEntity(BaseApplication.phoneEquipmentNum, 1, msg)));
                break;
            case R.id.tv_file:
                //提示获取读写权限
                if (EasyPermissions.hasPermissions(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // 添加附件
                    getFile();
                } else {
                    EasyPermissions.requestPermissions(MainActivity.this, getString(R.string.permission_write_external_storage),
                            2, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                break;
            default:
                break;
        }

    }

    /**
     * 更新聊天列表信息
     *
     * @param msg
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MsgEntity msg) {
        if (msg.isGoBack()) {
            etMsg.getText().clear();
            if (msg.getMessageType() == 2) {
                msgList.add(new ItemMsg("a", 2, msg.getFilePathUri(), System.currentTimeMillis(), 2));
            } else {
                msgList.add(new ItemMsg("a", 1, msg.getData(), System.currentTimeMillis(), 2));
            }
            adapterMsg.notifyDataSetChanged();
            recyclerView.scrollToPosition(msgList.size() - 1);
            KLog.d(BaseConfig.LOG, "发送成功");
            showToast("发送成功");
        } else {
            KLog.d(BaseConfig.LOG, "服务器响应：" + DataFormatUtils.GsonString(msg));
            // 文件消息
            if (msg.getMessageType() == 2) {
                String fileName = msg.getData().substring(msg.getData().lastIndexOf("/") + 1, msg.getData().length()).toLowerCase();
                //提示获取读写权限
                if (EasyPermissions.hasPermissions(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    downLoadFile(fileName, msg.getData());
                } else {
                    EasyPermissions.requestPermissions(MainActivity.this, getString(R.string.permission_write_external_storage),
                            3, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            } else {
                // 文字消息
                msgList.add(new ItemMsg("b", 1, msg.getData(), System.currentTimeMillis(), 1));
                adapterMsg.notifyDataSetChanged();
                recyclerView.scrollToPosition(msgList.size() - 1);
            }
        }
    }


    /**
     * 上传文件到服务器
     *
     * @param filePathUri
     */
    private void uploadFile(String filePathUri) {
        File fileInit = new File(filePathUri);
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addFormDataPart("file", fileInit.getName(), RequestBody.create(MediaType.parse("image/*"), fileInit));
        RequestBody requestBody = builder.build();

        CgwRequest.RequestServer(CgwRequest.getCgwApi().uploadUserImgFile(requestBody)
                , new SubscriberSi<ResultBean<JsonFileList>>() {
                    @Override
                    public void onError(Throwable e) {
                        KLog.e(BaseConfig.LOG, "上传文件 Error:" + e.getMessage());
                        showToast("发送失败，请重试");
                    }

                    @Override
                    public void onNext(ResultBean<JsonFileList> response) {
                        KLog.e(BaseConfig.LOG, "上传文件 msg：" + DataFormatUtils.GsonString(response));
                        if (response.getCode() == 200) {
                            KLog.e(BaseConfig.LOG, "上传文件 msg：" + response.getMsg());
                            // 附件上传成功，发送通知
                            String filePath = response.getRepData().getFile_path().get(0);
                            if (filePath.length() > 0) {
                                MQTTService.instance.publish(DataFormatUtils.GsonString(new MsgEntity(BaseApplication.phoneEquipmentNum, 2, filePath, filePathUri)));
                            } else {
                                showToast("发送失败");
                            }
                        }
                    }
                });
    }


    /**
     * 下载附件
     *
     * @param fileName 文件名称
     * @param fileUrl  文件下载地址
     */
    public void downLoadFile(String fileName, String fileUrl) {
        // 获取附件 start
        String fileUri = FileUtils.getFileDefaultPath(BaseConfig.FileTempName);
        File outputFile = new File(fileUri);
        new DownloadAPI(BaseConfig.SERVICE).downloadFile(fileUrl, outputFile,
                new Subscriber() {
                    @Override
                    public void onCompleted() {
                        KLog.e(BaseConfig.LOG, "下载完成 onCompleted");
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onError(Throwable e) {
                        try {
                            showToast(R.string.system_web_busy);
                            e.printStackTrace();
                            KLog.e(BaseConfig.LOG, "onError:" + e.getMessage());
                        } catch (Exception ex) {

                        }
                    }

                    @Override
                    public void onNext(Object o) {
                        KLog.e(BaseConfig.LOG, "onError:" + DataFormatUtils.GsonString(o));
                        try {
                            if (outputFile.exists()) {
                                String fileUriStr = FileUtils.getFileDefaultPath(fileName);
                                File reNameFile = new File(fileUriStr);
                                outputFile.renameTo(reNameFile);
                                KLog.e(BaseConfig.LOG, "下载完成 onNext 路径：" + fileUriStr);
                                showToast("文件已下载到本地" + fileUriStr);
                                msgList.add(new ItemMsg("b", 2, fileUriStr, System.currentTimeMillis(), 1));
                                adapterMsg.notifyDataSetChanged();
                                recyclerView.scrollToPosition(msgList.size() - 1);
                            }
                        } catch (Exception ex) {
                            showToast(R.string.system_web_busy);
                            KLog.e(BaseConfig.LOG, "onError:" + ex.getMessage());
                            showToast("文件下载失败");
                        }
                    }
                });
        // 获取附件 end

    }


    /**
     * 当权限授予时的回调
     *
     * @param requestCode 请求码
     * @param perms       权限参数集合
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (requestCode == 1) {
            if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                showToast(R.string.permission_hint_content);
            }
        } else if (requestCode == 2) {
            // 发送文件
            getFile();
        }
    }

    /**
     * 权限被拒绝的回调
     *
     * @param requestCode 请求码
     * @param perms       权限参数集合
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (requestCode == BaseConfig.REQUESTCODE_WRITE_EXTERNAL_STORAGE) {
            showToast(R.string.permission_hint_content);
        }
    }

    /**
     * 请求授予权限的结果回调
     *
     * @param requestCode  请求码
     * @param permissions  权限数组
     * @param grantResults 授予的权限数组
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
