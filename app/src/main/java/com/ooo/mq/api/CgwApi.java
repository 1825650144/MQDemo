package com.ooo.mq.api;


import com.ooo.mq.mode.entity.JsonFileList;
import com.ooo.mq.mode.entity.ResultBean;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import rx.Observable;

/**
 * API接口类
 * dongdd on 2017/10/11 16:10
 */

public interface CgwApi {


    /**
     * 多文件上传
     *
     * @param body 文件参数
     * @return
     */
    @POST("file/fileuploadFor81")
    Observable<ResultBean<JsonFileList>> uploadUserImgFile(@Body RequestBody body);

    /**
     * 通知/报告下载附件
     *
     * @param filePath 需要下载的文件地址。
     * @return
     */
    @GET("/downloadFor81")
    Observable<Response<ResponseBody>> downLoadFiles(@Query("file_path") String filePath);

    /**
     * 文件下载
     *
     * @param filePath
     * @return
     */
    @Streaming
    @GET("/downloadFor81")
    Observable<ResponseBody> download(@Query("file_path") String filePath);





}
