package com.finalshell.permission;

/**
 * 文件权限
 */
public class FilePermission {
    private String path;
    private String owner;
    private String group;
    private int mode;        // 八进制权限值 (如 755)
    private String modeString; // 权限字符串 (如 rwxr-xr-x)
    
    // 权限位
    private boolean ownerRead;
    private boolean ownerWrite;
    private boolean ownerExecute;
    private boolean groupRead;
    private boolean groupWrite;
    private boolean groupExecute;
    private boolean otherRead;
    private boolean otherWrite;
    private boolean otherExecute;
    
    // 特殊权限
    private boolean setuid;
    private boolean setgid;
    private boolean sticky;
    
    public FilePermission() {}
    
    public FilePermission(String path) {
        this.path = path;
    }
    
    /**
     * 从权限字符串解析 (如 -rwxr-xr-x)
     */
    public static FilePermission fromModeString(String path, String modeStr) {
        FilePermission perm = new FilePermission(path);
        perm.modeString = modeStr;
        
        if (modeStr.length() >= 10) {
            // Owner
            perm.ownerRead = modeStr.charAt(1) == 'r';
            perm.ownerWrite = modeStr.charAt(2) == 'w';
            perm.ownerExecute = modeStr.charAt(3) == 'x' || modeStr.charAt(3) == 's';
            perm.setuid = modeStr.charAt(3) == 's' || modeStr.charAt(3) == 'S';
            
            // Group
            perm.groupRead = modeStr.charAt(4) == 'r';
            perm.groupWrite = modeStr.charAt(5) == 'w';
            perm.groupExecute = modeStr.charAt(6) == 'x' || modeStr.charAt(6) == 's';
            perm.setgid = modeStr.charAt(6) == 's' || modeStr.charAt(6) == 'S';
            
            // Other
            perm.otherRead = modeStr.charAt(7) == 'r';
            perm.otherWrite = modeStr.charAt(8) == 'w';
            perm.otherExecute = modeStr.charAt(9) == 'x' || modeStr.charAt(9) == 't';
            perm.sticky = modeStr.charAt(9) == 't' || modeStr.charAt(9) == 'T';
        }
        
        perm.mode = perm.calculateMode();
        return perm;
    }
    
    /**
     * 从八进制权限值解析
     */
    public static FilePermission fromMode(String path, int mode) {
        FilePermission perm = new FilePermission(path);
        perm.mode = mode;
        
        // Special bits
        perm.setuid = (mode & 04000) != 0;
        perm.setgid = (mode & 02000) != 0;
        perm.sticky = (mode & 01000) != 0;
        
        // Owner
        perm.ownerRead = (mode & 0400) != 0;
        perm.ownerWrite = (mode & 0200) != 0;
        perm.ownerExecute = (mode & 0100) != 0;
        
        // Group
        perm.groupRead = (mode & 040) != 0;
        perm.groupWrite = (mode & 020) != 0;
        perm.groupExecute = (mode & 010) != 0;
        
        // Other
        perm.otherRead = (mode & 04) != 0;
        perm.otherWrite = (mode & 02) != 0;
        perm.otherExecute = (mode & 01) != 0;
        
        perm.modeString = perm.toModeString();
        return perm;
    }
    
    /**
     * 计算八进制权限值
     */
    public int calculateMode() {
        int m = 0;
        
        // Special
        if (setuid) m |= 04000;
        if (setgid) m |= 02000;
        if (sticky) m |= 01000;
        
        // Owner
        if (ownerRead) m |= 0400;
        if (ownerWrite) m |= 0200;
        if (ownerExecute) m |= 0100;
        
        // Group
        if (groupRead) m |= 040;
        if (groupWrite) m |= 020;
        if (groupExecute) m |= 010;
        
        // Other
        if (otherRead) m |= 04;
        if (otherWrite) m |= 02;
        if (otherExecute) m |= 01;
        
        return m;
    }
    
    /**
     * 转换为权限字符串
     */
    public String toModeString() {
        StringBuilder sb = new StringBuilder("-");
        
        // Owner
        sb.append(ownerRead ? 'r' : '-');
        sb.append(ownerWrite ? 'w' : '-');
        if (setuid) {
            sb.append(ownerExecute ? 's' : 'S');
        } else {
            sb.append(ownerExecute ? 'x' : '-');
        }
        
        // Group
        sb.append(groupRead ? 'r' : '-');
        sb.append(groupWrite ? 'w' : '-');
        if (setgid) {
            sb.append(groupExecute ? 's' : 'S');
        } else {
            sb.append(groupExecute ? 'x' : '-');
        }
        
        // Other
        sb.append(otherRead ? 'r' : '-');
        sb.append(otherWrite ? 'w' : '-');
        if (sticky) {
            sb.append(otherExecute ? 't' : 'T');
        } else {
            sb.append(otherExecute ? 'x' : '-');
        }
        
        return sb.toString();
    }
    
    /**
     * 获取八进制字符串表示 (如 0755)
     */
    public String getOctalString() {
        return String.format("%04o", calculateMode());
    }
    
    /**
     * 获取简短八进制表示 (如 755)
     */
    public String getShortOctalString() {
        int m = calculateMode();
        if (m > 0777) {
            return String.format("%04o", m);
        }
        return String.format("%03o", m);
    }
    
    // Getters and Setters
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    
    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
    
    public String getGroup() { return group; }
    public void setGroup(String group) { this.group = group; }
    
    public int getMode() { return mode; }
    public void setMode(int mode) { this.mode = mode; }
    
    public boolean isOwnerRead() { return ownerRead; }
    public void setOwnerRead(boolean ownerRead) { this.ownerRead = ownerRead; }
    
    public boolean isOwnerWrite() { return ownerWrite; }
    public void setOwnerWrite(boolean ownerWrite) { this.ownerWrite = ownerWrite; }
    
    public boolean isOwnerExecute() { return ownerExecute; }
    public void setOwnerExecute(boolean ownerExecute) { this.ownerExecute = ownerExecute; }
    
    public boolean isGroupRead() { return groupRead; }
    public void setGroupRead(boolean groupRead) { this.groupRead = groupRead; }
    
    public boolean isGroupWrite() { return groupWrite; }
    public void setGroupWrite(boolean groupWrite) { this.groupWrite = groupWrite; }
    
    public boolean isGroupExecute() { return groupExecute; }
    public void setGroupExecute(boolean groupExecute) { this.groupExecute = groupExecute; }
    
    public boolean isOtherRead() { return otherRead; }
    public void setOtherRead(boolean otherRead) { this.otherRead = otherRead; }
    
    public boolean isOtherWrite() { return otherWrite; }
    public void setOtherWrite(boolean otherWrite) { this.otherWrite = otherWrite; }
    
    public boolean isOtherExecute() { return otherExecute; }
    public void setOtherExecute(boolean otherExecute) { this.otherExecute = otherExecute; }
    
    public boolean isSetuid() { return setuid; }
    public void setSetuid(boolean setuid) { this.setuid = setuid; }
    
    public boolean isSetgid() { return setgid; }
    public void setSetgid(boolean setgid) { this.setgid = setgid; }
    
    public boolean isSticky() { return sticky; }
    public void setSticky(boolean sticky) { this.sticky = sticky; }
}
