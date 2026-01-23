package com.finalshell.theme;

/**
 * 自定义皮肤类
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class MySkin {
    
    private String skinPath;
    
    public MySkin() {
        this.skinPath = "resources/skin.xml";
    }
    
    public MySkin(String skinPath) {
        this.skinPath = skinPath;
    }
    
    public String getSkinPath() {
        return skinPath;
    }
    
    public void setSkinPath(String skinPath) {
        this.skinPath = skinPath;
    }
}
