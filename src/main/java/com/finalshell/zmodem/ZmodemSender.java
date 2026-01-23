package com.finalshell.zmodem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Zmodem发送器 - 处理rz上传
 */
public class ZmodemSender {
    private static final Logger logger = LoggerFactory.getLogger(ZmodemSender.class);
    
    private static final int BLOCK_SIZE = 1024;
    
    private final InputStream in;
    private final OutputStream out;
    private final ZmodemReceiver.ZmodemListener listener;
    
    private boolean useCrc32 = true;
    private volatile boolean cancelled = false;
    
    public ZmodemSender(InputStream in, OutputStream out, ZmodemReceiver.ZmodemListener listener) {
        this.in = in;
        this.out = out;
        this.listener = listener;
    }
    
    /**
     * 发送文件
     */
    public void send(File[] files) throws ZmodemException {
        try {
            // 等待ZRINIT
            if (!waitForZRINIT()) {
                throw new ZmodemException("未收到ZRINIT");
            }
            
            for (File file : files) {
                if (cancelled) break;
                sendFile(file);
            }
            
            // 发送ZFIN
            sendZFIN();
            
            // 等待对方ZFIN
            waitForZFIN();
            
            // 发送OO
            out.write('O');
            out.write('O');
            out.flush();
            
        } catch (IOException e) {
            throw new ZmodemException("IO错误: " + e.getMessage(), e);
        }
    }
    
    private boolean waitForZRINIT() throws IOException {
        long timeout = System.currentTimeMillis() + 30000;
        
        while (System.currentTimeMillis() < timeout) {
            int[] header = receiveHeader();
            if (header != null && header[0] == ZmodemProtocol.ZRINIT) {
                // 检查是否支持CRC32
                useCrc32 = (header[1] & ZmodemProtocol.CANFC32) != 0;
                return true;
            }
        }
        return false;
    }
    
    private void sendFile(File file) throws IOException, ZmodemException {
        logger.info("发送文件: {} ({}字节)", file.getName(), file.length());
        
        if (listener != null) {
            listener.onFileStart(file.getName(), file.length());
        }
        
        // 发送ZFILE
        sendZFILE(file);
        
        // 等待ZRPOS
        long offset = waitForZRPOS();
        
        // 发送数据
        sendFileData(file, offset);
        
        // 发送ZEOF
        sendZEOF(file.length());
        
        // 等待ZRINIT (准备下一个文件)
        waitForZRINIT();
        
        if (listener != null) {
            listener.onFileComplete(file.getName(), file);
        }
    }
    
    private void sendZFILE(File file) throws IOException {
        // 发送ZFILE头
        sendBinaryHeader(ZmodemProtocol.ZFILE, new byte[4]);
        
        // 发送文件信息: filename\0size mtime mode
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(file.getName().getBytes(StandardCharsets.UTF_8));
        baos.write(0);
        String info = file.length() + " " + (file.lastModified() / 1000) + " 0 0 1 " + file.length();
        baos.write(info.getBytes(StandardCharsets.UTF_8));
        
        byte[] data = baos.toByteArray();
        sendDataSubpacket(data, ZmodemProtocol.ZCRCW);
    }
    
    private long waitForZRPOS() throws IOException, ZmodemException {
        long timeout = System.currentTimeMillis() + 30000;
        
        while (System.currentTimeMillis() < timeout) {
            int[] header = receiveHeader();
            if (header != null) {
                if (header[0] == ZmodemProtocol.ZRPOS) {
                    return (header[1] & 0xFF) | 
                           ((header[2] & 0xFF) << 8) |
                           ((header[3] & 0xFF) << 16) |
                           ((header[4] & 0xFF) << 24);
                }
                if (header[0] == ZmodemProtocol.ZSKIP) {
                    throw new ZmodemException("文件被跳过");
                }
            }
        }
        throw new ZmodemException("等待ZRPOS超时");
    }
    
    private void sendFileData(File file, long offset) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            if (offset > 0) {
                fis.skip(offset);
            }
            
            long sent = offset;
            long total = file.length();
            
            // 发送ZDATA
            sendBinaryHeader(ZmodemProtocol.ZDATA, longToBytes(sent));
            
            byte[] buffer = new byte[BLOCK_SIZE];
            int read;
            
            while ((read = fis.read(buffer)) > 0 && !cancelled) {
                byte[] data = new byte[read];
                System.arraycopy(buffer, 0, data, 0, read);
                
                boolean isLast = (sent + read >= total);
                int subpacketType = isLast ? ZmodemProtocol.ZCRCE : ZmodemProtocol.ZCRCG;
                
                sendDataSubpacket(data, subpacketType);
                sent += read;
                
                if (listener != null) {
                    listener.onProgress(file.getName(), sent, total);
                }
            }
        }
    }
    
    private void sendZEOF(long fileSize) throws IOException {
        sendBinaryHeader(ZmodemProtocol.ZEOF, longToBytes(fileSize));
    }
    
    private void sendZFIN() throws IOException {
        sendHexHeader(ZmodemProtocol.ZFIN, new byte[4]);
    }
    
    private void waitForZFIN() throws IOException {
        long timeout = System.currentTimeMillis() + 10000;
        
        while (System.currentTimeMillis() < timeout) {
            int[] header = receiveHeader();
            if (header != null && header[0] == ZmodemProtocol.ZFIN) {
                return;
            }
        }
    }
    
    private void sendBinaryHeader(int type, byte[] data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        baos.write(ZmodemProtocol.ZPAD);
        baos.write(ZmodemProtocol.ZDLE);
        baos.write(useCrc32 ? ZmodemProtocol.ZBIN32 : ZmodemProtocol.ZBIN);
        
        writeZDLE(baos, type);
        for (byte b : data) {
            writeZDLE(baos, b & 0xFF);
        }
        
        // CRC
        byte[] crcData = new byte[5];
        crcData[0] = (byte) type;
        System.arraycopy(data, 0, crcData, 1, 4);
        
        if (useCrc32) {
            long crc = ZmodemProtocol.crc32(crcData, 0, 5);
            for (int i = 0; i < 4; i++) {
                writeZDLE(baos, (int) ((crc >> (i * 8)) & 0xFF));
            }
        } else {
            int crc = ZmodemProtocol.crc16(crcData, 0, 5);
            writeZDLE(baos, (crc >> 8) & 0xFF);
            writeZDLE(baos, crc & 0xFF);
        }
        
        out.write(baos.toByteArray());
        out.flush();
    }
    
    private void sendHexHeader(int type, byte[] data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        baos.write(ZmodemProtocol.ZPAD);
        baos.write(ZmodemProtocol.ZPAD);
        baos.write(ZmodemProtocol.ZDLE);
        baos.write(ZmodemProtocol.ZHEX);
        
        writeHex(baos, type);
        for (byte b : data) {
            writeHex(baos, b & 0xFF);
        }
        
        // CRC
        byte[] crcData = new byte[5];
        crcData[0] = (byte) type;
        System.arraycopy(data, 0, crcData, 1, 4);
        int crc = ZmodemProtocol.crc16(crcData, 0, 5);
        writeHex(baos, (crc >> 8) & 0xFF);
        writeHex(baos, crc & 0xFF);
        
        baos.write('\r');
        baos.write('\n');
        
        out.write(baos.toByteArray());
        out.flush();
    }
    
    private void sendDataSubpacket(byte[] data, int endType) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        for (byte b : data) {
            writeZDLE(baos, b & 0xFF);
        }
        
        baos.write(ZmodemProtocol.ZDLE);
        baos.write(endType);
        
        // CRC
        byte[] crcData = new byte[data.length + 1];
        System.arraycopy(data, 0, crcData, 0, data.length);
        crcData[data.length] = (byte) endType;
        
        if (useCrc32) {
            long crc = ZmodemProtocol.crc32(crcData, 0, crcData.length);
            for (int i = 0; i < 4; i++) {
                writeZDLE(baos, (int) ((crc >> (i * 8)) & 0xFF));
            }
        } else {
            int crc = ZmodemProtocol.crc16(crcData, 0, crcData.length);
            writeZDLE(baos, (crc >> 8) & 0xFF);
            writeZDLE(baos, crc & 0xFF);
        }
        
        out.write(baos.toByteArray());
        out.flush();
    }
    
    private void writeZDLE(ByteArrayOutputStream baos, int c) {
        if (c == ZmodemProtocol.ZDLE) {
            baos.write(ZmodemProtocol.ZDLE);
            baos.write(ZmodemProtocol.ZDLEE);
        } else if (c == ZmodemProtocol.XON || c == ZmodemProtocol.XOFF ||
                   c == (ZmodemProtocol.XON | 0x80) || c == (ZmodemProtocol.XOFF | 0x80)) {
            baos.write(ZmodemProtocol.ZDLE);
            baos.write(c ^ 0x40);
        } else {
            baos.write(c);
        }
    }
    
    private void writeHex(ByteArrayOutputStream baos, int value) {
        baos.write(ZmodemProtocol.toHex(value >> 4));
        baos.write(ZmodemProtocol.toHex(value));
    }
    
    private int[] receiveHeader() throws IOException {
        int[] header = new int[5];
        
        // 等待ZPAD
        long timeout = System.currentTimeMillis() + 5000;
        while (System.currentTimeMillis() < timeout) {
            if (in.available() > 0) {
                int c = in.read();
                if (c == ZmodemProtocol.ZPAD) break;
            } else {
                try { Thread.sleep(10); } catch (InterruptedException e) { break; }
            }
        }
        
        // 跳过额外的ZPAD
        int c;
        do {
            if (in.available() == 0) return null;
            c = in.read();
        } while (c == ZmodemProtocol.ZPAD);
        
        if (c != ZmodemProtocol.ZDLE) return null;
        
        int format = in.read();
        if (format == ZmodemProtocol.ZHEX) {
            for (int i = 0; i < 5; i++) {
                int h1 = ZmodemProtocol.fromHex((char) in.read());
                int h2 = ZmodemProtocol.fromHex((char) in.read());
                if (h1 < 0 || h2 < 0) return null;
                header[i] = (h1 << 4) | h2;
            }
            // 跳过CRC和换行
            for (int i = 0; i < 6; i++) in.read();
        } else if (format == ZmodemProtocol.ZBIN || format == ZmodemProtocol.ZBIN32) {
            for (int i = 0; i < 5; i++) {
                header[i] = readZDLE();
            }
            int crcLen = (format == ZmodemProtocol.ZBIN32) ? 4 : 2;
            for (int i = 0; i < crcLen; i++) readZDLE();
        } else {
            return null;
        }
        
        return header;
    }
    
    private int readZDLE() throws IOException {
        int c = in.read();
        if (c == ZmodemProtocol.ZDLE) {
            c = in.read();
            if (c == ZmodemProtocol.ZDLEE) {
                return ZmodemProtocol.ZDLE;
            }
            return c ^ 0x40;
        }
        return c;
    }
    
    private byte[] longToBytes(long value) {
        return new byte[] {
            (byte) (value & 0xFF),
            (byte) ((value >> 8) & 0xFF),
            (byte) ((value >> 16) & 0xFF),
            (byte) ((value >> 24) & 0xFF)
        };
    }
    
    public void cancel() {
        this.cancelled = true;
    }
}
