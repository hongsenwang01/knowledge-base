package com.example.filemanager.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 目录更新请求DTO
 * 
 * @author system
 * @since 2024-01-01
 */
public class DirectoryUpdateDTO {
    
    /**
     * 目录名称
     */
    @NotBlank(message = "目录名称不能为空")
    @Size(max = 100, message = "目录名称长度不能超过100个字符")
    private String name;
    
    /**
     * 目录描述
     */
    @Size(max = 500, message = "目录描述长度不能超过500个字符")
    private String description;
    
    public DirectoryUpdateDTO() {}
    
    public DirectoryUpdateDTO(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    // Getter and Setter methods
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return "DirectoryUpdateDTO{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
} 