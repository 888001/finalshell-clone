package com.finalshell.network;

import javax.swing.*;
import java.awt.*;

/**
 * 主机检测面板
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class HostDetectPanel extends JPanel {
    
    private JTabbedPane tabbedPane;
    private PingPanel pingPanel;
    private TracertPanel tracertPanel;
    private DetectDetailPanel detailPanel;
    private DetectCommandBar commandBar;
    
    public HostDetectPanel() {
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        commandBar = new DetectCommandBar();
        
        tabbedPane = new JTabbedPane();
        pingPanel = new PingPanel();
        tracertPanel = new TracertPanel();
        
        tabbedPane.addTab("Ping", pingPanel);
        tabbedPane.addTab("Traceroute", tracertPanel);
        
        detailPanel = new DetectDetailPanel();
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabbedPane, detailPanel);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.7);
        
        add(commandBar, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }
    
    public void startPing(String host) {
        pingPanel.start(host);
    }
    
    public void startTracert(String host) {
        tracertPanel.start(host);
    }
    
    public void stop() {
        pingPanel.stop();
        tracertPanel.stop();
    }
    
    public DetectCommandBar getCommandBar() {
        return commandBar;
    }
    
    public PingPanel getPingPanel() {
        return pingPanel;
    }
    
    public TracertPanel getTracertPanel() {
        return tracertPanel;
    }
}
