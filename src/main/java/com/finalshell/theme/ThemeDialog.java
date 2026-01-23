package com.finalshell.theme;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 主题选择对话框
 */
public class ThemeDialog extends JDialog {
    private final ThemeManager themeManager;
    private JList<ThemeItem> themeList;
    private JPanel previewPanel;
    private boolean confirmed = false;
    
    public ThemeDialog(Window owner) {
        super(owner, "选择主题", ModalityType.APPLICATION_MODAL);
        this.themeManager = ThemeManager.getInstance();
        
        setSize(500, 400);
        setLocationRelativeTo(owner);
        
        initComponents();
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 主题列表
        DefaultListModel<ThemeItem> listModel = new DefaultListModel<>();
        for (ThemeConfig theme : themeManager.getAllThemes()) {
            listModel.addElement(new ThemeItem(theme));
        }
        
        themeList = new JList<>(listModel);
        themeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        themeList.setCellRenderer(new ThemeListRenderer());
        
        // 选中当前主题
        ThemeConfig current = themeManager.getCurrentTheme();
        for (int i = 0; i < listModel.size(); i++) {
            if (listModel.get(i).theme.getId().equals(current.getId())) {
                themeList.setSelectedIndex(i);
                break;
            }
        }
        
        JScrollPane listScroll = new JScrollPane(themeList);
        listScroll.setPreferredSize(new Dimension(150, 0));
        mainPanel.add(listScroll, BorderLayout.WEST);
        
        // 预览面板
        previewPanel = new JPanel(new BorderLayout());
        previewPanel.setBorder(BorderFactory.createTitledBorder("预览"));
        updatePreview();
        mainPanel.add(previewPanel, BorderLayout.CENTER);
        
        // 主题选择监听
        themeList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updatePreview();
            }
        });
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton applyBtn = new JButton("应用");
        JButton cancelBtn = new JButton("取消");
        
        applyBtn.addActionListener(e -> {
            ThemeItem selected = themeList.getSelectedValue();
            if (selected != null) {
                themeManager.setTheme(selected.theme.getId());
                themeManager.refreshAllWindows();
                confirmed = true;
            }
            dispose();
        });
        
        cancelBtn.addActionListener(e -> dispose());
        
        buttonPanel.add(applyBtn);
        buttonPanel.add(cancelBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private void updatePreview() {
        ThemeItem selected = themeList.getSelectedValue();
        if (selected == null) return;
        
        ThemeConfig theme = selected.theme;
        previewPanel.removeAll();
        
        JPanel preview = new JPanel(new GridLayout(0, 1, 5, 5));
        preview.setBackground(theme.getBackgroundColor());
        preview.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 标题
        JLabel titleLabel = new JLabel(theme.getName());
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));
        titleLabel.setForeground(theme.getTextColor());
        preview.add(titleLabel);
        
        // 颜色条
        JPanel colorBar = new JPanel(new GridLayout(1, 6, 2, 0));
        colorBar.setOpaque(false);
        addColorSwatch(colorBar, "主色", theme.getPrimaryColor());
        addColorSwatch(colorBar, "次色", theme.getSecondaryColor());
        addColorSwatch(colorBar, "强调", theme.getAccentColor());
        addColorSwatch(colorBar, "成功", theme.getSuccessColor());
        addColorSwatch(colorBar, "警告", theme.getWarningColor());
        addColorSwatch(colorBar, "错误", theme.getErrorColor());
        preview.add(colorBar);
        
        // 示例控件
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        controlsPanel.setBackground(theme.getPanelColor());
        
        JButton sampleBtn = new JButton("按钮");
        sampleBtn.setBackground(theme.getPrimaryColor());
        sampleBtn.setForeground(Color.WHITE);
        controlsPanel.add(sampleBtn);
        
        JTextField sampleField = new JTextField("输入框", 10);
        sampleField.setBackground(theme.getInputColor());
        sampleField.setForeground(theme.getTextColor());
        controlsPanel.add(sampleField);
        
        JCheckBox sampleCheck = new JCheckBox("复选框");
        sampleCheck.setBackground(theme.getPanelColor());
        sampleCheck.setForeground(theme.getTextColor());
        controlsPanel.add(sampleCheck);
        
        preview.add(controlsPanel);
        
        // 示例文本
        JTextArea sampleText = new JTextArea(3, 30);
        sampleText.setBackground(theme.getInputColor());
        sampleText.setForeground(theme.getTextColor());
        sampleText.setText("示例文本\nSample Text\n文字颜色预览");
        sampleText.setEditable(false);
        preview.add(new JScrollPane(sampleText));
        
        previewPanel.add(preview, BorderLayout.CENTER);
        previewPanel.revalidate();
        previewPanel.repaint();
    }
    
    private void addColorSwatch(JPanel panel, String name, Color color) {
        JPanel swatch = new JPanel(new BorderLayout());
        swatch.setBackground(color);
        swatch.setPreferredSize(new Dimension(50, 30));
        swatch.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        swatch.setToolTipText(name);
        panel.add(swatch);
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    /**
     * 显示对话框
     */
    public static void show(Window owner) {
        ThemeDialog dialog = new ThemeDialog(owner);
        dialog.setVisible(true);
    }
    
    /**
     * 主题项
     */
    private static class ThemeItem {
        final ThemeConfig theme;
        
        ThemeItem(ThemeConfig theme) {
            this.theme = theme;
        }
        
        @Override
        public String toString() {
            return theme.getName();
        }
    }
    
    /**
     * 主题列表渲染器
     */
    private static class ThemeListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof ThemeItem) {
                ThemeConfig theme = ((ThemeItem) value).theme;
                setText(theme.getName());
                
                // 显示主题颜色图标
                setIcon(new ColorIcon(theme.getPrimaryColor(), theme.getBackgroundColor()));
            }
            
            return this;
        }
    }
    
    /**
     * 颜色图标
     */
    private static class ColorIcon implements Icon {
        private final Color primary;
        private final Color background;
        
        ColorIcon(Color primary, Color background) {
            this.primary = primary;
            this.background = background;
        }
        
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(background);
            g.fillRect(x, y, 20, 16);
            g.setColor(primary);
            g.fillRect(x + 2, y + 2, 16, 12);
            g.setColor(Color.DARK_GRAY);
            g.drawRect(x, y, 19, 15);
        }
        
        @Override
        public int getIconWidth() { return 20; }
        
        @Override
        public int getIconHeight() { return 16; }
    }
}
