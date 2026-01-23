package com.finalshell.terminal;

import javax.swing.*;
import java.awt.*;

/**
 * 终端帮助对话框
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class TerminalHelpDialog extends JDialog {
    
    public TerminalHelpDialog(Window owner) {
        super(owner, "终端帮助", ModalityType.APPLICATION_MODAL);
        initUI();
        setSize(500, 400);
        setLocationRelativeTo(owner);
    }
    
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextArea helpArea = new JTextArea();
        helpArea.setEditable(false);
        helpArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        helpArea.setText(getHelpText());
        
        JScrollPane scrollPane = new JScrollPane(helpArea);
        
        JButton closeButton = new JButton("关闭");
        closeButton.addActionListener(e -> dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private String getHelpText() {
        return "=== 终端快捷键 ===\n\n" +
               "Ctrl+C        中断当前命令\n" +
               "Ctrl+L        清屏\n" +
               "Ctrl+D        发送EOF\n" +
               "Ctrl+Z        挂起进程\n" +
               "Tab           自动补全\n" +
               "↑/↓           浏览历史命令\n" +
               "Ctrl+R        搜索历史命令\n" +
               "Ctrl+A        移动到行首\n" +
               "Ctrl+E        移动到行尾\n" +
               "Ctrl+U        删除光标前内容\n" +
               "Ctrl+K        删除光标后内容\n" +
               "Ctrl+W        删除前一个单词\n\n" +
               "=== 常用命令 ===\n\n" +
               "ls            列出目录内容\n" +
               "cd <dir>      切换目录\n" +
               "pwd           显示当前目录\n" +
               "cat <file>    查看文件内容\n" +
               "grep <pat>    搜索文本\n" +
               "find          查找文件\n" +
               "ps            查看进程\n" +
               "top           系统监控\n" +
               "df -h         查看磁盘空间\n" +
               "free -h       查看内存使用\n";
    }
}
