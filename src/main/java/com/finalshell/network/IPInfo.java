package com.finalshell.network;

/**
 * IP地理位置信息
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: MysshRoot_Classes_DeepAnalysis.md - IPInfo
 */
public class IPInfo {
    
    private String ip;
    private String country;
    private String province;
    private String city;
    private String isp;
    private String locationInfo;
    
    public IPInfo() {}
    
    public IPInfo(String ip) {
        this.ip = ip;
    }
    
    /**
     * 获取完整位置信息
     */
    public String getLocationInfo() {
        if (locationInfo != null && !locationInfo.isEmpty()) {
            return locationInfo;
        }
        
        StringBuilder sb = new StringBuilder();
        if (country != null && !country.isEmpty()) {
            sb.append(country);
        }
        if (province != null && !province.isEmpty()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(province);
        }
        if (city != null && !city.isEmpty()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(city);
        }
        if (isp != null && !isp.isEmpty()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(isp);
        }
        
        return sb.length() > 0 ? sb.toString() : "未知位置";
    }
    
    // Getters and Setters
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getIsp() { return isp; }
    public void setIsp(String isp) { this.isp = isp; }
    
    public void setLocationInfo(String locationInfo) { 
        this.locationInfo = locationInfo; 
    }
    
    @Override
    public String toString() {
        return String.format("IPInfo[ip=%s, location=%s]", ip, getLocationInfo());
    }
}
