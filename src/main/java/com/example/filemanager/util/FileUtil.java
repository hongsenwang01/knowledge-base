package com.example.filemanager.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件工具类
 * 
 * @author system
 * @since 2024-01-01
 */
public class FileUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);
    
    /**
     * 生成唯一的存储文件名
     * 
     * @param originalFilename 原始文件名
     * @return 唯一的存储文件名
     */
    public static String generateStoredFilename(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return timestamp + "_" + uuid + (extension.isEmpty() ? "" : "." + extension);
    }
    
    /**
     * 获取文件扩展名
     * 
     * @param filename 文件名
     * @return 扩展名（不包含点）
     */
    public static String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
            return filename.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }
    
    /**
     * 根据文件扩展名获取文件类型
     * 
     * @param extension 文件扩展名
     * @return 文件类型
     */
    public static String getFileType(String extension) {
        if (extension == null || extension.isEmpty()) {
            return "其他";
        }
        
        extension = extension.toLowerCase();
        
        // 文档类型
        if ("doc,docx,pdf,txt,rtf,odt".contains(extension)) {
            return "文档";
        }
        // 表格类型
        if ("xls,xlsx,csv,ods".contains(extension)) {
            return "表格";
        }
        // 演示文档
        if ("ppt,pptx,odp".contains(extension)) {
            return "演示";
        }
        // 图片类型
        if ("jpg,jpeg,png,gif,bmp,svg,ico,webp".contains(extension)) {
            return "图片";
        }
        // 音频类型
        if ("mp3,wav,flac,aac,ogg,wma".contains(extension)) {
            return "音频";
        }
        // 视频类型
        if ("mp4,avi,mov,wmv,flv,mkv,webm".contains(extension)) {
            return "视频";
        }
        // 压缩包
        if ("zip,rar,7z,tar,gz,bz2".contains(extension)) {
            return "压缩包";
        }
        // 程序文件
        if ("exe,msi,dmg,deb,rpm,apk".contains(extension)) {
            return "程序";
        }
        
        return "其他";
    }
    
    /**
     * 计算文件的MD5哈希值
     * 
     * @param file 文件对象
     * @return MD5哈希值
     */
    public static String calculateMD5(MultipartFile file) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = file.getBytes();
            byte[] digest = md.digest(bytes);
            
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | IOException e) {
            logger.error("计算文件MD5失败", e);
            return null;
        }
    }
    
    /**
     * 计算文件的MD5哈希值（通过文件路径）
     * 
     * @param filePath 文件路径
     * @return MD5哈希值
     */
    public static String calculateMD5(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192];
            int bytesRead;
            
            while ((bytesRead = fis.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }
            
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | IOException e) {
            logger.error("计算文件MD5失败", e);
            return null;
        }
    }
    
    /**
     * 格式化文件大小
     * 
     * @param bytes 文件大小（字节）
     * @return 格式化后的文件大小
     */
    public static String formatFileSize(Long bytes) {
        if (bytes == null || bytes <= 0) {
            return "0 B";
        }
        
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double size = bytes.doubleValue();
        
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        
        return String.format("%.2f %s", size, units[unitIndex]);
    }
    
    /**
     * 创建目录（如果不存在）
     * 
     * @param directoryPath 目录路径
     * @return 是否创建成功
     */
    public static boolean createDirectoryIfNotExists(String directoryPath) {
        try {
            // 使用File类处理路径，避免编码问题
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                if (created) {
                    logger.info("创建目录成功：{}", directoryPath);
                } else {
                    logger.error("创建目录失败：{}", directoryPath);
                    return false;
                }
            } else {
                logger.debug("目录已存在：{}", directoryPath);
            }
            return true;
        } catch (Exception e) {
            logger.error("创建目录失败：{}", directoryPath, e);
            return false;
        }
    }
    
    /**
     * 保存上传的文件
     * 
     * @param file 上传的文件
     * @param savePath 保存路径（包含文件名）
     * @return 是否保存成功
     */
    public static boolean saveUploadedFile(MultipartFile file, String savePath) {
        try {
            // 确保父目录存在
            File saveFile = new File(savePath);
            File parentDir = saveFile.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
            
            // 保存文件
            file.transferTo(saveFile);
            logger.info("文件保存成功：{}", savePath);
            return true;
        } catch (IOException e) {
            logger.error("文件保存失败：{}", savePath, e);
            return false;
        }
    }
    
    /**
     * 删除文件
     * 
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    public static boolean deleteFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                boolean deleted = file.delete();
                if (deleted) {
                    logger.info("文件删除成功：{}", filePath);
                } else {
                    logger.warn("文件删除失败：{}", filePath);
                }
                return deleted;
            } else {
                logger.warn("文件不存在：{}", filePath);
                return true; // 文件不存在也算删除成功
            }
        } catch (Exception e) {
            logger.error("文件删除失败：{}", filePath, e);
            return false;
        }
    }
} 