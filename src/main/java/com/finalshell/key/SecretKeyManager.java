package com.finalshell.key;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * SSH密钥管理器
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: SecretKey_Manager_DeepAnalysis.md
 */
public class SecretKeyManager {
    
    private static final Logger logger = LoggerFactory.getLogger(SecretKeyManager.class);
    
    private static SecretKeyManager instance;
    
    private final Path keyDir;
    private final Path keyFile;
    private final List<SecretKey> keys = new ArrayList<>();
    private final List<KeyChangeListener> listeners = new ArrayList<>();
    
    private SecretKeyManager() {
        String userHome = System.getProperty("user.home");
        String os = System.getProperty("os.name").toLowerCase();
        
        Path configDir;
        if (os.contains("win")) {
            configDir = Paths.get(System.getenv("APPDATA"), "finalshell");
        } else if (os.contains("mac")) {
            configDir = Paths.get(userHome, "Library/Application Support/finalshell");
        } else {
            configDir = Paths.get(userHome, ".finalshell");
        }
        
        this.keyDir = configDir.resolve("keys");
        this.keyFile = configDir.resolve("secretkeys.json");
        
        try {
            Files.createDirectories(keyDir);
        } catch (IOException e) {
            logger.error("Failed to create key directory", e);
        }
        
        loadKeys();
    }
    
    public static synchronized SecretKeyManager getInstance() {
        if (instance == null) {
            instance = new SecretKeyManager();
        }
        return instance;
    }
    
    /**
     * 加载密钥列表
     */
    private void loadKeys() {
        keys.clear();
        
        if (Files.exists(keyFile)) {
            try {
                String json = new String(Files.readAllBytes(keyFile), StandardCharsets.UTF_8);
                List<SecretKey> loaded = JSON.parseArray(json, SecretKey.class);
                if (loaded != null) {
                    keys.addAll(loaded);
                }
                logger.info("Loaded {} secret keys", keys.size());
            } catch (IOException e) {
                logger.error("Failed to load secret keys", e);
            }
        }
    }
    
    /**
     * 保存密钥列表
     */
    private void saveKeys() {
        try {
            String json = JSON.toJSONString(keys, SerializerFeature.PrettyFormat);
            Files.write(keyFile, json.getBytes(StandardCharsets.UTF_8));
            logger.info("Saved {} secret keys", keys.size());
        } catch (IOException e) {
            logger.error("Failed to save secret keys", e);
        }
    }
    
    /**
     * 获取所有密钥
     */
    public List<SecretKey> getKeys() {
        return new ArrayList<>(keys);
    }
    
    /**
     * 根据ID获取密钥
     */
    public SecretKey getKeyById(String id) {
        for (SecretKey key : keys) {
            if (key.getId().equals(id)) {
                return key;
            }
        }
        return null;
    }
    
    /**
     * 根据名称获取密钥
     */
    public SecretKey getKeyByName(String name) {
        for (SecretKey key : keys) {
            if (key.getName().equals(name)) {
                return key;
            }
        }
        return null;
    }
    
    /**
     * 添加密钥
     */
    public void addKey(SecretKey key) {
        keys.add(key);
        saveKeys();
        fireKeyChanged();
    }
    
    /**
     * 删除密钥
     */
    public void removeKey(String id) {
        keys.removeIf(k -> k.getId().equals(id));
        saveKeys();
        fireKeyChanged();
    }
    
    /**
     * 更新密钥
     */
    public void updateKey(SecretKey key) {
        for (int i = 0; i < keys.size(); i++) {
            if (keys.get(i).getId().equals(key.getId())) {
                keys.set(i, key);
                break;
            }
        }
        saveKeys();
        fireKeyChanged();
    }
    
    /**
     * 从文件导入密钥
     */
    public SecretKey importKey(File file, String name, String password) throws IOException, JSchException {
        byte[] keyData = Files.readAllBytes(file.toPath());
        
        // 验证密钥格式
        KeyPair kpair = KeyPair.load(new JSch(), keyData, null);
        if (kpair == null) {
            throw new JSchException("Invalid key format");
        }
        
        SecretKey key = new SecretKey();
        key.setName(name != null ? name : file.getName());
        key.setKeyData(keyData);
        key.setPassword(password);
        
        addKey(key);
        return key;
    }
    
    /**
     * 导出密钥到文件
     */
    public void exportKey(String id, File file) throws IOException {
        SecretKey key = getKeyById(id);
        if (key != null && key.getKeyData() != null) {
            Files.write(file.toPath(), key.getKeyData());
        }
    }
    
    /**
     * 生成新密钥对
     */
    public SecretKey generateKey(String name, int type, int keySize, String password) throws JSchException {
        JSch jsch = new JSch();
        KeyPair kpair = KeyPair.genKeyPair(jsch, type, keySize);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (password != null && !password.isEmpty()) {
            kpair.writePrivateKey(baos, password.getBytes(StandardCharsets.UTF_8));
        } else {
            kpair.writePrivateKey(baos);
        }
        
        SecretKey key = new SecretKey();
        key.setName(name);
        key.setKeyData(baos.toByteArray());
        key.setPassword(password);
        key.setKeyType(type);
        
        addKey(key);
        return key;
    }
    
    /**
     * 将密钥添加到JSch
     */
    public void addIdentity(JSch jsch, String keyId) throws JSchException {
        SecretKey key = getKeyById(keyId);
        if (key != null && key.getKeyData() != null) {
            byte[] passphrase = key.getPassword() != null ? 
                key.getPassword().getBytes(StandardCharsets.UTF_8) : null;
            jsch.addIdentity(key.getName(), key.getKeyData(), null, passphrase);
            key.setUsed(true);
        }
    }
    
    /**
     * 添加密钥变更监听器
     */
    public void addKeyChangeListener(KeyChangeListener listener) {
        listeners.add(listener);
    }
    
    /**
     * 移除密钥变更监听器
     */
    public void removeKeyChangeListener(KeyChangeListener listener) {
        listeners.remove(listener);
    }
    
    private void fireKeyChanged() {
        for (KeyChangeListener listener : listeners) {
            listener.onKeyChanged();
        }
    }
    
    /**
     * 密钥变更监听器接口
     */
    public interface KeyChangeListener {
        void onKeyChanged();
    }
}
