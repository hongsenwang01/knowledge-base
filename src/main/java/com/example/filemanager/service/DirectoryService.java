package com.example.filemanager.service;

import com.example.filemanager.dto.request.DirectoryCreateDTO;
import com.example.filemanager.dto.request.DirectoryMoveDTO;
import com.example.filemanager.dto.request.DirectoryUpdateDTO;
import com.example.filemanager.dto.response.DeleteResultDTO;
import com.example.filemanager.dto.response.DirectoryResponseDTO;
import com.example.filemanager.dto.response.PageResponseDTO;
import com.example.filemanager.entity.Directory;

import java.util.List;

/**
 * 目录服务接口
 * 
 * @author system
 * @since 2024-01-01
 */
public interface DirectoryService {
    
    /**
     * 获取所有目录列表
     * 
     * @return 目录列表
     */
    List<DirectoryResponseDTO> getAllDirectories();
    
    /**
     * 分页获取目录列表
     * 
     * @param current 当前页码
     * @param size 每页显示条数
     * @return 分页目录列表
     */
    PageResponseDTO<DirectoryResponseDTO> getAllDirectoriesWithPage(Long current, Long size);
    
    /**
     * 获取目录树结构
     * 
     * @return 目录树
     */
    List<DirectoryResponseDTO> getDirectoryTree();
    
    /**
     * 根据父目录ID获取子目录
     * 
     * @param parentId 父目录ID
     * @return 子目录列表
     */
    List<DirectoryResponseDTO> getDirectoriesByParentId(Long parentId);
    
    /**
     * 根据ID获取目录详情
     * 
     * @param id 目录ID
     * @return 目录详情
     */
    DirectoryResponseDTO getDirectoryById(Long id);
    
    /**
     * 创建新目录
     * 
     * @param createDTO 创建请求数据
     * @return 创建后的目录信息
     */
    DirectoryResponseDTO createDirectory(DirectoryCreateDTO createDTO);
    
    /**
     * 更新目录信息
     * 
     * @param id 目录ID
     * @param updateDTO 更新信息
     * @return 更新后的目录信息
     */
    DirectoryResponseDTO updateDirectory(Long id, DirectoryUpdateDTO updateDTO);
    
    /**
     * 移动目录到新的父目录下
     * 
     * @param id 要移动的目录ID
     * @param moveDTO 移动请求数据
     * @return 移动后的目录信息
     */
    DirectoryResponseDTO moveDirectory(Long id, DirectoryMoveDTO moveDTO);
    
    /**
     * 软删除目录
     * 
     * @param id 目录ID
     * @return 删除结果（包含成功状态和错误信息）
     */
    DeleteResultDTO deleteDirectory(Long id);
    
    /**
     * 将Directory实体转换为响应DTO
     * 
     * @param directory 目录实体
     * @return 响应DTO
     */
    DirectoryResponseDTO convertToResponseDTO(Directory directory);
    
    /**
     * 统计总目录数量
     * 
     * @return 目录总数
     */
    Long countTotalDirectories();
} 