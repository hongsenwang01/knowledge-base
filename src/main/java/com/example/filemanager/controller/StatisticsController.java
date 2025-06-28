package com.example.filemanager.controller;

import com.example.filemanager.common.result.Result;
import com.example.filemanager.dto.response.StatisticsResponseDTO;
import com.example.filemanager.service.DirectoryService;
import com.example.filemanager.service.FileService;
import com.example.filemanager.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 统计信息控制器
 * 
 * @author system
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/statistics")
@CrossOrigin(origins = {
    "http://localhost:3000",
    "http://127.0.0.1:3000",
    "https://localhost:3000",
    "https://127.0.0.1:3000"
}, methods = {
    RequestMethod.GET,
    RequestMethod.OPTIONS
}, allowCredentials = "true")
public class StatisticsController {
    
    private static final Logger logger = LoggerFactory.getLogger(StatisticsController.class);
    
    @Autowired
    private DirectoryService directoryService;
    
    @Autowired
    private FileService fileService;
    
    /**
     * 获取系统统计信息
     * 
     * @return 统计信息，包括总文件夹数量、总文件数量、文件总大小
     */
    @GetMapping
    public Result<StatisticsResponseDTO> getStatistics() {
        logger.info("接收到获取系统统计信息的请求");
        
        try {
            // 统计总文件夹数量
            Long totalDirectories = directoryService.countTotalDirectories();
            
            // 统计总文件数量
            Long totalFiles = fileService.countTotalFiles();
            
            // 统计文件总大小
            Long totalFileSize = fileService.sumTotalFileSize();
            
            // 格式化文件总大小
            String totalFileSizeFormatted = FileUtil.formatFileSize(totalFileSize);
            
            // 构建响应DTO
            StatisticsResponseDTO statistics = new StatisticsResponseDTO(
                totalDirectories, 
                totalFiles, 
                totalFileSize, 
                totalFileSizeFormatted
            );
            
            logger.info("成功获取系统统计信息 - 总文件夹数: {}, 总文件数: {}, 文件总大小: {} ({})", 
                totalDirectories, totalFiles, totalFileSize, totalFileSizeFormatted);
            
            return Result.success("获取统计信息成功", statistics);
            
        } catch (Exception e) {
            logger.error("获取系统统计信息失败", e);
            return Result.error("获取统计信息失败");
        }
    }
} 