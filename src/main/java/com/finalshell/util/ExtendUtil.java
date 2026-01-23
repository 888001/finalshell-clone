package com.finalshell.util;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * 扩展工具类
 * 用于动态加载扩展和插件
 */
public class ExtendUtil {
    
    private static List<File> loadedJars = new ArrayList<>();
    private static URLClassLoader extClassLoader;
    
    public static void loadExtension(File jarFile) throws Exception {
        if (jarFile == null || !jarFile.exists()) {
            throw new IllegalArgumentException("JAR file does not exist: " + jarFile);
        }
        
        if (loadedJars.contains(jarFile)) {
            return;
        }
        
        URL url = jarFile.toURI().toURL();
        addURL(url);
        loadedJars.add(jarFile);
    }
    
    public static void loadExtensions(File directory) throws Exception {
        if (directory == null || !directory.isDirectory()) {
            return;
        }
        
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".jar"));
        if (files != null) {
            for (File file : files) {
                loadExtension(file);
            }
        }
    }
    
    private static void addURL(URL url) throws Exception {
        URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        method.invoke(classLoader, url);
    }
    
    public static Class<?> loadClass(String className) throws ClassNotFoundException {
        return Class.forName(className);
    }
    
    public static Object createInstance(String className) throws Exception {
        Class<?> clazz = loadClass(className);
        return clazz.getDeclaredConstructor().newInstance();
    }
    
    public static Object invokeMethod(Object obj, String methodName, Object... args) throws Exception {
        Class<?>[] paramTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            paramTypes[i] = args[i].getClass();
        }
        
        Method method = obj.getClass().getMethod(methodName, paramTypes);
        return method.invoke(obj, args);
    }
    
    public static List<File> getLoadedJars() {
        return new ArrayList<>(loadedJars);
    }
    
    public static boolean isLoaded(File jarFile) {
        return loadedJars.contains(jarFile);
    }
    
    public static void clearLoaded() {
        loadedJars.clear();
    }
}
