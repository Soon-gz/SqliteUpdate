package com.example.administrator.greendao.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ShuWen on 2017/2/18.
 */

public class FileUtils {
    public static void copySingleFile(String oldFilePath ,String newFilePath){
        File oldfile = new File(oldFilePath);
        File newFile = new File(newFilePath);
        File file = newFile.getParentFile();
        FileOutputStream fos = null;
        FileInputStream fis = null;
        byte[]readBytes = new byte[1024];
        int readNum = -1;

        if (!file.mkdirs()){
            file.mkdirs();
        }

        if (oldfile.exists()){
            try {
                fis = new FileInputStream(oldfile);
                fos = new FileOutputStream(newFile);
                while ((readNum = fis.read(readBytes)) != -1){
                    fos.write(readBytes,0,readNum);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (fis != null){
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fos != null){
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
}
