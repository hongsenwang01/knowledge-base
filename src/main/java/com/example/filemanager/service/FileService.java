package com.example.filemanager.service;

import com.example.filemanager.dto.response.FileResponseDTO;
import com.example.filemanager.dto.response.PageResponseDTO;
import com.example.filemanager.entity.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件服务接口
 * 
 * @author system
 * @since 2024-01-01
 */
public interface FileService {
    
    /**
     * 上传文件到指定目录
     * 
     * @param file 上传的文件
     * @param directoryId 目录ID
     * @param description 文件描述（可选）
     * @return 上传后的文件信息
     */
    FileResponseDTO uploadFile(MultipartFile file, Long directoryId, String description);
    
    /**
     * 根据目录ID获取文件列表
     * 
     * @param directoryId 目录ID
     * @return 文件列表
     */
    List<FileResponseDTO> getFilesByDirectoryId(Long directoryId);
    
    /**
     * 分页获取文件列表
     * 
     * @param current 当前页码
     * @param size 每页显示条数
     * @param directoryId 目录ID（可选）
     * @return 分页文件列表
     */
    PageResponseDTO<FileResponseDTO> getFilesWithPage(Long current, Long size, Long directoryId);
    
    /**
     * 根据文件ID获取文件详情
     * 
     * @param fileId 文件ID
     * @return 文件详情
     */
    FileResponseDTO getFileById(Long fileId);
    
    /**
     * 删除文件
     * 
     * @param fileId 文件ID
     * @return 删除是否成功
     */
    boolean deleteFile(Long fileId);
    
    /**
     * 更新文件下载次数
     * 
     * @param fileId 文件ID
     * @return 更新是否成功
     */
    boolean updateDownloadCount(Long fileId);
    
    /**
     * 获取文件的完整存储路径
     * 
     * @param fileId 文件ID
     * @return 完整文件路径
     */
    String getFullFilePath(Long fileId);
    
    /**
     * 将FileInfo实体转换为响应DTO
     * 
     * @param fileInfo 文件实体
     * @return 响应DTO
     */
    FileResponseDTO convertToResponseDTO(FileInfo fileInfo);
    
    /**
     * 统计总文件数量
     * 
     * @return 文件总数
     */
    Long countTotalFiles();
    
    /**
     * 统计文件总大小
     * 
     * @return 文件总大小（字节）
     */
    Long sumTotalFileSize();
} 