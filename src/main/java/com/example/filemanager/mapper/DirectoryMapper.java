package com.example.filemanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.filemanager.entity.Directory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 目录Mapper接口
 * 
 * @author system
 * @since 2024-01-01
 */
@Mapper
public interface DirectoryMapper extends BaseMapper<Directory> {
    
    /**
     * 查询所有未删除的目录
     * 
     * @return 目录列表
     */
    List<Directory> selectAllDirectories();
    
    /**
     * 分页查询未删除的目录
     * 
     * @param page 分页对象
     * @return 分页目录列表
     */
    Page<Directory> selectAllDirectoriesWithPage(Page<Directory> page);
    
    /**
     * 根据父目录ID查询子目录
     * 
     * @param parentId 父目录ID
     * @return 子目录列表
     */
    List<Directory> selectByParentId(Long parentId);
    
    /**
     * 根据路径查询目录
     * 
     * @param path 目录路径
     * @return 目录信息
     */
    Directory selectByPath(String path);
} 