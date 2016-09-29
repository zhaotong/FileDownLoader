package com.tone.library;

import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * Created by zhaotong on 2016/9/28.
 */
public class FileUtil {
    private static final String SD_PATH = Environment.getExternalStorageDirectory().getPath();
    private static final String DIR = SD_PATH + File.separator + "download";

    public static File getFile(String url) {
        try {
            File file = new File(DIR);
            if (!file.exists())
                file.mkdirs();
            File file1 = new File(DIR + File.separator + getFileName(url));
            file1.createNewFile();
            return file1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getFileName(String url) {
        String[] str = url.split("/");
        return str[str.length - 1];
    }
}
