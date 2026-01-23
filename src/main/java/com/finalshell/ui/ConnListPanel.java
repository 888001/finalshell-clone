package com.finalshell.ui;

import com.finalshell.config.ConnectConfig;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 连接列表面板
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Nav_Loading_UI_DeepAnalysis.md - ConnListPanel
 */
public class ConnListPanel extends JPanel {
    
    private JTable listTable;
    private ConnListModel listModel;
    private JScrollPane scrollPane;
    private ConnListListener listener;
    
    public ConnListPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        
        listModel = new ConnListModel();
        listTable = new JTable(listModel);
        listTable.setRowHeight(40);
        listTable.setShowGrid(false);
        listTable.setIntercellSpacing(new Dimension(0, 0));
        listTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listTable.setDefaultRenderer(Object.class, new ConnListRenderer());
        listTable.setTableHeader(null);
        
        // 双击连接
        listTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = listTable.getSelectedRow();
                    if (row >= 0 && listener != null) {
                        ConnectConfig config = listModel.getConfigAt(row);
                        listener.onConnectSelected(config);
                    }
                }
            }
        });
        
        scrollPane = new JScrollPane(listTable);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    public void setConfigs(List<ConnectConfig> configs) {
        listModel.setConfigs(configs);
    }
    
    public void addConfig(ConnectConfig config) {
        listModel.addConfig(config);
    }
    
    public void removeConfig(ConnectConfig config) {
        listModel.removeConfig(config);
    }
    
    public ConnectConfig getSelectedConfig() {
        int row = listTable.getSelectedRow();
        return row >= 0 ? listModel.getConfigAt(row) : null;
    }
    
    public void setListener(ConnListListener listener) {
        this.listener = listener;
    }
    
    /**
     * 连接列表数据模型
     */
    private static class ConnListModel extends AbstractTableModel {
        private List<ConnectConfig> configs = new ArrayList<>();
        
        public void setConfigs(List<ConnectConfig> configs) {
            this.configs = new ArrayList<>(configs);
            fireTableDataChanged();
        }
        
        public void addConfig(ConnectConfig config) {
            configs.add(config);
            fireTableRowsInserted(configs.size() - 1, configs.size() - 1);
        }
        
        public void removeConfig(ConnectConfig config) {
            int index = configs.indexOf(config);
            if (index >= 0) {
                configs.remove(index);
                fireTableRowsDeleted(index, index);
            }
        }
        
        public ConnectConfig getConfigAt(int row) {
            return row >= 0 && row < configs.size() ? configs.get(row) : null;
        }
        
        @Override
        public int getRowCount() { return configs.size(); }
        
        @Override
        public int getColumnCount() { return 1; }
        
        @Override
        public Object getValueAt(int row, int col) { 
            return configs.get(row); 
        }
    }
    
    /**
     * 连接列表渲染器
     */
    private static class ConnListRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            JPanel panel = new JPanel(new BorderLayout(10, 0));
            panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            
            if (value instanceof ConnectConfig) {
                ConnectConfig config = (ConnectConfig) value;
                
                // 图标
                JLabel iconLabel = new JLabel();
                iconLabel.setPreferredSize(new Dimension(32, 32));
                panel.add(iconLabel, BorderLayout.WEST);
                
                // 信息面板
                JPanel infoPanel = new JPanel(new GridLayout(2, 1));
                infoPanel.setOpaque(false);
                
                JLabel nameLabel = new JLabel(config.getName());
                nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 14f));
                infoPanel.add(nameLabel);
                
                JLabel hostLabel = new JLabel(config.getHost() + ":" + config.getPort());
                hostLabel.setForeground(Color.GRAY);
                infoPanel.add(hostLabel);
                
                panel.add(infoPanel, BorderLayout.CENTER);
            }
            
            if (isSelected) {
                panel.setBackground(new Color(220, 235, 252));
            } else {
                panel.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 248, 248));
            }
            
            return panel;
        }
    }
    
    /**
     * 连接选择监听器
     */
    public interface ConnListListener {
        void onConnectSelected(ConnectConfig config);
    }
}
