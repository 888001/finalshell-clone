package com.finalshell.plugin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.jar.*;

/**
 * 插件管理器
 */
public class PluginManager {
    private static final Logger logger = LoggerFactory.getLogger(PluginManager.class);
    
    private static final String PLUGIN_DIR = "plugins";
    private static final String PLUGIN_CONFIG = "plugin.json";
    private static final String ENABLED_FILE = "enabled.json";
    
    private static PluginManager instance;
    
    private final Path pluginPath;
    private final Map<String, PluginInfo> plugins = new LinkedHashMap<>();
    private final Set<String> enabledPlugins = new HashSet<>();
    private PluginContext context;
    
    private PluginManager() {
        String userHome = System.getProperty("user.home");
        this.pluginPath = Paths.get(userHome, ".finalshell", PLUGIN_DIR);
        
        try {
            Files.createDirectories(pluginPath);
        } catch (IOException e) {
            logger.error("创建插件目录失败", e);
        }
        
        loadEnabledList();
    }
    
    public static synchronized PluginManager getInstance() {
        if (instance == null) {
            instance = new PluginManager();
        }
        return instance;
    }
    
    /**
     * 初始化插件管理器
     */
    public void init(PluginContext context) {
        this.context = context;
        scanPlugins();
        loadEnabledPlugins();
    }
    
    /**
     * 扫描插件目录
     */
    public void scanPlugins() {
        plugins.clear();
        
        try {
            Files.list(pluginPath)
                .filter(p -> p.toString().endsWith(".jar"))
                .forEach(this::loadPluginInfo);
        } catch (IOException e) {
            logger.error("扫描插件目录失败", e);
        }
        
        logger.info("发现 {} 个插件", plugins.size());
    }
    
    private void loadPluginInfo(Path jarPath) {
        try (JarFile jarFile = new JarFile(jarPath.toFile())) {
            JarEntry configEntry = jarFile.getJarEntry(PLUGIN_CONFIG);
            if (configEntry == null) {
                logger.warn("插件缺少配置文件: {}", jarPath.getFileName());
                return;
            }
            
            try (InputStream is = jarFile.getInputStream(configEntry)) {
                String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                JSONObject obj = JSON.parseObject(json);
                
                PluginInfo info = new PluginInfo();
                info.setId(obj.getString("id"));
                info.setName(obj.getString("name"));
                info.setVersion(obj.getString("version"));
                info.setAuthor(obj.getString("author"));
                info.setDescription(obj.getString("description"));
                info.setMainClass(obj.getString("mainClass"));
                info.setJarFile(jarPath.toFile());
                info.setEnabled(enabledPlugins.contains(info.getId()));
                
                plugins.put(info.getId(), info);
                logger.info("加载插件信息: {} v{}", info.getName(), info.getVersion());
            }
            
        } catch (Exception e) {
            logger.error("加载插件信息失败: {}", jarPath.getFileName(), e);
        }
    }
    
    /**
     * 加载已启用的插件
     */
    private void loadEnabledPlugins() {
        for (PluginInfo info : plugins.values()) {
            if (info.isEnabled()) {
                loadPlugin(info);
            }
        }
    }
    
    /**
     * 加载插件
     */
    public boolean loadPlugin(PluginInfo info) {
        if (info.getInstance() != null) {
            return true; // 已加载
        }
        
        try {
            URL[] urls = {info.getJarFile().toURI().toURL()};
            URLClassLoader classLoader = new URLClassLoader(urls, getClass().getClassLoader());
            
            Class<?> pluginClass = classLoader.loadClass(info.getMainClass());
            Plugin plugin = (Plugin) pluginClass.getDeclaredConstructor().newInstance();
            
            info.setInstance(plugin);
            plugin.init(context);
            plugin.enable();
            
            logger.info("插件已加载: {}", info.getName());
            return true;
            
        } catch (Exception e) {
            logger.error("加载插件失败: {}", info.getName(), e);
            return false;
        }
    }
    
    /**
     * 卸载插件
     */
    public void unloadPlugin(PluginInfo info) {
        Plugin plugin = info.getInstance();
        if (plugin != null) {
            try {
                plugin.disable();
                plugin.destroy();
            } catch (Exception e) {
                logger.error("卸载插件失败: {}", info.getName(), e);
            }
            info.setInstance(null);
        }
    }
    
    /**
     * 启用插件
     */
    public boolean enablePlugin(String pluginId) {
        PluginInfo info = plugins.get(pluginId);
        if (info == null) return false;
        
        if (loadPlugin(info)) {
            info.setEnabled(true);
            enabledPlugins.add(pluginId);
            saveEnabledList();
            return true;
        }
        return false;
    }
    
    /**
     * 禁用插件
     */
    public void disablePlugin(String pluginId) {
        PluginInfo info = plugins.get(pluginId);
        if (info != null) {
            unloadPlugin(info);
            info.setEnabled(false);
            enabledPlugins.remove(pluginId);
            saveEnabledList();
        }
    }
    
    /**
     * 安装插件
     */
    public boolean installPlugin(File jarFile) {
        try {
            Path target = pluginPath.resolve(jarFile.getName());
            Files.copy(jarFile.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
            
            loadPluginInfo(target);
            return true;
            
        } catch (IOException e) {
            logger.error("安装插件失败", e);
            return false;
        }
    }
    
    /**
     * 卸载插件文件
     */
    public boolean uninstallPlugin(String pluginId) {
        PluginInfo info = plugins.get(pluginId);
        if (info == null) return false;
        
        disablePlugin(pluginId);
        
        try {
            Files.deleteIfExists(info.getJarFile().toPath());
            plugins.remove(pluginId);
            return true;
        } catch (IOException e) {
            logger.error("删除插件文件失败", e);
            return false;
        }
    }
    
    private void loadEnabledList() {
        Path path = pluginPath.resolve(ENABLED_FILE);
        if (Files.exists(path)) {
            try {
                String json = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
                List<String> list = JSON.parseArray(json, String.class);
                enabledPlugins.addAll(list);
            } catch (Exception e) {
                logger.error("加载启用列表失败", e);
            }
        }
    }
    
    private void saveEnabledList() {
        try {
            Path path = pluginPath.resolve(ENABLED_FILE);
            String json = JSON.toJSONString(new ArrayList<>(enabledPlugins));
            Files.write(path, json.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.error("保存启用列表失败", e);
        }
    }
    
    /**
     * 获取所有插件
     */
    public Collection<PluginInfo> getAllPlugins() {
        return plugins.values();
    }
    
    /**
     * 获取插件
     */
    public PluginInfo getPlugin(String pluginId) {
        return plugins.get(pluginId);
    }
    
    /**
     * 获取插件目录
     */
    public Path getPluginPath() {
        return pluginPath;
    }
    
    /**
     * 关闭所有插件
     */
    public void shutdown() {
        for (PluginInfo info : plugins.values()) {
            if (info.getInstance() != null) {
                unloadPlugin(info);
            }
        }
    }
}
