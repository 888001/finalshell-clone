package com.finalshell.terminal;

import com.finalshell.ssh.SSHSession;
import com.jediterm.terminal.Questioner;
import com.jediterm.terminal.TtyConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * SSH TTY Connector - Bridges SSHSession with JediTerm
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Terminal_DeepAnalysis.md
 */
public class SSHTtyConnector implements TtyConnector {
    
    private static final Logger logger = LoggerFactory.getLogger(SSHTtyConnector.class);
    
    private final SSHSession sshSession;
    private final Charset charset;
    private final InputStreamReader reader;
    private final OutputStream writer;
    
    private volatile boolean closed = false;
    
    public SSHTtyConnector(SSHSession sshSession, Charset charset) {
        this.sshSession = sshSession;
        this.charset = charset;
        
        InputStream is = sshSession.getInputStream();
        this.reader = new InputStreamReader(is, charset);
        this.writer = sshSession.getOutputStream();
    }
    
    @Override
    public boolean init(Questioner questioner) {
        return sshSession.isShellOpen();
    }
    
    @Override
    public void close() {
        closed = true;
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {
            logger.debug("Error closing reader", e);
        }
    }
    
    @Override
    public String getName() {
        return sshSession.getConfig().getName();
    }
    
    @Override
    public int read(char[] buf, int offset, int length) throws IOException {
        if (closed) {
            return -1;
        }
        return reader.read(buf, offset, length);
    }
    
    @Override
    public void write(byte[] bytes) throws IOException {
        if (!closed && writer != null) {
            writer.write(bytes);
            writer.flush();
        }
    }
    
    @Override
    public void write(String text) throws IOException {
        write(text.getBytes(charset));
    }
    
    @Override
    public boolean isConnected() {
        return !closed && sshSession.isShellOpen();
    }
    
    @Override
    public int waitFor() throws InterruptedException {
        while (isConnected()) {
            Thread.sleep(100);
        }
        return 0;
    }
    
    @Override
    public boolean ready() throws IOException {
        return reader.ready();
    }
    
    public void resize(Dimension termSize, Dimension pixelSize) {
        if (sshSession != null && sshSession.isShellOpen()) {
            sshSession.resizeTerminal(termSize.width, termSize.height);
        }
    }
}
