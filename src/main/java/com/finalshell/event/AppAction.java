package com.finalshell.event;

import java.util.HashMap;
import java.util.Map;

/**
 * 应用动作定义
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: MysshRoot_Classes_DeepAnalysis.md
 */
public class AppAction {
    
    // 终端动作
    public static final int COPY = 100;
    public static final int PASTE = 110;
    public static final int SELECT_PASTE = 120;
    public static final int POPUP_MENU = 121;
    
    // 标签页动作
    public static final int TAB_CLOSE = 130;
    public static final int TAB_CLOSE_ALL = 131;
    public static final int TAB_CLOSE_OTHER = 132;
    
    // 连接动作
    public static final int CONNECT = 140;
    public static final int DISCONNECT = 141;
    public static final int RECONNECT = 142;
    public static final int DUPLICATE = 143;
    
    // 文件动作
    public static final int FILE_UPLOAD = 200;
    public static final int FILE_DOWNLOAD = 201;
    public static final int FILE_DELETE = 202;
    public static final int FILE_RENAME = 203;
    public static final int FILE_NEW_FOLDER = 204;
    public static final int FILE_REFRESH = 205;
    
    // 编辑动作
    public static final int EDIT_CUT = 300;
    public static final int EDIT_COPY = 301;
    public static final int EDIT_PASTE = 302;
    public static final int EDIT_SELECT_ALL = 303;
    
    // 视图动作
    public static final int VIEW_FULLSCREEN = 400;
    public static final int VIEW_SPLIT = 401;
    
    private static final Map<Integer, AppAction> actionMap = new HashMap<>();
    
    private final int type;
    private final String name;
    private final String description;
    
    static {
        // 初始化动作映射
        register(COPY, "终端-复制", "复制选中文本");
        register(PASTE, "终端-粘贴", "粘贴剪贴板内容");
        register(SELECT_PASTE, "终端-粘贴选中", "粘贴选中文本");
        register(POPUP_MENU, "终端-弹出菜单", "显示右键菜单");
        register(TAB_CLOSE, "标签-关闭", "关闭当前标签");
        register(TAB_CLOSE_ALL, "标签-全部关闭", "关闭所有标签");
        register(CONNECT, "连接-建立", "建立SSH连接");
        register(DISCONNECT, "连接-断开", "断开SSH连接");
        register(FILE_UPLOAD, "文件-上传", "上传文件到服务器");
        register(FILE_DOWNLOAD, "文件-下载", "从服务器下载文件");
    }
    
    public AppAction(int type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
    }
    
    private static void register(int type, String name, String description) {
        actionMap.put(type, new AppAction(type, name, description));
    }
    
    public static AppAction getAction(int type) {
        return actionMap.get(type);
    }
    
    public static String getActionName(int type) {
        AppAction action = actionMap.get(type);
        return action != null ? action.getName() : "未知动作";
    }
    
    public int getType() { return type; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    
    @Override
    public String toString() {
        return name;
    }
}
