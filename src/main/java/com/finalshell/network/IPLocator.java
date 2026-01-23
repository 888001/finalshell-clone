package com.finalshell.network;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

/**
 * IP地理位置查询工具
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: IPLoc_BufferedWrap_Decoder_DeepAnalysis.md
 */
public class IPLocator {
    
    private static final Logger logger = LoggerFactory.getLogger(IPLocator.class);
    
    private static final String[] IP_SERVICES = {
        "http://ip-api.com/json/%s?lang=zh-CN",
        "https://ipapi.co/%s/json/"
    };
    
    /**
     * 查询IP地理位置
     */
    public static IPInfo lookup(String ip) {
        for (String service : IP_SERVICES) {
            try {
                String url = String.format(service, ip);
                String response = httpGet(url);
                if (response != null) {
                    return parseResponse(response, service);
                }
            } catch (Exception e) {
                logger.warn("IP lookup failed with service: " + service, e);
            }
        }
        return null;
    }
    
    /**
     * 获取本机公网IP
     */
    public static String getPublicIP() {
        String[] services = {
            "https://api.ipify.org",
            "https://icanhazip.com",
            "https://ifconfig.me/ip"
        };
        
        for (String service : services) {
            try {
                String ip = httpGet(service);
                if (ip != null) {
                    return ip.trim();
                }
            } catch (Exception e) {
                logger.debug("Failed to get public IP from: " + service, e);
            }
        }
        return null;
    }
    
    private static String httpGet(String urlStr) throws IOException {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestProperty("User-Agent", "FinalShell-Clone/1.0");
            
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                return null;
            }
            
            try (InputStream is = conn.getInputStream();
                 BufferedReader reader = new BufferedReader(
                     new InputStreamReader(is, StandardCharsets.UTF_8))) {
                
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                return sb.toString();
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
    
    private static IPInfo parseResponse(String response, String service) {
        try {
            JSONObject json = JSON.parseObject(response);
            IPInfo info = new IPInfo();
            
            if (service.contains("ip-api.com")) {
                info.setIp(json.getString("query"));
                info.setCountry(json.getString("country"));
                info.setCountryCode(json.getString("countryCode"));
                info.setRegion(json.getString("regionName"));
                info.setCity(json.getString("city"));
                info.setIsp(json.getString("isp"));
                info.setOrg(json.getString("org"));
                info.setLatitude(json.getDoubleValue("lat"));
                info.setLongitude(json.getDoubleValue("lon"));
                info.setTimezone(json.getString("timezone"));
            } else if (service.contains("ipapi.co")) {
                info.setIp(json.getString("ip"));
                info.setCountry(json.getString("country_name"));
                info.setCountryCode(json.getString("country_code"));
                info.setRegion(json.getString("region"));
                info.setCity(json.getString("city"));
                info.setIsp(json.getString("org"));
                info.setLatitude(json.getDoubleValue("latitude"));
                info.setLongitude(json.getDoubleValue("longitude"));
                info.setTimezone(json.getString("timezone"));
            }
            
            return info;
        } catch (Exception e) {
            logger.error("Failed to parse IP info response", e);
            return null;
        }
    }
    
    /**
     * IP信息数据类
     */
    public static class IPInfo {
        private String ip;
        private String country;
        private String countryCode;
        private String region;
        private String city;
        private String isp;
        private String org;
        private double latitude;
        private double longitude;
        private String timezone;
        
        public String getIp() { return ip; }
        public void setIp(String ip) { this.ip = ip; }
        
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        
        public String getCountryCode() { return countryCode; }
        public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
        
        public String getRegion() { return region; }
        public void setRegion(String region) { this.region = region; }
        
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        
        public String getIsp() { return isp; }
        public void setIsp(String isp) { this.isp = isp; }
        
        public String getOrg() { return org; }
        public void setOrg(String org) { this.org = org; }
        
        public double getLatitude() { return latitude; }
        public void setLatitude(double latitude) { this.latitude = latitude; }
        
        public double getLongitude() { return longitude; }
        public void setLongitude(double longitude) { this.longitude = longitude; }
        
        public String getTimezone() { return timezone; }
        public void setTimezone(String timezone) { this.timezone = timezone; }
        
        public String getLocation() {
            StringBuilder sb = new StringBuilder();
            if (country != null) sb.append(country);
            if (region != null) sb.append(" ").append(region);
            if (city != null) sb.append(" ").append(city);
            return sb.toString().trim();
        }
        
        @Override
        public String toString() {
            return String.format("%s [%s] - %s", ip, getLocation(), isp);
        }
    }
}
