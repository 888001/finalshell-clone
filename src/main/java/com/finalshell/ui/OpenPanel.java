package com.finalshell.ui;

import com.finalshell.config.ConnectConfig;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * 连接管理面板
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Core_UI_Components_DeepAnalysis.md - OpenPanel
 */
public class OpenPanel extends JPanel {
    
    private JPanel containerPanel;
    private AllPanel allPanel;
    private AllPanel searchPanel;
    private int currentView = -1;
    private JTextField searchField;
    private JToolBar toolbar;
    private OpenPanelListener listener;
    
    public OpenPanel() {
        setLayout(new BorderLayout());
        initComponents();
    }
    
    private void initComponents() {
        // 顶部工具栏
        toolbar = new JToolBar();
        toolbar.setFloatable(false);
        
        // 搜索框
        searchField = new JTextField(15);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { doSearch(); }
            @Override
            public void removeUpdate(DocumentEvent e) { doSearch(); }
            @Override
            public void changedUpdate(DocumentEvent e) { doSearch(); }
        });
        toolbar.add(new JLabel("搜索: "));
        toolbar.add(searchField);
        
        toolbar.addSeparator();
        
        // 视图按钮
        JButton allBtn = new JButton("全部");
        JButton sshBtn = new JButton("SSH");
        JButton rdpBtn = new JButton("RDP");
        
        allBtn.addActionListener(e -> showView(0));
        sshBtn.addActionListener(e -> showView(1));
        rdpBtn.addActionListener(e -> showView(2));
        
        toolbar.add(allBtn);
        toolbar.add(sshBtn);
        toolbar.add(rdpBtn);
        
        add(toolbar, BorderLayout.NORTH);
        
        // 容器面板
        containerPanel = new JPanel(new CardLayout());
        
        allPanel = new AllPanel(this, false);
        searchPanel = new AllPanel(this, true);
        
        containerPanel.add(allPanel, "all");
        containerPanel.add(searchPanel, "search");
        
        add(containerPanel, BorderLayout.CENTER);
        
        showView(0);
    }
    
    private void doSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            CardLayout cl = (CardLayout) containerPanel.getLayout();
            cl.show(containerPanel, "all");
        } else {
            searchPanel.filter(keyword);
            CardLayout cl = (CardLayout) containerPanel.getLayout();
            cl.show(containerPanel, "search");
        }
    }
    
    private void showView(int view) {
        currentView = view;
        // 切换视图模式并刷新显示
        CardLayout cl = (CardLayout) containerPanel.getLayout();
        cl.show(containerPanel, "all");
    }
    
    public void setConfigs(List<ConnectConfig> configs) {
        allPanel.setConfigs(configs);
    }
    
    public void openConnection(ConnectConfig config) {
        if (listener != null) {
            listener.onOpenConnection(config);
        }
    }
    
    public void setListener(OpenPanelListener listener) {
        this.listener = listener;
    }
    
    public interface OpenPanelListener {
        void onOpenConnection(ConnectConfig config);
    }
}
