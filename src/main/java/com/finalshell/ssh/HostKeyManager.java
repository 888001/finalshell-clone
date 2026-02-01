package com.finalshell.ssh;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.finalshell.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * SSH主机密钥管理器 - 对齐原版myssh实现
 * 
 * Based on analysis of myssh/HostKeyManage.java (14行)
 * 管理SSH连接的主机密钥验证和存储
 */
public class HostKeyManager implements HostKeyManage {
    
    private static final Logger logger = LoggerFactory.getLogger(HostKeyManager.class);
    private static HostKeyManager instance;
    
    // 主机密钥存储映射 - 格式: "host:port" -> HostKeyInfo
    private Map<String, HostKeyInfo> hostKeys = new HashMap<>();
    private String configFilePath;
    
    private HostKeyManager() {
        configFilePath = ConfigManager.getInstance().getConfigDir() + File.separator + "host_keys.json";
        loadHostKeys();
    }
    
    public static synchronized HostKeyManager getInstance() {
        if (instance == null) {
            instance = new HostKeyManager();
        }
        return instance;
    }
    
    @Override
    public boolean verifyHostKey(String host, int port, String keyType, byte[] key) {
        String hostPort = getHostPortKey(host, port);
        HostKeyInfo keyInfo = hostKeys.get(hostPort);
        
        if (keyInfo == null) {
            // 首次连接，需要用户确认
            return false;
        }
        
        // 验证密钥类型和内容
        return keyType.equals(keyInfo.keyType) && 
               java.util.Arrays.equals(key, keyInfo.keyData);
    }
    
    @Override
    public void addHostKey(String host, int port, String keyType, byte[] key) {
        String hostPort = getHostPortKey(host, port);
        HostKeyInfo keyInfo = new HostKeyInfo();
        keyInfo.host = host;
        keyInfo.port = port;
        keyInfo.keyType = keyType;
        keyInfo.keyData = key;
        keyInfo.addTime = System.currentTimeMillis();
        
        hostKeys.put(hostPort, keyInfo);
        saveHostKeys();
        
        logger.info("已添加主机密钥: {}:{} ({})", host, port, keyType);
    }
    
    @Override
    public void removeHostKey(String host, int port) {
        String hostPort = getHostPortKey(host, port);
        HostKeyInfo removed = hostKeys.remove(hostPort);
        
        if (removed != null) {
            saveHostKeys();
            logger.info("已删除主机密钥: {}:{}", host, port);
        }
    }
    
    @Override
    public boolean hasHostKey(String host, int port) {
        String hostPort = getHostPortKey(host, port);
        return hostKeys.containsKey(hostPort);
    }
    
    /**
     * 获取主机端口组合键
     */
    private String getHostPortKey(String host, int port) {
        return host + ":" + port;
    }
    
    /**
     * 加载主机密钥配置
     */
    private void loadHostKeys() {
        try {
            File configFile = new File(configFilePath);
            if (configFile.exists()) {
                String content = new String(Files.readAllBytes(configFile.toPath()), StandardCharsets.UTF_8);
                JSONObject json = JSON.parseObject(content);
                JSONArray keyArray = json.getJSONArray("host_keys");
                
                if (keyArray != null) {
                    for (int i = 0; i < keyArray.size(); i++) {
                        JSONObject keyObj = keyArray.getJSONObject(i);
                        HostKeyInfo keyInfo = new HostKeyInfo();
                        keyInfo.host = keyObj.getString("host");
                        keyInfo.port = keyObj.getIntValue("port");
                        keyInfo.keyType = keyObj.getString("keyType");
                        keyInfo.keyData = Base64.getDecoder().decode(keyObj.getString("keyData"));
                        keyInfo.addTime = keyObj.getLongValue("addTime");
                        
                        String hostPort = getHostPortKey(keyInfo.host, keyInfo.port);
                        hostKeys.put(hostPort, keyInfo);
                    }
                }
                logger.info("已加载主机密钥: {} 个", hostKeys.size());
            }
        } catch (Exception e) {
            logger.error("加载主机密钥失败", e);
        }
    }
    
    /**
     * 保存主机密钥配置
     */
    private void saveHostKeys() {
        try {
            JSONObject json = new JSONObject();
            JSONArray keyArray = new JSONArray();
            
            for (HostKeyInfo keyInfo : hostKeys.values()) {
                JSONObject keyObj = new JSONObject();
                keyObj.put("host", keyInfo.host);
                keyObj.put("port", keyInfo.port);
                keyObj.put("keyType", keyInfo.keyType);
                keyObj.put("keyData", Base64.getEncoder().encodeToString(keyInfo.keyData));
                keyObj.put("addTime", keyInfo.addTime);
                keyArray.add(keyObj);
            }
            
            json.put("host_keys", keyArray);
            
            File configFile = new File(configFilePath);
            Files.write(configFile.toPath(), JSON.toJSONString(json, true).getBytes(StandardCharsets.UTF_8));
            logger.debug("已保存主机密钥配置");
        } catch (Exception e) {
            logger.error("保存主机密钥失败", e);
        }
    }
    
    /**
     * 获取所有主机密钥
     */
    public Map<String, HostKeyInfo> getAllHostKeys() {
        return new HashMap<>(hostKeys);
    }
    
    /**
     * 清空所有主机密钥
     */
    public void clearAllHostKeys() {
        hostKeys.clear();
        saveHostKeys();
        logger.info("已清空所有主机密钥");
    }
    
    /**
     * 主机密钥信息类
     */
    public static class HostKeyInfo {
        public String host;
        public int port;
        public String keyType;
        public byte[] keyData;
        public long addTime;
        
        public String getFingerprint() {
            try {
                java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
                byte[] digest = md.digest(keyData);
                StringBuilder sb = new StringBuilder();
                for (byte b : digest) {
                    sb.append(String.format("%02x", b));
                }
                return sb.toString();
            } catch (Exception e) {
                return "";
            }
        }
        
        @Override
        public String toString() {
            return String.format("%s:%d (%s)", host, port, keyType);
        }
    }
}
