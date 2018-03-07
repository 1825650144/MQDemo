package com.ooo.mq.mode.entity;

import java.util.List;

/**
 * 文件上传返回的文件路径
 * dongtengfei on 2017/10/19 08:52
 */

public class JsonFileList {
    /**
     * code : 200.0
     * msg : 请求成功
     * repData : {"file_path":["group1/M00/00/1F/wKgBw1noyNuAQaGYAATpC6zrEWE716.jpg","group1/M00/00/1F/wKgBw1noyNuAGGcOAAAtnfcJdac15.xlsx"]}
     */
    private List<String> file_path;//返回文件保存地址列表

    public List<String> getFile_path() {
        return file_path;
    }

    public void setFile_path(List<String> file_path) {
        this.file_path = file_path;
    }
}
