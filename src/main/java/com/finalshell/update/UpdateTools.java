package com.finalshell.update;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 更新工具类
 * 提供版本检查和下载更新的工具方法
 */
public class UpdateTools {
    
    private static final int BUFFER_SIZE = 8192;
    private static final int TIMEOUT = 30000;
    
    public static UpdateConfig checkUpdate(String checkUrl) throws Exception {
        URL url = new URL(checkUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(TIMEOUT);
        conn.setReadTimeout(TIMEOUT);
        
        try (InputStream is = conn.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return parseUpdateConfig(sb.toString());
        } finally {
            conn.disconnect();
        }
    }
    
    private static UpdateConfig parseUpdateConfig(String json) {
        UpdateConfig config = new UpdateConfig();

        if (json == null || json.trim().isEmpty()) {
            return config;
        }

        JSONObject root;
        try {
            root = JSON.parseObject(json);
        } catch (Exception e) {
            return config;
        }

        if (root == null) {
            return config;
        }

        String version = root.getString("version");
        if (version == null || version.isEmpty()) {
            version = root.getString("version_name");
        }
        config.setVersion(version);

        String updateUrl = root.getString("updateUrl");
        if (updateUrl == null || updateUrl.isEmpty()) {
            updateUrl = root.getString("update_url");
        }
        if (updateUrl == null || updateUrl.isEmpty()) {
            updateUrl = root.getString("download_url");
        }
        config.setUpdateUrl(updateUrl);

        String releaseNotes = root.getString("releaseNotes");
        if (releaseNotes == null || releaseNotes.isEmpty()) {
            releaseNotes = root.getString("release_notes");
        }
        if (releaseNotes == null || releaseNotes.isEmpty()) {
            releaseNotes = root.getString("changelog");
        }
        config.setReleaseNotes(releaseNotes);

        long releaseDate = root.getLongValue("releaseDate");
        if (releaseDate <= 0) {
            releaseDate = root.getLongValue("release_date");
        }
        config.setReleaseDate(releaseDate);

        boolean forceUpdate = root.getBooleanValue("forceUpdate") || root.getBooleanValue("force_update");
        config.setForceUpdate(forceUpdate);

        JSONArray items = root.getJSONArray("downloadItems");
        if (items == null) {
            items = root.getJSONArray("download_items");
        }
        if (items == null) {
            items = root.getJSONArray("files");
        }
        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                JSONObject obj = items.getJSONObject(i);
                if (obj == null) {
                    continue;
                }

                DownloadItem item = new DownloadItem();
                String url = obj.getString("url");
                item.setUrl(url);

                String fileName = obj.getString("fileName");
                if (fileName == null || fileName.isEmpty()) {
                    fileName = obj.getString("file_name");
                }
                if ((fileName == null || fileName.isEmpty()) && url != null) {
                    int idx = url.lastIndexOf('/');
                    if (idx >= 0 && idx < url.length() - 1) {
                        fileName = url.substring(idx + 1);
                    }
                }
                item.setFileName(fileName);

                String targetPath = obj.getString("targetPath");
                if (targetPath == null || targetPath.isEmpty()) {
                    targetPath = obj.getString("target_path");
                }
                item.setTargetPath(targetPath);

                long size = obj.getLongValue("size");
                item.setSize(size);

                String md5 = obj.getString("md5");
                item.setMd5(md5);

                config.addDownloadItem(item);
            }
        }

        return config;
    }
    
    public static void downloadFile(String urlStr, File destFile, DownloadListener listener) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(TIMEOUT);
        conn.setReadTimeout(TIMEOUT);
        
        long totalSize = conn.getContentLengthLong();
        long downloaded = 0;
        
        try (InputStream is = conn.getInputStream();
             FileOutputStream fos = new FileOutputStream(destFile)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int read;
            while ((read = is.read(buffer)) != -1) {
                fos.write(buffer, 0, read);
                downloaded += read;
                if (listener != null) {
                    listener.onProgress(downloaded, totalSize);
                }
            }
        } finally {
            conn.disconnect();
        }
        
        if (listener != null) {
            listener.onComplete();
        }
    }
    
    public static int compareVersion(String v1, String v2) {
        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");
        
        int length = Math.max(parts1.length, parts2.length);
        for (int i = 0; i < length; i++) {
            int num1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int num2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;
            
            if (num1 < num2) return -1;
            if (num1 > num2) return 1;
        }
        return 0;
    }
    
    public static boolean needUpdate(String currentVersion, String newVersion) {
        return compareVersion(currentVersion, newVersion) < 0;
    }
    
    public interface DownloadListener {
        void onProgress(long downloaded, long total);
        void onComplete();
        void onError(Exception e);
    }
}
