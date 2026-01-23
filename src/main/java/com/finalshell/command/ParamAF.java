package com.finalshell.command;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 参数自动填充
 * 用于解析命令中的参数占位符并自动填充
 */
public class ParamAF {
    
    private static final Pattern PARAM_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");
    
    /**
     * 解析命令中的参数
     * @param command 包含参数占位符的命令
     * @return 参数名列表
     */
    public static List<String> parseParams(String command) {
        List<String> params = new ArrayList<>();
        if (command == null || command.isEmpty()) {
            return params;
        }
        
        Matcher matcher = PARAM_PATTERN.matcher(command);
        while (matcher.find()) {
            String param = matcher.group(1);
            if (!params.contains(param)) {
                params.add(param);
            }
        }
        return params;
    }
    
    /**
     * 填充命令参数
     * @param command 包含参数占位符的命令
     * @param values 参数值映射
     * @return 填充后的命令
     */
    public static String fillParams(String command, java.util.Map<String, String> values) {
        if (command == null || values == null) {
            return command;
        }
        
        String result = command;
        for (java.util.Map.Entry<String, String> entry : values.entrySet()) {
            String placeholder = "${" + entry.getKey() + "}";
            result = result.replace(placeholder, entry.getValue());
        }
        return result;
    }
    
    /**
     * 显示参数输入对话框
     * @param parent 父组件
     * @param command 命令
     * @param params 参数列表
     * @return 参数值映射，如果取消返回null
     */
    public static java.util.Map<String, String> showParamDialog(Component parent, String command, List<String> params) {
        if (params == null || params.isEmpty()) {
            return new java.util.HashMap<>();
        }
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        List<JTextField> fields = new ArrayList<>();
        
        for (int i = 0; i < params.size(); i++) {
            gbc.gridx = 0; gbc.gridy = i;
            panel.add(new JLabel(params.get(i) + ":"), gbc);
            
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            JTextField field = new JTextField(20);
            fields.add(field);
            panel.add(field, gbc);
        }
        
        int result = JOptionPane.showConfirmDialog(parent, panel, "输入参数", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            java.util.Map<String, String> values = new java.util.HashMap<>();
            for (int i = 0; i < params.size(); i++) {
                values.put(params.get(i), fields.get(i).getText());
            }
            return values;
        }
        return null;
    }
    
    /**
     * 处理命令：解析参数、显示对话框、填充参数
     * @param parent 父组件
     * @param command 原始命令
     * @return 填充后的命令，如果取消返回null
     */
    public static String processCommand(Component parent, String command) {
        List<String> params = parseParams(command);
        if (params.isEmpty()) {
            return command;
        }
        
        java.util.Map<String, String> values = showParamDialog(parent, command, params);
        if (values == null) {
            return null;
        }
        
        return fillParams(command, values);
    }
}
