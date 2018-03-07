package com.ooo.mq.utils.net;


import com.ooo.mq.mode.entity.FileProgressEntity;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;
import xiaofei.library.hermeseventbus.HermesEventBus;

/**
 * 下载进度监听处理
 * dongdd on 2017/10/29 18:15
 */

public class DownloadProgressResponseBody extends ResponseBody {
    private ResponseBody responseBody;
    private BufferedSource bufferedSource;

    public DownloadProgressResponseBody(ResponseBody responseBody) {
        this.responseBody = responseBody;
    }


    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }


    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    /**
     * 文件下载进度输出处理
     *
     * @param source
     * @return
     */
    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;

                FileProgressEntity fileProgressEntity = new FileProgressEntity();
                fileProgressEntity.setTotalFileSize(responseBody.contentLength());
                fileProgressEntity.setCurrentFileSize(totalBytesRead);
                int progress = (int) ((totalBytesRead * 100) / responseBody.contentLength());
                fileProgressEntity.setProgress(progress);

//                KLog.e(BaseConfig.LOG, "下载进度:" + DataFormatUtils.GsonString(download));
                HermesEventBus.getDefault().post(fileProgressEntity); // 更新下载进度
                return bytesRead;
            }
        };
    }
}
