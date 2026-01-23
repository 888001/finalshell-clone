package com.finalshell.sync;

import java.io.Serializable;
import java.util.*;

/**
 * 邮件头信息
 * 存储邮件/消息的头部元数据
 */
public class MailHeadInfo implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private EmailID emailId;
    private String subject;
    private String from;
    private List<String> to = new ArrayList<>();
    private List<String> cc = new ArrayList<>();
    private Date sentDate;
    private Date receivedDate;
    private long size;
    private boolean read;
    private boolean flagged;
    private String contentType;
    private Map<String, String> headers = new HashMap<>();
    
    public MailHeadInfo() {}
    
    public MailHeadInfo(EmailID emailId, String subject) {
        this.emailId = emailId;
        this.subject = subject;
    }
    
    public EmailID getEmailId() { return emailId; }
    public void setEmailId(EmailID emailId) { this.emailId = emailId; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }
    
    public List<String> getTo() { return to; }
    public void setTo(List<String> to) { this.to = to; }
    public void addTo(String address) { this.to.add(address); }
    
    public List<String> getCc() { return cc; }
    public void setCc(List<String> cc) { this.cc = cc; }
    public void addCc(String address) { this.cc.add(address); }
    
    public Date getSentDate() { return sentDate; }
    public void setSentDate(Date sentDate) { this.sentDate = sentDate; }
    
    public Date getReceivedDate() { return receivedDate; }
    public void setReceivedDate(Date receivedDate) { this.receivedDate = receivedDate; }
    
    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }
    
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
    
    public boolean isFlagged() { return flagged; }
    public void setFlagged(boolean flagged) { this.flagged = flagged; }
    
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    
    public Map<String, String> getHeaders() { return headers; }
    public void setHeaders(Map<String, String> headers) { this.headers = headers; }
    public void addHeader(String name, String value) { this.headers.put(name, value); }
    public String getHeader(String name) { return headers.get(name); }
}
