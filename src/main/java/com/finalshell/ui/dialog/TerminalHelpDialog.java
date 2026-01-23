package com.finalshell.ui.dialog;

import javax.swing.*;
import java.awt.*;

/**
 * 终端帮助对话框
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Dialog_Tree_Module_Analysis.md
 */
public class TerminalHelpDialog extends JDialog {
    
    public TerminalHelpDialog(Frame owner) {
        super(owner, "终端快捷键帮助", true);
        initUI();
    }
    
    private void initUI() {
        setSize(450, 400);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));
        
        JTextPane helpPane = new JTextPane();
        helpPane.setContentType("text/html");
        helpPane.setEditable(false);
        helpPane.setText(getHelpContent());
        helpPane.setCaretPosition(0);
        
        JScrollPane scrollPane = new JScrollPane(helpPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton closeButton = new JButton("关闭");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private String getHelpContent() {
        return "<html><body style='font-family:sans-serif;font-size:12px;'>" +
            "<h2>终端快捷键</h2>" +
            "<table border='0' cellpadding='5'>" +
            "<tr><td><b>Ctrl+C</b></td><td>复制选中文本 / 中断当前命令</td></tr>" +
            "<tr><td><b>Ctrl+V</b></td><td>粘贴</td></tr>" +
            "<tr><td><b>Ctrl+Shift+C</b></td><td>复制</td></tr>" +
            "<tr><td><b>Ctrl+Shift+V</b></td><td>粘贴</td></tr>" +
            "<tr><td><b>Ctrl+A</b></td><td>全选</td></tr>" +
            "<tr><td><b>Ctrl+L</b></td><td>清屏</td></tr>" +
            "<tr><td><b>Ctrl+U</b></td><td>清除当前行</td></tr>" +
            "<tr><td><b>Ctrl+W</b></td><td>删除前一个单词</td></tr>" +
            "<tr><td><b>Ctrl+K</b></td><td>删除到行尾</td></tr>" +
            "<tr><td><b>Ctrl+D</b></td><td>退出 / 删除光标处字符</td></tr>" +
            "<tr><td><b>Ctrl+Z</b></td><td>后台运行</td></tr>" +
            "<tr><td><b>Tab</b></td><td>自动补全</td></tr>" +
            "<tr><td><b>↑ / ↓</b></td><td>历史命令</td></tr>" +
            "<tr><td><b>Ctrl+R</b></td><td>搜索历史命令</td></tr>" +
            "<tr><td><b>Ctrl+F</b></td><td>搜索</td></tr>" +
            "<tr><td><b>Ctrl++</b></td><td>放大字体</td></tr>" +
            "<tr><td><b>Ctrl+-</b></td><td>缩小字体</td></tr>" +
            "<tr><td><b>Ctrl+0</b></td><td>重置字体大小</td></tr>" +
            "</table>" +
            "<h2>鼠标操作</h2>" +
            "<table border='0' cellpadding='5'>" +
            "<tr><td><b>左键拖动</b></td><td>选择文本</td></tr>" +
            "<tr><td><b>双击</b></td><td>选择单词</td></tr>" +
            "<tr><td><b>三击</b></td><td>选择整行</td></tr>" +
            "<tr><td><b>右键</b></td><td>弹出菜单</td></tr>" +
            "<tr><td><b>滚轮</b></td><td>滚动</td></tr>" +
            "<tr><td><b>Ctrl+滚轮</b></td><td>缩放</td></tr>" +
            "</table>" +
            "</body></html>";
    }
}
