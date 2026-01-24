package com.finalshell.terminal;

import com.finalshell.config.AppConfig;
import com.finalshell.config.ConfigManager;
import com.finalshell.config.ConnectConfig;
import com.finalshell.util.ResourceLoader;
import com.jediterm.terminal.TerminalColor;
import com.jediterm.terminal.TextStyle;
import com.jediterm.terminal.emulator.ColorPalette;
import com.jediterm.terminal.ui.settings.DefaultSettingsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

/**
 * Terminal Settings Provider - Configures JediTerm appearance
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: UI_Parameters_Reference.md - Terminal Parameters
 */
public class TerminalSettingsProvider extends DefaultSettingsProvider {
    
    private static final Logger logger = LoggerFactory.getLogger(TerminalSettingsProvider.class);
    
    private final ConnectConfig connectConfig;
    private final AppConfig appConfig;
    private ResourceLoader.TerminalTheme theme;
    
    public TerminalSettingsProvider(ConnectConfig config) {
        this.connectConfig = config;
        this.appConfig = ConfigManager.getInstance().getAppConfig();
        loadTheme();
    }
    
    private void loadTheme() {
        String themeName = appConfig.getTerminalTheme();
        if (themeName != null && !themeName.isEmpty()) {
            theme = ResourceLoader.getInstance().getTheme(themeName);
        }
        if (theme == null) {
            theme = ResourceLoader.getInstance().getDefaultTheme();
        }
    }
    
    @Override
    public Font getTerminalFont() {
        String fontName = appConfig.getTerminalFont();
        int fontSize = appConfig.getTerminalFontSize();
        
        if (fontName == null || fontName.isEmpty()) {
            fontName = "Consolas";
        }
        if (fontSize <= 0) {
            fontSize = 14;
        }
        
        return new Font(fontName, Font.PLAIN, fontSize);
    }
    
    @Override
    public float getTerminalFontSize() {
        int fontSize = appConfig.getTerminalFontSize();
        return fontSize > 0 ? fontSize : 14;
    }
    
    @Override
    public TextStyle getDefaultStyle() {
        return super.getDefaultStyle();
    }
    
    @Override
    public ColorPalette getTerminalColorPalette() {
        return super.getTerminalColorPalette();
    }
    
    @Override
    public boolean useAntialiasing() {
        return true;
    }
    
    @Override
    public boolean ambiguousCharsAreDoubleWidth() {
        // Important for Chinese character display
        return true;
    }
    
    @Override
    public boolean scrollToBottomOnTyping() {
        return true;
    }
    
    @Override
    public boolean copyOnSelect() {
        return appConfig.isCopyOnSelect();
    }
    
    @Override
    public boolean pasteOnMiddleMouseClick() {
        return true;
    }
    
    @Override
    public boolean emulateX11CopyPaste() {
        return false;
    }
    
    @Override
    public boolean useInverseSelectionColor() {
        return true;
    }
    
    @Override
    public int getBufferMaxLinesCount() {
        int scrollback = appConfig.getScrollbackLines();
        return scrollback > 0 ? scrollback : 10000;
    }
    
    @Override
    public boolean audibleBell() {
        return appConfig.isAudibleBell();
    }
    
    @Override
    public boolean enableMouseReporting() {
        return true;
    }
    
    @Override
    public int caretBlinkingMs() {
        return 500;
    }
    
    private Color parseColor(String hex, Color defaultColor) {
        if (hex == null || hex.isEmpty()) {
            return defaultColor;
        }
        try {
            if (hex.startsWith("#")) {
                hex = hex.substring(1);
            }
            return new Color(Integer.parseInt(hex, 16));
        } catch (Exception e) {
            return defaultColor;
        }
    }
}
