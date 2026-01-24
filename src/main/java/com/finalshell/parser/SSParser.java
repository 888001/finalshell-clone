package com.finalshell.parser;

import com.finalshell.monitor.parser.BaseParser;

import java.util.*;
import java.util.regex.*;

/**
 * ss命令解析器
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class SSParser extends BaseParser {
    
    private List<SSRecord> records;
    
    public SSParser() {
        this.records = new ArrayList<>();
    }
    
    @Override
    public void parse() {
        if (rawOutput == null || rawOutput.isEmpty()) return;
        
        records.clear();
        String[] lines = rawOutput.split("\n");
        boolean headerSkipped = false;
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            
            if (!headerSkipped) {
                headerSkipped = true;
                continue;
            }
            
            SSRecord record = parseLine(line);
            if (record != null) {
                records.add(record);
            }
        }
    }
    
    private SSRecord parseLine(String line) {
        String[] parts = line.split("\\s+");
        if (parts.length < 5) return null;
        
        SSRecord record = new SSRecord();
        record.setState(parts[0]);
        record.setRecvQ(parts[1]);
        record.setSendQ(parts[2]);
        record.setLocalAddress(parts[3]);
        record.setPeerAddress(parts[4]);
        
        if (parts.length > 5) {
            record.setProcess(parts[5]);
        }
        
        return record;
    }
    
    public List<SSRecord> getRecords() {
        return records;
    }
    
    public List<SSRecord> getListeningRecords() {
        List<SSRecord> result = new ArrayList<>();
        for (SSRecord record : records) {
            if ("LISTEN".equals(record.getState())) {
                result.add(record);
            }
        }
        return result;
    }
    
    public List<SSRecord> getEstablishedRecords() {
        List<SSRecord> result = new ArrayList<>();
        for (SSRecord record : records) {
            if ("ESTAB".equals(record.getState())) {
                result.add(record);
            }
        }
        return result;
    }
}
