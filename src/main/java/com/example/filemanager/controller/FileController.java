package com.example.filemanager.controller;

import com.example.filemanager.common.result.Result;
import com.example.filemanager.dto.response.FileResponseDTO;
import com.example.filemanager.dto.response.PageResponseDTO;
import com.example.filemanager.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 文件管理控制器
 * 
 * @author system
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/files")
@CrossOrigin(origins = {
    "http://localhost:3000",
    "http://127.0.0.1:3000",
    "https://localhost:3000",
    "https://127.0.0.1:3000"
}, methods = {
    RequestMethod.GET,
    RequestMethod.POST,
    RequestMethod.PUT,
    RequestMethod.DELETE,
    RequestMethod.OPTIONS
}, allowCredentials = "true")
public class FileController {
    
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);
    
    @Autowired
    private FileService fileService;
    
    /**
     * 上传文件到指定目录
     * 
     * @param file 上传的文件
     * @param directoryId 目录ID
     * @param description 文件描述（可选）
     * @return 上传后的文件信息
     */
    @PostMapping("/upload")
    public Result<FileResponseDTO> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("directoryId") Long directoryId,
            @RequestParam(value = "description", required = false) String description) {
        logger.info("接收到文件上传请求，文件名：{}，目录ID：{}", file.getOriginalFilename(), directoryId);
        
        try {
            // 验证参数
            if (file.isEmpty()) {
                return Result.error("上传文件不能为空");
            }
            if (directoryId == null) {
                return Result.error("目录ID不能为空");
            }
            
            FileResponseDTO fileResponse = fileService.uploadFile(file, directoryId, description);
            logger.info("文件上传成功，文件ID：{}，文件名：{}", fileResponse.getId(), fileResponse.getOriginalName());
            return Result.success("文件上传成功", fileResponse);
        } catch (Exception e) {
            logger.error("文件上传失败，文件名：{}，目录ID：{}", file.getOriginalFilename(), directoryId, e);
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据目录ID获取文件列表
     * 
     * @param directoryId 目录ID
     * @return 文件列表
     */
    @GetMapping("/directory/{directoryId}")
    public Result<List<FileResponseDTO>> getFilesByDirectoryId(@PathVariable Long directoryId) {
        logger.info("接收到获取目录文件列表请求，目录ID：{}", directoryId);
        
        try {
            List<FileResponseDTO> files = fileService.getFilesByDirectoryId(directoryId);
            logger.info("成功返回{}个文件", files.size());
            return Result.success("获取文件列表成功", files);
        } catch (Exception e) {
            logger.error("获取文件列表失败，目录ID：{}", directoryId, e);
            return Result.error("获取文件列表失败");
        }
    }
    
    /**
     * 分页获取文件列表
     * 
     * @param current 当前页码，默认为1
     * @param size 每页显示条数，默认为10
     * @param directoryId 目录ID（可选，为空时查询所有文件）
     * @return 分页文件列表
     */
    @GetMapping("/page")
    public Result<PageResponseDTO<FileResponseDTO>> getFilesWithPage(
            @RequestParam(value = "current", defaultValue = "1") Long current,
            @RequestParam(value = "size", defaultValue = "10") Long size,
            @RequestParam(value = "directoryId", required = false) Long directoryId) {
        logger.info("接收到分页获取文件列表请求，当前页：{}，每页大小：{}，目录ID：{}", current, size, directoryId);
        
        try {
            // 限制参数范围
            if (size > 100) {
                size = 100L;
            }
            if (current < 1) {
                current = 1L;
            }
            
            PageResponseDTO<FileResponseDTO> pageResult = fileService.getFilesWithPage(current, size, directoryId);
            logger.info("成功返回分页文件数据，当前页：{}，每页大小：{}，总记录数：{}", 
                current, size, pageResult.getTotal());
            return Result.success("获取分页文件列表成功", pageResult);
        } catch (Exception e) {
            logger.error("获取分页文件列表失败，当前页：{}，每页大小：{}，目录ID：{}", current, size, directoryId, e);
            return Result.error("获取分页文件列表失败");
        }
    }
    
    /**
     * 下载文件
     * 
     * @param fileId 文件ID
     * @return 文件内容
     */
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
        logger.info("接收到文件下载请求，文件ID：{}", fileId);
        
        try {
            // 获取文件信息
            FileResponseDTO fileResponse = fileService.getFileById(fileId);
            
            logger.info("文件下载信息 - ID: {}, 原始名: {}, 存储名: {}, 路径: {}", 
                fileId, fileResponse.getOriginalName(), fileResponse.getStoredName(), fileResponse.getFilePath());
            
            // 检查文件是否存在
            File file = new File(fileResponse.getFilePath());
            logger.info("检查文件存在性 - 路径: {}, 存在: {}, 绝对路径: {}", 
                fileResponse.getFilePath(), file.exists(), file.getAbsolutePath());
            
            if (!file.exists()) {
                logger.error("文件不存在，文件ID：{}，路径：{}", fileId, fileResponse.getFilePath());
                
                // 尝试列出目录内容，帮助调试
                File parentDir = file.getParentFile();
                if (parentDir != null && parentDir.exists()) {
                    logger.info("父目录存在，列出目录内容：{}", java.util.Arrays.toString(parentDir.list()));
                } else {
                    logger.error("父目录不存在：{}", parentDir != null ? parentDir.getAbsolutePath() : "null");
                }
                
                return ResponseEntity.notFound().build();
            }
            
            // 更新下载次数
            fileService.updateDownloadCount(fileId);
            
            // 创建文件资源，使用InputStreamResource避免中文路径编码问题
            FileInputStream fileInputStream = new FileInputStream(file);
            Resource resource = new InputStreamResource(fileInputStream);
            
            // 设置文件名，处理中文文件名编码问题
            String encodedFilename = URLEncoder.encode(fileResponse.getOriginalName(), StandardCharsets.UTF_8.toString())
                    .replaceAll("\\+", "%20");
            
            // 构建响应头，避免中文字符编码问题
            HttpHeaders headers = new HttpHeaders();
            
            // 只使用 filename*=UTF-8'' 格式，防止Tomcat编码问题
            // 不设置 filename= 参数，避免Unicode字符编码错误
            headers.add(HttpHeaders.CONTENT_DISPOSITION, 
                "attachment; filename*=UTF-8''" + encodedFilename);
            
            // 设置Content-Type，特别处理文本文件的编码
            String contentType = fileResponse.getMimeType();
            if (contentType != null && contentType.startsWith("text/")) {
                contentType = contentType + "; charset=UTF-8";
            }
            
            // 添加其他响应头
            headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()));
            headers.add(HttpHeaders.CACHE_CONTROL, "no-cache");
            headers.add("X-Content-Type-Options", "nosniff");
            
            logger.info("文件下载成功，文件ID：{}，文件名：{}", fileId, fileResponse.getOriginalName());
            
            // 构建响应，避免重复设置Content-Type
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.parseMediaType(contentType != null ? contentType : fileResponse.getMimeType()))
                    .body(resource);
                    
        } catch (Exception e) {
            logger.error("文件下载失败，文件ID：{}", fileId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 根据文件ID获取文件详情
     * 
     * @param fileId 文件ID
     * @return 文件详情
     */
    @GetMapping("/{fileId}")
    public Result<FileResponseDTO> getFileById(@PathVariable Long fileId) {
        logger.info("接收到获取文件详情请求，文件ID：{}", fileId);
        
        try {
            FileResponseDTO fileResponse = fileService.getFileById(fileId);
            logger.info("成功返回文件详情，文件名：{}", fileResponse.getOriginalName());
            return Result.success("获取文件详情成功", fileResponse);
        } catch (Exception e) {
            logger.error("获取文件详情失败，文件ID：{}", fileId, e);
            return Result.error("获取文件详情失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除文件
     * 
     * @param fileId 文件ID
     * @return 删除结果
     */
    @DeleteMapping("/{fileId}")
    public Result<Boolean> deleteFile(@PathVariable Long fileId) {
        logger.info("接收到删除文件请求，文件ID：{}", fileId);
        
        try {
            boolean success = fileService.deleteFile(fileId);
            if (success) {
                logger.info("文件删除成功，文件ID：{}", fileId);
                return Result.success("文件删除成功", true);
            } else {
                logger.warn("文件删除失败，文件ID：{}", fileId);
                return Result.error("文件删除失败");
            }
        } catch (Exception e) {
            logger.error("文件删除失败，文件ID：{}", fileId, e);
            return Result.error("文件删除失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新文件下载次数
     * 
     * @param fileId 文件ID
     * @return 更新结果
     */
    @PutMapping("/{fileId}/download")
    public Result<Boolean> updateDownloadCount(@PathVariable Long fileId) {
        logger.info("接收到更新下载次数请求，文件ID：{}", fileId);
        
        try {
            boolean success = fileService.updateDownloadCount(fileId);
            if (success) {
                logger.info("下载次数更新成功，文件ID：{}", fileId);
                return Result.success("下载次数更新成功", true);
            } else {
                logger.warn("下载次数更新失败，文件ID：{}", fileId);
                return Result.error("下载次数更新失败");
            }
        } catch (Exception e) {
            logger.error("下载次数更新失败，文件ID：{}", fileId, e);
            return Result.error("下载次数更新失败：" + e.getMessage());
        }
    }
} 