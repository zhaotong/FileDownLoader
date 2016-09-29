package com.tone.library;

import android.content.ContentValues;
import android.content.Context;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhaotong on 2016/9/28.
 */
public class DownloadTask {

    private static DownloadTask instance;

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAX_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final Executor threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, 10L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2000));

    private HashMap<String, DownloadRunnable> downloadRunnableHashMap = new HashMap<>();
    private DownloadListener listener;
    private Context context;

    public static DownloadTask getInstance(Context context) {
        if (instance == null) {
            synchronized (DownloadDB.class) {
                if (instance == null)
                    instance = new DownloadTask(context);
            }
        }
        return instance;
    }

    protected DownloadTask(Context context) {
        this.context = context;
    }

    public DownloadListener getListener() {
        return listener;
    }

    public void setListener(DownloadListener listener) {
        this.listener = listener;
    }

    public void startDownload(DownloadInfo info) {
        DownloadRunnable runnable = new DownloadRunnable(info);

        threadPool.execute(runnable);
        if (downloadRunnableHashMap.containsKey(info.getFileUrl()))
            downloadRunnableHashMap.remove(info.getFileUrl());
        downloadRunnableHashMap.put(info.getFileUrl(), runnable);
    }

    public void stopDownload(DownloadInfo info) {
        if (downloadRunnableHashMap.containsKey(info.getFileUrl())) {
            DownloadRunnable runnable = downloadRunnableHashMap.get(info.getFileUrl());
            runnable.setStop(true);
            downloadRunnableHashMap.remove(info.getFileUrl());
        }
    }


    public boolean isDownloading(String url) {
        return downloadRunnableHashMap.containsKey(url);
    }

    private class DownloadRunnable implements Runnable {

        private DownloadInfo downloadInfo;

        public DownloadRunnable(DownloadInfo downloadInfo) {
            this.downloadInfo = downloadInfo;
        }

        private boolean isStop = false;

        @Override
        public void run() {
            if (listener != null) {
                listener.onStart(downloadInfo);
            }
            if (downloadInfo.getTotalSize() == 0) {
                downloadInfo.setTotalSize(getFileSize(downloadInfo.getFileUrl()));
            }
            int result;
            int count = 0;
            do {
                result = download();
                if (result == -1) {
                    count++;
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (result != 0 && count < 3);
        }

        public void setStop(boolean stop) {
            isStop = stop;
        }

        private int download() {
            HttpURLConnection httpURLConnection = null;
            RandomAccessFile randomAccessFile = null;
            InputStream inputStream = null;
            try {
                long currentSize = downloadInfo.getCurrentSize();
                long totalSize = downloadInfo.getTotalSize();
                URL url = new URL(downloadInfo.getFileUrl());
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(10000);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                httpURLConnection.setRequestProperty("RANGE", "bytes=" + currentSize + "-" + totalSize);
                httpURLConnection.setRequestProperty("Charset", "UTF-8");
                httpURLConnection.connect();

                if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK
                        && httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_PARTIAL) {
                    return -1;
                }

                File file = new File(downloadInfo.getFilePath());
                randomAccessFile = new RandomAccessFile(file, "rw");
                randomAccessFile.seek(downloadInfo.getCurrentSize());
                byte[] buffer = new byte[20480];
                inputStream = httpURLConnection.getInputStream();
                int length = 0;
                while ((length = inputStream.read(buffer, 0, buffer.length)) != -1) {
                    randomAccessFile.write(buffer, 0, length);
                    currentSize += length;
                    downloadInfo.setCurrentSize(currentSize);
                    DownloadDB.getInstance(context).updateOrInsert(downloadInfo);
                    if (isStop) {
                        randomAccessFile.close();
                        inputStream.close();
                        httpURLConnection.disconnect();
                        if (listener != null) {
                            listener.onStop(downloadInfo);
                        }
                        break;
                    }
                    if (listener != null) {
                        listener.onProgress(downloadInfo);
                    }
                }
                if (listener != null) {
                    if (downloadInfo.getTotalSize() == downloadInfo.getCurrentSize())
                        listener.onSuccess(downloadInfo);
                }
                return 0;

            } catch (IOException e) {
                e.printStackTrace();
                if (listener != null) {
                    listener.onError(downloadInfo);
                }
                return -1;
            } finally {
                try {
                    if (randomAccessFile != null) {
                        randomAccessFile.close();
                    }
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private long getFileSize(String fileUrl) {
            long totalSize = 0;
            HttpURLConnection httpURLConnection = null;
            try {
                URL url = new URL(fileUrl);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(10000);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();
                int responseConde = httpURLConnection.getResponseCode();
                if (responseConde == HttpURLConnection.HTTP_OK) {
                    totalSize = httpURLConnection.getContentLength();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                httpURLConnection.disconnect();
            }
            return totalSize;
        }
    }
}
