package com.finalshell.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 参数匹配结果
 * 存储参数匹配的位置和值
 */
public class ParamMatch {
    
    private String name;
    private String value;
    private int start;
    private int end;
    private String defaultValue;
    
    public ParamMatch() {
    }
    
    public ParamMatch(String name, int start, int end) {
        this.name = name;
        this.start = start;
        this.end = end;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public int getStart() {
        return start;
    }
    
    public void setStart(int start) {
        this.start = start;
    }
    
    public int getEnd() {
        return end;
    }
    
    public void setEnd(int end) {
        this.end = end;
    }
    
    public String getDefaultValue() {
        return defaultValue;
    }
    
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    public int getLength() {
        return end - start;
    }
    
    public String getPlaceholder() {
        return "${" + name + "}";
    }
    
    /**
     * 获取实际值（如果值为空则返回默认值）
     */
    public String getActualValue() {
        if (value != null && !value.isEmpty()) {
            return value;
        }
        return defaultValue != null ? defaultValue : "";
    }
    
    /**
     * 从文本中查找参数匹配
     */
    public static java.util.List<ParamMatch> findMatches(String text) {
        java.util.List<ParamMatch> matches = new java.util.ArrayList<>();
        if (text == null || text.isEmpty()) {
            return matches;
        }
        
        Pattern pattern = Pattern.compile("\\$\\{([^}:]+)(?::([^}]*))?\\}");
        Matcher matcher = pattern.matcher(text);
        
        while (matcher.find()) {
            ParamMatch match = new ParamMatch();
            match.setName(matcher.group(1));
            match.setStart(matcher.start());
            match.setEnd(matcher.end());
            if (matcher.groupCount() > 1 && matcher.group(2) != null) {
                match.setDefaultValue(matcher.group(2));
            }
            matches.add(match);
        }
        
        return matches;
    }
    
    @Override
    public String toString() {
        return "ParamMatch{name='" + name + "', start=" + start + ", end=" + end + "}";
    }
}
