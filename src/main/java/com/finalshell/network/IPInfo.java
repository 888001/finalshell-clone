package com.finalshell.network;

/**
 * IP地理位置信息 - 对齐原版myssh实现
 * 
 * Based on analysis of myssh/IPInfo.java (66行)
 * 包含地理位置解析和字符串处理功能
 */
public class IPInfo {
    
    private String country;     // 国家
    private String province;    // 省份/地区  
    private String city;        // 城市
    private String isp;         // 运营商
    private String locationInfo; // 位置信息字符串
    
    public IPInfo() {}
    
    /**
     * 获取完整位置信息字符串 - 对齐原版myssh逻辑
     */
    public String getLocationString() {
        StringBuilder newS = new StringBuilder();
        
        if (this.country != null) {
            String pc = "";
            if (!this.province.equals(this.city)) {
                pc = this.province + this.city;
            } else {
                pc = this.city;
            }
            newS.append(this.country).append(pc);
            
            if (this.locationInfo != null) {
                newS.append(this.locationInfo);
            }
        }
        
        // 清理字符串 - 对齐原版myssh逻辑
        String result = newS.toString();
        result = result.replaceAll("中国", "");
        result = result.replaceAll("ipip.net", "");
        result = result.replaceAll("ipip", "");
        
        return result;
    }
    
    // Getter and Setter methods - 对齐原版myssh
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getProvince() {
        return province;
    }
    
    public void setProvince(String province) {
        this.province = province;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getIsp() {
        return isp;
    }
    
    public void setIsp(String isp) {
        this.isp = isp;
    }
    
    public String getLocationInfo() {
        return locationInfo;
    }
    
    public void setLocationInfo(String locationInfo) {
        this.locationInfo = locationInfo;
    }
}
