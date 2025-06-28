package com.example.filemanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.filemanager.entity.FileInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文件信息Mapper接口
 * 
 * @author system
 * @since 2024-01-01
 */
@Mapper
public interface FileInfoMapper extends BaseMapper<FileInfo> {
    
    /**
     * 根据目录ID查询文件列表
     * 
     * @param directoryId 目录ID
     * @return 文件列表
     */
    List<FileInfo> selectByDirectoryId(@Param("directoryId") Long directoryId);
    
    /**
     * 分页查询文件列表
     * 
     * @param page 分页对象
     * @param directoryId 目录ID（可选）
     * @return 分页文件列表
     */
    Page<FileInfo> selectFilesWithPage(Page<FileInfo> page, @Param("directoryId") Long directoryId);
    
    /**
     * 根据MD5查询文件
     * 
     * @param md5Hash MD5哈希值
     * @return 文件信息
     */
    FileInfo selectByMd5Hash(@Param("md5Hash") String md5Hash);
    
    /**
     * 更新文件下载次数
     * 
     * @param fileId 文件ID
     * @return 更新结果
     */
    int updateDownloadCount(@Param("fileId") Long fileId);
    
    /**
     * 统计文件总大小
     * 
     * @return 文件总大小（字节）
     */
    Long sumTotalFileSize();
} 