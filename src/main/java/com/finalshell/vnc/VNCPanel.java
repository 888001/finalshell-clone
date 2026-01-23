package com.finalshell.vnc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * VNC Panel - VNC viewer with keyboard/mouse support
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class VNCPanel extends JPanel implements VNCSession.VNCListener {
    
    private static final Logger logger = LoggerFactory.getLogger(VNCPanel.class);
    
    private final VNCConfig config;
    private VNCSession session;
    private BufferedImage framebuffer;
    
    private JPanel displayPanel;
    private JToolBar toolbar;
    private JLabel statusLabel;
    private JButton connectBtn;
    private JButton disconnectBtn;
    private JComboBox<String> scaleCombo;
    
    private volatile boolean running = false;
    private Thread readerThread;
    
    public VNCPanel(VNCConfig config) {
        this.config = config;
        initComponents();
        initLayout();
        initListeners();
    }
    
    private void initComponents() {
        toolbar = new JToolBar();
        toolbar.setFloatable(false);
        
        connectBtn = new JButton("连接");
        disconnectBtn = new JButton("断开");
        disconnectBtn.setEnabled(false);
        
        scaleCombo = new JComboBox<>(new String[]{"100%", "75%", "50%", "适应窗口"});
        
        JButton refreshBtn = new JButton("刷新");
        JButton ctrlAltDelBtn = new JButton("Ctrl+Alt+Del");
        
        toolbar.add(connectBtn);
        toolbar.add(disconnectBtn);
        toolbar.addSeparator();
        toolbar.add(new JLabel("缩放:"));
        toolbar.add(scaleCombo);
        toolbar.addSeparator();
        toolbar.add(refreshBtn);
        toolbar.add(ctrlAltDelBtn);
        
        // Display panel
        displayPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (framebuffer != null) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    
                    double scale = getScale();
                    int w = (int) (framebuffer.getWidth() * scale);
                    int h = (int) (framebuffer.getHeight() * scale);
                    int x = (getWidth() - w) / 2;
                    int y = (getHeight() - h) / 2;
                    
                    g2d.drawImage(framebuffer, x, y, w, h, null);
                }
            }
        };
        displayPanel.setBackground(Color.BLACK);
        displayPanel.setFocusable(true);
        
        // Status bar
        statusLabel = new JLabel("未连接");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        
        // Button actions
        connectBtn.addActionListener(e -> connect());
        disconnectBtn.addActionListener(e -> disconnect());
        refreshBtn.addActionListener(e -> requestFullUpdate());
        ctrlAltDelBtn.addActionListener(e -> sendCtrlAltDel());
    }
    
    private void initLayout() {
        setLayout(new BorderLayout());
        add(toolbar, BorderLayout.NORTH);
        add(new JScrollPane(displayPanel), BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }
    
    private void initListeners() {
        // Keyboard listener
        displayPanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                sendKeyEvent(e, true);
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                sendKeyEvent(e, false);
            }
        });
        
        // Mouse listeners
        displayPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                displayPanel.requestFocusInWindow();
                sendMouseEvent(e);
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                sendMouseEvent(e);
            }
        });
        
        displayPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                sendMouseEvent(e);
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                sendMouseEvent(e);
            }
        });
        
        displayPanel.addMouseWheelListener(this::sendWheelEvent);
    }
    
    private double getScale() {
        String selected = (String) scaleCombo.getSelectedItem();
        if ("适应窗口".equals(selected) && framebuffer != null) {
            double scaleX = (double) displayPanel.getWidth() / framebuffer.getWidth();
            double scaleY = (double) displayPanel.getHeight() / framebuffer.getHeight();
            return Math.min(scaleX, scaleY);
        }
        if (selected != null && selected.endsWith("%")) {
            return Integer.parseInt(selected.replace("%", "")) / 100.0;
        }
        return 1.0;
    }
    
    private Point translateCoords(MouseEvent e) {
        if (framebuffer == null) return new Point(0, 0);
        
        double scale = getScale();
        int w = (int) (framebuffer.getWidth() * scale);
        int h = (int) (framebuffer.getHeight() * scale);
        int offsetX = (displayPanel.getWidth() - w) / 2;
        int offsetY = (displayPanel.getHeight() - h) / 2;
        
        int x = (int) ((e.getX() - offsetX) / scale);
        int y = (int) ((e.getY() - offsetY) / scale);
        
        x = Math.max(0, Math.min(x, framebuffer.getWidth() - 1));
        y = Math.max(0, Math.min(y, framebuffer.getHeight() - 1));
        
        return new Point(x, y);
    }
    
    /**
     * Connect to VNC server
     */
    public void connect() {
        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                publish("正在连接...");
                session = new VNCSession(config);
                session.addListener(VNCPanel.this);
                session.connect();
                
                // Initialize framebuffer
                framebuffer = new BufferedImage(
                    session.getFrameWidth(), 
                    session.getFrameHeight(), 
                    BufferedImage.TYPE_INT_RGB);
                
                // Start reader thread
                running = true;
                startReaderThread();
                
                // Request initial framebuffer
                session.requestFramebufferUpdate(false);
                
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
                    connectBtn.setEnabled(false);
                    disconnectBtn.setEnabled(true);
                    statusLabel.setText("已连接: " + session.getServerName());
                    displayPanel.requestFocusInWindow();
                } catch (Exception e) {
                    logger.error("VNC connection failed", e);
                    statusLabel.setText("连接失败: " + e.getMessage());
                    JOptionPane.showMessageDialog(VNCPanel.this, 
                        e.getMessage(), "连接失败", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
    
    /**
     * Start framebuffer reader thread
     */
    private void startReaderThread() {
        readerThread = new Thread(() -> {
            try {
                while (running && session != null && session.isConnected()) {
                    readServerMessage();
                }
            } catch (IOException e) {
                if (running) {
                    logger.error("VNC read error", e);
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("连接断开: " + e.getMessage());
                        disconnect();
                    });
                }
            }
        }, "VNC-Reader");
        readerThread.setDaemon(true);
        readerThread.start();
    }
    
    /**
     * Read and process server message
     */
    private void readServerMessage() throws IOException {
        DataInputStream in = session.getInputStream();
        int messageType = in.readUnsignedByte();
        
        switch (messageType) {
            case 0: // Framebuffer update
                readFramebufferUpdate(in);
                break;
            case 1: // SetColorMapEntries
                readColorMapEntries(in);
                break;
            case 2: // Bell
                Toolkit.getDefaultToolkit().beep();
                break;
            case 3: // ServerCutText
                readServerCutText(in);
                break;
            default:
                logger.warn("Unknown VNC message type: {}", messageType);
        }
    }
    
    /**
     * Read framebuffer update
     */
    private void readFramebufferUpdate(DataInputStream in) throws IOException {
        in.readByte(); // padding
        int numRects = in.readUnsignedShort();
        
        Graphics2D g = framebuffer.createGraphics();
        
        for (int i = 0; i < numRects; i++) {
            int x = in.readUnsignedShort();
            int y = in.readUnsignedShort();
            int w = in.readUnsignedShort();
            int h = in.readUnsignedShort();
            int encoding = in.readInt();
            
            switch (encoding) {
                case 0: // Raw
                    readRawRect(in, g, x, y, w, h);
                    break;
                case 1: // CopyRect
                    readCopyRect(in, g, x, y, w, h);
                    break;
                default:
                    // Skip unknown encoding
                    logger.debug("Unsupported encoding: {}", encoding);
            }
        }
        
        g.dispose();
        
        // Request next update
        try {
            session.requestFramebufferUpdate(true);
        } catch (IOException e) {
            logger.debug("Failed to request update", e);
        }
        
        // Repaint
        SwingUtilities.invokeLater(displayPanel::repaint);
    }
    
    /**
     * Read raw rectangle
     */
    private void readRawRect(DataInputStream in, Graphics2D g, int x, int y, int w, int h) throws IOException {
        int[] pixels = new int[w * h];
        for (int i = 0; i < pixels.length; i++) {
            int b = in.readUnsignedByte();
            int green = in.readUnsignedByte();
            int r = in.readUnsignedByte();
            in.readByte(); // padding
            pixels[i] = (r << 16) | (green << 8) | b;
        }
        framebuffer.setRGB(x, y, w, h, pixels, 0, w);
    }
    
    /**
     * Read copy rectangle
     */
    private void readCopyRect(DataInputStream in, Graphics2D g, int x, int y, int w, int h) throws IOException {
        int srcX = in.readUnsignedShort();
        int srcY = in.readUnsignedShort();
        g.copyArea(srcX, srcY, w, h, x - srcX, y - srcY);
    }
    
    private void readColorMapEntries(DataInputStream in) throws IOException {
        in.readByte(); // padding
        in.readUnsignedShort(); // first color
        int numColors = in.readUnsignedShort();
        in.skipBytes(numColors * 6);
    }
    
    private void readServerCutText(DataInputStream in) throws IOException {
        in.skipBytes(3); // padding
        int len = in.readInt();
        byte[] text = new byte[len];
        in.readFully(text);
        // Could copy to clipboard
    }
    
    /**
     * Send key event
     */
    private void sendKeyEvent(KeyEvent e, boolean down) {
        if (session == null || !session.isConnected() || config.isViewOnly()) return;
        
        try {
            int key = translateKeyCode(e);
            if (key != 0) {
                session.sendKeyEvent(key, down);
            }
        } catch (IOException ex) {
            logger.error("Failed to send key event", ex);
        }
    }
    
    private int translateKeyCode(KeyEvent e) {
        // Map Java key codes to X11 keysyms
        int keyCode = e.getKeyCode();
        char keyChar = e.getKeyChar();
        
        // Special keys
        switch (keyCode) {
            case KeyEvent.VK_BACK_SPACE: return 0xff08;
            case KeyEvent.VK_TAB: return 0xff09;
            case KeyEvent.VK_ENTER: return 0xff0d;
            case KeyEvent.VK_ESCAPE: return 0xff1b;
            case KeyEvent.VK_DELETE: return 0xffff;
            case KeyEvent.VK_HOME: return 0xff50;
            case KeyEvent.VK_LEFT: return 0xff51;
            case KeyEvent.VK_UP: return 0xff52;
            case KeyEvent.VK_RIGHT: return 0xff53;
            case KeyEvent.VK_DOWN: return 0xff54;
            case KeyEvent.VK_PAGE_UP: return 0xff55;
            case KeyEvent.VK_PAGE_DOWN: return 0xff56;
            case KeyEvent.VK_END: return 0xff57;
            case KeyEvent.VK_INSERT: return 0xff63;
            case KeyEvent.VK_F1: return 0xffbe;
            case KeyEvent.VK_F2: return 0xffbf;
            case KeyEvent.VK_F3: return 0xffc0;
            case KeyEvent.VK_F4: return 0xffc1;
            case KeyEvent.VK_F5: return 0xffc2;
            case KeyEvent.VK_F6: return 0xffc3;
            case KeyEvent.VK_F7: return 0xffc4;
            case KeyEvent.VK_F8: return 0xffc5;
            case KeyEvent.VK_F9: return 0xffc6;
            case KeyEvent.VK_F10: return 0xffc7;
            case KeyEvent.VK_F11: return 0xffc8;
            case KeyEvent.VK_F12: return 0xffc9;
            case KeyEvent.VK_SHIFT: return 0xffe1;
            case KeyEvent.VK_CONTROL: return 0xffe3;
            case KeyEvent.VK_ALT: return 0xffe9;
        }
        
        // Printable characters
        if (keyChar != KeyEvent.CHAR_UNDEFINED && keyChar >= 32 && keyChar < 127) {
            return keyChar;
        }
        
        return 0;
    }
    
    /**
     * Send mouse event
     */
    private void sendMouseEvent(MouseEvent e) {
        if (session == null || !session.isConnected() || config.isViewOnly()) return;
        
        try {
            Point p = translateCoords(e);
            int buttonMask = 0;
            if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) buttonMask |= 1;
            if ((e.getModifiersEx() & MouseEvent.BUTTON2_DOWN_MASK) != 0) buttonMask |= 2;
            if ((e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) != 0) buttonMask |= 4;
            
            session.sendPointerEvent(p.x, p.y, buttonMask);
        } catch (IOException ex) {
            logger.error("Failed to send mouse event", ex);
        }
    }
    
    /**
     * Send mouse wheel event
     */
    private void sendWheelEvent(MouseWheelEvent e) {
        if (session == null || !session.isConnected() || config.isViewOnly()) return;
        
        try {
            Point p = translateCoords(e);
            int button = e.getWheelRotation() < 0 ? 8 : 16; // scroll up/down
            
            session.sendPointerEvent(p.x, p.y, button);
            session.sendPointerEvent(p.x, p.y, 0);
        } catch (IOException ex) {
            logger.error("Failed to send wheel event", ex);
        }
    }
    
    /**
     * Send Ctrl+Alt+Del
     */
    private void sendCtrlAltDel() {
        if (session == null || !session.isConnected()) return;
        
        try {
            session.sendKeyEvent(0xffe3, true);  // Ctrl down
            session.sendKeyEvent(0xffe9, true);  // Alt down
            session.sendKeyEvent(0xffff, true);  // Delete down
            session.sendKeyEvent(0xffff, false); // Delete up
            session.sendKeyEvent(0xffe9, false); // Alt up
            session.sendKeyEvent(0xffe3, false); // Ctrl up
        } catch (IOException e) {
            logger.error("Failed to send Ctrl+Alt+Del", e);
        }
    }
    
    /**
     * Request full framebuffer update
     */
    private void requestFullUpdate() {
        if (session == null || !session.isConnected()) return;
        
        try {
            session.requestFramebufferUpdate(false);
        } catch (IOException e) {
            logger.error("Failed to request update", e);
        }
    }
    
    /**
     * Disconnect from VNC server
     */
    public void disconnect() {
        running = false;
        
        if (session != null) {
            session.disconnect();
            session = null;
        }
        
        connectBtn.setEnabled(true);
        disconnectBtn.setEnabled(false);
        statusLabel.setText("已断开");
    }
    
    public void close() {
        disconnect();
    }
    
    @Override
    public void onVNCEvent(VNCSession session, VNCSession.VNCEvent event, String message) {
        SwingUtilities.invokeLater(() -> {
            switch (event) {
                case CONNECTED:
                    statusLabel.setText("已连接");
                    break;
                case DISCONNECTED:
                    statusLabel.setText("已断开");
                    connectBtn.setEnabled(true);
                    disconnectBtn.setEnabled(false);
                    break;
                case ERROR:
                    statusLabel.setText("错误: " + message);
                    break;
            }
        });
    }
}
