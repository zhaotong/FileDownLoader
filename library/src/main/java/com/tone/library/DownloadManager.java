package com.tone.library;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

/**
 * Created by zhaotong on 2016/9/28.
 */
public class DownloadManager {
    private Context context;
    private static DownloadManager instance;
    private HandleAidl handle;
    private boolean isBind = false;
    public static DownloadManager getInstance(Context context) {
        if (instance == null) {
            synchronized (DownloadManager.class) {
                if (instance == null)
                    instance = new DownloadManager(context);
            }
        }
        return instance;
    }

    protected DownloadManager(Context c) {
        this.context = c;
        if (!isBind) {
            Intent intent = new Intent(c, DownloadService.class);
            c.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }
    }

    public void setListener(DownloadListener listener){
        DownloadTask.getInstance(context).setListener(listener);
    }

    public void unbindService(){
        context.unbindService(connection);
    }

    private ServiceConnection connection =new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            try {
                handle = HandleAidl.Stub.asInterface(iBinder);
                handle.registerCallback(callback);
                isBind = true;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            handle = null;
            isBind = false;
        }
    };

    public void start(String url){
        try {
            handle.start(url);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private CallBackAidl.Stub callback = new CallBackAidl.Stub() {
        @Override
        public void onStart(DownloadInfo info) throws RemoteException {

        }

        @Override
        public void onProgress(DownloadInfo info) throws RemoteException {

        }

        @Override
        public void onError(DownloadInfo info, String error) throws RemoteException {

        }

        @Override
        public void onSuccess(DownloadInfo info) throws RemoteException {

        }
    };
}
