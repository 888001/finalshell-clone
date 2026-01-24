package com.finalshell.search;

import com.finalshell.ssh.SSHSession;
import com.finalshell.editor.RemoteFileEditor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * 文件搜索面板
 */
public class FileSearchPanel extends JPanel {
    private final SSHSession session;
    private FileSearcher searcher;
    
    // 搜索控件
    private JTextField patternField;
    private JTextField directoryField;
    private JTextField contentField;
    private JComboBox<String> searchTypeCombo;
    private JCheckBox caseSensitiveCheck;
    private JCheckBox regexCheck;
    private JCheckBox includeHiddenCheck;
    private JSpinner maxResultsSpinner;
    
    // 结果表格
    private JTable resultTable;
    private DefaultTableModel tableModel;
    
    // 按钮
    private JButton searchBtn;
    private JButton cancelBtn;
    
    // 状态
    private JLabel statusLabel;
    private JProgressBar progressBar;
    
    public FileSearchPanel(SSHSession session) {
        this.session = session;
        
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        initComponents();
        setupListeners();
        
        if (session.isConnected()) {
            searcher = new FileSearcher(session);
        }
    }
    
    private void initComponents() {
        // 搜索条件面板
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBorder(BorderFactory.createTitledBorder("搜索条件"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // 搜索类型
        gbc.gridx = 0; gbc.gridy = 0;
        searchPanel.add(new JLabel("搜索类型:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        searchTypeCombo = new JComboBox<>(new String[]{"按文件名", "按内容", "按大小", "按时间"});
        searchPanel.add(searchTypeCombo, gbc);
        
        // 目录
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        searchPanel.add(new JLabel("搜索目录:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 1.0;
        directoryField = new JTextField("/", 30);
        searchPanel.add(directoryField, gbc);
        
        // 文件名/模式
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; gbc.weightx = 0;
        searchPanel.add(new JLabel("文件名:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 1.0;
        patternField = new JTextField("*", 30);
        patternField.setToolTipText("支持通配符: * ?");
        searchPanel.add(patternField, gbc);
        
        // 内容搜索
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; gbc.weightx = 0;
        searchPanel.add(new JLabel("搜索内容:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 1.0;
        contentField = new JTextField(30);
        contentField.setEnabled(false);
        searchPanel.add(contentField, gbc);
        
        // 选项
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 3;
        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        caseSensitiveCheck = new JCheckBox("区分大小写");
        regexCheck = new JCheckBox("正则表达式");
        includeHiddenCheck = new JCheckBox("包含隐藏文件");
        optionsPanel.add(caseSensitiveCheck);
        optionsPanel.add(regexCheck);
        optionsPanel.add(includeHiddenCheck);
        optionsPanel.add(new JLabel("最大结果:"));
        maxResultsSpinner = new JSpinner(new SpinnerNumberModel(1000, 10, 10000, 100));
        optionsPanel.add(maxResultsSpinner);
        searchPanel.add(optionsPanel, gbc);
        
        // 按钮
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 3;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchBtn = new JButton("搜索");
        cancelBtn = new JButton("取消");
        cancelBtn.setEnabled(false);
        buttonPanel.add(searchBtn);
        buttonPanel.add(cancelBtn);
        searchPanel.add(buttonPanel, gbc);
        
        add(searchPanel, BorderLayout.NORTH);
        
        // 结果表格
        String[] columns = {"文件名", "路径", "大小", "修改时间", "匹配内容"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        resultTable = new JTable(tableModel);
        resultTable.setAutoCreateRowSorter(true);
        resultTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        resultTable.getColumnModel().getColumn(1).setPreferredWidth(300);
        resultTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        resultTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        resultTable.getColumnModel().getColumn(4).setPreferredWidth(300);
        
        // 目录图标
        resultTable.setDefaultRenderer(Object.class, new FileRenderer());
        
        JScrollPane scrollPane = new JScrollPane(resultTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // 状态栏
        JPanel statusPanel = new JPanel(new BorderLayout(5, 0));
        statusLabel = new JLabel("就绪");
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        progressBar.setPreferredSize(new Dimension(150, 20));
        statusPanel.add(statusLabel, BorderLayout.CENTER);
        statusPanel.add(progressBar, BorderLayout.EAST);
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private void setupListeners() {
        searchTypeCombo.addActionListener(e -> {
            int idx = searchTypeCombo.getSelectedIndex();
            contentField.setEnabled(idx == 1); // 按内容搜索
            patternField.setEnabled(idx != 1); // 非内容搜索时可用
        });
        
        searchBtn.addActionListener(e -> doSearch());
        cancelBtn.addActionListener(e -> cancelSearch());
        
        patternField.addActionListener(e -> doSearch());
        contentField.addActionListener(e -> doSearch());
        
        resultTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openSelectedFile();
                }
            }
        });
    }
    
    private void doSearch() {
        if (searcher == null) return;
        
        String directory = directoryField.getText().trim();
        if (directory.isEmpty()) directory = "/";
        
        int maxResults = (Integer) maxResultsSpinner.getValue();
        boolean caseSensitive = caseSensitiveCheck.isSelected();
        boolean includeHidden = includeHiddenCheck.isSelected();
        
        tableModel.setRowCount(0);
        searchBtn.setEnabled(false);
        cancelBtn.setEnabled(true);
        progressBar.setVisible(true);
        statusLabel.setText("搜索中...");
        
        int searchType = searchTypeCombo.getSelectedIndex();
        
        FileSearcher.SearchCallback callback = new FileSearcher.SearchCallback() {
            @Override
            public void onSearchStart() {}
            
            @Override
            public void onSearchComplete(List<FileSearchResult> results) {
                SwingUtilities.invokeLater(() -> {
                    for (FileSearchResult r : results) {
                        String match = r.getMatchLine() != null ? 
                            "Line " + r.getLineNumber() + ": " + r.getMatchLine() : "";
                        tableModel.addRow(new Object[]{
                            r.getName(),
                            r.getPath(),
                            r.getSizeDisplay(),
                            r.getModifyTime(),
                            match
                        });
                    }
                    statusLabel.setText("找到 " + results.size() + " 个结果");
                    searchComplete();
                });
            }
            
            @Override
            public void onSearchError(String error) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("错误: " + error);
                    searchComplete();
                });
            }
        };
        
        switch (searchType) {
            case 0: // 按文件名
                String pattern = patternField.getText().trim();
                if (pattern.isEmpty()) pattern = "*";
                searcher.searchByName(directory, pattern, caseSensitive, includeHidden, maxResults, callback);
                break;
                
            case 1: // 按内容
                String content = contentField.getText().trim();
                if (content.isEmpty()) {
                    statusLabel.setText("请输入搜索内容");
                    searchComplete();
                    return;
                }
                String filePattern = patternField.getText().trim();
                searcher.searchByContent(directory, content, filePattern, caseSensitive, 
                    regexCheck.isSelected(), maxResults, callback);
                break;
                
            case 2: // 按大小
                // 简化: 搜索大于1MB的文件
                searcher.searchBySize(directory, 1024 * 1024, 0, maxResults, callback);
                break;
                
            case 3: // 按时间
                // 简化: 搜索最近7天修改的文件
                searcher.searchByTime(directory, 7, true, maxResults, callback);
                break;
        }
    }
    
    private void cancelSearch() {
        if (searcher != null) {
            searcher.cancel();
        }
        statusLabel.setText("已取消");
        searchComplete();
    }
    
    private void searchComplete() {
        searchBtn.setEnabled(true);
        cancelBtn.setEnabled(false);
        progressBar.setVisible(false);
    }
    
    private void openSelectedFile() {
        int row = resultTable.getSelectedRow();
        if (row < 0) return;
        
        int modelRow = resultTable.convertRowIndexToModel(row);
        String path = (String) tableModel.getValueAt(modelRow, 1);
        
        Window owner = SwingUtilities.getWindowAncestor(this);
        RemoteFileEditor.open(owner, session, path);
    }
    
    public void cleanup() {
        if (searcher != null) {
            searcher.shutdown();
        }
    }
    
    /**
     * 文件渲染器
     */
    private static class FileRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            return this;
        }
    }
}
