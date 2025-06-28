package com.example.filemanager.common.constant;

/**
 * 文件相关常量
 * 
 * @author system
 * @since 2024-01-01
 */
public class FileConstant {
    
    /**
     * 文件上传最大大小（100MB）
     */
    public static final long MAX_FILE_SIZE = 100 * 1024 * 1024L;
    
    /**
     * 默认文件存储路径
     */
    public static final String DEFAULT_UPLOAD_PATH = "uploads";
    
    /**
     * 文件名分隔符
     */
    public static final String FILE_SEPARATOR = "/";
    
    /**
     * 支持的图片格式
     */
    public static final String[] IMAGE_TYPES = {"jpg", "jpeg", "png", "gif", "bmp", "webp"};
    
    /**
     * 支持的文档格式
     */
    public static final String[] DOCUMENT_TYPES = {"doc", "docx", "xls", "xlsx", "ppt", "pptx", "pdf", "txt"};
    
    /**
     * 支持的压缩格式
     */
    public static final String[] ARCHIVE_TYPES = {"zip", "rar", "7z", "tar", "gz"};
    
    /**
     * 根目录ID
     */
    public static final Long ROOT_DIRECTORY_ID = 1L;
    
    /**
     * 根目录路径
     */
    public static final String ROOT_DIRECTORY_PATH = "/";
    
    /**
     * 默认页面大小
     */
    public static final int DEFAULT_PAGE_SIZE = 20;
    
    /**
     * 最大页面大小
     */
    public static final int MAX_PAGE_SIZE = 100;
} 