package com.tone.library;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by zhaotong on 2016/9/28.
 */
public class DownloadDB extends SQLiteOpenHelper {
    private static final String DBNAME = "filedownloader.db";
    private static final int mVersion = 2;
    private static final String TABLE_NAME = "downloadinfo";


    private static final String SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "( id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ,"
            + " filePath VARCHAR ," + " fileUrl VARCHAR ," + " fileName VARCHAR ," + " currentSize VARCHAR ," + " totalSize VARCHAR ," + " data VARCHAR " + ")";

    private static DownloadDB instance;

    public static DownloadDB getInstance(Context context) {
        if (instance == null) {
            synchronized (DownloadDB.class) {
                if (instance == null)
                    instance = new DownloadDB(context);
            }
        }
        return instance;
    }


    protected DownloadDB(Context context) {
        super(context, DBNAME, null, mVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void updateOrInsert(DownloadInfo downloadInfo) {
        ContentValues cv = new ContentValues();
        cv.put("filePath", downloadInfo.getFilePath());
        cv.put("fileUrl", downloadInfo.getFileUrl());
        cv.put("fileName", downloadInfo.getFileName());
        cv.put("currentSize", downloadInfo.getCurrentSize());
        cv.put("data", downloadInfo.getData());
        cv.put("totalSize", downloadInfo.getTotalSize());
        Cursor cursor = null;
        try {
            String sql = "SELECT * from " + TABLE_NAME + " WHERE fileUrl = ? ";
            cursor = getWritableDatabase().rawQuery(sql, new String[]{downloadInfo.getFileUrl()});
            if (cursor.moveToNext()) {
                getWritableDatabase().update(TABLE_NAME, cv, "fileUrl = ? ", new String[]{downloadInfo.getFileUrl()});
            } else {
                getWritableDatabase().insert(TABLE_NAME, null, cv);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public ArrayList<DownloadInfo> getAllDownLoadInfo() {
        ArrayList<DownloadInfo> list = new ArrayList<>();
        Cursor cursor = getWritableDatabase().rawQuery("SELECT * from " + TABLE_NAME, null);
        while (cursor.moveToNext()) {
            DownloadInfo downloadinfo = new DownloadInfo();
            downloadinfo.setTotalSize(cursor.getLong(cursor.getColumnIndex("totalSize")));
            downloadinfo.setFileName(cursor.getString(cursor.getColumnIndex("fileName")));
            downloadinfo.setFilePath(cursor.getString(cursor.getColumnIndex("filePath")));
            downloadinfo.setCurrentSize(cursor.getLong(cursor.getColumnIndex("currentSize")));
            downloadinfo.setFileUrl(cursor.getString(cursor.getColumnIndex("fileUrl")));
            downloadinfo.setData(cursor.getString(cursor.getColumnIndex("data")));
            list.add(downloadinfo);
        }
        cursor.close();
        return list;
    }

    public int deleteDownload(String url) {
        return getWritableDatabase().delete(TABLE_NAME, "WHERE fileUrl = ?", new String[]{url});
    }

    public DownloadInfo getDownLoadInfo(String url) {
        String sql = "SELECT * from " + TABLE_NAME + " WHERE fileUrl = ? ";
        Cursor cursor = getWritableDatabase().rawQuery(sql, new String[]{url});
        DownloadInfo downloadinfo = null;
        while (cursor.moveToNext()) {
            downloadinfo = new DownloadInfo();
            downloadinfo.setTotalSize(cursor.getLong(cursor.getColumnIndex("totalSize")));
            downloadinfo.setFileName(cursor.getString(cursor.getColumnIndex("fileName")));
            downloadinfo.setFilePath(cursor.getString(cursor.getColumnIndex("filePath")));
            downloadinfo.setCurrentSize(cursor.getLong(cursor.getColumnIndex("currentSize")));
            downloadinfo.setFileUrl(cursor.getString(cursor.getColumnIndex("fileUrl")));
            downloadinfo.setData(cursor.getString(cursor.getColumnIndex("data")));
        }
        cursor.close();
        return downloadinfo;
    }
}
