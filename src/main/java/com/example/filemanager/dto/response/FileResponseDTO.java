package com.example.filemanager.dto.response;

import java.time.LocalDateTime;

/**
 * 文件响应DTO
 * 
 * @author system
 * @since 2024-01-01
 */
public class FileResponseDTO {
    
    /**
     * 文件ID
     */
    private Long id;
    
    /**
     * 原始文件名
     */
    private String originalName;
    
    /**
     * 存储文件名
     */
    private String storedName;
    
    /**
     * 完整文件路径
     */
    private String filePath;
    
    /**
     * 文件大小（字节）
     */
    private Long fileSize;
    
    /**
     * 文件类型
     */
    private String fileType;
    
    /**
     * MIME类型
     */
    private String mimeType;
    
    /**
     * 文件MD5哈希值
     */
    private String md5Hash;
    
    /**
     * 所属目录ID
     */
    private Long directoryId;
    
    /**
     * 所属目录名称
     */
    private String directoryName;
    
    /**
     * 文件描述
     */
    private String description;
    
    /**
     * 下载次数
     */
    private Integer downloadCount;
    
    /**
     * 文件大小（格式化显示）
     */
    private String fileSizeFormatted;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    public FileResponseDTO() {}
    
    // Getter and Setter methods
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getOriginalName() {
        return originalName;
    }
    
    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }
    
    public String getStoredName() {
        return storedName;
    }
    
    public void setStoredName(String storedName) {
        this.storedName = storedName;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    public String getFileType() {
        return fileType;
    }
    
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    
    public String getMimeType() {
        return mimeType;
    }
    
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
    
    public String getMd5Hash() {
        return md5Hash;
    }
    
    public void setMd5Hash(String md5Hash) {
        this.md5Hash = md5Hash;
    }
    
    public Long getDirectoryId() {
        return directoryId;
    }
    
    public void setDirectoryId(Long directoryId) {
        this.directoryId = directoryId;
    }
    
    public String getDirectoryName() {
        return directoryName;
    }
    
    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Integer getDownloadCount() {
        return downloadCount;
    }
    
    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }
    
    public String getFileSizeFormatted() {
        return fileSizeFormatted;
    }
    
    public void setFileSizeFormatted(String fileSizeFormatted) {
        this.fileSizeFormatted = fileSizeFormatted;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "FileResponseDTO{" +
                "id=" + id +
                ", originalName='" + originalName + '\'' +
                ", storedName='" + storedName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileSize=" + fileSize +
                ", fileType='" + fileType + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", md5Hash='" + md5Hash + '\'' +
                ", directoryId=" + directoryId +
                ", directoryName='" + directoryName + '\'' +
                ", description='" + description + '\'' +
                ", downloadCount=" + downloadCount +
                ", fileSizeFormatted='" + fileSizeFormatted + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
} 