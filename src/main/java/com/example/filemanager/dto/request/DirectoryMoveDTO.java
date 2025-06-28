package com.example.filemanager.dto.request;


/**
 * 目录移动请求DTO
 * 
 * @author system
 * @since 2024-01-01
 */
public class DirectoryMoveDTO {
    
    /**
     * 新的父目录ID，null或0表示移动到根目录下
     */
    private Long newParentId;
    
    public DirectoryMoveDTO() {}
    
    public DirectoryMoveDTO(Long newParentId) {
        this.newParentId = newParentId;
    }
    
    // Getter and Setter methods
    public Long getNewParentId() {
        return newParentId;
    }
    
    public void setNewParentId(Long newParentId) {
        this.newParentId = newParentId;
    }
    
    @Override
    public String toString() {
        return "DirectoryMoveDTO{" +
                "newParentId=" + newParentId +
                '}';
    }
} 