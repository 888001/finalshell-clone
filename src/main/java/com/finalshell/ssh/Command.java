package com.finalshell.ssh;

import javax.swing.Icon;

/**
 * 命令对象封装
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: MysshRoot_Classes_DeepAnalysis.md - Command
 */
public class Command {
    
    private String text;
    private String name;
    private int index;
    private Object tabButton;
    private int matchCount;
    private String type = "";
    private SSHFile sshFile;
    private Icon icon;
    private boolean highlight;
    private long activeTime;
    
    public Command() {}
    
    public Command(String name, String text) {
        this.name = name;
        this.text = text;
    }
    
    // Getters and Setters
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }
    
    public Object getTabButton() { return tabButton; }
    public void setTabButton(Object tabButton) { this.tabButton = tabButton; }
    
    public int getMatchCount() { return matchCount; }
    public void setMatchCount(int matchCount) { this.matchCount = matchCount; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public SSHFile getSshFile() { return sshFile; }
    public void setSshFile(SSHFile sshFile) { this.sshFile = sshFile; }
    
    public Icon getIcon() { return icon; }
    public void setIcon(Icon icon) { this.icon = icon; }
    
    public boolean isHighlight() { return highlight; }
    public void setHighlight(boolean highlight) { this.highlight = highlight; }
    
    public long getActiveTime() { return activeTime; }
    public void setActiveTime(long activeTime) { this.activeTime = activeTime; }
    
    @Override
    public String toString() {
        return name != null ? name : text;
    }
}
