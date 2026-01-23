package com.finalshell.ui.table;

import com.finalshell.key.SecretKey;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * SSH密钥渲染器
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Table_Model_Renderer_Listener_Analysis.md
 */
public class KeyRenderer extends JPanel implements TableCellRenderer {
    
    private JRadioButton radioButton;
    private JLabel textLabel;
    
    private Color normalBg = Color.WHITE;
    private Color alternateBg = new Color(245, 245, 250);
    private Color selectedBg = new Color(200, 220, 240);
    
    public KeyRenderer() {
        setLayout(new BorderLayout());
        setOpaque(true);
        
        radioButton = new JRadioButton();
        radioButton.setOpaque(false);
        
        textLabel = new JLabel();
        textLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        removeAll();
        
        if (column == 0) {
            Boolean selected = (Boolean) value;
            radioButton.setSelected(selected != null && selected);
            add(radioButton, BorderLayout.CENTER);
        } else {
            SecretKey key = (SecretKey) value;
            if (key != null) {
                switch (column) {
                    case 1: textLabel.setText(key.getName()); break;
                    case 2: textLabel.setText(key.getType()); break;
                    case 3: textLabel.setText(key.getLength() + " bits"); break;
                }
            } else {
                textLabel.setText("");
            }
            add(textLabel, BorderLayout.CENTER);
        }
        
        if (isSelected) {
            setBackground(selectedBg);
        } else {
            setBackground(row % 2 == 0 ? normalBg : alternateBg);
        }
        
        return this;
    }
}
