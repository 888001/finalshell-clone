package com.finalshell.ftp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

/**
 * FTP Panel - FTP file browser with dual pane view
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class FTPPanel extends JPanel implements FTPSession.FTPListener {
    
    private static final Logger logger = LoggerFactory.getLogger(FTPPanel.class);
    
    private final FTPConfig config;
    private FTPSession session;
    
    // Local file browser
    private JTable localTable;
    private DefaultTableModel localModel;
    private JTextField localPathField;
    private File localCurrentDir;
    
    // Remote file browser
    private JTable remoteTable;
    private DefaultTableModel remoteModel;
    private JTextField remotePathField;
    
    // Transfer queue
    private JTable queueTable;
    private DefaultTableModel queueModel;
    
    // Status
    private JLabel statusLabel;
    private JProgressBar progressBar;
    
    public FTPPanel(FTPConfig config) {
        this.config = config;
        this.localCurrentDir = new File(config.getLocalDir());
        
        initComponents();
        initLayout();
        initListeners();
        
        refreshLocalFiles();
    }
    
    private void initComponents() {
        // Local panel
        String[] columns = {"名称", "大小", "日期"};
        
        localModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        localTable = new JTable(localModel);
        localTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        localTable.setAutoCreateRowSorter(true);
        localTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        localTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        
        localPathField = new JTextField();
        localPathField.setEditable(false);
        
        // Remote panel
        remoteModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        remoteTable = new JTable(remoteModel);
        remoteTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        remoteTable.setAutoCreateRowSorter(true);
        remoteTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        remoteTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        
        remotePathField = new JTextField();
        remotePathField.setEditable(false);
        
        // Queue panel
        String[] queueColumns = {"文件", "大小", "方向", "状态", "进度"};
        queueModel = new DefaultTableModel(queueColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        queueTable = new JTable(queueModel);
        
        // Status
        statusLabel = new JLabel("未连接");
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
    }
    
    private void initLayout() {
        setLayout(new BorderLayout());
        
        // Toolbar
        JToolBar toolbar = createToolbar();
        add(toolbar, BorderLayout.NORTH);
        
        // Main split pane (local | remote)
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setResizeWeight(0.5);
        
        // Local panel
        JPanel localPanel = new JPanel(new BorderLayout());
        JPanel localTopPanel = new JPanel(new BorderLayout());
        localTopPanel.add(new JLabel(" 本地: "), BorderLayout.WEST);
        localTopPanel.add(localPathField, BorderLayout.CENTER);
        localTopPanel.add(createLocalButtons(), BorderLayout.EAST);
        localPanel.add(localTopPanel, BorderLayout.NORTH);
        localPanel.add(new JScrollPane(localTable), BorderLayout.CENTER);
        
        // Remote panel
        JPanel remotePanel = new JPanel(new BorderLayout());
        JPanel remoteTopPanel = new JPanel(new BorderLayout());
        remoteTopPanel.add(new JLabel(" 远程: "), BorderLayout.WEST);
        remoteTopPanel.add(remotePathField, BorderLayout.CENTER);
        remoteTopPanel.add(createRemoteButtons(), BorderLayout.EAST);
        remotePanel.add(remoteTopPanel, BorderLayout.NORTH);
        remotePanel.add(new JScrollPane(remoteTable), BorderLayout.CENTER);
        
        mainSplit.setLeftComponent(localPanel);
        mainSplit.setRightComponent(remotePanel);
        
        // Bottom split (main | queue)
        JSplitPane bottomSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        bottomSplit.setResizeWeight(0.7);
        bottomSplit.setTopComponent(mainSplit);
        
        JPanel queuePanel = new JPanel(new BorderLayout());
        queuePanel.add(new JLabel(" 传输队列"), BorderLayout.NORTH);
        queuePanel.add(new JScrollPane(queueTable), BorderLayout.CENTER);
        bottomSplit.setBottomComponent(queuePanel);
        
        add(bottomSplit, BorderLayout.CENTER);
        
        // Status bar
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(progressBar, BorderLayout.EAST);
        progressBar.setPreferredSize(new Dimension(200, 20));
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private JToolBar createToolbar() {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        
        JButton connectBtn = new JButton("连接");
        connectBtn.addActionListener(e -> connect());
        
        JButton disconnectBtn = new JButton("断开");
        disconnectBtn.addActionListener(e -> disconnect());
        
        JButton uploadBtn = new JButton("上传 →");
        uploadBtn.addActionListener(e -> uploadSelected());
        
        JButton downloadBtn = new JButton("← 下载");
        downloadBtn.addActionListener(e -> downloadSelected());
        
        toolbar.add(connectBtn);
        toolbar.add(disconnectBtn);
        toolbar.addSeparator();
        toolbar.add(uploadBtn);
        toolbar.add(downloadBtn);
        toolbar.addSeparator();
        toolbar.add(new JLabel(" " + config.getHost()));
        
        return toolbar;
    }
    
    private JPanel createLocalButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
        
        JButton upBtn = new JButton("↑");
        upBtn.setToolTipText("上级目录");
        upBtn.addActionListener(e -> localGoUp());
        
        JButton refreshBtn = new JButton("刷新");
        refreshBtn.addActionListener(e -> refreshLocalFiles());
        
        panel.add(upBtn);
        panel.add(refreshBtn);
        return panel;
    }
    
    private JPanel createRemoteButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
        
        JButton upBtn = new JButton("↑");
        upBtn.setToolTipText("上级目录");
        upBtn.addActionListener(e -> remoteGoUp());
        
        JButton refreshBtn = new JButton("刷新");
        refreshBtn.addActionListener(e -> refreshRemoteFiles());
        
        JButton mkdirBtn = new JButton("新建");
        mkdirBtn.addActionListener(e -> createRemoteDir());
        
        JButton deleteBtn = new JButton("删除");
        deleteBtn.addActionListener(e -> deleteRemoteSelected());
        
        panel.add(upBtn);
        panel.add(refreshBtn);
        panel.add(mkdirBtn);
        panel.add(deleteBtn);
        return panel;
    }
    
    private void initListeners() {
        // Local double-click
        localTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = localTable.getSelectedRow();
                    if (row >= 0) {
                        row = localTable.convertRowIndexToModel(row);
                        String name = (String) localModel.getValueAt(row, 0);
                        File file = new File(localCurrentDir, name.replace("[DIR] ", ""));
                        if (file.isDirectory()) {
                            localCurrentDir = file;
                            refreshLocalFiles();
                        }
                    }
                }
            }
        });
        
        // Remote double-click
        remoteTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && session != null && session.isConnected()) {
                    int row = remoteTable.getSelectedRow();
                    if (row >= 0) {
                        row = remoteTable.convertRowIndexToModel(row);
                        String name = (String) remoteModel.getValueAt(row, 0);
                        if (name.startsWith("[DIR] ")) {
                            String dirName = name.replace("[DIR] ", "");
                            changeRemoteDir(dirName);
                        }
                    }
                }
            }
        });
    }
    
    /**
     * Connect to FTP server
     */
    public void connect() {
        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                publish("正在连接...");
                session = new FTPSession(config);
                session.addListener(FTPPanel.this);
                session.connect();
                return null;
            }
            
            @Override
            protected void process(List<String> chunks) {
                statusLabel.setText(chunks.get(chunks.size() - 1));
            }
            
            @Override
            protected void done() {
                try {
                    get();
                    statusLabel.setText("已连接: " + config.getHost());
                    refreshRemoteFiles();
                } catch (Exception e) {
                    logger.error("FTP connection failed", e);
                    statusLabel.setText("连接失败: " + e.getMessage());
                    JOptionPane.showMessageDialog(FTPPanel.this,
                        e.getMessage(), "连接失败", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
    
    /**
     * Disconnect from FTP server
     */
    public void disconnect() {
        if (session != null) {
            session.disconnect();
            session = null;
        }
        remoteModel.setRowCount(0);
        remotePathField.setText("");
        statusLabel.setText("已断开");
    }
    
    /**
     * Refresh local file list
     */
    private void refreshLocalFiles() {
        localModel.setRowCount(0);
        localPathField.setText(localCurrentDir.getAbsolutePath());
        
        File[] files = localCurrentDir.listFiles();
        if (files == null) return;
        
        Arrays.sort(files, (a, b) -> {
            if (a.isDirectory() != b.isDirectory()) {
                return a.isDirectory() ? -1 : 1;
            }
            return a.getName().compareToIgnoreCase(b.getName());
        });
        
        for (File file : files) {
            String name = file.isDirectory() ? "[DIR] " + file.getName() : file.getName();
            String size = file.isDirectory() ? "" : formatSize(file.length());
            String date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm")
                .format(new java.util.Date(file.lastModified()));
            localModel.addRow(new Object[]{name, size, date});
        }
    }
    
    /**
     * Refresh remote file list
     */
    private void refreshRemoteFiles() {
        if (session == null || !session.isConnected()) return;
        
        new SwingWorker<List<FTPFile>, Void>() {
            @Override
            protected List<FTPFile> doInBackground() throws Exception {
                String pwd = session.getCurrentDirectory();
                remotePathField.setText(pwd);
                return session.listFiles(pwd);
            }
            
            @Override
            protected void done() {
                try {
                    List<FTPFile> files = get();
                    remoteModel.setRowCount(0);
                    
                    // Sort: directories first
                    files.sort((a, b) -> {
                        if (a.isDirectory() != b.isDirectory()) {
                            return a.isDirectory() ? -1 : 1;
                        }
                        return a.getName().compareToIgnoreCase(b.getName());
                    });
                    
                    for (FTPFile file : files) {
                        if (".".equals(file.getName()) || "..".equals(file.getName())) continue;
                        
                        String name = file.isDirectory() ? "[DIR] " + file.getName() : file.getName();
                        String size = file.isDirectory() ? "" : formatSize(file.getSize());
                        remoteModel.addRow(new Object[]{name, size, file.getDate()});
                    }
                } catch (Exception e) {
                    logger.error("Failed to list files", e);
                    statusLabel.setText("列出文件失败: " + e.getMessage());
                }
            }
        }.execute();
    }
    
    private void localGoUp() {
        File parent = localCurrentDir.getParentFile();
        if (parent != null) {
            localCurrentDir = parent;
            refreshLocalFiles();
        }
    }
    
    private void remoteGoUp() {
        if (session == null || !session.isConnected()) return;
        changeRemoteDir("..");
    }
    
    private void changeRemoteDir(String dir) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                session.changeDirectory(dir);
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    get();
                    refreshRemoteFiles();
                } catch (Exception e) {
                    logger.error("Failed to change directory", e);
                    JOptionPane.showMessageDialog(FTPPanel.this,
                        e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
    
    private void createRemoteDir() {
        if (session == null || !session.isConnected()) return;
        
        String name = JOptionPane.showInputDialog(this, "目录名称:", "新建目录", JOptionPane.PLAIN_MESSAGE);
        if (name == null || name.isEmpty()) return;
        
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                session.makeDirectory(name);
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    get();
                    refreshRemoteFiles();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(FTPPanel.this,
                        e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
    
    private void deleteRemoteSelected() {
        if (session == null || !session.isConnected()) return;
        
        int[] rows = remoteTable.getSelectedRows();
        if (rows.length == 0) return;
        
        int result = JOptionPane.showConfirmDialog(this,
            "确定删除选中的 " + rows.length + " 个文件?",
            "确认删除", JOptionPane.YES_NO_OPTION);
        
        if (result != JOptionPane.YES_OPTION) return;
        
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (int row : rows) {
                    int modelRow = remoteTable.convertRowIndexToModel(row);
                    String name = (String) remoteModel.getValueAt(modelRow, 0);
                    boolean isDir = name.startsWith("[DIR] ");
                    name = name.replace("[DIR] ", "");
                    
                    if (isDir) {
                        session.deleteDirectory(name);
                    } else {
                        session.deleteFile(name);
                    }
                }
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    get();
                    refreshRemoteFiles();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(FTPPanel.this,
                        e.getMessage(), "删除失败", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
    
    /**
     * Upload selected local files
     */
    private void uploadSelected() {
        if (session == null || !session.isConnected()) return;
        
        int[] rows = localTable.getSelectedRows();
        if (rows.length == 0) return;
        
        for (int row : rows) {
            int modelRow = localTable.convertRowIndexToModel(row);
            String name = (String) localModel.getValueAt(modelRow, 0);
            if (name.startsWith("[DIR] ")) continue; // Skip directories for now
            
            File localFile = new File(localCurrentDir, name);
            uploadFile(localFile, name);
        }
    }
    
    private void uploadFile(File localFile, String remoteName) {
        // Add to queue
        int queueRow = queueModel.getRowCount();
        queueModel.addRow(new Object[]{localFile.getName(), formatSize(localFile.length()), "上传", "进行中", "0%"});
        
        new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() throws Exception {
                session.uploadFile(localFile, remoteName, new FTPSession.TransferListener() {
                    @Override
                    public void onProgress(long transferred, long total) {
                        int percent = total > 0 ? (int)(transferred * 100 / total) : 0;
                        publish(percent);
                    }
                    
                    @Override
                    public void onComplete() {
                        publish(100);
                    }
                });
                return null;
            }
            
            @Override
            protected void process(List<Integer> chunks) {
                int percent = chunks.get(chunks.size() - 1);
                queueModel.setValueAt(percent + "%", queueRow, 4);
                progressBar.setValue(percent);
            }
            
            @Override
            protected void done() {
                try {
                    get();
                    queueModel.setValueAt("完成", queueRow, 3);
                    refreshRemoteFiles();
                } catch (Exception e) {
                    queueModel.setValueAt("失败", queueRow, 3);
                    logger.error("Upload failed", e);
                }
            }
        }.execute();
    }
    
    /**
     * Download selected remote files
     */
    private void downloadSelected() {
        if (session == null || !session.isConnected()) return;
        
        int[] rows = remoteTable.getSelectedRows();
        if (rows.length == 0) return;
        
        for (int row : rows) {
            int modelRow = remoteTable.convertRowIndexToModel(row);
            String name = (String) remoteModel.getValueAt(modelRow, 0);
            if (name.startsWith("[DIR] ")) continue; // Skip directories for now
            
            File localFile = new File(localCurrentDir, name);
            downloadFile(name, localFile);
        }
    }
    
    private void downloadFile(String remoteName, File localFile) {
        // Add to queue
        int queueRow = queueModel.getRowCount();
        queueModel.addRow(new Object[]{remoteName, "", "下载", "进行中", "0%"});
        
        new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() throws Exception {
                session.downloadFile(remoteName, localFile, new FTPSession.TransferListener() {
                    @Override
                    public void onProgress(long transferred, long total) {
                        int percent = total > 0 ? (int)(transferred * 100 / total) : 0;
                        publish(percent);
                    }
                    
                    @Override
                    public void onComplete() {
                        publish(100);
                    }
                });
                return null;
            }
            
            @Override
            protected void process(List<Integer> chunks) {
                int percent = chunks.get(chunks.size() - 1);
                queueModel.setValueAt(percent + "%", queueRow, 4);
                progressBar.setValue(percent);
            }
            
            @Override
            protected void done() {
                try {
                    get();
                    queueModel.setValueAt("完成", queueRow, 3);
                    refreshLocalFiles();
                } catch (Exception e) {
                    queueModel.setValueAt("失败", queueRow, 3);
                    logger.error("Download failed", e);
                }
            }
        }.execute();
    }
    
    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        DecimalFormat df = new DecimalFormat("#.##");
        if (bytes < 1024 * 1024) return df.format(bytes / 1024.0) + " KB";
        if (bytes < 1024 * 1024 * 1024) return df.format(bytes / (1024.0 * 1024)) + " MB";
        return df.format(bytes / (1024.0 * 1024 * 1024)) + " GB";
    }
    
    @Override
    public void onFTPEvent(FTPSession session, FTPSession.FTPEvent event, String message) {
        SwingUtilities.invokeLater(() -> {
            switch (event) {
                case CONNECTED:
                    statusLabel.setText("已连接");
                    break;
                case DISCONNECTED:
                    statusLabel.setText("已断开");
                    break;
                case ERROR:
                    statusLabel.setText("错误: " + message);
                    break;
            }
        });
    }
    
    public void close() {
        disconnect();
    }
}
