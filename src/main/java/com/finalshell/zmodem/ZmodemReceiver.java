package com.finalshell.zmodem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Zmodem接收器 - 处理sz下载
 */
public class ZmodemReceiver {
    private static final Logger logger = LoggerFactory.getLogger(ZmodemReceiver.class);
    
    private final InputStream in;
    private final OutputStream out;
    private final File downloadDir;
    private final ZmodemListener listener;
    
    private boolean useCrc32 = true;
    private volatile boolean cancelled = false;
    private long totalBytes = 0;
    private long receivedBytes = 0;
    private String currentFileName;
    private File currentFile;
    private FileOutputStream currentFileOutput;
    private List<File> receivedFiles = new ArrayList<>();
    
    public ZmodemReceiver(InputStream in, OutputStream out, File downloadDir, ZmodemListener listener) {
        this.in = in;
        this.out = out;
        this.downloadDir = downloadDir;
        this.listener = listener;
        if (!downloadDir.exists()) {
            downloadDir.mkdirs();
        }
    }
    
    /**
     * 开始接收
     */
    public List<File> receive() throws ZmodemException {
        try {
            // 发送ZRINIT
            sendZRINIT();
            
            while (!cancelled) {
                int[] header = receiveHeader();
                if (header == null) {
                    throw new ZmodemException("接收头部失败");
                }
                
                int frameType = header[0];
                logger.debug("收到帧类型: {}", frameType);
                
                switch (frameType) {
                    case ZmodemProtocol.ZRQINIT:
                        sendZRINIT();
                        break;
                        
                    case ZmodemProtocol.ZFILE:
                        receiveFileHeader();
                        break;
                        
                    case ZmodemProtocol.ZDATA:
                        long offset = (header[1] & 0xFF) | 
                                     ((header[2] & 0xFF) << 8) |
                                     ((header[3] & 0xFF) << 16) |
                                     ((header[4] & 0xFF) << 24);
                        receiveFileData(offset);
                        break;
                        
                    case ZmodemProtocol.ZEOF:
                        finishFile();
                        sendZRINIT();
                        break;
                        
                    case ZmodemProtocol.ZFIN:
                        sendZFIN();
                        return receivedFiles;
                        
                    case ZmodemProtocol.ZCAN:
                    case ZmodemProtocol.ZABORT:
                        throw new ZmodemException("传输被取消");
                        
                    default:
                        logger.warn("未知帧类型: {}", frameType);
                }
            }
            
            throw new ZmodemException("传输被用户取消");
            
        } catch (IOException e) {
            throw new ZmodemException("IO错误: " + e.getMessage(), e);
        } finally {
            closeCurrentFile();
        }
    }
    
    private void sendZRINIT() throws IOException {
        byte[] header = new byte[20];
        int pos = 0;
        
        header[pos++] = ZmodemProtocol.ZPAD;
        header[pos++] = ZmodemProtocol.ZPAD;
        header[pos++] = ZmodemProtocol.ZDLE;
        header[pos++] = ZmodemProtocol.ZHEX;
        
        // 帧类型
        int type = ZmodemProtocol.ZRINIT;
        header[pos++] = (byte) ZmodemProtocol.toHex(type >> 4);
        header[pos++] = (byte) ZmodemProtocol.toHex(type);
        
        // 标志
        int flags = ZmodemProtocol.CANFDX | ZmodemProtocol.CANOVIO | ZmodemProtocol.CANFC32;
        header[pos++] = (byte) ZmodemProtocol.toHex(flags >> 4);
        header[pos++] = (byte) ZmodemProtocol.toHex(flags);
        
        // 填充0
        for (int i = 0; i < 6; i++) {
            header[pos++] = '0';
        }
        
        // CRC
        byte[] data = new byte[] { (byte) type, (byte) flags, 0, 0, 0 };
        int crc = ZmodemProtocol.crc16(data, 0, 5);
        header[pos++] = (byte) ZmodemProtocol.toHex(crc >> 12);
        header[pos++] = (byte) ZmodemProtocol.toHex(crc >> 8);
        header[pos++] = (byte) ZmodemProtocol.toHex(crc >> 4);
        header[pos++] = (byte) ZmodemProtocol.toHex(crc);
        
        header[pos++] = '\r';
        header[pos++] = '\n';
        
        out.write(header, 0, pos);
        out.flush();
    }
    
    private void sendZFIN() throws IOException {
        byte[] header = new byte[20];
        int pos = 0;
        
        header[pos++] = ZmodemProtocol.ZPAD;
        header[pos++] = ZmodemProtocol.ZPAD;
        header[pos++] = ZmodemProtocol.ZDLE;
        header[pos++] = ZmodemProtocol.ZHEX;
        
        int type = ZmodemProtocol.ZFIN;
        header[pos++] = (byte) ZmodemProtocol.toHex(type >> 4);
        header[pos++] = (byte) ZmodemProtocol.toHex(type);
        
        for (int i = 0; i < 6; i++) {
            header[pos++] = '0';
        }
        
        byte[] data = new byte[] { (byte) type, 0, 0, 0, 0 };
        int crc = ZmodemProtocol.crc16(data, 0, 5);
        header[pos++] = (byte) ZmodemProtocol.toHex(crc >> 12);
        header[pos++] = (byte) ZmodemProtocol.toHex(crc >> 8);
        header[pos++] = (byte) ZmodemProtocol.toHex(crc >> 4);
        header[pos++] = (byte) ZmodemProtocol.toHex(crc);
        
        header[pos++] = '\r';
        header[pos++] = '\n';
        
        out.write(header, 0, pos);
        out.flush();
        
        // 发送OO结束
        out.write('O');
        out.write('O');
        out.flush();
    }
    
    private int[] receiveHeader() throws IOException {
        int[] header = new int[5];
        
        // 等待ZPAD
        while (true) {
            int c = readByte();
            if (c == -1) return null;
            if (c == ZmodemProtocol.ZPAD) break;
        }
        
        // 跳过额外的ZPAD
        int c;
        do {
            c = readByte();
            if (c == -1) return null;
        } while (c == ZmodemProtocol.ZPAD);
        
        // 期望ZDLE
        if (c != ZmodemProtocol.ZDLE) {
            return null;
        }
        
        // 读取帧格式
        int format = readByte();
        if (format == -1) return null;
        
        if (format == ZmodemProtocol.ZHEX) {
            // 十六进制帧
            for (int i = 0; i < 5; i++) {
                int h1 = ZmodemProtocol.fromHex((char) readByte());
                int h2 = ZmodemProtocol.fromHex((char) readByte());
                if (h1 < 0 || h2 < 0) return null;
                header[i] = (h1 << 4) | h2;
            }
            // 读取CRC (忽略)
            for (int i = 0; i < 4; i++) readByte();
            // 读取CR LF
            readByte();
            readByte();
        } else if (format == ZmodemProtocol.ZBIN || format == ZmodemProtocol.ZBIN32) {
            useCrc32 = (format == ZmodemProtocol.ZBIN32);
            for (int i = 0; i < 5; i++) {
                header[i] = readZDLE();
                if (header[i] < 0) return null;
            }
            // 读取CRC
            int crcLen = useCrc32 ? 4 : 2;
            for (int i = 0; i < crcLen; i++) readZDLE();
        } else {
            return null;
        }
        
        return header;
    }
    
    private void receiveFileHeader() throws IOException, ZmodemException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // 读取数据直到ZCRC*
        while (true) {
            int c = readZDLE();
            if (c < 0) {
                throw new ZmodemException("读取文件头失败");
            }
            if (c == ZmodemProtocol.ZCRCW || c == ZmodemProtocol.ZCRCE ||
                c == ZmodemProtocol.ZCRCG || c == ZmodemProtocol.ZCRCQ) {
                // 读取CRC
                int crcLen = useCrc32 ? 4 : 2;
                for (int i = 0; i < crcLen; i++) readZDLE();
                break;
            }
            baos.write(c);
        }
        
        byte[] data = baos.toByteArray();
        parseFileInfo(data);
        
        // 发送ZRPOS
        sendZRPOS(0);
    }
    
    private void parseFileInfo(byte[] data) throws ZmodemException {
        // 格式: filename\0size mtime mode serial files-remaining bytes-remaining
        int nullPos = 0;
        while (nullPos < data.length && data[nullPos] != 0) nullPos++;
        
        if (nullPos == 0) {
            throw new ZmodemException("文件名为空");
        }
        
        currentFileName = new String(data, 0, nullPos, StandardCharsets.UTF_8);
        
        // 解析大小
        if (nullPos + 1 < data.length) {
            String info = new String(data, nullPos + 1, data.length - nullPos - 1, StandardCharsets.UTF_8);
            String[] parts = info.trim().split(" ");
            if (parts.length > 0) {
                try {
                    totalBytes = Long.parseLong(parts[0]);
                } catch (NumberFormatException e) {
                    totalBytes = 0;
                }
            }
        }
        
        receivedBytes = 0;
        currentFile = new File(downloadDir, currentFileName);
        
        logger.info("开始接收文件: {} ({}字节)", currentFileName, totalBytes);
        if (listener != null) {
            listener.onFileStart(currentFileName, totalBytes);
        }
        
        try {
            currentFileOutput = new FileOutputStream(currentFile);
        } catch (FileNotFoundException e) {
            throw new ZmodemException("无法创建文件: " + currentFile.getAbsolutePath());
        }
    }
    
    private void sendZRPOS(long offset) throws IOException {
        byte[] header = new byte[20];
        int pos = 0;
        
        header[pos++] = ZmodemProtocol.ZPAD;
        header[pos++] = ZmodemProtocol.ZPAD;
        header[pos++] = ZmodemProtocol.ZDLE;
        header[pos++] = ZmodemProtocol.ZHEX;
        
        int type = ZmodemProtocol.ZRPOS;
        header[pos++] = (byte) ZmodemProtocol.toHex(type >> 4);
        header[pos++] = (byte) ZmodemProtocol.toHex(type);
        
        // 位置 (4字节, 小端)
        for (int i = 0; i < 4; i++) {
            int b = (int) ((offset >> (i * 8)) & 0xFF);
            header[pos++] = (byte) ZmodemProtocol.toHex(b >> 4);
            header[pos++] = (byte) ZmodemProtocol.toHex(b);
        }
        
        // CRC
        byte[] data = new byte[] { 
            (byte) type, 
            (byte) (offset & 0xFF),
            (byte) ((offset >> 8) & 0xFF),
            (byte) ((offset >> 16) & 0xFF),
            (byte) ((offset >> 24) & 0xFF)
        };
        int crc = ZmodemProtocol.crc16(data, 0, 5);
        header[pos++] = (byte) ZmodemProtocol.toHex(crc >> 12);
        header[pos++] = (byte) ZmodemProtocol.toHex(crc >> 8);
        header[pos++] = (byte) ZmodemProtocol.toHex(crc >> 4);
        header[pos++] = (byte) ZmodemProtocol.toHex(crc);
        
        header[pos++] = '\r';
        header[pos++] = '\n';
        
        out.write(header, 0, pos);
        out.flush();
    }
    
    private void receiveFileData(long expectedOffset) throws IOException, ZmodemException {
        if (currentFileOutput == null) {
            throw new ZmodemException("未初始化文件");
        }
        
        while (!cancelled) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int endType = -1;
            
            while (true) {
                int c = readZDLE();
                if (c < 0) {
                    throw new ZmodemException("读取数据失败");
                }
                if (c == ZmodemProtocol.ZCRCW || c == ZmodemProtocol.ZCRCE ||
                    c == ZmodemProtocol.ZCRCG || c == ZmodemProtocol.ZCRCQ) {
                    endType = c;
                    // 读取CRC
                    int crcLen = useCrc32 ? 4 : 2;
                    for (int i = 0; i < crcLen; i++) readZDLE();
                    break;
                }
                baos.write(c);
            }
            
            byte[] data = baos.toByteArray();
            currentFileOutput.write(data);
            receivedBytes += data.length;
            
            if (listener != null) {
                listener.onProgress(currentFileName, receivedBytes, totalBytes);
            }
            
            if (endType == ZmodemProtocol.ZCRCE || endType == ZmodemProtocol.ZCRCW) {
                // 需要确认
                sendZACK(receivedBytes);
            }
            
            if (endType == ZmodemProtocol.ZCRCE) {
                // 数据结束
                return;
            }
        }
    }
    
    private void sendZACK(long offset) throws IOException {
        byte[] header = new byte[20];
        int pos = 0;
        
        header[pos++] = ZmodemProtocol.ZPAD;
        header[pos++] = ZmodemProtocol.ZPAD;
        header[pos++] = ZmodemProtocol.ZDLE;
        header[pos++] = ZmodemProtocol.ZHEX;
        
        int type = ZmodemProtocol.ZACK;
        header[pos++] = (byte) ZmodemProtocol.toHex(type >> 4);
        header[pos++] = (byte) ZmodemProtocol.toHex(type);
        
        for (int i = 0; i < 4; i++) {
            int b = (int) ((offset >> (i * 8)) & 0xFF);
            header[pos++] = (byte) ZmodemProtocol.toHex(b >> 4);
            header[pos++] = (byte) ZmodemProtocol.toHex(b);
        }
        
        byte[] data = new byte[] { 
            (byte) type, 
            (byte) (offset & 0xFF),
            (byte) ((offset >> 8) & 0xFF),
            (byte) ((offset >> 16) & 0xFF),
            (byte) ((offset >> 24) & 0xFF)
        };
        int crc = ZmodemProtocol.crc16(data, 0, 5);
        header[pos++] = (byte) ZmodemProtocol.toHex(crc >> 12);
        header[pos++] = (byte) ZmodemProtocol.toHex(crc >> 8);
        header[pos++] = (byte) ZmodemProtocol.toHex(crc >> 4);
        header[pos++] = (byte) ZmodemProtocol.toHex(crc);
        
        header[pos++] = '\r';
        header[pos++] = '\n';
        
        out.write(header, 0, pos);
        out.flush();
    }
    
    private void finishFile() throws IOException {
        closeCurrentFile();
        if (currentFile != null) {
            receivedFiles.add(currentFile);
            logger.info("文件接收完成: {}", currentFileName);
            if (listener != null) {
                listener.onFileComplete(currentFileName, currentFile);
            }
        }
        currentFile = null;
        currentFileName = null;
    }
    
    private void closeCurrentFile() {
        if (currentFileOutput != null) {
            try {
                currentFileOutput.close();
            } catch (IOException e) {
                logger.error("关闭文件失败", e);
            }
            currentFileOutput = null;
        }
    }
    
    private int readByte() throws IOException {
        return in.read();
    }
    
    private int readZDLE() throws IOException {
        int c = readByte();
        if (c == -1) return -1;
        if (c == ZmodemProtocol.ZDLE) {
            c = readByte();
            if (c == -1) return -1;
            if (c == ZmodemProtocol.ZDLEE) {
                return ZmodemProtocol.ZDLE;
            }
            if ((c & 0x40) != 0) {
                c = c ^ 0x40;
            }
            // 特殊结束符
            if (c == ZmodemProtocol.ZCRCW || c == ZmodemProtocol.ZCRCE ||
                c == ZmodemProtocol.ZCRCG || c == ZmodemProtocol.ZCRCQ) {
                return c;
            }
        }
        return c & 0xFF;
    }
    
    public void cancel() {
        this.cancelled = true;
    }
    
    /**
     * Zmodem监听器
     */
    public interface ZmodemListener {
        void onFileStart(String fileName, long totalSize);
        void onProgress(String fileName, long received, long total);
        void onFileComplete(String fileName, File file);
        void onError(String error);
    }
}
