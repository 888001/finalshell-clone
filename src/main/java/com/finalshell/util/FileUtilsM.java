package com.finalshell.util;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件工具类
 * 提供文件操作的各种工具方法
 */
public class FileUtilsM {
    
    private static final int BUFFER_SIZE = 8192;
    
    public static String readFileToString(File file) throws IOException {
        return readFileToString(file, StandardCharsets.UTF_8);
    }
    
    public static String readFileToString(File file, Charset charset) throws IOException {
        return new String(Files.readAllBytes(file.toPath()), charset);
    }
    
    public static List<String> readLines(File file) throws IOException {
        return readLines(file, StandardCharsets.UTF_8);
    }
    
    public static List<String> readLines(File file, Charset charset) throws IOException {
        return Files.readAllLines(file.toPath(), charset);
    }
    
    public static byte[] readFileToByteArray(File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }
    
    public static void writeStringToFile(File file, String data) throws IOException {
        writeStringToFile(file, data, StandardCharsets.UTF_8);
    }
    
    public static void writeStringToFile(File file, String data, Charset charset) throws IOException {
        Files.write(file.toPath(), data.getBytes(charset));
    }
    
    public static void writeLines(File file, List<String> lines) throws IOException {
        Files.write(file.toPath(), lines, StandardCharsets.UTF_8);
    }
    
    public static void writeByteArrayToFile(File file, byte[] data) throws IOException {
        Files.write(file.toPath(), data);
    }
    
    public static void copyFile(File srcFile, File destFile) throws IOException {
        Files.copy(srcFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
    
    public static void copyDirectory(File srcDir, File destDir) throws IOException {
        if (!srcDir.isDirectory()) {
            throw new IllegalArgumentException("Source is not a directory");
        }
        
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        
        File[] files = srcDir.listFiles();
        if (files != null) {
            for (File file : files) {
                File destFile = new File(destDir, file.getName());
                if (file.isDirectory()) {
                    copyDirectory(file, destFile);
                } else {
                    copyFile(file, destFile);
                }
            }
        }
    }
    
    public static void moveFile(File srcFile, File destFile) throws IOException {
        Files.move(srcFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
    
    public static boolean deleteQuietly(File file) {
        try {
            return deleteRecursive(file);
        } catch (Exception e) {
            return false;
        }
    }
    
    public static boolean deleteRecursive(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File child : files) {
                    deleteRecursive(child);
                }
            }
        }
        return file.delete();
    }
    
    public static void cleanDirectory(File directory) throws IOException {
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Not a directory");
        }
        
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                deleteRecursive(file);
            }
        }
    }
    
    public static long sizeOf(File file) {
        if (file.isDirectory()) {
            return sizeOfDirectory(file);
        }
        return file.length();
    }
    
    public static long sizeOfDirectory(File directory) {
        long size = 0;
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                size += sizeOf(file);
            }
        }
        return size;
    }
    
    public static String getExtension(String filename) {
        if (filename == null) return "";
        int dot = filename.lastIndexOf('.');
        if (dot > 0) {
            return filename.substring(dot + 1);
        }
        return "";
    }
    
    public static String getBaseName(String filename) {
        if (filename == null) return "";
        int dot = filename.lastIndexOf('.');
        if (dot > 0) {
            return filename.substring(0, dot);
        }
        return filename;
    }
    
    public static boolean isSymlink(File file) {
        return Files.isSymbolicLink(file.toPath());
    }
    
    public static File createTempFile(String prefix, String suffix) throws IOException {
        return File.createTempFile(prefix, suffix);
    }
    
    public static File createTempDirectory(String prefix) throws IOException {
        return Files.createTempDirectory(prefix).toFile();
    }
}
