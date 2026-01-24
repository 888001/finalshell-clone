package com.finalshell.key;

import java.util.List;
import java.util.ArrayList;

/**
 * KeyManager - Alias for SecretKeyManager for compatibility
 */
public class KeyManager {
    
    private static KeyManager instance;
    
    public static synchronized KeyManager getInstance() {
        if (instance == null) {
            instance = new KeyManager();
        }
        return instance;
    }
    
    private KeyManager() {}
    
    public List<KeyInfo> getAllKeys() {
        List<KeyInfo> result = new ArrayList<>();
        for (SecretKey key : SecretKeyManager.getInstance().getKeys()) {
            KeyInfo info = new KeyInfo();
            info.setId(key.getId());
            info.setName(key.getName());
            info.setType(key.getKeyTypeString());
            info.setPassphrase(key.getPassword());
            result.add(info);
        }
        return result;
    }
    
    public KeyInfo getKey(String id) {
        for (SecretKey key : SecretKeyManager.getInstance().getKeys()) {
            if (key.getId().equals(id)) {
                KeyInfo info = new KeyInfo();
                info.setId(key.getId());
                info.setName(key.getName());
                info.setType(key.getKeyTypeString());
                info.setPassphrase(key.getPassword());
                return info;
            }
        }
        return null;
    }
}
