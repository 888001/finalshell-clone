package com.finalshell.i18n;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.*;

/**
 * 国际化工具类
 */
public class I18n {
    private static final Logger logger = LoggerFactory.getLogger(I18n.class);
    
    private static final String BUNDLE_NAME = "messages";
    
    private static I18n instance;
    
    private Locale currentLocale;
    private ResourceBundle bundle;
    private final List<LocaleChangeListener> listeners = new ArrayList<>();
    
    // 支持的语言
    public static final Locale CHINESE = Locale.SIMPLIFIED_CHINESE;
    public static final Locale ENGLISH = Locale.ENGLISH;
    public static final Locale JAPANESE = Locale.JAPANESE;
    
    private static final Locale[] SUPPORTED_LOCALES = {
        CHINESE, ENGLISH, JAPANESE
    };
    
    private I18n() {
        // 默认使用系统语言
        currentLocale = Locale.getDefault();
        loadBundle();
    }
    
    public static synchronized I18n getInstance() {
        if (instance == null) {
            instance = new I18n();
        }
        return instance;
    }
    
    /**
     * 获取翻译文本
     */
    public static String get(String key) {
        return getInstance().getString(key);
    }
    
    /**
     * 获取带参数的翻译文本
     */
    public static String get(String key, Object... args) {
        return getInstance().getString(key, args);
    }
    
    /**
     * 获取翻译文本
     */
    public String getString(String key) {
        try {
            if (bundle != null && bundle.containsKey(key)) {
                return bundle.getString(key);
            }
        } catch (Exception e) {
            logger.debug("未找到翻译: {}", key);
        }
        return key;
    }
    
    /**
     * 获取带参数的翻译文本
     */
    public String getString(String key, Object... args) {
        String pattern = getString(key);
        try {
            return MessageFormat.format(pattern, args);
        } catch (Exception e) {
            return pattern;
        }
    }
    
    /**
     * 设置当前语言
     */
    public void setLocale(Locale locale) {
        if (locale != null && !locale.equals(currentLocale)) {
            currentLocale = locale;
            loadBundle();
            notifyListeners();
        }
    }
    
    /**
     * 获取当前语言
     */
    public Locale getLocale() {
        return currentLocale;
    }
    
    /**
     * 获取支持的语言列表
     */
    public Locale[] getSupportedLocales() {
        return SUPPORTED_LOCALES.clone();
    }
    
    /**
     * 获取语言显示名称
     */
    public String getLocaleDisplayName(Locale locale) {
        switch (locale.getLanguage()) {
            case "zh": return "简体中文";
            case "en": return "English";
            case "ja": return "日本語";
            default: return locale.getDisplayName(locale);
        }
    }
    
    private void loadBundle() {
        try {
            bundle = ResourceBundle.getBundle(BUNDLE_NAME, currentLocale, 
                new UTF8Control());
            logger.info("加载语言包: {}", currentLocale);
        } catch (MissingResourceException e) {
            logger.warn("未找到语言包: {}, 使用默认", currentLocale);
            try {
                bundle = ResourceBundle.getBundle(BUNDLE_NAME, Locale.ENGLISH,
                    new UTF8Control());
            } catch (MissingResourceException e2) {
                logger.error("无法加载默认语言包");
                bundle = null;
            }
        }
    }
    
    public void addListener(LocaleChangeListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(LocaleChangeListener listener) {
        listeners.remove(listener);
    }
    
    private void notifyListeners() {
        for (LocaleChangeListener l : listeners) {
            l.onLocaleChanged(currentLocale);
        }
    }
    
    /**
     * 语言变更监听器
     */
    public interface LocaleChangeListener {
        void onLocaleChanged(Locale newLocale);
    }
    
    /**
     * UTF-8 资源包控制器
     */
    private static class UTF8Control extends ResourceBundle.Control {
        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format,
                ClassLoader loader, boolean reload) throws java.io.IOException {
            
            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, "properties");
            
            java.io.InputStream stream = loader.getResourceAsStream(resourceName);
            if (stream != null) {
                try (java.io.InputStreamReader reader = 
                        new java.io.InputStreamReader(stream, java.nio.charset.StandardCharsets.UTF_8)) {
                    return new PropertyResourceBundle(reader);
                }
            }
            return null;
        }
    }
}
