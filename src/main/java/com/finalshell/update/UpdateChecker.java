package com.finalshell.update;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.finalshell.ui.dialog.UpdaterDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

/**
 * 软件更新检查器
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Update_Module_DeepAnalysis.md
 */
public class UpdateChecker {
    
    private static final Logger logger = LoggerFactory.getLogger(UpdateChecker.class);
    
    public static final int CURRENT_VERSION = 100;
    public static final String VERSION_STRING = "1.0.0";
    
    private static UpdateChecker instance;
    
    private String updateUrl;
    private boolean checking = false;
    private UpdateInfo latestInfo;
    
    public static synchronized UpdateChecker getInstance() {
        if (instance == null) {
            instance = new UpdateChecker();
        }
        return instance;
    }
    
    private UpdateChecker() {
        // 可配置的更新检查URL
        String url = System.getProperty("update.check.url");
        if (url != null && !url.trim().isEmpty()) {
            this.updateUrl = url.trim();
        } else {
            this.updateUrl = null;
        }
    }
    
    public void setUpdateUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            this.updateUrl = null;
        } else {
            this.updateUrl = url.trim();
        }
    }
    
    /**
     * 异步检查更新
     */
    public void checkUpdateAsync(UpdateCallback callback) {
        if (checking) {
            return;
        }
        
        checking = true;
        
        new Thread(() -> {
            try {
                UpdateInfo info = checkUpdate();
                SwingUtilities.invokeLater(() -> {
                    if (info != null && info.getVersionCode() > CURRENT_VERSION) {
                        callback.onUpdateAvailable(info);
                    } else {
                        callback.onNoUpdate();
                    }
                });
            } catch (Exception e) {
                logger.error("Update check failed", e);
                SwingUtilities.invokeLater(() -> callback.onError(e.getMessage()));
            } finally {
                checking = false;
            }
        }).start();
    }
    
    /**
     * 同步检查更新
     */
    public UpdateInfo checkUpdate() throws IOException {
        if (updateUrl == null || updateUrl.trim().isEmpty()) {
            throw new IOException("未配置 update.check.url，请通过 -Dupdate.check.url=<更新配置URL> 设置");
        }
        HttpURLConnection conn = null;
        try {
            URL url = new URL(updateUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);
            conn.setRequestProperty("User-Agent", "FinalShell-Clone/" + VERSION_STRING);
            
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new IOException("HTTP error: " + responseCode);
            }
            
            try (InputStream is = conn.getInputStream();
                 BufferedReader reader = new BufferedReader(
                     new InputStreamReader(is, StandardCharsets.UTF_8))) {
                
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                
                JSONObject json = JSON.parseObject(sb.toString());
                latestInfo = new UpdateInfo();
                latestInfo.setVersionCode(json.getIntValue("version_code"));
                latestInfo.setVersionName(json.getString("version_name"));
                latestInfo.setDownloadUrl(json.getString("download_url"));
                latestInfo.setChangelog(json.getString("changelog"));
                latestInfo.setForceUpdate(json.getBooleanValue("force_update"));
                latestInfo.setFileSize(json.getLongValue("file_size"));
                
                return latestInfo;
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
    
    /**
     * 显示更新对话框
     */
    public void showUpdateDialog(Component parent, UpdateInfo info) {
        if (parent instanceof Frame) {
            UpdaterDialog dialog = new UpdaterDialog((Frame) parent);
            dialog.setUpdateInfo(info.getVersionName(), info.getChangelog(), info.getDownloadUrl());
            dialog.setVisible(true);
            return;
        }
        String message = String.format(
            "发现新版本: %s\n\n更新内容:\n%s\n\n是否现在下载?",
            info.getVersionName(),
            info.getChangelog()
        );
        
        int result = JOptionPane.showConfirmDialog(
            parent,
            message,
            "软件更新",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            openDownloadPage(info.getDownloadUrl());
        }
    }
    
    private void openDownloadPage(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            logger.error("Failed to open download page", e);
        }
    }
    
    public UpdateInfo getLatestInfo() {
        return latestInfo;
    }
    
    /**
     * 更新信息
     */
    public static class UpdateInfo {
        private int versionCode;
        private String versionName;
        private String downloadUrl;
        private String changelog;
        private boolean forceUpdate;
        private long fileSize;
        
        public int getVersionCode() { return versionCode; }
        public void setVersionCode(int versionCode) { this.versionCode = versionCode; }
        
        public String getVersionName() { return versionName; }
        public void setVersionName(String versionName) { this.versionName = versionName; }
        
        public String getDownloadUrl() { return downloadUrl; }
        public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
        
        public String getChangelog() { return changelog; }
        public void setChangelog(String changelog) { this.changelog = changelog; }
        
        public boolean isForceUpdate() { return forceUpdate; }
        public void setForceUpdate(boolean forceUpdate) { this.forceUpdate = forceUpdate; }
        
        public long getFileSize() { return fileSize; }
        public void setFileSize(long fileSize) { this.fileSize = fileSize; }
    }
    
    /**
     * 更新回调接口
     */
    public interface UpdateCallback {
        void onUpdateAvailable(UpdateInfo info);
        void onNoUpdate();
        void onError(String message);
    }
}
