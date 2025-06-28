package com.example.filemanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.filemanager.common.exception.BusinessException;
import com.example.filemanager.common.result.ResultCode;
import com.example.filemanager.dto.request.DirectoryCreateDTO;
import com.example.filemanager.dto.request.DirectoryMoveDTO;
import com.example.filemanager.dto.request.DirectoryUpdateDTO;
import com.example.filemanager.dto.response.DeleteResultDTO;
import com.example.filemanager.dto.response.DirectoryResponseDTO;
import com.example.filemanager.dto.response.PageResponseDTO;
import com.example.filemanager.entity.Directory;
import com.example.filemanager.mapper.DirectoryMapper;
import com.example.filemanager.service.DirectoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 目录服务实现类
 * 
 * @author system
 * @since 2024-01-01
 */
@Service
public class DirectoryServiceImpl implements DirectoryService {
    
    private static final Logger logger = LoggerFactory.getLogger(DirectoryServiceImpl.class);
    
    @Autowired
    private DirectoryMapper directoryMapper;
    
    @Override
    public List<DirectoryResponseDTO> getAllDirectories() {
        try {
            logger.info("开始查询所有目录");
            List<Directory> directories = directoryMapper.selectAllDirectories();
            
            List<DirectoryResponseDTO> result = new ArrayList<>();
            for (Directory directory : directories) {
                result.add(convertToResponseDTO(directory));
            }
            
            logger.info("查询所有目录完成，共{}个目录", result.size());
            return result;
            
        } catch (Exception e) {
            logger.error("查询所有目录失败", e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }
    
    @Override
    public PageResponseDTO<DirectoryResponseDTO> getAllDirectoriesWithPage(Long current, Long size) {
        try {
            logger.info("开始分页查询目录，当前页：{}，每页大小：{}", current, size);
            
            // 创建分页对象
            Page<Directory> page = new Page<>(current, size);
            
            // 执行分页查询
            Page<Directory> pageResult = directoryMapper.selectAllDirectoriesWithPage(page);
            
            // 转换为响应DTO
            List<DirectoryResponseDTO> directoryDTOs = new ArrayList<>();
            for (Directory directory : pageResult.getRecords()) {
                directoryDTOs.add(convertToResponseDTO(directory));
            }
            
            // 构建分页响应对象
            PageResponseDTO<DirectoryResponseDTO> result = new PageResponseDTO<>(
                pageResult.getCurrent(),
                pageResult.getSize(),
                pageResult.getTotal(),
                directoryDTOs
            );
            
            logger.info("分页查询目录完成，当前页：{}，每页大小：{}，总记录数：{}，总页数：{}", 
                result.getCurrent(), result.getSize(), result.getTotal(), result.getPages());
            
            return result;
            
        } catch (Exception e) {
            logger.error("分页查询目录失败，当前页：{}，每页大小：{}", current, size, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }
    
    @Override
    public List<DirectoryResponseDTO> getDirectoryTree() {
        try {
            logger.info("开始构建目录树");
            List<Directory> allDirectories = directoryMapper.selectAllDirectories();
            
            // 将所有目录转换为DTO
            Map<Long, DirectoryResponseDTO> directoryMap = new HashMap<>();
            List<DirectoryResponseDTO> rootDirectories = new ArrayList<>();
            
            // 先将所有目录转换为DTO并建立映射
            for (Directory directory : allDirectories) {
                DirectoryResponseDTO dto = convertToResponseDTO(directory);
                dto.setChildren(new ArrayList<>());
                directoryMap.put(directory.getId(), dto);
            }
            
            // 构建树形结构
            for (Directory directory : allDirectories) {
                DirectoryResponseDTO currentDto = directoryMap.get(directory.getId());
                
                if (directory.getParentId() == null || directory.getParentId() == 0) {
                    // 根目录
                    rootDirectories.add(currentDto);
                } else {
                    // 子目录，添加到父目录的children中
                    DirectoryResponseDTO parentDto = directoryMap.get(directory.getParentId());
                    if (parentDto != null) {
                        parentDto.getChildren().add(currentDto);
                        parentDto.setHasChildren(true);
                    }
                }
            }
            
            // 设置hasChildren标志
            for (DirectoryResponseDTO dto : directoryMap.values()) {
                dto.setHasChildren(dto.getChildren() != null && !dto.getChildren().isEmpty());
            }
            
            logger.info("构建目录树完成，根目录数量：{}", rootDirectories.size());
            return rootDirectories;
            
        } catch (Exception e) {
            logger.error("构建目录树失败", e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }
    
    @Override
    public List<DirectoryResponseDTO> getDirectoriesByParentId(Long parentId) {
        try {
            logger.info("查询父目录ID为{}的子目录", parentId);
            List<Directory> directories = directoryMapper.selectByParentId(parentId);
            
            List<DirectoryResponseDTO> result = new ArrayList<>();
            for (Directory directory : directories) {
                result.add(convertToResponseDTO(directory));
            }
            
            logger.info("查询子目录完成，共{}个子目录", result.size());
            return result;
            
        } catch (Exception e) {
            logger.error("查询子目录失败，父目录ID：{}", parentId, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }
    
    @Override
    public DirectoryResponseDTO getDirectoryById(Long id) {
        try {
            logger.info("查询目录详情，ID：{}", id);
            Directory directory = directoryMapper.selectById(id);
            
            if (directory == null || directory.getDeleted() == 1) {
                logger.warn("目录不存在，ID：{}", id);
                throw new BusinessException(ResultCode.DIRECTORY_NOT_FOUND);
            }
            
            DirectoryResponseDTO result = convertToResponseDTO(directory);
            logger.info("查询目录详情完成，目录名称：{}", result.getName());
            return result;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("查询目录详情失败，ID：{}", id, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }
    
    @Override
    public DirectoryResponseDTO updateDirectory(Long id, DirectoryUpdateDTO updateDTO) {
        try {
            logger.info("开始更新目录，ID：{}，请求数据：{}", id, updateDTO);
            
            // 检查目录是否存在
            Directory existingDirectory = directoryMapper.selectById(id);
            if (existingDirectory == null || existingDirectory.getDeleted() == 1) {
                logger.warn("目录不存在或已删除，ID：{}", id);
                throw new BusinessException(ResultCode.DIRECTORY_NOT_FOUND);
            }
            
            // 检查名称是否重复（同一父目录下不能有重名目录）
            if (!existingDirectory.getName().equals(updateDTO.getName())) {
                // 查询同一父目录下是否已存在同名目录
                List<Directory> siblings = directoryMapper.selectByParentId(existingDirectory.getParentId());
                boolean nameExists = siblings.stream()
                    .anyMatch(dir -> !dir.getId().equals(id) && dir.getName().equals(updateDTO.getName()));
                
                if (nameExists) {
                    logger.warn("同一父目录下已存在同名目录，父目录ID：{}，目录名称：{}", 
                        existingDirectory.getParentId(), updateDTO.getName());
                    throw new BusinessException(ResultCode.DIRECTORY_NAME_DUPLICATE);
                }
            }
            
            // 更新字段
            existingDirectory.setName(updateDTO.getName());
            existingDirectory.setDescription(updateDTO.getDescription());
            existingDirectory.setUpdatedAt(LocalDateTime.now());
            
            // 执行更新
            int updateResult = directoryMapper.updateById(existingDirectory);
            if (updateResult <= 0) {
                logger.error("更新目录失败，ID：{}", id);
                throw new BusinessException(ResultCode.SYSTEM_ERROR);
            }
            
            // 返回更新后的数据
            DirectoryResponseDTO result = convertToResponseDTO(existingDirectory);
            logger.info("更新目录成功，ID：{}，新名称：{}", id, result.getName());
            return result;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("更新目录失败，ID：{}", id, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }

    @Override
    public DeleteResultDTO deleteDirectory(Long id) {
        try {
            logger.info("开始软删除目录，ID：{}", id);
            
            // 检查目录是否存在
            Directory existingDirectory = directoryMapper.selectById(id);
            if (existingDirectory == null || existingDirectory.getDeleted() == 1) {
                logger.warn("目录不存在或已删除，ID：{}", id);
                return DeleteResultDTO.error(ResultCode.DIRECTORY_NOT_FOUND.getCode(), 
                    ResultCode.DIRECTORY_NOT_FOUND.getMessage());
            }
            
            // 检查是否有子目录
            List<Directory> children = directoryMapper.selectByParentId(id);
            if (!children.isEmpty()) {
                logger.warn("目录下存在子目录，无法删除，ID：{}，子目录数量：{}", id, children.size());
                return DeleteResultDTO.error(ResultCode.DIRECTORY_HAS_CHILDREN.getCode(), 
                    ResultCode.DIRECTORY_HAS_CHILDREN.getMessage());
            }
            
            // 使用MyBatis-Plus的逻辑删除
            int deleteResult = directoryMapper.deleteById(id);
            if (deleteResult <= 0) {
                logger.error("软删除目录失败，ID：{}", id);
                return DeleteResultDTO.error(ResultCode.SYSTEM_ERROR.getCode(), 
                    ResultCode.SYSTEM_ERROR.getMessage());
            }
            
            logger.info("软删除目录成功，ID：{}，目录名称：{}", id, existingDirectory.getName());
            return DeleteResultDTO.success("删除目录成功");
            
        } catch (Exception e) {
            logger.error("软删除目录失败，ID：{}", id, e);
            return DeleteResultDTO.error(ResultCode.SYSTEM_ERROR.getCode(), 
                ResultCode.SYSTEM_ERROR.getMessage());
        }
    }
    
    @Override
    public DirectoryResponseDTO createDirectory(DirectoryCreateDTO createDTO) {
        try {
            logger.info("开始创建目录，请求数据：{}", createDTO);
            
            // 处理父目录ID，如果为null或0，表示要在根目录下创建，需要找到根目录的ID
            Long parentId;
            if (createDTO.getParentId() == null || createDTO.getParentId() == 0) {
                // 查找根目录（parent_id = 0 的目录）
                Directory rootDirectory = findRootDirectory();
                if (rootDirectory == null) {
                    logger.error("系统根目录不存在");
                    throw new BusinessException(ResultCode.SYSTEM_ERROR);
                }
                parentId = rootDirectory.getId();
                logger.info("在根目录下创建子目录，根目录ID：{}", parentId);
            } else {
                parentId = createDTO.getParentId();
            }
            
            // 检查父目录是否存在
            Directory parentDirectory = directoryMapper.selectById(parentId);
            if (parentDirectory == null || parentDirectory.getDeleted() == 1) {
                logger.warn("父目录不存在，父目录ID：{}", parentId);
                throw new BusinessException(ResultCode.DIRECTORY_NOT_FOUND);
            }
            
            // 检查同一父目录下是否已存在同名目录
            List<Directory> siblings = directoryMapper.selectByParentId(parentId);
            boolean nameExists = siblings.stream()
                .anyMatch(dir -> dir.getName().equals(createDTO.getName()));
            
            if (nameExists) {
                logger.warn("同一父目录下已存在同名目录，父目录ID：{}，目录名称：{}", 
                    parentId, createDTO.getName());
                throw new BusinessException(ResultCode.DIRECTORY_NAME_DUPLICATE);
            }
            
            // 生成目录路径
            String path = generateDirectoryPath(parentId, createDTO.getName());
            
            // 创建目录对象
            Directory newDirectory = new Directory();
            newDirectory.setName(createDTO.getName());
            newDirectory.setParentId(parentId);
            newDirectory.setPath(path);
            newDirectory.setDescription(createDTO.getDescription());
            newDirectory.setCreatedAt(LocalDateTime.now());
            newDirectory.setUpdatedAt(LocalDateTime.now());
            newDirectory.setDeleted(0);
            
            // 保存到数据库
            int insertResult = directoryMapper.insert(newDirectory);
            if (insertResult <= 0) {
                logger.error("创建目录失败，请求数据：{}", createDTO);
                throw new BusinessException(ResultCode.SYSTEM_ERROR);
            }
            
            // 返回创建后的目录信息
            DirectoryResponseDTO result = convertToResponseDTO(newDirectory);
            logger.info("创建目录成功，目录ID：{}，目录名称：{}", newDirectory.getId(), newDirectory.getName());
            return result;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("创建目录失败，请求数据：{}", createDTO, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }
    
    @Override
    public DirectoryResponseDTO moveDirectory(Long id, DirectoryMoveDTO moveDTO) {
        try {
            logger.info("开始移动目录，目录ID：{}，移动请求：{}", id, moveDTO);
            
            // 检查要移动的目录是否存在
            Directory directoryToMove = directoryMapper.selectById(id);
            if (directoryToMove == null || directoryToMove.getDeleted() == 1) {
                logger.warn("要移动的目录不存在，目录ID：{}", id);
                throw new BusinessException(ResultCode.DIRECTORY_NOT_FOUND);
            }
            
            // 不能移动根目录
            if (directoryToMove.getParentId() != null && directoryToMove.getParentId() == 0) {
                logger.warn("不能移动根目录，目录ID：{}", id);
                throw new BusinessException(ResultCode.DIRECTORY_NOT_FOUND); // 可以定义一个新的错误码
            }
            
            // 处理新的父目录ID
            Long newParentId;
            if (moveDTO.getNewParentId() == null || moveDTO.getNewParentId() == 0) {
                // 移动到根目录下
                Directory rootDirectory = findRootDirectory();
                if (rootDirectory == null) {
                    logger.error("系统根目录不存在");
                    throw new BusinessException(ResultCode.SYSTEM_ERROR);
                }
                newParentId = rootDirectory.getId();
                logger.info("移动到根目录下，根目录ID：{}", newParentId);
            } else {
                newParentId = moveDTO.getNewParentId();
            }
            
            // 如果新父目录和当前父目录相同，无需移动
            if (newParentId.equals(directoryToMove.getParentId())) {
                logger.info("目录已在目标位置，无需移动，目录ID：{}", id);
                return convertToResponseDTO(directoryToMove);
            }
            
            // 检查新的父目录是否存在
            Directory newParentDirectory = directoryMapper.selectById(newParentId);
            if (newParentDirectory == null || newParentDirectory.getDeleted() == 1) {
                logger.warn("新的父目录不存在，父目录ID：{}", newParentId);
                throw new BusinessException(ResultCode.DIRECTORY_NOT_FOUND);
            }
            
            // 检查是否移动到自己的子目录下（防止循环引用）
            if (isChildDirectory(id, newParentId)) {
                logger.warn("不能移动到自己的子目录下，目录ID：{}，目标父目录ID：{}", id, newParentId);
                throw new BusinessException(ResultCode.SYSTEM_ERROR); // 可以定义一个新的错误码
            }
            
            // 检查新父目录下是否已存在同名目录
            List<Directory> siblings = directoryMapper.selectByParentId(newParentId);
            boolean nameExists = siblings.stream()
                .anyMatch(dir -> !dir.getId().equals(id) && dir.getName().equals(directoryToMove.getName()));
            
            if (nameExists) {
                logger.warn("目标父目录下已存在同名目录，父目录ID：{}，目录名称：{}", 
                    newParentId, directoryToMove.getName());
                throw new BusinessException(ResultCode.DIRECTORY_NAME_DUPLICATE);
            }
            
            // 计算新的路径
            String newPath = generateDirectoryPath(newParentId, directoryToMove.getName());
            String oldPath = directoryToMove.getPath();
            
            // 更新目录信息
            directoryToMove.setParentId(newParentId);
            directoryToMove.setPath(newPath);
            directoryToMove.setUpdatedAt(LocalDateTime.now());
            
            // 执行更新
            int updateResult = directoryMapper.updateById(directoryToMove);
            if (updateResult <= 0) {
                logger.error("移动目录失败，目录ID：{}", id);
                throw new BusinessException(ResultCode.SYSTEM_ERROR);
            }
            
            // 递归更新所有子目录的路径
            updateChildrenPaths(id, oldPath, newPath);
            
            // 返回移动后的目录信息
            DirectoryResponseDTO result = convertToResponseDTO(directoryToMove);
            logger.info("移动目录成功，目录ID：{}，旧路径：{}，新路径：{}", id, oldPath, newPath);
            return result;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("移动目录失败，目录ID：{}", id, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }
    
    /**
     * 检查目标目录是否是当前目录的子目录（防止循环引用）
     * 
     * @param currentDirectoryId 当前目录ID
     * @param targetParentId 目标父目录ID
     * @return 是否是子目录
     */
    private boolean isChildDirectory(Long currentDirectoryId, Long targetParentId) {
        try {
            // 获取目标父目录的所有父级目录，看是否包含当前目录
            Long checkId = targetParentId;
            while (checkId != null && checkId > 0) {
                if (checkId.equals(currentDirectoryId)) {
                    return true; // 找到了循环引用
                }
                
                Directory checkDirectory = directoryMapper.selectById(checkId);
                if (checkDirectory == null || checkDirectory.getDeleted() == 1) {
                    break;
                }
                
                // 如果到达根目录，停止检查
                if (checkDirectory.getParentId() != null && checkDirectory.getParentId() == 0) {
                    break;
                }
                
                checkId = checkDirectory.getParentId();
            }
            
            return false;
        } catch (Exception e) {
            logger.error("检查循环引用失败", e);
            return true; // 出错时保守处理，防止循环引用
        }
    }
    
    /**
     * 递归更新所有子目录的路径
     * 
     * @param parentId 父目录ID
     * @param oldParentPath 旧的父目录路径
     * @param newParentPath 新的父目录路径
     */
    private void updateChildrenPaths(Long parentId, String oldParentPath, String newParentPath) {
        try {
            // 获取所有子目录
            List<Directory> children = directoryMapper.selectByParentId(parentId);
            
            for (Directory child : children) {
                // 计算新的路径：将旧路径前缀替换为新路径前缀
                String oldChildPath = child.getPath();
                String newChildPath;
                
                if (oldChildPath.equals(oldParentPath)) {
                    // 如果路径完全相同，直接使用新路径
                    newChildPath = newParentPath;
                } else if (oldChildPath.startsWith(oldParentPath + "/")) {
                    // 替换路径前缀
                    newChildPath = newParentPath + oldChildPath.substring(oldParentPath.length());
                } else {
                    // 异常情况，重新生成路径
                    newChildPath = generateDirectoryPath(parentId, child.getName());
                }
                
                // 更新子目录路径
                child.setPath(newChildPath);
                child.setUpdatedAt(LocalDateTime.now());
                directoryMapper.updateById(child);
                
                logger.debug("更新子目录路径，目录ID：{}，旧路径：{}，新路径：{}", 
                    child.getId(), oldChildPath, newChildPath);
                
                // 递归更新子目录的子目录
                updateChildrenPaths(child.getId(), oldChildPath, newChildPath);
            }
        } catch (Exception e) {
            logger.error("递归更新子目录路径失败，父目录ID：{}", parentId, e);
            // 这里可以选择抛出异常或者继续处理，根据业务需求决定
        }
    }
    
    /**
     * 查找根目录
     * 
     * @return 根目录信息
     */
    private Directory findRootDirectory() {
        try {
            // 使用 MyBatis-Plus 查询 parent_id = 0 且未删除的目录（根目录）
            QueryWrapper<Directory> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("parent_id", 0)
                       .eq("deleted", 0);
            
            Directory rootDirectory = directoryMapper.selectOne(queryWrapper);
            if (rootDirectory != null) {
                logger.debug("找到根目录，ID：{}，名称：{}", rootDirectory.getId(), rootDirectory.getName());
            } else {
                logger.warn("未找到根目录");
            }
            return rootDirectory;
        } catch (Exception e) {
            logger.error("查找根目录失败", e);
            return null;
        }
    }
    
    /**
     * 生成目录路径
     * 
     * @param parentId 父目录ID
     * @param directoryName 目录名称
     * @return 目录路径
     */
    private String generateDirectoryPath(Long parentId, String directoryName) {
        // 获取父目录信息
        Directory parentDirectory = directoryMapper.selectById(parentId);
        if (parentDirectory != null) {
            if (parentDirectory.getParentId() != null && parentDirectory.getParentId() == 0) {
                // 父目录是根目录，子目录路径就是 /目录名
                return "/" + directoryName;
            } else {
                // 子目录路径 = 父目录路径 + "/" + 当前目录名
                return parentDirectory.getPath() + "/" + directoryName;
            }
        } else {
            // 如果父目录不存在，默认处理
            return "/" + directoryName;
        }
    }
    
    @Override
    public DirectoryResponseDTO convertToResponseDTO(Directory directory) {
        if (directory == null) {
            return null;
        }
        
        DirectoryResponseDTO dto = new DirectoryResponseDTO();
        dto.setId(directory.getId());
        dto.setName(directory.getName());
        dto.setParentId(directory.getParentId());
        dto.setPath(directory.getPath());
        dto.setDescription(directory.getDescription());
        dto.setCreatedAt(directory.getCreatedAt());
        dto.setUpdatedAt(directory.getUpdatedAt());
        
        // 检查是否有子目录（这里简单设置为false，在需要时会被覆盖）
        dto.setHasChildren(false);
        
        return dto;
    }
    
    @Override
    public Long countTotalDirectories() {
        try {
            logger.info("开始统计总目录数量");
            Long count = directoryMapper.selectCount(new QueryWrapper<Directory>().eq("deleted", 0));
            logger.info("统计总目录数量完成，总数：{}", count);
            return count;
        } catch (Exception e) {
            logger.error("统计总目录数量失败", e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }
} 