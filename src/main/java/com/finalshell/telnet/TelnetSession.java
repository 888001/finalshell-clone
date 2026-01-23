package com.finalshell.telnet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Telnet Session - Telnet protocol implementation
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class TelnetSession {
    
    private static final Logger logger = LoggerFactory.getLogger(TelnetSession.class);
    
    // Telnet commands
    private static final int IAC = 255;   // Interpret As Command
    private static final int DONT = 254;
    private static final int DO = 253;
    private static final int WONT = 252;
    private static final int WILL = 251;
    private static final int SB = 250;    // Subnegotiation Begin
    private static final int SE = 240;    // Subnegotiation End
    
    // Telnet options
    private static final int OPT_ECHO = 1;
    private static final int OPT_SUPPRESS_GO_AHEAD = 3;
    private static final int OPT_TERMINAL_TYPE = 24;
    private static final int OPT_NAWS = 31;  // Window Size
    
    private final TelnetConfig config;
    private Socket socket;
    private InputStream rawIn;
    private OutputStream rawOut;
    private PipedInputStream dataIn;
    private PipedOutputStream dataOut;
    
    private boolean connected = false;
    private boolean localEcho = true;
    private Thread readerThread;
    
    private final List<TelnetListener> listeners = new ArrayList<>();
    private Charset charset;
    
    public TelnetSession(TelnetConfig config) {
        this.config = config;
        this.charset = Charset.forName(config.getCharset());
    }
    
    /**
     * Connect to Telnet server
     */
    public void connect() throws IOException {
        logger.info("Connecting to {}:{}", config.getHost(), config.getPort());
        
        socket = new Socket();
        socket.connect(new java.net.InetSocketAddress(config.getHost(), config.getPort()), 
            config.getConnectionTimeout());
        
        if (config.getReadTimeout() > 0) {
            socket.setSoTimeout(config.getReadTimeout());
        }
        
        rawIn = socket.getInputStream();
        rawOut = socket.getOutputStream();
        
        // Setup data pipes
        dataOut = new PipedOutputStream();
        dataIn = new PipedInputStream(dataOut, 8192);
        
        connected = true;
        localEcho = config.isLocalEcho();
        
        // Start reader thread
        startReaderThread();
        
        fireEvent(TelnetEvent.CONNECTED, "Connected to " + config.getHost());
        logger.info("Telnet connected to {}", config.getHost());
        
        // Auto login if configured
        if (config.isAutoLogin() && config.getUsername() != null) {
            performAutoLogin();
        }
    }
    
    /**
     * Start reader thread for processing telnet data
     */
    private void startReaderThread() {
        readerThread = new Thread(() -> {
            try {
                while (connected) {
                    int b = rawIn.read();
                    if (b == -1) {
                        break;
                    }
                    
                    if (b == IAC) {
                        handleTelnetCommand();
                    } else {
                        dataOut.write(b);
                        dataOut.flush();
                    }
                }
            } catch (SocketTimeoutException e) {
                // Timeout is OK
            } catch (IOException e) {
                if (connected) {
                    logger.error("Telnet read error", e);
                    fireEvent(TelnetEvent.ERROR, e.getMessage());
                }
            } finally {
                if (connected) {
                    disconnect();
                }
            }
        }, "Telnet-Reader");
        readerThread.setDaemon(true);
        readerThread.start();
    }
    
    /**
     * Handle telnet IAC command
     */
    private void handleTelnetCommand() throws IOException {
        int cmd = rawIn.read();
        if (cmd == -1) return;
        
        switch (cmd) {
            case WILL:
                handleWill(rawIn.read());
                break;
            case WONT:
                handleWont(rawIn.read());
                break;
            case DO:
                handleDo(rawIn.read());
                break;
            case DONT:
                handleDont(rawIn.read());
                break;
            case SB:
                handleSubnegotiation();
                break;
            case IAC:
                // Escaped IAC, write as data
                dataOut.write(IAC);
                break;
        }
    }
    
    private void handleWill(int option) throws IOException {
        logger.debug("Received WILL {}", option);
        switch (option) {
            case OPT_ECHO:
                // Server will echo
                localEcho = false;
                sendCommand(DO, option);
                break;
            case OPT_SUPPRESS_GO_AHEAD:
                sendCommand(DO, option);
                break;
            default:
                sendCommand(DONT, option);
        }
    }
    
    private void handleWont(int option) throws IOException {
        logger.debug("Received WONT {}", option);
        if (option == OPT_ECHO) {
            localEcho = true;
        }
    }
    
    private void handleDo(int option) throws IOException {
        logger.debug("Received DO {}", option);
        switch (option) {
            case OPT_TERMINAL_TYPE:
                sendCommand(WILL, option);
                break;
            case OPT_NAWS:
                sendCommand(WILL, option);
                sendWindowSize();
                break;
            case OPT_SUPPRESS_GO_AHEAD:
                sendCommand(WILL, option);
                break;
            default:
                sendCommand(WONT, option);
        }
    }
    
    private void handleDont(int option) throws IOException {
        logger.debug("Received DONT {}", option);
        sendCommand(WONT, option);
    }
    
    private void handleSubnegotiation() throws IOException {
        int option = rawIn.read();
        
        // Read until IAC SE
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int b;
        while ((b = rawIn.read()) != -1) {
            if (b == IAC) {
                int next = rawIn.read();
                if (next == SE) break;
                buffer.write(b);
                buffer.write(next);
            } else {
                buffer.write(b);
            }
        }
        
        byte[] data = buffer.toByteArray();
        
        if (option == OPT_TERMINAL_TYPE && data.length > 0 && data[0] == 1) {
            // Send terminal type
            sendTerminalType();
        }
    }
    
    private void sendCommand(int cmd, int option) throws IOException {
        rawOut.write(new byte[]{(byte) IAC, (byte) cmd, (byte) option});
        rawOut.flush();
    }
    
    private void sendWindowSize() throws IOException {
        int width = config.getTerminalWidth();
        int height = config.getTerminalHeight();
        
        rawOut.write(new byte[]{
            (byte) IAC, (byte) SB, (byte) OPT_NAWS,
            (byte) (width >> 8), (byte) (width & 0xff),
            (byte) (height >> 8), (byte) (height & 0xff),
            (byte) IAC, (byte) SE
        });
        rawOut.flush();
        logger.debug("Sent window size: {}x{}", width, height);
    }
    
    private void sendTerminalType() throws IOException {
        String termType = config.getTerminalType();
        byte[] typeBytes = termType.getBytes();
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(IAC);
        out.write(SB);
        out.write(OPT_TERMINAL_TYPE);
        out.write(0); // IS
        out.write(typeBytes);
        out.write(IAC);
        out.write(SE);
        
        rawOut.write(out.toByteArray());
        rawOut.flush();
        logger.debug("Sent terminal type: {}", termType);
    }
    
    /**
     * Perform auto login
     */
    private void performAutoLogin() {
        new Thread(() -> {
            try {
                // Wait for login prompt
                Thread.sleep(1000);
                
                // Send username
                if (config.getUsername() != null) {
                    write(config.getUsername() + "\r\n");
                }
                
                // Wait for password prompt
                Thread.sleep(500);
                
                // Send password
                if (config.getPassword() != null) {
                    write(config.getPassword() + "\r\n");
                }
            } catch (Exception e) {
                logger.error("Auto login failed", e);
            }
        }, "Telnet-AutoLogin").start();
    }
    
    /**
     * Write data to telnet connection
     */
    public void write(String text) throws IOException {
        write(text.getBytes(charset));
    }
    
    public void write(byte[] data) throws IOException {
        if (!connected) return;
        
        // Escape IAC in data
        for (byte b : data) {
            if ((b & 0xff) == IAC) {
                rawOut.write(IAC);
            }
            rawOut.write(b);
        }
        rawOut.flush();
    }
    
    /**
     * Resize terminal
     */
    public void resize(int width, int height) {
        config.setTerminalWidth(width);
        config.setTerminalHeight(height);
        
        if (connected) {
            try {
                sendWindowSize();
            } catch (IOException e) {
                logger.error("Failed to send window size", e);
            }
        }
    }
    
    /**
     * Disconnect from server
     */
    public void disconnect() {
        if (!connected) return;
        
        connected = false;
        
        try {
            if (dataOut != null) dataOut.close();
            if (dataIn != null) dataIn.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            logger.debug("Error closing telnet", e);
        }
        
        fireEvent(TelnetEvent.DISCONNECTED, "Disconnected");
        logger.info("Telnet disconnected");
    }
    
    // Listener management
    public void addListener(TelnetListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(TelnetListener listener) {
        listeners.remove(listener);
    }
    
    private void fireEvent(TelnetEvent event, String message) {
        for (TelnetListener listener : listeners) {
            listener.onTelnetEvent(this, event, message);
        }
    }
    
    // Getters
    public boolean isConnected() { return connected; }
    public boolean isLocalEcho() { return localEcho; }
    public InputStream getInputStream() { return dataIn; }
    public OutputStream getOutputStream() { return rawOut; }
    public TelnetConfig getConfig() { return config; }
    
    public enum TelnetEvent {
        CONNECTING, CONNECTED, DISCONNECTED, ERROR
    }
    
    public interface TelnetListener {
        void onTelnetEvent(TelnetSession session, TelnetEvent event, String message);
    }
}
