// CallBackAidl.aidl
package com.tone.library;

// Declare any non-default types here with import statements

import com.tone.library.DownloadInfo;
interface CallBackAidl {

    void onStart(inout DownloadInfo info);
    void onProgress(inout DownloadInfo info);
    void onError(inout DownloadInfo info,String error);
    void onSuccess(inout DownloadInfo info);
}
