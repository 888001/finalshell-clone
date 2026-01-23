package com.finalshell.monitor;

import javax.swing.*;
import java.awt.*;

/**
 * Shell详情面板
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class ShellDetailPanel extends JPanel {
    
    private JTabbedPane tabbedPane;
    private JTextArea systemInfoArea;
    private JTextArea processArea;
    private JTextArea networkArea;
    private JTextArea diskArea;
    
    public ShellDetailPanel() {
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout());
        
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        
        systemInfoArea = createTextArea();
        tabbedPane.addTab("系统信息", new JScrollPane(systemInfoArea));
        
        processArea = createTextArea();
        tabbedPane.addTab("进程列表", new JScrollPane(processArea));
        
        networkArea = createTextArea();
        tabbedPane.addTab("网络连接", new JScrollPane(networkArea));
        
        diskArea = createTextArea();
        tabbedPane.addTab("磁盘信息", new JScrollPane(diskArea));
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JTextArea createTextArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        return area;
    }
    
    public void setSystemInfo(String info) {
        systemInfoArea.setText(info);
        systemInfoArea.setCaretPosition(0);
    }
    
    public void setProcessInfo(String info) {
        processArea.setText(info);
        processArea.setCaretPosition(0);
    }
    
    public void setNetworkInfo(String info) {
        networkArea.setText(info);
        networkArea.setCaretPosition(0);
    }
    
    public void setDiskInfo(String info) {
        diskArea.setText(info);
        diskArea.setCaretPosition(0);
    }
    
    public void appendSystemInfo(String info) {
        systemInfoArea.append(info + "\n");
    }
    
    public void appendProcessInfo(String info) {
        processArea.append(info + "\n");
    }
    
    public void appendNetworkInfo(String info) {
        networkArea.append(info + "\n");
    }
    
    public void appendDiskInfo(String info) {
        diskArea.append(info + "\n");
    }
    
    public void clear() {
        systemInfoArea.setText("");
        processArea.setText("");
        networkArea.setText("");
        diskArea.setText("");
    }
}
