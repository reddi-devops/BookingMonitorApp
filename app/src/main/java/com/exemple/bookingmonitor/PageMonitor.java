package com.exemple.bookingmonitor;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.util.concurrent.TimeUnit;

public class PageMonitor {

    private OkHttpClient httpClient;

    public PageMonitor() {
        httpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build();
    }

    public String fetchPageContent(String url) {
        try {
            Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    return response.body().string();
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getPageStatusCode(String url) {
        try {
            Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .build();

            try (Response response = httpClient.newCall(request).execute()) {
                return response.code();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean isPageAvailable(String url) {
        int statusCode = getPageStatusCode(url);
        return statusCode >= 200 && statusCode < 400;
    }
}
