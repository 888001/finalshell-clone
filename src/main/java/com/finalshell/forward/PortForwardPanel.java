package com.finalshell.forward;

import com.finalshell.ssh.SSHSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Port Forward Panel - Manages port forwarding rules
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class PortForwardPanel extends JPanel implements PortForwardManager.ForwardListener {
    
    private static final Logger logger = LoggerFactory.getLogger(PortForwardPanel.class);
    
    private final SSHSession sshSession;
    private PortForwardManager forwardManager;
    
    private JTable forwardTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    
    public PortForwardPanel(SSHSession sshSession) {
        this.sshSession = sshSession;
        this.forwardManager = new PortForwardManager(sshSession);
        this.forwardManager.addListener(this);
        
        initComponents();
        initLayout();
    }
    
    private void initComponents() {
        // Table model
        String[] columns = {"类型", "描述", "状态", "创建时间"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        forwardTable = new JTable(tableModel);
        forwardTable.setRowHeight(24);
        forwardTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        forwardTable.getTableHeader().setReorderingAllowed(false);
        
        // Column widths
        TableColumnModel cm = forwardTable.getColumnModel();
        cm.getColumn(0).setPreferredWidth(80);
        cm.getColumn(1).setPreferredWidth(250);
        cm.getColumn(2).setPreferredWidth(70);
        cm.getColumn(3).setPreferredWidth(130);
        
        // Double click to edit
        forwardTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    // Could edit, but for now just show info
                }
            }
        });
        
        statusLabel = new JLabel("就绪");
    }
    
    private void initLayout() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Toolbar
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        
        JButton addLocalBtn = new JButton("本地转发");
        addLocalBtn.setToolTipText("添加本地端口转发 (L)");
        addLocalBtn.addActionListener(e -> showAddDialog(PortForwardManager.ForwardType.LOCAL));
        toolbar.add(addLocalBtn);
        
        JButton addRemoteBtn = new JButton("远程转发");
        addRemoteBtn.setToolTipText("添加远程端口转发 (R)");
        addRemoteBtn.addActionListener(e -> showAddDialog(PortForwardManager.ForwardType.REMOTE));
        toolbar.add(addRemoteBtn);
        
        JButton addDynamicBtn = new JButton("动态转发");
        addDynamicBtn.setToolTipText("添加SOCKS代理 (D)");
        addDynamicBtn.addActionListener(e -> showAddDialog(PortForwardManager.ForwardType.DYNAMIC));
        toolbar.add(addDynamicBtn);
        
        toolbar.addSeparator();
        
        JButton removeBtn = new JButton("删除");
        removeBtn.addActionListener(e -> removeSelected());
        toolbar.add(removeBtn);
        
        JButton removeAllBtn = new JButton("全部删除");
        removeAllBtn.addActionListener(e -> removeAll());
        toolbar.add(removeAllBtn);
        
        add(toolbar, BorderLayout.NORTH);
        
        // Table
        add(new JScrollPane(forwardTable), BorderLayout.CENTER);
        
        // Status bar
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.add(statusLabel, BorderLayout.WEST);
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Show add forward dialog
     */
    private void showAddDialog(PortForwardManager.ForwardType type) {
        PortForwardDialog dialog = new PortForwardDialog(
            SwingUtilities.getWindowAncestor(this), 
            type
        );
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            try {
                switch (type) {
                    case LOCAL:
                        forwardManager.addLocalForward(
                            dialog.getLocalPort(),
                            dialog.getRemoteHost(),
                            dialog.getRemotePort()
                        );
                        break;
                    case REMOTE:
                        forwardManager.addRemoteForward(
                            dialog.getRemotePort(),
                            dialog.getLocalHost(),
                            dialog.getLocalPort()
                        );
                        break;
                    case DYNAMIC:
                        forwardManager.addDynamicForward(dialog.getLocalPort());
                        break;
                }
                setStatus("转发规则已添加");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "添加转发失败: " + e.getMessage(), 
                    "错误", JOptionPane.ERROR_MESSAGE);
                logger.error("Add forward failed", e);
            }
        }
    }
    
    /**
     * Remove selected forward
     */
    private void removeSelected() {
        int row = forwardTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请选择要删除的转发规则", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        var forwards = forwardManager.getActiveForwards();
        if (row < forwards.size()) {
            try {
                forwardManager.removeForward(forwards.get(row).getId());
                setStatus("转发规则已删除");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "删除转发失败: " + e.getMessage(), 
                    "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Remove all forwards
     */
    private void removeAll() {
        if (forwardManager.getActiveForwards().isEmpty()) {
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "确定删除所有转发规则?", "确认", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            forwardManager.removeAllForwards();
            setStatus("所有转发规则已删除");
        }
    }
    
    /**
     * Refresh table
     */
    private void refreshTable() {
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        for (var entry : forwardManager.getActiveForwards()) {
            tableModel.addRow(new Object[]{
                entry.getType().getDisplayName(),
                entry.getDescription(),
                entry.getStatus().getDisplayName(),
                sdf.format(new Date(entry.getCreatedTime()))
            });
        }
    }
    
    private void setStatus(String message) {
        statusLabel.setText(message);
    }
    
    @Override
    public void onForwardEvent(PortForwardManager.ForwardEvent event, PortForwardManager.ForwardEntry entry) {
        SwingUtilities.invokeLater(this::refreshTable);
    }
    
    /**
     * Close and cleanup
     */
    public void close() {
        forwardManager.removeAllForwards();
    }
    
    public PortForwardManager getForwardManager() {
        return forwardManager;
    }
}
