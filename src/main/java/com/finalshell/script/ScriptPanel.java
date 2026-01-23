package com.finalshell.script;

import com.finalshell.ssh.SSHSession;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

/**
 * 脚本执行面板
 */
public class ScriptPanel extends JPanel {
    private SSHSession session;
    private ScriptEngine scriptEngine;
    
    private JTextArea scriptArea;
    private JTextArea outputArea;
    private JButton runBtn;
    private JButton stopBtn;
    private JButton loadBtn;
    private JButton saveBtn;
    private JButton clearBtn;
    private JLabel statusLabel;
    
    private File currentFile;
    
    public ScriptPanel() {
        this(null);
    }
    
    public ScriptPanel(SSHSession session) {
        this.session = session;
        this.scriptEngine = new ScriptEngine();
        
        if (session != null) {
            scriptEngine.setSession(session);
        }
        
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        initComponents();
        setupCallback();
    }
    
    private void initComponents() {
        // 工具栏
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        runBtn = new JButton("运行");
        stopBtn = new JButton("停止");
        loadBtn = new JButton("加载");
        saveBtn = new JButton("保存");
        clearBtn = new JButton("清空输出");
        
        stopBtn.setEnabled(false);
        
        runBtn.addActionListener(e -> runScript());
        stopBtn.addActionListener(e -> stopScript());
        loadBtn.addActionListener(e -> loadScript());
        saveBtn.addActionListener(e -> saveScript());
        clearBtn.addActionListener(e -> outputArea.setText(""));
        
        toolBar.add(runBtn);
        toolBar.add(stopBtn);
        toolBar.addSeparator();
        toolBar.add(loadBtn);
        toolBar.add(saveBtn);
        toolBar.addSeparator();
        toolBar.add(clearBtn);
        
        add(toolBar, BorderLayout.NORTH);
        
        // 脚本编辑区
        scriptArea = new JTextArea();
        scriptArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        scriptArea.setTabSize(4);
        scriptArea.setText(getDefaultScript());
        
        // 输出区
        outputArea = new JTextArea();
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        outputArea.setEditable(false);
        outputArea.setBackground(new Color(30, 30, 30));
        outputArea.setForeground(new Color(200, 200, 200));
        
        // 分割面板
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
            createTitledPanel("脚本", new JScrollPane(scriptArea)),
            createTitledPanel("输出", new JScrollPane(outputArea)));
        splitPane.setResizeWeight(0.6);
        add(splitPane, BorderLayout.CENTER);
        
        // 状态栏
        statusLabel = new JLabel("就绪");
        add(statusLabel, BorderLayout.SOUTH);
    }
    
    private JPanel createTitledPanel(String title, JComponent content) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }
    
    private void setupCallback() {
        scriptEngine.setCallback(new ScriptEngine.ScriptCallback() {
            @Override
            public void onStart() {
                SwingUtilities.invokeLater(() -> {
                    runBtn.setEnabled(false);
                    stopBtn.setEnabled(true);
                    statusLabel.setText("运行中...");
                    appendOutput("=== 脚本开始执行 ===\n");
                });
            }
            
            @Override
            public void onOutput(String text) {
                SwingUtilities.invokeLater(() -> appendOutput(text));
            }
            
            @Override
            public void onComplete(Object result) {
                SwingUtilities.invokeLater(() -> {
                    if (result != null) {
                        appendOutput("\n返回值: " + result);
                    }
                    appendOutput("\n=== 脚本执行完成 ===\n");
                    scriptComplete();
                });
            }
            
            @Override
            public void onError(String error) {
                SwingUtilities.invokeLater(() -> {
                    appendOutput("\n错误: " + error + "\n");
                    scriptComplete();
                });
            }
        });
    }
    
    private void scriptComplete() {
        runBtn.setEnabled(true);
        stopBtn.setEnabled(false);
        statusLabel.setText("就绪");
    }
    
    private void appendOutput(String text) {
        outputArea.append(text);
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }
    
    private void runScript() {
        String script = scriptArea.getText();
        if (script.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入脚本内容", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        scriptEngine.execute(script);
    }
    
    private void stopScript() {
        scriptEngine.stop();
        appendOutput("\n=== 脚本已停止 ===\n");
        scriptComplete();
    }
    
    private void loadScript() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JavaScript文件", "js"));
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            currentFile = chooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(currentFile))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                scriptArea.setText(sb.toString());
                statusLabel.setText("已加载: " + currentFile.getName());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "加载失败: " + e.getMessage(), 
                    "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void saveScript() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JavaScript文件", "js"));
        
        if (currentFile != null) {
            chooser.setSelectedFile(currentFile);
        }
        
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            currentFile = chooser.getSelectedFile();
            if (!currentFile.getName().endsWith(".js")) {
                currentFile = new File(currentFile.getAbsolutePath() + ".js");
            }
            
            try (FileWriter writer = new FileWriter(currentFile)) {
                writer.write(scriptArea.getText());
                statusLabel.setText("已保存: " + currentFile.getName());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "保存失败: " + e.getMessage(),
                    "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private String getDefaultScript() {
        return "// FinalShell 脚本示例\n" +
               "// 可用API:\n" +
               "//   print(text)     - 输出文本\n" +
               "//   println(text)   - 输出文本并换行\n" +
               "//   sleep(ms)       - 延时毫秒\n" +
               "//   ssh.exec(cmd)   - 执行SSH命令\n" +
               "//   ssh.isConnected() - 检查连接状态\n" +
               "//   utils.date()    - 获取当前时间\n" +
               "//   utils.timestamp() - 获取时间戳\n" +
               "\n" +
               "println('Hello, FinalShell Script!');\n" +
               "println('当前时间: ' + utils.date());\n" +
               "\n" +
               "if (ssh.isConnected()) {\n" +
               "    println('\\n执行远程命令...');\n" +
               "    var result = ssh.exec('uname -a');\n" +
               "    println(result);\n" +
               "} else {\n" +
               "    println('SSH未连接');\n" +
               "}\n";
    }
    
    public void setSession(SSHSession session) {
        this.session = session;
        scriptEngine.setSession(session);
    }
    
    public void cleanup() {
        scriptEngine.shutdown();
    }
}
