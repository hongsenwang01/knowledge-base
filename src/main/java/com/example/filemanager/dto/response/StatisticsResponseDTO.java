package com.example.filemanager.dto.response;

/**
 * 统计信息响应DTO
 * 
 * @author system
 * @since 2024-01-01
 */
public class StatisticsResponseDTO {
    
    /**
     * 总文件夹数量
     */
    private Long totalDirectories;
    
    /**
     * 总文件数量
     */
    private Long totalFiles;
    
    /**
     * 文件总大小（字节）
     */
    private Long totalFileSize;
    
    /**
     * 文件总大小（格式化显示）
     */
    private String totalFileSizeFormatted;
    
    public StatisticsResponseDTO() {}
    
    public StatisticsResponseDTO(Long totalDirectories, Long totalFiles, Long totalFileSize, String totalFileSizeFormatted) {
        this.totalDirectories = totalDirectories;
        this.totalFiles = totalFiles;
        this.totalFileSize = totalFileSize;
        this.totalFileSizeFormatted = totalFileSizeFormatted;
    }
    
    // Getter and Setter methods
    public Long getTotalDirectories() {
        return totalDirectories;
    }
    
    public void setTotalDirectories(Long totalDirectories) {
        this.totalDirectories = totalDirectories;
    }
    
    public Long getTotalFiles() {
        return totalFiles;
    }
    
    public void setTotalFiles(Long totalFiles) {
        this.totalFiles = totalFiles;
    }
    
    public Long getTotalFileSize() {
        return totalFileSize;
    }
    
    public void setTotalFileSize(Long totalFileSize) {
        this.totalFileSize = totalFileSize;
    }
    
    public String getTotalFileSizeFormatted() {
        return totalFileSizeFormatted;
    }
    
    public void setTotalFileSizeFormatted(String totalFileSizeFormatted) {
        this.totalFileSizeFormatted = totalFileSizeFormatted;
    }
    
    @Override
    public String toString() {
        return "StatisticsResponseDTO{" +
                "totalDirectories=" + totalDirectories +
                ", totalFiles=" + totalFiles +
                ", totalFileSize=" + totalFileSize +
                ", totalFileSizeFormatted='" + totalFileSizeFormatted + '\'' +
                '}';
    }
} 