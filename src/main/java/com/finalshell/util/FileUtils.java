package com.finalshell.util;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * 文件工具类
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Obfuscated_Subpackages_DeepAnalysis.md - FileUtilsM
 */
public class FileUtils {
    
    public static final long ONE_KB = 1024L;
    public static final long ONE_MB = ONE_KB * 1024;
    public static final long ONE_GB = ONE_MB * 1024;
    public static final long ONE_TB = ONE_GB * 1024;
    
    public static final BigInteger ONE_KB_BI = BigInteger.valueOf(ONE_KB);
    public static final BigInteger ONE_MB_BI = ONE_KB_BI.multiply(ONE_KB_BI);
    public static final BigInteger ONE_GB_BI = ONE_KB_BI.multiply(ONE_MB_BI);
    public static final BigInteger ONE_TB_BI = ONE_KB_BI.multiply(ONE_GB_BI);
    
    private static final int COPY_BUFFER_SIZE = 8192;
    
    private FileUtils() {}
    
    /**
     * 获取临时目录路径
     */
    public static String getTempDirectoryPath() {
        return System.getProperty("java.io.tmpdir");
    }
    
    /**
     * 获取用户目录路径
     */
    public static String getUserDirectoryPath() {
        return System.getProperty("user.home");
    }
    
    /**
     * 获取临时目录
     */
    public static File getTempDirectory() {
        return new File(getTempDirectoryPath());
    }
    
    /**
     * 获取用户目录
     */
    public static File getUserDirectory() {
        return new File(getUserDirectoryPath());
    }
    
    /**
     * 构建文件路径
     */
    public static File getFile(File directory, String... names) {
        File file = directory;
        for (String name : names) {
            file = new File(file, name);
        }
        return file;
    }
    
    /**
     * 构建文件路径
     */
    public static File getFile(String... names) {
        File file = null;
        for (String name : names) {
            if (file == null) {
                file = new File(name);
            } else {
                file = new File(file, name);
            }
        }
        return file;
    }
    
    /**
     * 打开文件输入流
     */
    public static FileInputStream openInputStream(File file) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException("File does not exist: " + file);
        }
        if (file.isDirectory()) {
            throw new IOException("File is a directory: " + file);
        }
        if (!file.canRead()) {
            throw new IOException("Cannot read file: " + file);
        }
        return new FileInputStream(file);
    }
    
    /**
     * 打开文件输出流
     */
    public static FileOutputStream openOutputStream(File file) throws IOException {
        return openOutputStream(file, false);
    }
    
    /**
     * 打开文件输出流
     */
    public static FileOutputStream openOutputStream(File file, boolean append) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File is a directory: " + file);
            }
            if (!file.canWrite()) {
                throw new IOException("Cannot write to file: " + file);
            }
        } else {
            File parent = file.getParentFile();
            if (parent != null && !parent.mkdirs() && !parent.isDirectory()) {
                throw new IOException("Cannot create parent directory: " + parent);
            }
        }
        return new FileOutputStream(file, append);
    }
    
    /**
     * 强制创建目录
     */
    public static void forceMkdir(File directory) throws IOException {
        if (directory.exists()) {
            if (!directory.isDirectory()) {
                throw new IOException("File exists but is not a directory: " + directory);
            }
        } else {
            if (!directory.mkdirs()) {
                if (!directory.isDirectory()) {
                    throw new IOException("Cannot create directory: " + directory);
                }
            }
        }
    }
    
    /**
     * 更新文件修改时间
     */
    public static void touch(File file) throws IOException {
        if (!file.exists()) {
            openOutputStream(file).close();
        }
        if (!file.setLastModified(System.currentTimeMillis())) {
            throw new IOException("Cannot set last modified time: " + file);
        }
    }
    
    /**
     * 读取文件内容为字符串
     */
    public static String readFileToString(File file, String encoding) throws IOException {
        try (InputStream in = openInputStream(file)) {
            return toString(in, encoding);
        }
    }
    
    /**
     * 读取文件内容为字符串 (UTF-8)
     */
    public static String readFileToString(File file) throws IOException {
        return readFileToString(file, StandardCharsets.UTF_8.name());
    }
    
    /**
     * 写入字符串到文件
     */
    public static void writeStringToFile(File file, String data, String encoding) throws IOException {
        try (OutputStream out = openOutputStream(file)) {
            out.write(data.getBytes(encoding));
        }
    }
    
    /**
     * 写入字符串到文件 (UTF-8)
     */
    public static void writeStringToFile(File file, String data) throws IOException {
        writeStringToFile(file, data, StandardCharsets.UTF_8.name());
    }
    
    /**
     * 读取文件为字节数组
     */
    public static byte[] readFileToByteArray(File file) throws IOException {
        try (InputStream in = openInputStream(file)) {
            return toByteArray(in);
        }
    }
    
    /**
     * 写入字节数组到文件
     */
    public static void writeByteArrayToFile(File file, byte[] data) throws IOException {
        try (OutputStream out = openOutputStream(file)) {
            out.write(data);
        }
    }
    
    /**
     * 复制文件
     */
    public static void copyFile(File srcFile, File destFile) throws IOException {
        if (srcFile.isDirectory()) {
            throw new IOException("Source is a directory: " + srcFile);
        }
        
        if (destFile.exists() && destFile.isDirectory()) {
            throw new IOException("Destination is a directory: " + destFile);
        }
        
        File parent = destFile.getParentFile();
        if (parent != null && !parent.mkdirs() && !parent.isDirectory()) {
            throw new IOException("Cannot create parent directory: " + parent);
        }
        
        try (InputStream in = openInputStream(srcFile);
             OutputStream out = openOutputStream(destFile)) {
            copy(in, out);
        }
        
        destFile.setLastModified(srcFile.lastModified());
    }
    
    /**
     * 复制目录
     */
    public static void copyDirectory(File srcDir, File destDir) throws IOException {
        if (!srcDir.isDirectory()) {
            throw new IOException("Source is not a directory: " + srcDir);
        }
        
        if (destDir.exists() && !destDir.isDirectory()) {
            throw new IOException("Destination exists but is not a directory: " + destDir);
        }
        
        forceMkdir(destDir);
        
        File[] files = srcDir.listFiles();
        if (files != null) {
            for (File file : files) {
                File dest = new File(destDir, file.getName());
                if (file.isDirectory()) {
                    copyDirectory(file, dest);
                } else {
                    copyFile(file, dest);
                }
            }
        }
    }
    
    /**
     * 删除文件或目录
     */
    public static boolean deleteQuietly(File file) {
        if (file == null) return false;
        
        try {
            if (file.isDirectory()) {
                cleanDirectory(file);
            }
        } catch (Exception ignored) {}
        
        try {
            return file.delete();
        } catch (Exception ignored) {
            return false;
        }
    }
    
    /**
     * 清空目录
     */
    public static void cleanDirectory(File directory) throws IOException {
        if (!directory.isDirectory()) {
            throw new IOException("Not a directory: " + directory);
        }
        
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                forceDelete(file);
            }
        }
    }
    
    /**
     * 强制删除
     */
    public static void forceDelete(File file) throws IOException {
        if (file.isDirectory()) {
            cleanDirectory(file);
        }
        
        if (!file.delete()) {
            throw new IOException("Cannot delete: " + file);
        }
    }
    
    /**
     * 获取目录大小
     */
    public static long sizeOfDirectory(File directory) {
        if (!directory.isDirectory()) {
            return 0;
        }
        
        long size = 0;
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    size += sizeOfDirectory(file);
                } else {
                    size += file.length();
                }
            }
        }
        return size;
    }
    
    /**
     * 字节数转显示大小
     */
    public static String byteCountToDisplaySize(long size) {
        return byteCountToDisplaySize(BigInteger.valueOf(size));
    }
    
    /**
     * 字节数转显示大小
     */
    public static String byteCountToDisplaySize(BigInteger size) {
        if (size.divide(ONE_TB_BI).compareTo(BigInteger.ZERO) > 0) {
            return size.divide(ONE_TB_BI) + " TB";
        } else if (size.divide(ONE_GB_BI).compareTo(BigInteger.ZERO) > 0) {
            return size.divide(ONE_GB_BI) + " GB";
        } else if (size.divide(ONE_MB_BI).compareTo(BigInteger.ZERO) > 0) {
            return size.divide(ONE_MB_BI) + " MB";
        } else if (size.divide(ONE_KB_BI).compareTo(BigInteger.ZERO) > 0) {
            return size.divide(ONE_KB_BI) + " KB";
        } else {
            return size + " bytes";
        }
    }
    
    /**
     * 流复制
     */
    public static long copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[COPY_BUFFER_SIZE];
        long count = 0;
        int n;
        while ((n = input.read(buffer)) != -1) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
    
    /**
     * 输入流转字节数组
     */
    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }
    
    /**
     * 输入流转字符串
     */
    public static String toString(InputStream input, String encoding) throws IOException {
        return new String(toByteArray(input), encoding);
    }
    
    /**
     * 列出目录中的文件
     */
    public static Collection<File> listFiles(File directory, String[] extensions, boolean recursive) {
        List<File> files = new ArrayList<>();
        
        if (!directory.isDirectory()) {
            return files;
        }
        
        File[] found = directory.listFiles();
        if (found != null) {
            for (File file : found) {
                if (file.isDirectory()) {
                    if (recursive) {
                        files.addAll(listFiles(file, extensions, true));
                    }
                } else {
                    if (extensions == null || extensions.length == 0) {
                        files.add(file);
                    } else {
                        for (String ext : extensions) {
                            if (file.getName().toLowerCase().endsWith("." + ext.toLowerCase())) {
                                files.add(file);
                                break;
                            }
                        }
                    }
                }
            }
        }
        
        return files;
    }
    
    /**
     * 检查文件是否存在
     */
    public static boolean exists(File file) {
        return file != null && file.exists();
    }
    
    /**
     * 获取文件扩展名
     */
    public static String getExtension(String filename) {
        if (filename == null) return "";
        int index = filename.lastIndexOf('.');
        if (index == -1) return "";
        return filename.substring(index + 1);
    }
    
    /**
     * 获取不带扩展名的文件名
     */
    public static String getBaseName(String filename) {
        if (filename == null) return "";
        int index = filename.lastIndexOf('.');
        if (index == -1) return filename;
        return filename.substring(0, index);
    }
}
