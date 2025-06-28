package com.example.filemanager.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.File;

/**
 * 文件存储配置类
 * 
 * @author system
 * @since 2024-01-01
 */
@Configuration
public class FileStorageConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(FileStorageConfig.class);
    
    /**
     * 文件存储根目录
     */
    @Value("${file.upload.path:#{systemProperties['user.home']}/文档管理}")
    private String uploadPath;
    
    /**
     * 最大文件大小（默认100MB）
     */
    @Value("${file.upload.max-size:104857600}")
    private long maxFileSize;
    
    /**
     * 允许的文件类型
     */
    @Value("${file.upload.allowed-types:*}")
    private String allowedTypes;
    
    @PostConstruct
    public void init() {
        try {
            // 创建上传根目录
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                if (created) {
                    logger.info("文件上传根目录创建成功：{}", uploadPath);
                } else {
                    logger.error("文件上传根目录创建失败：{}", uploadPath);
                    throw new RuntimeException("无法创建文件上传根目录: " + uploadPath);
                }
            } else {
                logger.info("文件上传根目录已存在：{}", uploadPath);
            }
            
            // 验证目录是否可写
            if (!uploadDir.canWrite()) {
                logger.error("文件上传根目录不可写：{}", uploadPath);
                throw new RuntimeException("文件上传根目录不可写: " + uploadPath);
            }
            
            logger.info("文件上传配置初始化完成，根目录：{}", uploadDir.getAbsolutePath());
        } catch (Exception e) {
            logger.error("文件上传配置初始化失败", e);
            throw new RuntimeException("文件上传配置初始化失败", e);
        }
    }
    
    public String getUploadPath() {
        return uploadPath;
    }
    
    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }
    
    public long getMaxFileSize() {
        return maxFileSize;
    }
    
    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }
    
    public String getAllowedTypes() {
        return allowedTypes;
    }
    
    public void setAllowedTypes(String allowedTypes) {
        this.allowedTypes = allowedTypes;
    }
} 