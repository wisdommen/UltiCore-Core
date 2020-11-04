package com.ultikits.utils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class HttpDownloadUtils {

    public static void download(String urlString, String fileName, String savePath) {

        new Runnable(){
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    URLConnection conn = url.openConnection();
                    InputStream is = conn.getInputStream();

                    byte[] buff = new byte[1024];
                    int len = 0;
                    File file = new File(savePath);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    OutputStream os = new FileOutputStream(file.getPath() + "/" + fileName);
                    while ((len = is.read(buff)) != -1) {
                        os.write(buff, 0, len);
                    }
                    os.close();
                    is.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        };

    }
}
