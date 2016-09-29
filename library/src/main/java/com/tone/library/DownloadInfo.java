package com.tone.library;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhaotong on 2016/9/28.
 */
public class DownloadInfo implements Parcelable {
    private String filePath;
    private String fileUrl;
    private long currentSize;
    private long totalSize;
    private String fileName;
    private String data;

    public DownloadInfo() {
    }

    public DownloadInfo(String filePath, String fileUrl, long currentSize, long totalSize, String fileName, String data) {
        this.filePath = filePath;
        this.fileUrl = fileUrl;
        this.currentSize = currentSize;
        this.totalSize = totalSize;
        this.fileName = fileName;
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public long getCurrentSize() {
        return currentSize;
    }

    public void setCurrentSize(long currentSize) {
        this.currentSize = currentSize;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.filePath);
        dest.writeString(this.fileUrl);
        dest.writeLong(this.currentSize);
        dest.writeLong(this.totalSize);
        dest.writeString(this.fileName);
        dest.writeString(this.data);
    }

    private DownloadInfo(Parcel in) {
        this.filePath = in.readString();
        this.fileUrl = in.readString();
        this.currentSize = in.readLong();
        this.totalSize = in.readLong();
        this.fileName = in.readString();
        this.data = in.readString();
    }

    public void readFromParcel(Parcel in) {
        this.filePath = in.readString();
        this.fileUrl = in.readString();
        this.currentSize = in.readLong();
        this.totalSize = in.readLong();
        this.fileName = in.readString();
        this.data = in.readString();
    }

    public static final Creator<DownloadInfo> CREATOR = new Creator<DownloadInfo>() {
        @Override
        public DownloadInfo createFromParcel(Parcel source) {
            return new DownloadInfo(source);
        }

        @Override
        public DownloadInfo[] newArray(int size) {
            return new DownloadInfo[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DownloadInfo that = (DownloadInfo) o;

        if (totalSize != that.totalSize) return false;
        if (filePath != null ? !filePath.equals(that.filePath) : that.filePath != null)
            return false;
        if (fileUrl != null ? !fileUrl.equals(that.fileUrl) : that.fileUrl != null) return false;
        if (fileName != null ? !fileName.equals(that.fileName) : that.fileName != null)
            return false;
        return data != null ? data.equals(that.data) : that.data == null;

    }

    @Override
    public int hashCode() {
        int result = filePath != null ? filePath.hashCode() : 0;
        result = 31 * result + (fileUrl != null ? fileUrl.hashCode() : 0);
        result = 31 * result + (int) (totalSize ^ (totalSize >>> 32));
        result = 31 * result + (fileName != null ? fileName.hashCode() : 0);
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }
}
