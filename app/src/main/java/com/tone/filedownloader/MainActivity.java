package com.tone.filedownloader;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.tone.library.DownloadDB;
import com.tone.library.DownloadInfo;
import com.tone.library.DownloadListener;
import com.tone.library.DownloadManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Adapter adapter;
    ArrayList<DownloadInfo> downloads = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        adapter =new Adapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        downloads = DownloadDB.getInstance(this).getAllDownLoadInfo();
        adapter.setDownloadInfos(downloads);
        recyclerView.setAdapter(adapter);
        DownloadManager.getInstance(this).setListener(new DownloadListener() {
            @Override
            public void onStart(DownloadInfo info) {

                Log.d("MainActivity", "onStart: "+info.getFileName());
            }

            @Override
            public void onProgress(DownloadInfo info) {
                Log.d("MainActivity", "onProgress: "+info.getFileName()+"  progress==" +(int) (info.getCurrentSize()*100/info.getTotalSize())+"%");

                Message msg = hander.obtainMessage();
                msg.obj = info;
                msg.what=1;
                hander.sendMessage(msg);
            }

            @Override
            public void onStop(DownloadInfo info) {

            }

            @Override
            public void onError(DownloadInfo info) {

                Log.d("MainActivity", "onError: "+info.getFileName());
            }

            @Override
            public void onSuccess(DownloadInfo info) {
                Log.d("MainActivity", "onSuccess: "+info.getFileName());
            }
        });
    }


    private Handler hander = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            DownloadInfo info = (DownloadInfo) msg.obj;
            Log.d("MainActivity", "handleMessage: "+info.getFileName()+"  progress==" +(int) (info.getCurrentSize()*100/info.getTotalSize())+"%");

            if (adapter.getDownloadInfos().contains(info)) {
                int position = adapter.getDownloadInfos().indexOf(info);
                adapter.getDownloadInfos().get(position).setCurrentSize(info.getCurrentSize());
                adapter.notifyItemChanged(position);
            }else {
                adapter.addItem(info);
            }

        }
    };

    public void start(View v){
        DownloadManager.getInstance(this).start("http://www.xzcmvideo.cn//masvod/public/2016/09/12/20160912_1571ca8079f_r1.mp4");
        DownloadManager.getInstance(this).start("http://www.xzcmvideo.cn//masvod/public/2016/09/21/20160921_1574c418010_r1.mp4");
        DownloadManager.getInstance(this).start("http://www.xzcmvideo.cn//masvod/public/2016/09/08/20160908_15707ff550a_r1_1200k.mp4");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DownloadManager.getInstance(this).unbindService();
    }
}
