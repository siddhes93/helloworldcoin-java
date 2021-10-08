package com.xingkaichun.helloworldblockchain.util;

import java.io.*;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class FileUtil {

    public static String newPath(String parent, String child) {
        return new File(parent,child).getAbsolutePath();
    }

    public static void makeDirectory(String path) {
        File file = new File(path);
        if(file.exists()){
            return;
        }
        boolean isMakeDirectorySuccess = file.mkdirs();
        if(!isMakeDirectorySuccess){
            SystemUtil.errorExit("create directory failed. path is "+path + ".",null);
        }
    }

    public static void deleteDirectory(String path) {
        File file = new File(path);
        if(file.isDirectory()){
            File[] childrenFiles = file.listFiles();
            if(childrenFiles != null){
                for (File childFile:childrenFiles){
                    deleteDirectory(childFile.getAbsolutePath());
                }
            }
        }
        boolean isDeleteDirectorySuccess = file.delete();
        if(!isDeleteDirectorySuccess){
            SystemUtil.errorExit("delete directory failed. path is "+path + ".",null);
        }
    }

    public static String read(String path) {
        File file = new File(path);
        FileInputStream fileStream = null;
        try {
            fileStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileStream, "UTF-8");
            BufferedReader br = new BufferedReader(inputStreamReader);
            String text = "";
            String line;
            while((line = br.readLine()) != null){
                text += line;
            }
            return text;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                fileStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
