package com.finalshell.ui;

import com.finalshell.config.AppConfig;
import com.finalshell.config.ConnectConfig;
import com.finalshell.config.ConfigManager;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
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
    private JComboBox<String> filterTypeCombo;
    private JCheckBox closeAfterConnectCheck;
    private JToolBar toolbar;
    private OpenPanelListener listener;
    private List<ConnectConfig> allConfigs = new ArrayList<>();
    private final AppConfig appConfig;

    public OpenPanel() {
        setLayout(new BorderLayout());
        this.appConfig = ConfigManager.getInstance().getAppConfig();
        initComponents();
    }

    private void initComponents() {
        // 顶部工具栏
        toolbar = new JToolBar();
        toolbar.setFloatable(false);

        // 搜索框
        searchField = new JTextField(15);
        searchField.putClientProperty("JTextField.placeholderText", "搜索");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                doSearch();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                doSearch();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                doSearch();
            }
        });
        toolbar.add(new JLabel("搜索: "));
        toolbar.add(searchField);

        toolbar.addSeparator();

        filterTypeCombo = new JComboBox<>(new String[]{"全部", "名称", "主机", "端口", "用户名", "描述"});
        filterTypeCombo.setToolTipText("搜索范围");
        filterTypeCombo.setSelectedIndex(Math.max(0, Math.min(5, appConfig.getOpenPanelSearchFilterType())));
        filterTypeCombo.addActionListener(e -> {
            int idx = filterTypeCombo.getSelectedIndex();
            appConfig.setOpenPanelSearchFilterType(idx);
            ConfigManager.getInstance().saveAppConfig();
            refreshDisplay();
        });
        toolbar.add(new JLabel("范围: "));
        toolbar.add(filterTypeCombo);

        toolbar.addSeparator();

        closeAfterConnectCheck = new JCheckBox("连接后关闭窗口");
        closeAfterConnectCheck.setOpaque(false);
        closeAfterConnectCheck.setSelected(appConfig.isCloseWindowAfterConnect());
        closeAfterConnectCheck.addActionListener(e -> {
            appConfig.setCloseWindowAfterConnect(closeAfterConnectCheck.isSelected());
            ConfigManager.getInstance().saveAppConfig();
        });
        toolbar.add(closeAfterConnectCheck);

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
        refreshDisplay();
    }

    private void showView(int view) {
        currentView = view;
        refreshDisplay();
    }

    public void setConfigs(List<ConnectConfig> configs) {
        this.allConfigs = configs != null ? new ArrayList<>(configs) : new ArrayList<>();
        refreshDisplay();
    }

    private boolean acceptByView(ConnectConfig config) {
        if (currentView == 2) {
            return config != null && config.getType() == ConnectConfig.TYPE_RDP;
        }
        if (currentView == 1) {
            return config != null && config.getType() != ConnectConfig.TYPE_RDP;
        }
        return config != null;
    }

    private void refreshDisplay() {
        List<ConnectConfig> viewList = new ArrayList<>();
        for (ConnectConfig config : allConfigs) {
            if (acceptByView(config)) {
                viewList.add(config);
            }
        }

        allPanel.setConfigs(viewList);
        searchPanel.setConfigs(viewList);

        String keyword = searchField.getText().trim();
        CardLayout cl = (CardLayout) containerPanel.getLayout();
        if (keyword.isEmpty()) {
            cl.show(containerPanel, "all");
        } else {
            searchPanel.filter(keyword, Math.max(0, Math.min(5, appConfig.getOpenPanelSearchFilterType())));
            cl.show(containerPanel, "search");
        }
    }

    public void openConnection(ConnectConfig config) {
        if (listener != null) {
            listener.onOpenConnection(config);
        }

        if (appConfig.isCloseWindowAfterConnect()) {
            Window w = SwingUtilities.getWindowAncestor(this);
            if (w != null && !(w instanceof MainWindow)) {
                w.setVisible(false);
            }
        }
    }

    public void setListener(OpenPanelListener listener) {
        this.listener = listener;
    }

    public interface OpenPanelListener {
        void onOpenConnection(ConnectConfig config);
    }
}
