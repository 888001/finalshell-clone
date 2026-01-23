package com.finalshell.theme;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * 背景配置面板
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class BgConfigPanel extends JPanel {
    
    private JCheckBox enableBgCheck;
    private JTextField bgPathField;
    private JButton browseButton;
    private JSlider opacitySlider;
    private JComboBox<String> positionCombo;
    private JLabel previewLabel;
    
    public BgConfigPanel() {
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("背景设置"));
        
        JPanel optionsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        enableBgCheck = new JCheckBox("启用背景图片");
        optionsPanel.add(enableBgCheck, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0;
        optionsPanel.add(new JLabel("图片路径:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        bgPathField = new JTextField(20);
        optionsPanel.add(bgPathField, gbc);
        gbc.gridx = 2; gbc.weightx = 0;
        browseButton = new JButton("浏览");
        browseButton.addActionListener(e -> browseImage());
        optionsPanel.add(browseButton, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        optionsPanel.add(new JLabel("透明度:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 1.0;
        opacitySlider = new JSlider(0, 100, 50);
        opacitySlider.setMajorTickSpacing(25);
        opacitySlider.setPaintTicks(true);
        opacitySlider.setPaintLabels(true);
        optionsPanel.add(opacitySlider, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0;
        optionsPanel.add(new JLabel("位置:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 1.0;
        positionCombo = new JComboBox<>(new String[]{"居中", "平铺", "拉伸", "填充"});
        optionsPanel.add(positionCombo, gbc);
        
        previewLabel = new JLabel("预览区域", SwingConstants.CENTER);
        previewLabel.setPreferredSize(new Dimension(200, 150));
        previewLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        previewLabel.setOpaque(true);
        previewLabel.setBackground(Color.DARK_GRAY);
        
        add(optionsPanel, BorderLayout.NORTH);
        add(previewLabel, BorderLayout.CENTER);
    }
    
    private void browseImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) return true;
                String name = f.getName().toLowerCase();
                return name.endsWith(".png") || name.endsWith(".jpg") || 
                       name.endsWith(".jpeg") || name.endsWith(".gif");
            }
            
            @Override
            public String getDescription() {
                return "图片文件 (*.png, *.jpg, *.gif)";
            }
        });
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            bgPathField.setText(chooser.getSelectedFile().getAbsolutePath());
            updatePreview();
        }
    }
    
    private void updatePreview() {
        String path = bgPathField.getText();
        if (path != null && !path.isEmpty()) {
            ImageIcon icon = new ImageIcon(path);
            Image img = icon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
            previewLabel.setIcon(new ImageIcon(img));
            previewLabel.setText("");
        }
    }
    
    public boolean isBackgroundEnabled() {
        return enableBgCheck.isSelected();
    }
    
    public String getBackgroundPath() {
        return bgPathField.getText();
    }
    
    public int getOpacity() {
        return opacitySlider.getValue();
    }
    
    public String getPosition() {
        return (String) positionCombo.getSelectedItem();
    }
}
