package com.finalshell.i18n;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

/**
 * 语言选择对话框
 */
public class LanguageDialog extends JDialog {
    private final I18n i18n;
    private JComboBox<LocaleItem> languageCombo;
    private boolean confirmed = false;
    
    public LanguageDialog(Window owner) {
        super(owner, I18n.get("dialog.language.title"), ModalityType.APPLICATION_MODAL);
        this.i18n = I18n.getInstance();
        
        setSize(350, 150);
        setLocationRelativeTo(owner);
        setResizable(false);
        
        initComponents();
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 语言选择
        JPanel selectPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        selectPanel.add(new JLabel(I18n.get("dialog.language.select") + ":"));
        
        languageCombo = new JComboBox<>();
        Locale current = i18n.getLocale();
        
        for (Locale locale : i18n.getSupportedLocales()) {
            LocaleItem item = new LocaleItem(locale, i18n.getLocaleDisplayName(locale));
            languageCombo.addItem(item);
            if (locale.getLanguage().equals(current.getLanguage())) {
                languageCombo.setSelectedItem(item);
            }
        }
        
        languageCombo.setPreferredSize(new Dimension(150, 25));
        selectPanel.add(languageCombo);
        
        mainPanel.add(selectPanel, BorderLayout.CENTER);
        
        // 按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton okBtn = new JButton(I18n.get("button.ok"));
        JButton cancelBtn = new JButton(I18n.get("button.cancel"));
        
        okBtn.addActionListener(e -> {
            confirmed = true;
            dispose();
        });
        
        cancelBtn.addActionListener(e -> dispose());
        
        buttonPanel.add(okBtn);
        buttonPanel.add(cancelBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // 提示
        JLabel noteLabel = new JLabel(I18n.get("dialog.language.note"));
        noteLabel.setFont(noteLabel.getFont().deriveFont(Font.ITALIC, 11f));
        noteLabel.setForeground(Color.GRAY);
        noteLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(noteLabel, BorderLayout.NORTH);
        
        setContentPane(mainPanel);
    }
    
    public Locale getSelectedLocale() {
        LocaleItem item = (LocaleItem) languageCombo.getSelectedItem();
        return item != null ? item.locale : null;
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    /**
     * 显示对话框并返回选择的语言
     */
    public static Locale showDialog(Window owner) {
        LanguageDialog dialog = new LanguageDialog(owner);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            Locale selected = dialog.getSelectedLocale();
            if (selected != null) {
                I18n.getInstance().setLocale(selected);
                return selected;
            }
        }
        return null;
    }
    
    /**
     * 语言项
     */
    private static class LocaleItem {
        final Locale locale;
        final String displayName;
        
        LocaleItem(Locale locale, String displayName) {
            this.locale = locale;
            this.displayName = displayName;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
    }
}
