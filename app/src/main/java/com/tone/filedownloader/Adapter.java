package com.tone.filedownloader;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tone.library.DownloadInfo;
import com.tone.library.DownloadTask;

import java.util.ArrayList;

/**
 * Created by zhaotong on 2016/9/29.
 */
public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<DownloadInfo> downloadInfos = new ArrayList<>();

    private Context context;

    public void setDownloadInfos(ArrayList<DownloadInfo> downloadInfos) {
        this.downloadInfos = downloadInfos;
        notifyDataSetChanged();
    }

    public ArrayList<DownloadInfo> getDownloadInfos() {
        return downloadInfos;
    }

    public void addItem(DownloadInfo info){
        downloadInfos.add(info);
        notifyItemInserted(downloadInfos.size()-1);
    }

    public Adapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
        final DownloadInfo info = downloadInfos.get(position);
        final Holder holder = (Holder) h;
        holder.file_name.setText(info.getFileName());
        int progress = (int) (info.getCurrentSize()*100/info.getTotalSize());
        Log.d("Adapter", "onBindViewHolder:  progress =="+progress);
        holder.progressbar.setProgress(progress);
        holder.progress.setText(progress+"%");
        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DownloadTask.getInstance(context).isDownloading(info.getFileUrl())){
                    DownloadTask.getInstance(context).stopDownload(info);
                    holder.btn.setText("开始");
                }else {
                    DownloadTask.getInstance(context).startDownload(info);
                    holder.btn.setText("暂停");
                }
            }
        });
        if (info.getCurrentSize()==info.getTotalSize()){
            holder.btn.setText("下载完毕");
        }

    }

    @Override
    public int getItemCount() {
        return downloadInfos.size();
    }



    static class Holder extends RecyclerView.ViewHolder{

        private TextView file_name,btn,progress;
        private ProgressBar progressbar;
        public Holder(View itemView) {
            super(itemView);
            file_name = (TextView) itemView.findViewById(R.id.file_name);
            progress = (TextView) itemView.findViewById(R.id.progress);
            btn = (TextView) itemView.findViewById(R.id.btn);
            progressbar = (ProgressBar) itemView.findViewById(R.id.progressbar);
        }
    }

}
