package com.ultikits.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ultikits.beans.CheckResponse;
import com.ultikits.beans.EmailBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class SendEmailUtils {

    public static CheckResponse sendEmail(String target, String title, String content) {
        CheckResponse response = new CheckResponse();

        EmailBean emailBean = new EmailBean(target, title, content);
        String serializedEmail = null;
        try {
            serializedEmail = SerializationUtils.serialize(emailBean.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (serializedEmail == null){
            return response;
        }
        String url = "https://panel.ultikits.com:4433/utils/sendemail?data=" + serializedEmail;
        String result = HttpsPostUtils.doPost(url);
        response = JSON.toJavaObject(JSONObject.parseObject(result), CheckResponse.class);
        return response;
    }
}
