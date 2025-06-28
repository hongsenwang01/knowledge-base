package com.example.filemanager.dto.response;

import java.util.List;

/**
 * 分页响应DTO
 * 
 * @author system
 * @since 2024-01-01
 */
public class PageResponseDTO<T> {
    
    /**
     * 当前页码
     */
    private Long current;
    
    /**
     * 每页显示条数
     */
    private Long size;
    
    /**
     * 总记录数
     */
    private Long total;
    
    /**
     * 总页数
     */
    private Long pages;
    
    /**
     * 数据列表
     */
    private List<T> records;
    
    public PageResponseDTO() {}
    
    public PageResponseDTO(Long current, Long size, Long total, List<T> records) {
        this.current = current;
        this.size = size;
        this.total = total;
        this.records = records;
        this.pages = (total + size - 1) / size;
    }
    
    // Getter and Setter methods
    public Long getCurrent() {
        return current;
    }
    
    public void setCurrent(Long current) {
        this.current = current;
    }
    
    public Long getSize() {
        return size;
    }
    
    public void setSize(Long size) {
        this.size = size;
    }
    
    public Long getTotal() {
        return total;
    }
    
    public void setTotal(Long total) {
        this.total = total;
    }
    
    public Long getPages() {
        return pages;
    }
    
    public void setPages(Long pages) {
        this.pages = pages;
    }
    
    public List<T> getRecords() {
        return records;
    }
    
    public void setRecords(List<T> records) {
        this.records = records;
    }
} 