package com.finalshell.telnet;

import com.jediterm.terminal.TtyConnector;
import com.jediterm.terminal.ui.JediTermWidget;
import com.jediterm.terminal.ui.settings.DefaultSettingsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.*;
import java.nio.charset.Charset;

/**
 * Telnet Panel - Terminal panel for Telnet connections
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class TelnetPanel extends JPanel implements TelnetSession.TelnetListener {
    
    private static final Logger logger = LoggerFactory.getLogger(TelnetPanel.class);
    
    private final TelnetConfig config;
    private TelnetSession session;
    private JediTermWidget terminalWidget;
    private TelnetTtyConnector ttyConnector;
    
    private JPanel statusPanel;
    private JLabel statusLabel;
    private JLabel charsetLabel;
    
    private boolean connected = false;
    
    public TelnetPanel(TelnetConfig config) {
        this.config = config;
        initComponents();
        initLayout();
    }
    
    private void initComponents() {
        statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        
        statusLabel = new JLabel("未连接");
        statusLabel.setForeground(Color.GRAY);
        
        charsetLabel = new JLabel(config.getCharset());
        charsetLabel.setForeground(Color.GRAY);
        
        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(charsetLabel, BorderLayout.EAST);
    }
    
    private void initLayout() {
        setLayout(new BorderLayout());
        add(statusPanel, BorderLayout.SOUTH);
        
        // Placeholder
        JPanel placeholder = new JPanel(new GridBagLayout());
        placeholder.setBackground(new Color(30, 30, 30));
        JLabel label = new JLabel("正在连接 " + config.getHost() + "...");
        label.setForeground(Color.WHITE);
        placeholder.add(label);
        add(placeholder, BorderLayout.CENTER);
    }
    
    /**
     * Connect to Telnet server
     */
    public void connect() {
        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                publish("正在连接...");
                
                session = new TelnetSession(config);
                session.addListener(TelnetPanel.this);
                session.connect();
                
                return null;
            }
            
            @Override
            protected void process(java.util.List<String> chunks) {
                statusLabel.setText(chunks.get(chunks.size() - 1));
            }
            
            @Override
            protected void done() {
                try {
                    get();
                    setupTerminal();
                    connected = true;
                    statusLabel.setText("已连接");
                    statusLabel.setForeground(new Color(0, 180, 0));
                } catch (Exception e) {
                    logger.error("Telnet connection failed", e);
                    statusLabel.setText("连接失败: " + e.getMessage());
                    statusLabel.setForeground(Color.RED);
                    JOptionPane.showMessageDialog(TelnetPanel.this,
                        e.getMessage(), "连接失败", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
    
    /**
     * Setup terminal widget
     */
    private void setupTerminal() {
        removeAll();
        
        ttyConnector = new TelnetTtyConnector(session, Charset.forName(config.getCharset()));
        
        terminalWidget = new JediTermWidget(new DefaultSettingsProvider());
        terminalWidget.setTtyConnector(ttyConnector);
        terminalWidget.start();
        
        // Resize listener
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (session != null && session.isConnected()) {
                    Dimension size = terminalWidget.getTerminalPanel().getTerminalSizeFromComponent();
                    if (size != null) {
                        session.resize(size.width, size.height);
                    }
                }
            }
        });
        
        setLayout(new BorderLayout());
        add(terminalWidget, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
        
        revalidate();
        repaint();
        
        SwingUtilities.invokeLater(() -> terminalWidget.requestFocusInWindow());
        
        logger.info("Telnet terminal setup complete for {}", config.getName());
    }
    
    /**
     * Disconnect from server
     */
    public void disconnect() {
        if (ttyConnector != null) {
            ttyConnector.close();
        }
        
        if (session != null) {
            session.disconnect();
        }
        
        connected = false;
        statusLabel.setText("已断开");
        statusLabel.setForeground(Color.GRAY);
    }
    
    /**
     * Reconnect
     */
    public void reconnect() {
        disconnect();
        connect();
    }
    
    @Override
    public void onTelnetEvent(TelnetSession session, TelnetSession.TelnetEvent event, String message) {
        SwingUtilities.invokeLater(() -> {
            switch (event) {
                case CONNECTED:
                    statusLabel.setText("已连接");
                    statusLabel.setForeground(new Color(0, 180, 0));
                    break;
                case DISCONNECTED:
                    statusLabel.setText("已断开");
                    statusLabel.setForeground(Color.GRAY);
                    connected = false;
                    break;
                case ERROR:
                    statusLabel.setText("错误: " + message);
                    statusLabel.setForeground(Color.RED);
                    break;
            }
        });
    }
    
    public boolean isConnected() {
        return connected && session != null && session.isConnected();
    }
    
    public TelnetSession getSession() {
        return session;
    }
    
    public void close() {
        disconnect();
    }
    
    /**
     * TTY Connector for Telnet
     */
    private static class TelnetTtyConnector implements TtyConnector {
        
        private final TelnetSession session;
        private final Charset charset;
        private final InputStreamReader reader;
        
        public TelnetTtyConnector(TelnetSession session, Charset charset) {
            this.session = session;
            this.charset = charset;
            this.reader = new InputStreamReader(session.getInputStream(), charset);
        }
        
        @Override
        public boolean init(Questioner questioner) {
            return session.isConnected();
        }
        
        @Override
        public void close() {
            session.disconnect();
        }
        
        @Override
        public String getName() {
            return session.getConfig().getName();
        }
        
        @Override
        public int read(char[] buf, int offset, int length) throws IOException {
            return reader.read(buf, offset, length);
        }
        
        @Override
        public void write(byte[] bytes) throws IOException {
            session.write(bytes);
        }
        
        @Override
        public boolean isConnected() {
            return session.isConnected();
        }
        
        @Override
        public void write(String string) throws IOException {
            session.write(string);
        }
        
        @Override
        public int waitFor() throws InterruptedException {
            while (session.isConnected()) {
                Thread.sleep(100);
            }
            return 0;
        }
        
        @Override
        public boolean ready() throws IOException {
            return session.getInputStream().available() > 0;
        }
    }
}
