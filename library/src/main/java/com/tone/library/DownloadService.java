package com.tone.library;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

public class DownloadService extends Service {
    public DownloadService() {
    }

    private RemoteCallbackList<CallBackAidl> callbackList = new RemoteCallbackList<>();
    private HandleAidl.Stub binder = new HandleAidl.Stub() {
        @Override
        public void registerCallback(CallBackAidl cb) throws RemoteException {
            if (cb != null)
                callbackList.register(cb);
        }

        @Override
        public void unregisterCallback(CallBackAidl cb) throws RemoteException {
            if (cb != null)
                callbackList.unregister(cb);
        }

        @Override
        public void start(String url) throws RemoteException {
            startDownload(url);
        }

        @Override
        public void stop(String url) throws RemoteException {
            stopDownload(url);
        }

        @Override
        public void delete(String url) throws RemoteException {
            deleteDownload(url);
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        callbackList.kill();
    }

    private void startDownload(String url) {
        try {
            DownloadInfo downloadInfo = DownloadDB.getInstance(this).getDownLoadInfo(url);
            if (downloadInfo == null) {
                downloadInfo = new DownloadInfo();
                downloadInfo.setTotalSize(0);
                downloadInfo.setData("");
                downloadInfo.setCurrentSize(0);
                downloadInfo.setFileUrl(url);
                downloadInfo.setFileName(FileUtil.getFileName(url));
                downloadInfo.setFilePath(FileUtil.getFile(url).getPath());
            }
            DownloadTask.getInstance(this).startDownload(downloadInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopDownload(String url) {
        try {
            DownloadInfo downloadInfo = DownloadDB.getInstance(this).getDownLoadInfo(url);
            if (downloadInfo != null)
                DownloadTask.getInstance(this).stopDownload(downloadInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteDownload(String url){
        try {
            if (DownloadTask.getInstance(this).isDownloading(url))
                stopDownload(url);
            DownloadDB.getInstance(this).deleteDownload(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
