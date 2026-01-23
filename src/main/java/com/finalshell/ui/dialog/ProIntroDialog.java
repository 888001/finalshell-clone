package com.finalshell.ui.dialog;

import javax.swing.*;
import java.awt.*;

/**
 * Pro版介绍对话框
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Dialog_Tree_Module_Analysis.md
 */
public class ProIntroDialog extends JDialog {
    
    private JButton upgradeButton;
    private JButton closeButton;
    
    public ProIntroDialog(Frame owner) {
        super(owner, "FinalShell Pro", true);
        initUI();
        setSize(450, 350);
        setLocationRelativeTo(owner);
    }
    
    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("FinalShell Pro 专业版");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel featurePanel = new JPanel();
        featurePanel.setLayout(new BoxLayout(featurePanel, BoxLayout.Y_AXIS));
        featurePanel.setBorder(BorderFactory.createTitledBorder("专业版功能"));
        
        String[] features = {
            "• 云同步 - 多设备配置同步",
            "• 高级终端 - 更多终端配色方案",
            "• 批量执行 - 多服务器命令批量执行",
            "• 高级监控 - 更详细的系统监控",
            "• 脚本管理 - 高级脚本管理功能",
            "• 优先支持 - 专属技术支持服务"
        };
        
        for (String feature : features) {
            JLabel label = new JLabel(feature);
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            featurePanel.add(label);
        }
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        upgradeButton = new JButton("升级到 Pro");
        closeButton = new JButton("稍后再说");
        
        upgradeButton.setPreferredSize(new Dimension(120, 30));
        closeButton.setPreferredSize(new Dimension(120, 30));
        
        upgradeButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                "请访问官网获取专业版授权", "提示", JOptionPane.INFORMATION_MESSAGE);
        });
        closeButton.addActionListener(e -> dispose());
        
        buttonPanel.add(upgradeButton);
        buttonPanel.add(closeButton);
        
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(featurePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
}
