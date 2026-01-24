package com.finalshell.sftp;

import com.finalshell.config.AppConfig;
import com.finalshell.config.ConfigManager;
import com.finalshell.ssh.SSHSession;
import com.finalshell.util.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * SFTP Panel - Dual-pane file browser (local + remote)
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: OpenPanel_DeepAnalysis.md, UI_Parameters_Reference.md
 */
public class SFTPPanel extends JPanel implements SFTPSession.SFTPEventListener {
    
    private static final Logger logger = LoggerFactory.getLogger(SFTPPanel.class);
    
    private final SSHSession sshSession;
    private SFTPSession sftpSession;
    private final AppConfig appConfig;
    
    // Local panel components
    private JTable localTable;
    private DefaultTableModel localModel;
    private JTextField localPathField;
    private File currentLocalDir;
    
    // Remote panel components
    private JTable remoteTable;
    private DefaultTableModel remoteModel;
    private JTextField remotePathField;
    private String currentRemotePath = "/";
    
    // Status
    private JLabel statusLabel;
    private JProgressBar progressBar;
    
    public SFTPPanel(SSHSession sshSession) {
        this.sshSession = sshSession;
        this.appConfig = ConfigManager.getInstance().getAppConfig();
        
        initComponents();
        initLayout();
        initListeners();
        
        // Connect SFTP
        connectSFTP();
    }
    
    private void initComponents() {
        // Local file table
        String[] columns = {"名称", "大小", "修改时间"};
        localModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        localTable = new JTable(localModel);
        setupTable(localTable);
        
        localPathField = new JTextField();
        localPathField.setEditable(false);
        
        // Remote file table
        remoteModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        remoteTable = new JTable(remoteModel);
        setupTable(remoteTable);
        
        remotePathField = new JTextField();
        remotePathField.setEditable(false);
        
        // Status bar
        statusLabel = new JLabel("就绪");
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
        
        // Initial local path
        String defaultPath = appConfig.getSftpDefaultLocalPath();
        if (defaultPath != null && !defaultPath.isEmpty()) {
            currentLocalDir = new File(defaultPath);
        } else {
            currentLocalDir = new File(System.getProperty("user.home"));
        }
    }
    
    private void setupTable(JTable table) {
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setRowHeight(22);
        table.getTableHeader().setReorderingAllowed(false);
        
        // Column widths
        TableColumnModel cm = table.getColumnModel();
        cm.getColumn(0).setPreferredWidth(200);
        cm.getColumn(1).setPreferredWidth(80);
        cm.getColumn(2).setPreferredWidth(130);
        
        // Double click handler
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    if (table == localTable) {
                        handleLocalDoubleClick();
                    } else {
                        handleRemoteDoubleClick();
                    }
                }
            }
        });
        
        // Enable drag and drop
        table.setDragEnabled(true);
        table.setTransferHandler(new FileTransferHandler(table == localTable));
        
        // Drop target
        new DropTarget(table, new FileDropTargetListener(table == localTable));
    }
    
    private void initLayout() {
        setLayout(new BorderLayout());
        
        // Split pane for local/remote
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        
        // Local panel (left)
        JPanel localPanel = createFilePanel("本地", localPathField, localTable, true);
        splitPane.setLeftComponent(localPanel);
        
        // Remote panel (right)
        JPanel remotePanel = createFilePanel("远程", remotePathField, remoteTable, false);
        splitPane.setRightComponent(remotePanel);
        
        add(splitPane, BorderLayout.CENTER);
        
        // Status bar
        JPanel statusPanel = new JPanel(new BorderLayout(5, 0));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        statusPanel.add(statusLabel, BorderLayout.CENTER);
        statusPanel.add(progressBar, BorderLayout.EAST);
        progressBar.setPreferredSize(new Dimension(150, 16));
        
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createFilePanel(String title, JTextField pathField, JTable table, boolean isLocal) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        
        // Toolbar
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        
        JButton upBtn = new JButton("↑");
        upBtn.setToolTipText("上一级");
        upBtn.addActionListener(e -> {
            if (isLocal) goUpLocal();
            else goUpRemote();
        });
        toolbar.add(upBtn);
        
        JButton refreshBtn = new JButton("⟳");
        refreshBtn.setToolTipText("刷新");
        refreshBtn.addActionListener(e -> {
            if (isLocal) refreshLocal();
            else refreshRemote();
        });
        toolbar.add(refreshBtn);
        
        JButton homeBtn = new JButton("⌂");
        homeBtn.setToolTipText("主目录");
        homeBtn.addActionListener(e -> {
            if (isLocal) goHomeLocal();
            else goHomeRemote();
        });
        toolbar.add(homeBtn);
        
        toolbar.addSeparator();
        
        JButton mkdirBtn = new JButton("+");
        mkdirBtn.setToolTipText("新建文件夹");
        mkdirBtn.addActionListener(e -> {
            if (isLocal) createLocalFolder();
            else createRemoteFolder();
        });
        toolbar.add(mkdirBtn);
        
        JButton deleteBtn = new JButton("✕");
        deleteBtn.setToolTipText("删除");
        deleteBtn.addActionListener(e -> {
            if (isLocal) deleteLocalFiles();
            else deleteRemoteFiles();
        });
        toolbar.add(deleteBtn);
        
        // Path panel
        JPanel pathPanel = new JPanel(new BorderLayout());
        pathPanel.add(toolbar, BorderLayout.WEST);
        pathPanel.add(pathField, BorderLayout.CENTER);
        
        panel.add(pathPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        return panel;
    }
    
    private void initListeners() {
        // Right-click context menus
        localTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) showLocalContextMenu(e);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) showLocalContextMenu(e);
            }
        });
        
        remoteTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) showRemoteContextMenu(e);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) showRemoteContextMenu(e);
            }
        });
    }
    
    /**
     * Connect SFTP session
     */
    private void connectSFTP() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                setStatus("正在连接SFTP...");
                sftpSession = new SFTPSession(sshSession);
                sftpSession.addListener(SFTPPanel.this);
                sftpSession.open();
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    get();
                    setStatus("SFTP已连接");
                    refreshLocal();
                    refreshRemote();
                } catch (Exception e) {
                    setStatus("SFTP连接失败: " + e.getMessage());
                    logger.error("SFTP connection failed", e);
                }
            }
        }.execute();
    }
    
    // Local file operations
    
    private void refreshLocal() {
        if (currentLocalDir == null || !currentLocalDir.exists()) {
            currentLocalDir = new File(System.getProperty("user.home"));
        }
        
        localPathField.setText(currentLocalDir.getAbsolutePath());
        localModel.setRowCount(0);
        
        // Add parent directory entry
        if (currentLocalDir.getParentFile() != null) {
            localModel.addRow(new Object[]{"[..]", "", ""});
        }
        
        File[] files = currentLocalDir.listFiles();
        if (files == null) return;
        
        // Sort: directories first
        Arrays.sort(files, (a, b) -> {
            if (a.isDirectory() != b.isDirectory()) {
                return a.isDirectory() ? -1 : 1;
            }
            return a.getName().compareToIgnoreCase(b.getName());
        });
        
        boolean showHidden = appConfig.isSftpShowHidden();
        
        for (File file : files) {
            if (!showHidden && file.isHidden()) continue;
            
            String name = file.isDirectory() ? "[" + file.getName() + "]" : file.getName();
            String size = file.isDirectory() ? "" : formatSize(file.length());
            String time = formatTime(file.lastModified());
            
            localModel.addRow(new Object[]{name, size, time});
        }
    }
    
    private void handleLocalDoubleClick() {
        int row = localTable.getSelectedRow();
        if (row < 0) return;
        
        String name = (String) localModel.getValueAt(row, 0);
        
        if ("[..]".equals(name)) {
            goUpLocal();
        } else if (name.startsWith("[") && name.endsWith("]")) {
            String dirName = name.substring(1, name.length() - 1);
            currentLocalDir = new File(currentLocalDir, dirName);
            refreshLocal();
        }
    }
    
    private void goUpLocal() {
        File parent = currentLocalDir.getParentFile();
        if (parent != null) {
            currentLocalDir = parent;
            refreshLocal();
        }
    }
    
    private void goHomeLocal() {
        currentLocalDir = new File(System.getProperty("user.home"));
        refreshLocal();
    }
    
    private void createLocalFolder() {
        String name = JOptionPane.showInputDialog(this, "文件夹名称:");
        if (name != null && !name.trim().isEmpty()) {
            File newDir = new File(currentLocalDir, name.trim());
            if (newDir.mkdir()) {
                refreshLocal();
            } else {
                JOptionPane.showMessageDialog(this, "创建文件夹失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void deleteLocalFiles() {
        int[] rows = localTable.getSelectedRows();
        if (rows.length == 0) return;
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "确定删除选中的 " + rows.length + " 个文件/文件夹?",
            "确认删除", JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) return;
        
        for (int row : rows) {
            String name = (String) localModel.getValueAt(row, 0);
            if ("[..]".equals(name)) continue;
            
            if (name.startsWith("[") && name.endsWith("]")) {
                name = name.substring(1, name.length() - 1);
            }
            
            File file = new File(currentLocalDir, name);
            deleteRecursive(file);
        }
        
        refreshLocal();
    }
    
    private void deleteRecursive(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursive(child);
                }
            }
        }
        file.delete();
    }
    
    // Remote file operations
    
    private void refreshRemote() {
        if (sftpSession == null || !sftpSession.isConnected()) {
            return;
        }
        
        new SwingWorker<List<RemoteFile>, Void>() {
            @Override
            protected List<RemoteFile> doInBackground() throws Exception {
                String path = sftpSession.pwd();
                if (path == null || path.isEmpty()) {
                    path = "/";
                }
                currentRemotePath = path;
                return sftpSession.listFiles(currentRemotePath);
            }
            
            @Override
            protected void done() {
                try {
                    List<RemoteFile> files = get();
                    remotePathField.setText(currentRemotePath);
                    remoteModel.setRowCount(0);
                    
                    boolean showHidden = appConfig.isSftpShowHidden();
                    
                    for (RemoteFile file : files) {
                        if (!showHidden && file.isHidden()) continue;
                        
                        String name = file.isDirectory() ? "[" + file.getName() + "]" : file.getName();
                        String size = file.getFormattedSize();
                        String time = file.getFormattedTime();
                        
                        remoteModel.addRow(new Object[]{name, size, time});
                    }
                    
                    setStatus("远程: " + files.size() + " 项");
                } catch (Exception e) {
                    setStatus("刷新失败: " + e.getMessage());
                    logger.error("Refresh remote failed", e);
                }
            }
        }.execute();
    }
    
    private void handleRemoteDoubleClick() {
        int row = remoteTable.getSelectedRow();
        if (row < 0) return;
        
        String name = (String) remoteModel.getValueAt(row, 0);
        
        if (name.startsWith("[") && name.endsWith("]")) {
            String dirName = name.substring(1, name.length() - 1);
            
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    if ("..".equals(dirName)) {
                        sftpSession.cd("..");
                    } else {
                        sftpSession.cd(dirName);
                    }
                    return null;
                }
                
                @Override
                protected void done() {
                    try {
                        get();
                        refreshRemote();
                    } catch (Exception e) {
                        setStatus("切换目录失败: " + e.getMessage());
                    }
                }
            }.execute();
        }
    }
    
    private void goUpRemote() {
        if (sftpSession == null) return;
        
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                sftpSession.cd("..");
                return null;
            }
            
            @Override
            protected void done() {
                refreshRemote();
            }
        }.execute();
    }
    
    private void goHomeRemote() {
        if (sftpSession == null) return;
        
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                sftpSession.cd("~");
                return null;
            }
            
            @Override
            protected void done() {
                refreshRemote();
            }
        }.execute();
    }
    
    private void createRemoteFolder() {
        String name = JOptionPane.showInputDialog(this, "文件夹名称:");
        if (name != null && !name.trim().isEmpty()) {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    sftpSession.mkdir(currentRemotePath + "/" + name.trim());
                    return null;
                }
                
                @Override
                protected void done() {
                    try {
                        get();
                        refreshRemote();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(SFTPPanel.this, 
                            "创建文件夹失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }
    
    private void deleteRemoteFiles() {
        int[] rows = remoteTable.getSelectedRows();
        if (rows.length == 0) return;
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "确定删除选中的 " + rows.length + " 个文件/文件夹?",
            "确认删除", JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) return;
        
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (int row : rows) {
                    String name = (String) remoteModel.getValueAt(row, 0);
                    if ("[..]".equals(name)) continue;
                    
                    boolean isDir = name.startsWith("[") && name.endsWith("]");
                    if (isDir) {
                        name = name.substring(1, name.length() - 1);
                    }
                    
                    String path = currentRemotePath + "/" + name;
                    sftpSession.rm(path, isDir);
                }
                return null;
            }
            
            @Override
            protected void done() {
                refreshRemote();
            }
        }.execute();
    }
    
    // Context menus
    
    private void showLocalContextMenu(MouseEvent e) {
        JPopupMenu menu = new JPopupMenu();
        
        JMenuItem uploadItem = new JMenuItem("上传到远程");
        uploadItem.addActionListener(ev -> uploadSelected());
        menu.add(uploadItem);
        
        menu.addSeparator();
        
        JMenuItem refreshItem = new JMenuItem("刷新");
        refreshItem.addActionListener(ev -> refreshLocal());
        menu.add(refreshItem);
        
        JMenuItem mkdirItem = new JMenuItem("新建文件夹");
        mkdirItem.addActionListener(ev -> createLocalFolder());
        menu.add(mkdirItem);
        
        JMenuItem deleteItem = new JMenuItem("删除");
        deleteItem.addActionListener(ev -> deleteLocalFiles());
        menu.add(deleteItem);
        
        menu.show(localTable, e.getX(), e.getY());
    }
    
    private void showRemoteContextMenu(MouseEvent e) {
        JPopupMenu menu = new JPopupMenu();
        
        JMenuItem downloadItem = new JMenuItem("下载到本地");
        downloadItem.addActionListener(ev -> downloadSelected());
        menu.add(downloadItem);
        
        menu.addSeparator();
        
        JMenuItem refreshItem = new JMenuItem("刷新");
        refreshItem.addActionListener(ev -> refreshRemote());
        menu.add(refreshItem);
        
        JMenuItem mkdirItem = new JMenuItem("新建文件夹");
        mkdirItem.addActionListener(ev -> createRemoteFolder());
        menu.add(mkdirItem);
        
        JMenuItem deleteItem = new JMenuItem("删除");
        deleteItem.addActionListener(ev -> deleteRemoteFiles());
        menu.add(deleteItem);
        
        menu.show(remoteTable, e.getX(), e.getY());
    }
    
    // Transfer operations
    
    private void uploadSelected() {
        int[] rows = localTable.getSelectedRows();
        if (rows.length == 0) return;
        
        for (int row : rows) {
            String name = (String) localModel.getValueAt(row, 0);
            if ("[..]".equals(name)) continue;
            
            if (name.startsWith("[") && name.endsWith("]")) {
                name = name.substring(1, name.length() - 1);
            }
            
            File localFile = new File(currentLocalDir, name);
            String remotePath = currentRemotePath + "/" + name;
            
            FileTransferManager.getInstance().addUpload(sftpSession, localFile.getAbsolutePath(), remotePath);
        }
        
        setStatus("已添加 " + rows.length + " 个上传任务");
    }
    
    private void downloadSelected() {
        int[] rows = remoteTable.getSelectedRows();
        if (rows.length == 0) return;
        
        for (int row : rows) {
            String name = (String) remoteModel.getValueAt(row, 0);
            if ("[..]".equals(name)) continue;
            
            if (name.startsWith("[") && name.endsWith("]")) {
                name = name.substring(1, name.length() - 1);
            }
            
            String remotePath = currentRemotePath + "/" + name;
            String localPath = new File(currentLocalDir, name).getAbsolutePath();
            
            FileTransferManager.getInstance().addDownload(sftpSession, remotePath, localPath);
        }
        
        setStatus("已添加 " + rows.length + " 个下载任务");
    }
    
    // Utility methods
    
    private void setStatus(String message) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(message));
    }
    
    private String formatSize(long size) {
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        if (size < 1024 * 1024 * 1024) return String.format("%.1f MB", size / (1024.0 * 1024));
        return String.format("%.1f GB", size / (1024.0 * 1024 * 1024));
    }
    
    private String formatTime(long time) {
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date(time));
    }
    
    @Override
    public void onSFTPEvent(SFTPSession.SFTPEvent event, String path) {
        SwingUtilities.invokeLater(() -> {
            switch (event) {
                case UPLOAD_COMPLETE:
                case DOWNLOAD_COMPLETE:
                    refreshRemote();
                    refreshLocal();
                    break;
                default:
                    break;
            }
        });
    }
    
    /**
     * File Transfer Handler for drag and drop
     */
    private class FileTransferHandler extends TransferHandler {
        private final boolean isLocal;
        
        public FileTransferHandler(boolean isLocal) {
            this.isLocal = isLocal;
        }
        
        @Override
        public int getSourceActions(JComponent c) {
            return COPY;
        }
        
        @Override
        protected Transferable createTransferable(JComponent c) {
            JTable table = (JTable) c;
            int[] rows = table.getSelectedRows();
            if (rows.length == 0) return null;
            
            List<String> paths = new ArrayList<>();
            for (int row : rows) {
                String name = (String) table.getModel().getValueAt(row, 0);
                if ("[..]".equals(name)) continue;
                
                if (name.startsWith("[") && name.endsWith("]")) {
                    name = name.substring(1, name.length() - 1);
                }
                
                if (isLocal) {
                    paths.add(new File(currentLocalDir, name).getAbsolutePath());
                } else {
                    paths.add(currentRemotePath + "/" + name);
                }
            }
            
            return new StringSelection(String.join("\n", paths));
        }
    }
    
    /**
     * Drop Target Listener
     */
    private class FileDropTargetListener extends DropTargetAdapter {
        private final boolean isLocal;
        
        public FileDropTargetListener(boolean isLocal) {
            this.isLocal = isLocal;
        }
        
        @Override
        public void drop(DropTargetDropEvent dtde) {
            try {
                dtde.acceptDrop(DnDConstants.ACTION_COPY);
                
                Transferable t = dtde.getTransferable();
                
                if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    @SuppressWarnings("unchecked")
                    List<File> files = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
                    
                    for (File file : files) {
                        if (isLocal) {
                            // Dropped on local - ignore
                        } else {
                            // Dropped on remote - upload
                            String remotePath = currentRemotePath + "/" + file.getName();
                            FileTransferManager.getInstance().addUpload(sftpSession, file.getAbsolutePath(), remotePath);
                        }
                    }
                }
                
                dtde.dropComplete(true);
            } catch (Exception e) {
                logger.error("Drop failed", e);
                dtde.dropComplete(false);
            }
        }
    }
    
    /**
     * Close SFTP session
     */
    public void close() {
        if (sftpSession != null) {
            sftpSession.close();
        }
    }
}
