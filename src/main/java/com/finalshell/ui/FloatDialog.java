package com.finalshell.ui;

import com.finalshell.config.ConfigManager;
import com.finalshell.config.ConnectConfig;
import com.finalshell.ui.VFile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 浮动对话框
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Core_UI_Components_DeepAnalysis.md - FloatDialog
 */
public class FloatDialog extends JFrame {
    
    private MainWindow mainWindow;
    private Window owner;
    private OpenPanel openPanel;
    private Dimension savedSize;
    private Point savedLocation;
    private boolean menuShowing;
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
        addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {}
            
            @Override
            public void windowLostFocus(WindowEvent e) {
                if (!menuShowing) {
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
}
