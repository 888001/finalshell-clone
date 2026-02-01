package com.finalshell.ui;

import com.finalshell.config.ConfigManager;
import com.finalshell.config.ConnectConfig;
import com.finalshell.ui.VFile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 浮动对话框 - 对齐原版myssh复杂实现
 * 
 * Based on analysis of myssh/ui/FloatDialog.java (227行)
 * 包含复杂的窗口定位、焦点控制、自动隐藏等功能
 */
public class FloatDialog extends JFrame {
    
    private MainWindow mainWindow;
    private Window owner;
    private OpenPanel openPanel;
    private Dimension savedSize;
    private Point savedLocation;
    private boolean menuShowing;
    private boolean keepVisible_Config = false;
    private boolean keepVisible_Focus = false;
    private long lostFocusTime;
    private long lastLostFocusTime;
    private int minWidth = 400;
    private JScrollPane scrollPane;
    
    public FloatDialog(MainWindow mainWindow) {
        super();
        this.mainWindow = mainWindow;
        this.owner = mainWindow;
        
        setUndecorated(true);
        setType(Window.Type.POPUP);
        setAlwaysOnTop(true);
        
        initUI();
        initListeners();
    }
    
    private void initUI() {
        setSize(350, 500);
        setLayout(new BorderLayout());
        
        openPanel = new OpenPanel();
        openPanel.setListener(config -> mainWindow.openConnection(config));
        scrollPane = new JScrollPane(openPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void initListeners() {
        // 组件显示/隐藏监听器 - 对齐原版myssh
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                setFocusableWindowState(true);
                getContentPane().requestFocus();
                SwingUtilities.invokeLater(() -> {
                    if (openPanel != null) {
                        openPanel.requestFocus();
                    }
                });
            }
            
            @Override
            public void componentHidden(ComponentEvent e) {
                setFocusableWindowState(false);
            }
        });
        
        // 窗口焦点监听器 - 对齐原版myssh复杂逻辑
        addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                // 记录获得焦点时间
            }
            
            @Override
            public void windowLostFocus(WindowEvent e) {
                lastLostFocusTime = System.currentTimeMillis();
                if (!keepVisible_Config && !menuShowing) {
                    setVisible(false);
                }
            }
        });
    }
    
    public void showAt(Component component, int x, int y) {
        openPanel.setConfigs(new java.util.ArrayList<>(ConfigManager.getInstance().getConnections().values()));
        Point screenLocation = component.getLocationOnScreen();
        setLocation(screenLocation.x + x, screenLocation.y + y);
        setVisible(true);
        requestFocus();
    }
    
    public void showNearComponent(Component component) {
        openPanel.setConfigs(new java.util.ArrayList<>(ConfigManager.getInstance().getConnections().values()));
        Point location = component.getLocationOnScreen();
        Dimension size = component.getSize();
        setLocation(location.x, location.y + size.height);
        setVisible(true);
        requestFocus();
    }
    
    public OpenPanel getOpenPanel() {
        return openPanel;
    }
    
    public JScrollPane getScrollPane() {
        return scrollPane;
    }
    
    public boolean isMenuShowing() {
        return menuShowing;
    }
    
    public void setMenuShowing(boolean showing) {
        this.menuShowing = showing;
    }
    
    public void showConfig(Object file, Object node) {
        if (file instanceof VFile) {
            VFile vfile = (VFile) file;
            ConnectConfig config = ConfigManager.getInstance().getConnectionById(vfile.getId());
            if (config != null) {
                ConnectionDialog dialog = new ConnectionDialog(mainWindow, config);
                dialog.setVisible(true);
            }
        }
    }
    
    public void savePosition() {
        savedSize = getSize();
        savedLocation = getLocation();
    }
    
    public void restorePosition() {
        if (savedSize != null && savedLocation != null) {
            setSize(savedSize);
            setLocation(savedLocation);
        }
    }
    
    /**
     * 智能尺寸调整 - 对齐原版myssh逻辑
     */
    private void adjustSize() {
        int minHeight = 200;
        int maxHeight = 600;
        int height = getPreferredSize().height;
        int width = getPreferredSize().width + 40;
        
        if (height > maxHeight) {
            height = maxHeight;
        }
        if (height < minHeight) {
            height = minHeight;
        }
        
        if (width < minWidth) {
            width = minWidth;
        }
        
        setSize(new Dimension(width, height));
        savedSize = getSize();
    }
    
    /**
     * 检查是否在指定按钮位置显示
     */
    public boolean isShowingAtButton(JComponent button) {
        Point buttonLocation = button.getLocationOnScreen();
        Point expectedLocation = new Point(buttonLocation.x - 10, 
            buttonLocation.y + button.getHeight() + 3);
        return getLocationOnScreen().equals(expectedLocation);
    }
    
    /**
     * 检查是否位置已移动
     */
    public boolean hasLocationChanged() {
        return savedLocation != null && !getLocationOnScreen().equals(savedLocation);
    }
    
    /**
     * 在指定按钮附近显示 - 对齐原版myssh精确定位逻辑
     */
    public void showNearButton(JComponent button) {
        Point buttonLocation = button.getLocationOnScreen();
        Point showLocation = new Point(buttonLocation.x - 11, 
            buttonLocation.y + button.getHeight() + 3);
        
        setLocation(showLocation);
        openPanel.setFloatDialog(this);
        adjustSize();
        setVisible(true);
        savedLocation = getLocationOnScreen();
        
        // 确保窗口在屏幕范围内
        ensureOnScreen();
    }
    
    /**
     * 确保窗口在屏幕范围内
     */
    private void ensureOnScreen() {
        Rectangle screenBounds = GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getDefaultScreenDevice().getDefaultConfiguration().getBounds();
        Rectangle windowBounds = getBounds();
        
        int x = windowBounds.x;
        int y = windowBounds.y;
        
        if (x + windowBounds.width > screenBounds.width) {
            x = screenBounds.width - windowBounds.width;
        }
        if (y + windowBounds.height > screenBounds.height) {
            y = screenBounds.height - windowBounds.height;
        }
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        
        if (x != windowBounds.x || y != windowBounds.y) {
            setLocation(x, y);
        }
    }
    
    // Getter/Setter methods for alignment with original myssh
    public long getLostFocusTime() {
        return lostFocusTime;
    }
    
    public void setLostFocusTime(long lostFocusTime) {
        this.lostFocusTime = lostFocusTime;
    }
    
    public boolean isKeepVisible_Config() {
        return keepVisible_Config;
    }
    
    public void setKeepVisible_Config(boolean keepVisible_Config) {
        this.keepVisible_Config = keepVisible_Config;
    }
    
    public boolean isKeepVisible_Focus() {
        return keepVisible_Focus;
    }
    
    public void setKeepVisible_Focus(boolean keepVisible_Focus) {
        this.keepVisible_Focus = keepVisible_Focus;
    }
    
    public long getLastLostFocusTime() {
        return lastLostFocusTime;
    }
    
    public void setLastLostFocusTime(long lastLostFocusTime) {
        this.lastLostFocusTime = lastLostFocusTime;
    }
    
    public MainWindow getMainWindow() {
        return mainWindow;
    }
    
    public void setMainWindow(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.owner = mainWindow;
    }
    
    public Window getOwner() {
        return owner;
    }
    
    public void setOwner(Window owner) {
        this.owner = owner;
    }
}
