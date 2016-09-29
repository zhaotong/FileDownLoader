package com.tone.library;

/**
 * Created by zhaotong on 2016/9/28.
 */
public interface DownloadListener {
    public void onStart(DownloadInfo info);

    public void onProgress(DownloadInfo info);

    public void onStop(DownloadInfo info);

    public void onError(DownloadInfo info);

    public void onSuccess(DownloadInfo info);
}
