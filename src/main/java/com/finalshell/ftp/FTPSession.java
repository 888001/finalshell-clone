package com.finalshell.ftp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

/**
 * FTP Session - FTP protocol implementation
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class FTPSession {
    
    private static final Logger logger = LoggerFactory.getLogger(FTPSession.class);
    
    private final FTPConfig config;
    private Socket controlSocket;
    private BufferedReader reader;
    private BufferedWriter writer;
    
    private boolean connected = false;
    private String currentDir = "/";
    
    private final List<FTPListener> listeners = new ArrayList<>();
    
    public FTPSession(FTPConfig config) {
        this.config = config;
    }
    
    /**
     * Connect to FTP server
     */
    public void connect() throws IOException {
        logger.info("Connecting to FTP {}:{}", config.getHost(), config.getPort());
        
        controlSocket = new Socket();
        controlSocket.connect(new InetSocketAddress(config.getHost(), config.getPort()), 
            config.getConnectionTimeout());
        controlSocket.setSoTimeout(config.getDataTimeout());
        
        reader = new BufferedReader(new InputStreamReader(controlSocket.getInputStream(), config.getCharset()));
        writer = new BufferedWriter(new OutputStreamWriter(controlSocket.getOutputStream(), config.getCharset()));
        
        // Read welcome message
        String response = readResponse();
        if (!response.startsWith("220")) {
            throw new IOException("Invalid FTP server response: " + response);
        }
        
        // Login
        login();
        
        // Set binary mode if configured
        if (config.isBinaryMode()) {
            sendCommand("TYPE I");
        } else {
            sendCommand("TYPE A");
        }
        
        // Set passive mode if configured
        if (config.getTransferMode() == FTPConfig.TransferMode.PASSIVE) {
            // Will be set on each data transfer
        }
        
        connected = true;
        fireEvent(FTPEvent.CONNECTED, "Connected to " + config.getHost());
        
        logger.info("FTP connected and logged in");
    }
    
    /**
     * Login to FTP server
     */
    private void login() throws IOException {
        // Send username
        String response = sendCommand("USER " + config.getUsername());
        if (response.startsWith("331")) {
            // Password required
            response = sendCommand("PASS " + config.getPassword());
        }
        
        if (!response.startsWith("230")) {
            throw new IOException("Login failed: " + response);
        }
        
        logger.debug("FTP login successful");
    }
    
    /**
     * Send command and get response
     */
    public String sendCommand(String command) throws IOException {
        logger.debug("FTP > {}", command.startsWith("PASS") ? "PASS ***" : command);
        
        writer.write(command + "\r\n");
        writer.flush();
        
        return readResponse();
    }
    
    /**
     * Read multi-line response
     */
    private String readResponse() throws IOException {
        StringBuilder response = new StringBuilder();
        String line;
        
        while ((line = reader.readLine()) != null) {
            logger.debug("FTP < {}", line);
            response.append(line).append("\n");
            
            // Check if this is the last line (format: "XXX message" not "XXX-message")
            if (line.length() >= 4 && Character.isDigit(line.charAt(0)) 
                && Character.isDigit(line.charAt(1)) && Character.isDigit(line.charAt(2))
                && line.charAt(3) == ' ') {
                break;
            }
        }
        
        return response.toString().trim();
    }
    
    /**
     * Open data connection in passive mode
     */
    private Socket openDataConnection() throws IOException {
        if (config.getTransferMode() == FTPConfig.TransferMode.PASSIVE) {
            return openPassiveConnection();
        } else {
            return openActiveConnection();
        }
    }
    
    private Socket openPassiveConnection() throws IOException {
        String response = sendCommand("PASV");
        if (!response.startsWith("227")) {
            throw new IOException("PASV failed: " + response);
        }
        
        // Parse PASV response: 227 Entering Passive Mode (h1,h2,h3,h4,p1,p2)
        Pattern pattern = Pattern.compile("\\((\\d+),(\\d+),(\\d+),(\\d+),(\\d+),(\\d+)\\)");
        Matcher matcher = pattern.matcher(response);
        
        if (!matcher.find()) {
            throw new IOException("Cannot parse PASV response: " + response);
        }
        
        String host = matcher.group(1) + "." + matcher.group(2) + "." 
                    + matcher.group(3) + "." + matcher.group(4);
        int port = Integer.parseInt(matcher.group(5)) * 256 + Integer.parseInt(matcher.group(6));
        
        Socket dataSocket = new Socket();
        dataSocket.connect(new InetSocketAddress(host, port), config.getConnectionTimeout());
        return dataSocket;
    }
    
    private Socket openActiveConnection() throws IOException {
        // Create server socket
        ServerSocket serverSocket = new ServerSocket(0);
        serverSocket.setSoTimeout(config.getDataTimeout());
        
        InetAddress localAddr = controlSocket.getLocalAddress();
        int localPort = serverSocket.getLocalPort();
        
        // Send PORT command
        byte[] addr = localAddr.getAddress();
        String portCmd = String.format("PORT %d,%d,%d,%d,%d,%d",
            addr[0] & 0xff, addr[1] & 0xff, addr[2] & 0xff, addr[3] & 0xff,
            localPort / 256, localPort % 256);
        
        String response = sendCommand(portCmd);
        if (!response.startsWith("200")) {
            serverSocket.close();
            throw new IOException("PORT failed: " + response);
        }
        
        // Accept connection
        Socket dataSocket = serverSocket.accept();
        serverSocket.close();
        return dataSocket;
    }
    
    /**
     * List files in directory
     */
    public List<FTPFile> listFiles(String path) throws IOException {
        if (path == null || path.isEmpty()) {
            path = currentDir;
        }
        
        Socket dataSocket = openDataConnection();
        String response = sendCommand("LIST " + path);
        
        if (!response.startsWith("150") && !response.startsWith("125")) {
            dataSocket.close();
            throw new IOException("LIST failed: " + response);
        }
        
        List<FTPFile> files = new ArrayList<>();
        BufferedReader dataReader = new BufferedReader(
            new InputStreamReader(dataSocket.getInputStream(), config.getCharset()));
        
        String line;
        while ((line = dataReader.readLine()) != null) {
            FTPFile file = parseFTPEntry(line);
            if (file != null) {
                files.add(file);
            }
        }
        
        dataReader.close();
        dataSocket.close();
        
        // Read final response
        readResponse();
        
        return files;
    }
    
    /**
     * Parse FTP LIST entry
     */
    private FTPFile parseFTPEntry(String line) {
        // Unix format: drwxr-xr-x 2 user group 4096 Jan 1 00:00 filename
        // Windows format: 01-01-21 12:00AM <DIR> dirname
        
        if (line == null || line.isEmpty()) return null;
        
        FTPFile file = new FTPFile();
        
        // Try Unix format
        if (line.length() > 10 && (line.charAt(0) == 'd' || line.charAt(0) == '-' || line.charAt(0) == 'l')) {
            file.setDirectory(line.charAt(0) == 'd');
            file.setLink(line.charAt(0) == 'l');
            file.setPermissions(line.substring(0, 10));
            
            String[] parts = line.split("\\s+", 9);
            if (parts.length >= 9) {
                try {
                    file.setSize(Long.parseLong(parts[4]));
                } catch (NumberFormatException e) {
                    file.setSize(0);
                }
                file.setDate(parts[5] + " " + parts[6] + " " + parts[7]);
                file.setName(parts[8]);
                return file;
            }
        }
        
        // Try Windows format
        Pattern pattern = Pattern.compile("(\\d{2}-\\d{2}-\\d{2})\\s+(\\d{2}:\\d{2}[AP]M)\\s+(<DIR>|\\d+)\\s+(.+)");
        Matcher matcher = pattern.matcher(line);
        if (matcher.matches()) {
            file.setDate(matcher.group(1) + " " + matcher.group(2));
            String sizeStr = matcher.group(3);
            file.setDirectory("<DIR>".equals(sizeStr));
            if (!file.isDirectory()) {
                try {
                    file.setSize(Long.parseLong(sizeStr));
                } catch (NumberFormatException e) {
                    file.setSize(0);
                }
            }
            file.setName(matcher.group(4));
            return file;
        }
        
        return null;
    }
    
    /**
     * Download file
     */
    public void downloadFile(String remotePath, File localFile, TransferListener listener) throws IOException {
        Socket dataSocket = openDataConnection();
        String response = sendCommand("RETR " + remotePath);
        
        if (!response.startsWith("150") && !response.startsWith("125")) {
            dataSocket.close();
            throw new IOException("RETR failed: " + response);
        }
        
        // Get file size from response
        long totalSize = 0;
        Pattern sizePattern = Pattern.compile("\\((\\d+) bytes\\)");
        Matcher matcher = sizePattern.matcher(response);
        if (matcher.find()) {
            totalSize = Long.parseLong(matcher.group(1));
        }
        
        InputStream in = dataSocket.getInputStream();
        FileOutputStream out = new FileOutputStream(localFile);
        
        byte[] buffer = new byte[config.getBufferSize()];
        long transferred = 0;
        int read;
        
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
            transferred += read;
            
            if (listener != null) {
                listener.onProgress(transferred, totalSize);
            }
        }
        
        out.close();
        in.close();
        dataSocket.close();
        
        // Read final response
        readResponse();
        
        if (listener != null) {
            listener.onComplete();
        }
        
        logger.info("Downloaded {} to {}", remotePath, localFile);
    }
    
    /**
     * Upload file
     */
    public void uploadFile(File localFile, String remotePath, TransferListener listener) throws IOException {
        Socket dataSocket = openDataConnection();
        String response = sendCommand("STOR " + remotePath);
        
        if (!response.startsWith("150") && !response.startsWith("125")) {
            dataSocket.close();
            throw new IOException("STOR failed: " + response);
        }
        
        FileInputStream in = new FileInputStream(localFile);
        OutputStream out = dataSocket.getOutputStream();
        
        byte[] buffer = new byte[config.getBufferSize()];
        long totalSize = localFile.length();
        long transferred = 0;
        int read;
        
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
            transferred += read;
            
            if (listener != null) {
                listener.onProgress(transferred, totalSize);
            }
        }
        
        out.close();
        in.close();
        dataSocket.close();
        
        // Read final response
        readResponse();
        
        if (listener != null) {
            listener.onComplete();
        }
        
        logger.info("Uploaded {} to {}", localFile, remotePath);
    }
    
    /**
     * Change directory
     */
    public void changeDirectory(String path) throws IOException {
        String response = sendCommand("CWD " + path);
        if (!response.startsWith("250")) {
            throw new IOException("CWD failed: " + response);
        }
        currentDir = path;
    }
    
    /**
     * Get current directory
     */
    public String getCurrentDirectory() throws IOException {
        String response = sendCommand("PWD");
        if (!response.startsWith("257")) {
            throw new IOException("PWD failed: " + response);
        }
        
        // Parse: 257 "/path" is current directory
        int start = response.indexOf('"');
        int end = response.indexOf('"', start + 1);
        if (start >= 0 && end > start) {
            currentDir = response.substring(start + 1, end);
        }
        
        return currentDir;
    }
    
    /**
     * Create directory
     */
    public void makeDirectory(String path) throws IOException {
        String response = sendCommand("MKD " + path);
        if (!response.startsWith("257")) {
            throw new IOException("MKD failed: " + response);
        }
    }
    
    /**
     * Delete file
     */
    public void deleteFile(String path) throws IOException {
        String response = sendCommand("DELE " + path);
        if (!response.startsWith("250")) {
            throw new IOException("DELE failed: " + response);
        }
    }
    
    /**
     * Delete directory
     */
    public void deleteDirectory(String path) throws IOException {
        String response = sendCommand("RMD " + path);
        if (!response.startsWith("250")) {
            throw new IOException("RMD failed: " + response);
        }
    }
    
    /**
     * Rename file
     */
    public void rename(String from, String to) throws IOException {
        String response = sendCommand("RNFR " + from);
        if (!response.startsWith("350")) {
            throw new IOException("RNFR failed: " + response);
        }
        
        response = sendCommand("RNTO " + to);
        if (!response.startsWith("250")) {
            throw new IOException("RNTO failed: " + response);
        }
    }
    
    /**
     * Disconnect from server
     */
    public void disconnect() {
        if (!connected) return;
        
        try {
            sendCommand("QUIT");
        } catch (IOException e) {
            // Ignore
        }
        
        try {
            if (controlSocket != null) controlSocket.close();
        } catch (IOException e) {
            logger.debug("Error closing socket", e);
        }
        
        connected = false;
        fireEvent(FTPEvent.DISCONNECTED, "Disconnected");
        logger.info("FTP disconnected");
    }
    
    // Listener management
    public void addListener(FTPListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(FTPListener listener) {
        listeners.remove(listener);
    }
    
    private void fireEvent(FTPEvent event, String message) {
        for (FTPListener listener : listeners) {
            listener.onFTPEvent(this, event, message);
        }
    }
    
    // Getters
    public boolean isConnected() { return connected; }
    public String getWorkingDirectory() { return currentDir; }
    
    public enum FTPEvent {
        CONNECTING, CONNECTED, DISCONNECTED, ERROR
    }
    
    public interface FTPListener {
        void onFTPEvent(FTPSession session, FTPEvent event, String message);
    }
    
    public interface TransferListener {
        void onProgress(long transferred, long total);
        void onComplete();
    }
}
