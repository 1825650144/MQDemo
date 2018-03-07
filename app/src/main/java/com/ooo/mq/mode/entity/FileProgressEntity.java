package com.ooo.mq.mode.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 文件下载、上传进度记录实体类
 * dongdd on 2017/10/29 18:30
 */

public class FileProgressEntity implements Parcelable {
    private int progress; // 当前进度
    private long currentFileSize; //  已完成文件大小
    private long totalFileSize; // 文件总大小

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public long getCurrentFileSize() {
        return currentFileSize;
    }

    public void setCurrentFileSize(long currentFileSize) {
        this.currentFileSize = currentFileSize;
    }

    public long getTotalFileSize() {
        return totalFileSize;
    }

    public void setTotalFileSize(long totalFileSize) {
        this.totalFileSize = totalFileSize;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.progress);
        dest.writeLong(this.currentFileSize);
        dest.writeLong(this.totalFileSize);
    }

    public FileProgressEntity() {
    }

    protected FileProgressEntity(Parcel in) {
        this.progress = in.readInt();
        this.currentFileSize = in.readLong();
        this.totalFileSize = in.readLong();
    }

    public static final Creator<FileProgressEntity> CREATOR = new Creator<FileProgressEntity>() {
        @Override
        public FileProgressEntity createFromParcel(Parcel source) {
            return new FileProgressEntity(source);
        }

        @Override
        public FileProgressEntity[] newArray(int size) {
            return new FileProgressEntity[size];
        }
    };
}
