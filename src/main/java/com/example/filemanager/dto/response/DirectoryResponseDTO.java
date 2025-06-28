package com.example.filemanager.dto.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 目录响应DTO
 * 
 * @author system
 * @since 2024-01-01
 */
public class DirectoryResponseDTO {
    
    /**
     * 目录ID
     */
    private Long id;
    
    /**
     * 目录名称
     */
    private String name;
    
    /**
     * 父目录ID
     */
    private Long parentId;
    
    /**
     * 目录路径
     */
    private String path;
    
    /**
     * 目录描述
     */
    private String description;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 子目录列表（用于树形结构）
     */
    private List<DirectoryResponseDTO> children;
    
    /**
     * 是否有子目录
     */
    private Boolean hasChildren;
    
    public DirectoryResponseDTO() {}
    
    // Getter and Setter methods
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Long getParentId() {
        return parentId;
    }
    
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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
    
    public List<DirectoryResponseDTO> getChildren() {
        return children;
    }
    
    public void setChildren(List<DirectoryResponseDTO> children) {
        this.children = children;
    }
    
    public Boolean getHasChildren() {
        return hasChildren;
    }
    
    public void setHasChildren(Boolean hasChildren) {
        this.hasChildren = hasChildren;
    }
} 