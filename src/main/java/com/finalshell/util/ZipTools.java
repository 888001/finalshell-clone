package com.finalshell.util;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.zip.*;

/**
 * 压缩解压工具类
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Utility_DeepAnalysis.md - Tools.java 压缩/解压方法
 */
public class ZipTools {
    
    private static final Logger logger = LoggerFactory.getLogger(ZipTools.class);
    private static final int BUFFER_SIZE = 8192;
    
    /**
     * ZIP压缩目录
     */
    public static void zip(String sourceDir, String outputFile) throws IOException {
        Path sourcePath = Paths.get(sourceDir);
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputFile))) {
            Files.walk(sourcePath)
                .filter(path -> !Files.isDirectory(path))
                .forEach(path -> {
                    try {
                        String entryName = sourcePath.relativize(path).toString();
                        zos.putNextEntry(new ZipEntry(entryName));
                        Files.copy(path, zos);
                        zos.closeEntry();
                    } catch (IOException e) {
                        logger.error("压缩文件失败: {}", path, e);
                    }
                });
        }
    }
    
    /**
     * ZIP压缩文件列表
     */
    public static void zip(List<File> files, String basePath, String outputFile) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputFile))) {
            for (File file : files) {
                if (file.isDirectory()) {
                    addDirectoryToZip(file, file.getName(), zos);
                } else {
                    addFileToZip(file, basePath, zos);
                }
            }
        }
    }
    
    private static void addDirectoryToZip(File dir, String parentPath, ZipOutputStream zos) throws IOException {
        File[] files = dir.listFiles();
        if (files == null) return;
        
        for (File file : files) {
            String entryPath = parentPath + "/" + file.getName();
            if (file.isDirectory()) {
                addDirectoryToZip(file, entryPath, zos);
            } else {
                zos.putNextEntry(new ZipEntry(entryPath));
                try (FileInputStream fis = new FileInputStream(file)) {
                    IOUtils.copy(fis, zos);
                }
                zos.closeEntry();
            }
        }
    }
    
    private static void addFileToZip(File file, String basePath, ZipOutputStream zos) throws IOException {
        String entryName = file.getAbsolutePath();
        if (basePath != null && entryName.startsWith(basePath)) {
            entryName = entryName.substring(basePath.length());
            if (entryName.startsWith(File.separator)) {
                entryName = entryName.substring(1);
            }
        }
        
        zos.putNextEntry(new ZipEntry(entryName));
        try (FileInputStream fis = new FileInputStream(file)) {
            IOUtils.copy(fis, zos);
        }
        zos.closeEntry();
    }
    
    /**
     * ZIP解压
     */
    public static void unzip(String srcPath, String outPath) throws IOException {
        File destDir = new File(outPath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(srcPath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File destFile = newFile(destDir, entry);
                
                if (entry.isDirectory()) {
                    destFile.mkdirs();
                } else {
                    File parent = destFile.getParentFile();
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }
                    try (FileOutputStream fos = new FileOutputStream(destFile)) {
                        IOUtils.copy(zis, fos);
                    }
                }
                zis.closeEntry();
            }
        }
    }
    
    /**
     * 内存压缩
     */
    public static byte[] compress(byte[] data, String entryName) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            zos.putNextEntry(new ZipEntry(entryName));
            zos.write(data);
            zos.closeEntry();
        }
        return baos.toByteArray();
    }
    
    /**
     * 内存解压
     */
    public static byte[] decompress(byte[] data) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(data))) {
            ZipEntry entry = zis.getNextEntry();
            if (entry != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                IOUtils.copy(zis, baos);
                return baos.toByteArray();
            }
        }
        return new byte[0];
    }
    
    /**
     * TAR.GZ压缩
     */
    public static void tarGz(List<File> fileList, String basePath, String savePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(savePath);
             GzipCompressorOutputStream gcos = new GzipCompressorOutputStream(fos);
             TarArchiveOutputStream tos = new TarArchiveOutputStream(gcos, "UTF-8")) {
            
            tos.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
            
            for (File file : fileList) {
                if (file.isDirectory()) {
                    addDirectoryToTar(tos, file, basePath);
                } else {
                    addFileToTar(tos, file, basePath);
                }
            }
        }
    }
    
    private static void addDirectoryToTar(TarArchiveOutputStream tos, File dir, String basePath) throws IOException {
        File[] files = dir.listFiles();
        if (files == null) return;
        
        for (File file : files) {
            if (file.isDirectory()) {
                addDirectoryToTar(tos, file, basePath);
            } else {
                addFileToTar(tos, file, basePath);
            }
        }
    }
    
    private static void addFileToTar(TarArchiveOutputStream tos, File file, String basePath) throws IOException {
        String entryName = file.getAbsolutePath();
        if (basePath != null && entryName.startsWith(basePath)) {
            entryName = entryName.substring(basePath.length());
            if (entryName.startsWith(File.separator)) {
                entryName = entryName.substring(1);
            }
        }
        
        TarArchiveEntry entry = new TarArchiveEntry(file, entryName);
        tos.putArchiveEntry(entry);
        try (FileInputStream fis = new FileInputStream(file)) {
            IOUtils.copy(fis, tos);
        }
        tos.closeArchiveEntry();
    }
    
    /**
     * TAR.GZ解压
     */
    public static void untarGz(String archive, String dstPath, boolean keepDate) throws IOException {
        File destDir = new File(dstPath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        
        try (FileInputStream fis = new FileInputStream(archive);
             GzipCompressorInputStream gis = new GzipCompressorInputStream(fis);
             TarArchiveInputStream tis = new TarArchiveInputStream(gis, "UTF-8")) {
            
            TarArchiveEntry entry;
            while ((entry = tis.getNextTarEntry()) != null) {
                File destFile = new File(destDir, entry.getName());
                
                if (entry.isDirectory()) {
                    destFile.mkdirs();
                } else {
                    File parent = destFile.getParentFile();
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }
                    try (FileOutputStream fos = new FileOutputStream(destFile)) {
                        IOUtils.copy(tis, fos);
                    }
                    
                    if (keepDate) {
                        destFile.setLastModified(entry.getLastModifiedDate().getTime());
                    }
                }
            }
        }
    }
    
    /**
     * 安全创建文件 (防止Zip Slip攻击)
     */
    private static File newFile(File destinationDir, ZipEntry entry) throws IOException {
        File destFile = new File(destinationDir, entry.getName());
        
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();
        
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + entry.getName());
        }
        
        return destFile;
    }
}
