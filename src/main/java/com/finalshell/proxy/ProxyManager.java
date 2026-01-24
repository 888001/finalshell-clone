package com.finalshell.proxy;

import java.util.ArrayList;
import java.util.List;
import java.net.Proxy;
import java.net.InetSocketAddress;

/**
 * 代理管理器
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class ProxyManager {
    
    private static ProxyManager instance;
    private List<ProxyInfo> proxyList;
    private ProxyInfo defaultProxy;
    
    private ProxyManager() {
        this.proxyList = new ArrayList<>();
    }
    
    public static synchronized ProxyManager getInstance() {
        if (instance == null) {
            instance = new ProxyManager();
        }
        return instance;
    }
    
    public void addProxy(ProxyInfo proxy) {
        proxyList.add(proxy);
        if (proxy.isDefault()) {
            setDefaultProxy(proxy);
        }
    }
    
    public void removeProxy(ProxyInfo proxy) {
        proxyList.remove(proxy);
        if (proxy == defaultProxy) {
            defaultProxy = null;
        }
    }
    
    public List<ProxyInfo> getProxyList() {
        return new ArrayList<>(proxyList);
    }
    
    public ProxyInfo getDefaultProxy() {
        return defaultProxy;
    }
    
    public void setDefaultProxy(ProxyInfo proxy) {
        if (defaultProxy != null) {
            defaultProxy.setDefault(false);
        }
        this.defaultProxy = proxy;
        if (proxy != null) {
            proxy.setDefault(true);
        }
    }
    
    public ProxyInfo getProxyById(String id) {
        for (ProxyInfo proxy : proxyList) {
            if (proxy.getId().equals(id)) {
                return proxy;
            }
        }
        return null;
    }
    
    public Proxy createJavaProxy(ProxyInfo info) {
        if (info == null) {
            return Proxy.NO_PROXY;
        }
        
        Proxy.Type type;
        switch (info.getType()) {
            case ProxyInfo.TYPE_SOCKS5:
            case ProxyInfo.TYPE_SOCKS4:
                type = Proxy.Type.SOCKS;
                break;
            case ProxyInfo.TYPE_HTTP:
                type = Proxy.Type.HTTP;
                break;
            default:
                return Proxy.NO_PROXY;
        }
        
        return new Proxy(type, new InetSocketAddress(info.getHost(), info.getPort()));
    }
    
    public void clear() {
        proxyList.clear();
        defaultProxy = null;
    }
}
