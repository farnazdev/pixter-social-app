package com.example.socialnetwork.network;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class ApiRequest {

    public interface ApiCallback {
        void onSuccess(String response);
        void onError(String error);
    }

    public static void post(String urlString, Map<String, String> textParams, Map<String, File> fileParams, ApiCallback callback) {
        new Thread(() -> {
            String boundary = "===" + System.currentTimeMillis() + "===";
            String LINE_FEED = "\r\n";

            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setUseCaches(false);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                DataOutputStream request = new DataOutputStream(conn.getOutputStream());

                // متغیرهای متنی
                if (textParams != null) {
                    for (Map.Entry<String, String> entry : textParams.entrySet()) {
                        request.writeBytes("--" + boundary + LINE_FEED);
                        request.writeBytes("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINE_FEED);
                        request.writeBytes(LINE_FEED);
                        request.writeBytes(entry.getValue() + LINE_FEED);
                    }
                }

                // فایل‌ها
                if (fileParams != null) {
                    for (Map.Entry<String, File> entry : fileParams.entrySet()) {
                        String paramName = entry.getKey();
                        File file = entry.getValue();
                        String fileName = file.getName();
                        String mimeType = URLConnection.guessContentTypeFromName(fileName);

                        request.writeBytes("--" + boundary + LINE_FEED);
                        request.writeBytes("Content-Disposition: form-data; name=\"" + paramName + "\"; filename=\"" + fileName + "\"" + LINE_FEED);
                        request.writeBytes("Content-Type: " + mimeType + LINE_FEED);
                        request.writeBytes(LINE_FEED);

                        FileInputStream inputStream = new FileInputStream(file);
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            request.write(buffer, 0, bytesRead);
                        }
                        inputStream.close();
                        request.writeBytes(LINE_FEED);
                    }
                }

                request.writeBytes("--" + boundary + "--" + LINE_FEED);
                request.flush();
                request.close();

                // خواندن پاسخ
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(response.toString()));

            } catch (Exception e) {
                Log.e("ApiRequest", "Error: " + e.getMessage());
                new Handler(Looper.getMainLooper()).post(() -> callback.onError(e.toString()));
            }
        }).start();
    }

    // راحت‌تر کردن استفاده در اکتیویتی
    public static void post(String urlString, Map<String, String> textParams, ApiCallback callback) {
        post(urlString, textParams, null, callback);
    }
}

