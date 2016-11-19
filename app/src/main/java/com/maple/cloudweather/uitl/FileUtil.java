package com.maple.cloudweather.uitl;

import java.io.File;

/**
 * Created by San on 2016/11/17.
 */

public class FileUtil {

    public static boolean delete(File file) {
        if (file.isFile()) {
            return file.delete();
        }

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null || files.length == 0) {
                return file.delete();
            }

            for (File f : files) {
                delete(f);
            }
            return file.delete();
        }
        return false;
    }
}
