package com.example.filemanager.common.result;

/**
 * 返回状态码枚举
 * 
 * @author system
 * @since 2024-01-01
 */
public enum ResultCode {
    
    // 通用状态码
    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),
    
    // 请求相关
    PARAM_ERROR(400, "请求参数错误"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),
    
    // 文件相关
    FILE_NOT_FOUND(1001, "文件不存在"),
    FILE_UPLOAD_ERROR(1002, "文件上传失败"),
    FILE_DELETE_ERROR(1003, "文件删除失败"),
    FILE_TYPE_NOT_SUPPORTED(1004, "不支持的文件类型"),
    FILE_SIZE_EXCEED(1005, "文件大小超出限制"),
    FILE_NAME_INVALID(1006, "文件名不合法"),
    
    // 目录相关
    DIRECTORY_NOT_FOUND(2001, "目录不存在"),
    DIRECTORY_CREATE_ERROR(2002, "目录创建失败"),
    DIRECTORY_DELETE_ERROR(2003, "目录删除失败"),
    DIRECTORY_NOT_EMPTY(2004, "目录不为空"),
    DIRECTORY_NAME_INVALID(2005, "目录名不合法"),
    DIRECTORY_NAME_DUPLICATE(2006, "目录名称重复"),
    DIRECTORY_HAS_CHILDREN(2007, "目录下存在子目录，无法删除"),
    
    // 系统相关
    SYSTEM_ERROR(9999, "系统异常");
    
    private final Integer code;
    private final String message;
    
    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
} 