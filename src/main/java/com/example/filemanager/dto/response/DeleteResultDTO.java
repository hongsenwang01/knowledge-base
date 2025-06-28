package com.example.filemanager.dto.response;

/**
 * 删除结果DTO
 * 
 * @author system
 * @since 2024-01-01
 */
public class DeleteResultDTO {
    
    /**
     * 删除是否成功
     */
    private boolean success;
    
    /**
     * 错误码（成功时为null）
     */
    private Integer errorCode;
    
    /**
     * 消息
     */
    private String message;
    
    public DeleteResultDTO() {}
    
    public DeleteResultDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public DeleteResultDTO(boolean success, Integer errorCode, String message) {
        this.success = success;
        this.errorCode = errorCode;
        this.message = message;
    }
    
    /**
     * 创建成功结果
     */
    public static DeleteResultDTO success(String message) {
        return new DeleteResultDTO(true, message);
    }
    
    /**
     * 创建失败结果
     */
    public static DeleteResultDTO error(Integer errorCode, String message) {
        return new DeleteResultDTO(false, errorCode, message);
    }
    
    // Getter and Setter methods
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public Integer getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    @Override
    public String toString() {
        return "DeleteResultDTO{" +
                "success=" + success +
                ", errorCode=" + errorCode +
                ", message='" + message + '\'' +
                '}';
    }
} 