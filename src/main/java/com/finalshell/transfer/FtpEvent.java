package com.finalshell.transfer;

import com.finalshell.ui.VDir;
import com.finalshell.ui.VFile;

/**
 * FTP Event
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: FtpTransfer_Event_DeepAnalysis.md - FtpEvent
 */
public class FtpEvent {
    
    public static final int EVENT_LS = 100;
    public static final int EVENT_STAT = 101;
    
    private int type;
    private VDir lsDir;
    private VFile statFile;
    
    public FtpEvent(int type) {
        this.type = type;
    }
    
    public int getType() { return type; }
    public void setType(int type) { this.type = type; }
    
    public VDir getLsDir() { return lsDir; }
    public void setLsDir(VDir lsDir) { this.lsDir = lsDir; }
    
    public VFile getStatFile() { return statFile; }
    public void setStatFile(VFile statFile) { this.statFile = statFile; }
}