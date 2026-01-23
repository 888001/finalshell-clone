package com.finalshell.ui.table;

import com.finalshell.config.ConnectConfig;
import com.finalshell.ui.ImageManager;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * 连接列表渲染器
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Table_Model_Renderer_Listener_Analysis.md
 */
public class ConnListRenderer extends JPanel implements TableCellRenderer {
    
    private JLabel iconLabel;
    private JLabel nameLabel;
    private JLabel hostLabel;
    private JLabel descLabel;
    
    private Color normalBg = new Color(255, 255, 255);
    private Color hoverBg = new Color(230, 240, 250);
    private Color selectedBg = new Color(200, 220, 240);
    
    public ConnListRenderer() {
        setLayout(new BorderLayout(8, 0));
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        setOpaque(true);
        
        iconLabel = new JLabel();
        iconLabel.setPreferredSize(new Dimension(32, 32));
        add(iconLabel, BorderLayout.WEST);
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        
        nameLabel = new JLabel();
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 13f));
        textPanel.add(nameLabel);
        
        hostLabel = new JLabel();
        hostLabel.setFont(hostLabel.getFont().deriveFont(11f));
        hostLabel.setForeground(Color.GRAY);
        textPanel.add(hostLabel);
        
        descLabel = new JLabel();
        descLabel.setFont(descLabel.getFont().deriveFont(10f));
        descLabel.setForeground(new Color(150, 150, 150));
        textPanel.add(descLabel);
        
        add(textPanel, BorderLayout.CENTER);
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        ConnectConfig config = (ConnectConfig) value;
        
        if (config != null) {
            nameLabel.setText(config.getName());
            hostLabel.setText(config.getHost() + ":" + config.getPort());
            descLabel.setText(config.getDescription() != null ? config.getDescription() : "");
            
            String iconName = config.getType() == ConnectConfig.TYPE_RDP ? "rdp" : "ssh";
            iconLabel.setIcon(ImageManager.getInstance().getIcon(iconName, 32, 32));
        } else {
            nameLabel.setText("");
            hostLabel.setText("");
            descLabel.setText("");
            iconLabel.setIcon(null);
        }
        
        if (isSelected) {
            setBackground(selectedBg);
        } else if (table instanceof ConnListTable && ((ConnListTable) table).getHoverRow() == row) {
            setBackground(hoverBg);
        } else {
            setBackground(row % 2 == 0 ? normalBg : new Color(250, 250, 250));
        }
        
        return this;
    }
}
