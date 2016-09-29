// DownloadAidl.aidl
package com.tone.library;
import com.tone.library.CallBackAidl;
interface HandleAidl {

    void registerCallback(CallBackAidl cb);
    void unregisterCallback(CallBackAidl cb);
    void start(String url);
    void stop(String url);
    void delete(String url);

}
