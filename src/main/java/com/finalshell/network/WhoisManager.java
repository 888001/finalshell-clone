package com.finalshell.network;

/**
 * Whois管理器
 * 管理Whois查询服务
 */
public class WhoisManager {
    
    private static WhoisManager instance;
    private WhoisQuery query;
    
    private WhoisManager() {
        this.query = new WhoisQuery();
    }
    
    public static synchronized WhoisManager getInstance() {
        if (instance == null) {
            instance = new WhoisManager();
        }
        return instance;
    }
    
    public String query(String domain) {
        return query.query(domain);
    }
    
    public WhoisQuery getQuery() {
        return query;
    }
}
