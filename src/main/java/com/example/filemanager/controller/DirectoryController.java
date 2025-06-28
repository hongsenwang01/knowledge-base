package com.example.filemanager.controller;

import com.example.filemanager.common.result.Result;
import com.example.filemanager.dto.request.DirectoryCreateDTO;
import com.example.filemanager.dto.request.DirectoryMoveDTO;
import com.example.filemanager.dto.request.DirectoryUpdateDTO;
import com.example.filemanager.dto.response.DeleteResultDTO;
import com.example.filemanager.dto.response.DirectoryResponseDTO;
import com.example.filemanager.dto.response.PageResponseDTO;
import com.example.filemanager.service.DirectoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 目录控制器
 * 
 * @author system
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/directories")
@CrossOrigin(origins = {
    "http://localhost:3000",
    "http://127.0.0.1:3000",
    "https://localhost:3000",
    "https://127.0.0.1:3000"
}, methods = {
    RequestMethod.GET,
    RequestMethod.POST,
    RequestMethod.PUT,
    RequestMethod.PATCH,
    RequestMethod.DELETE,
    RequestMethod.OPTIONS
}, allowCredentials = "true")
public class DirectoryController {
    
    private static final Logger logger = LoggerFactory.getLogger(DirectoryController.class);
    
    @Autowired
    private DirectoryService directoryService;
    
    /**
     * 获取所有目录列表
     * 
     * @return 目录列表
     */
    @GetMapping
    public Result<List<DirectoryResponseDTO>> getAllDirectories() {
        logger.info("接收到获取所有目录的请求");
        try {
            List<DirectoryResponseDTO> directories = directoryService.getAllDirectories();
            logger.info("成功返回{}个目录", directories.size());
            return Result.success("获取目录列表成功", directories);
        } catch (Exception e) {
            logger.error("获取目录列表失败", e);
            return Result.error("获取目录列表失败");
        }
    }
    
    /**
     * 分页获取目录列表
     * 
     * @param current 当前页码，默认为1
     * @param size 每页显示条数，默认为10
     * @return 分页目录列表
     */
    @GetMapping("/page")
    public Result<PageResponseDTO<DirectoryResponseDTO>> getAllDirectoriesWithPage(
            @RequestParam(value = "current", defaultValue = "1") Long current,
            @RequestParam(value = "size", defaultValue = "10") Long size) {
        logger.info("接收到分页获取目录的请求，当前页：{}，每页大小：{}", current, size);
        try {
            // 限制每页最大数量
            if (size > 100) {
                size = 100L;
            }
            if (current < 1) {
                current = 1L;
            }
            
            PageResponseDTO<DirectoryResponseDTO> pageResult = directoryService.getAllDirectoriesWithPage(current, size);
            logger.info("成功返回分页目录数据，当前页：{}，每页大小：{}，总记录数：{}", 
                current, size, pageResult.getTotal());
            return Result.success("获取分页目录列表成功", pageResult);
        } catch (Exception e) {
            logger.error("获取分页目录列表失败，当前页：{}，每页大小：{}", current, size, e);
            return Result.error("获取分页目录列表失败");
        }
    }
    
    /**
     * 获取目录树结构
     * 
     * @return 目录树
     */
    @GetMapping("/tree")
    public Result<List<DirectoryResponseDTO>> getDirectoryTree() {
        logger.info("接收到获取目录树的请求");
        try {
            List<DirectoryResponseDTO> directoryTree = directoryService.getDirectoryTree();
            logger.info("成功返回目录树结构");
            return Result.success("获取目录树成功", directoryTree);
        } catch (Exception e) {
            logger.error("获取目录树失败", e);
            return Result.error("获取目录树失败");
        }
    }
    
    /**
     * 根据父目录ID获取子目录
     * 
     * @param parentId 父目录ID
     * @return 子目录列表
     */
    @GetMapping("/parent/{parentId}")
    public Result<List<DirectoryResponseDTO>> getDirectoriesByParentId(@PathVariable Long parentId) {
        logger.info("接收到获取子目录的请求，父目录ID：{}", parentId);
        try {
            List<DirectoryResponseDTO> directories = directoryService.getDirectoriesByParentId(parentId);
            logger.info("成功返回{}个子目录", directories.size());
            return Result.success("获取子目录成功", directories);
        } catch (Exception e) {
            logger.error("获取子目录失败，父目录ID：{}", parentId, e);
            return Result.error("获取子目录失败");
        }
    }
    
    /**
     * 根据ID获取目录详情
     * 
     * @param id 目录ID
     * @return 目录详情
     */
    @GetMapping("/{id}")
    public Result<DirectoryResponseDTO> getDirectoryById(@PathVariable Long id) {
        logger.info("接收到获取目录详情的请求，目录ID：{}", id);
        try {
            DirectoryResponseDTO directory = directoryService.getDirectoryById(id);
            logger.info("成功返回目录详情，目录名称：{}", directory.getName());
            return Result.success("获取目录详情成功", directory);
        } catch (Exception e) {
            logger.error("获取目录详情失败，目录ID：{}", id, e);
            return Result.error("获取目录详情失败");
        }
    }
    
    /**
     * 更新目录信息
     * 
     * @param id 目录ID
     * @param updateDTO 更新请求数据
     * @return 更新后的目录信息
     */
    @PutMapping("/{id}")
    public Result<DirectoryResponseDTO> updateDirectory(
            @PathVariable Long id, 
            @Valid @RequestBody DirectoryUpdateDTO updateDTO) {
        logger.info("接收到更新目录的请求，目录ID：{}，更新数据：{}", id, updateDTO);
        try {
            DirectoryResponseDTO updatedDirectory = directoryService.updateDirectory(id, updateDTO);
            logger.info("成功更新目录，目录ID：{}，新名称：{}", id, updatedDirectory.getName());
            return Result.success("更新目录成功", updatedDirectory);
        } catch (Exception e) {
            logger.error("更新目录失败，目录ID：{}", id, e);
            return Result.error("更新目录失败");
        }
    }
    
    /**
     * 删除目录（软删除）
     * 
     * @param id 目录ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<DeleteResultDTO> deleteDirectory(@PathVariable Long id) {
        logger.info("接收到删除目录的请求，目录ID：{}", id);
        try {
            DeleteResultDTO deleteResult = directoryService.deleteDirectory(id);
            if (deleteResult.isSuccess()) {
                logger.info("成功删除目录，目录ID：{}", id);
                return Result.success("删除目录成功", deleteResult);
            } else {
                logger.warn("删除目录失败，目录ID：{}，原因：{}", id, deleteResult.getMessage());
                return Result.error(deleteResult.getErrorCode(), deleteResult.getMessage());
            }
        } catch (Exception e) {
            logger.error("删除目录失败，目录ID：{}", id, e);
            return Result.error("删除目录失败");
        }
    }
    
    /**
     * 创建新目录
     * 
     * @param createDTO 创建请求数据
     * @return 创建后的目录信息
     */
    @PostMapping
    public Result<DirectoryResponseDTO> createDirectory(@Valid @RequestBody DirectoryCreateDTO createDTO) {
        logger.info("接收到创建目录的请求，请求数据：{}", createDTO);
        try {
            DirectoryResponseDTO newDirectory = directoryService.createDirectory(createDTO);
            logger.info("成功创建目录，目录ID：{}，目录名称：{}", newDirectory.getId(), newDirectory.getName());
            return Result.success("创建目录成功", newDirectory);
        } catch (Exception e) {
            logger.error("创建目录失败，请求数据：{}", createDTO, e);
            return Result.error("创建目录失败");
        }
    }
    
    /**
     * 移动目录到新的父目录下
     * 
     * @param id 要移动的目录ID
     * @param moveDTO 移动请求数据
     * @return 移动后的目录信息
     */
    @PatchMapping("/{id}/move")
    public Result<DirectoryResponseDTO> moveDirectory(
            @PathVariable Long id, 
            @Valid @RequestBody DirectoryMoveDTO moveDTO) {
        logger.info("接收到移动目录的请求，目录ID：{}，移动数据：{}", id, moveDTO);
        try {
            DirectoryResponseDTO movedDirectory = directoryService.moveDirectory(id, moveDTO);
            logger.info("成功移动目录，目录ID：{}，新父目录ID：{}", id, moveDTO.getNewParentId());
            return Result.success("移动目录成功", movedDirectory);
        } catch (Exception e) {
            logger.error("移动目录失败，目录ID：{}", id, e);
            return Result.error("移动目录失败");
        }
    }
} 