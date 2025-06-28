package com.example.filemanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.filemanager.config.FileStorageConfig;
import com.example.filemanager.dto.response.FileResponseDTO;
import com.example.filemanager.dto.response.PageResponseDTO;
import com.example.filemanager.entity.Directory;
import com.example.filemanager.entity.FileInfo;
import com.example.filemanager.mapper.DirectoryMapper;
import com.example.filemanager.mapper.FileInfoMapper;
import com.example.filemanager.service.FileService;
import com.example.filemanager.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件服务实现类
 * 
 * @author system
 * @since 2024-01-01
 */
@Service
public class FileServiceImpl implements FileService {
    
    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
    
    @Autowired
    private FileInfoMapper fileInfoMapper;
    
    @Autowired
    private DirectoryMapper directoryMapper;
    
    @Autowired
    private FileStorageConfig fileStorageConfig;
    
    @Override
    @Transactional
    public FileResponseDTO uploadFile(MultipartFile file, Long directoryId, String description) {
        logger.info("开始上传文件，文件名：{}，目录ID：{}", file.getOriginalFilename(), directoryId);
        
        // 验证文件
        if (file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        }
        
        // 查询目录信息
        Directory directory = directoryMapper.selectById(directoryId);
        if (directory == null) {
            throw new RuntimeException("目录不存在，ID：" + directoryId);
        }
        
        // 检查文件大小
        if (file.getSize() > fileStorageConfig.getMaxFileSize()) {
            throw new RuntimeException("文件大小超过限制，最大允许：" + 
                FileUtil.formatFileSize(fileStorageConfig.getMaxFileSize()));
        }
        
        // 计算文件MD5
        String md5Hash = FileUtil.calculateMD5(file);
        if (md5Hash == null) {
            throw new RuntimeException("计算文件MD5失败");
        }
        
        // 检查是否已存在相同MD5的文件（秒传功能）
        FileInfo existingFile = fileInfoMapper.selectByMd5Hash(md5Hash);
        if (existingFile != null) {
            logger.info("发现相同MD5的文件，执行秒传，文件ID：{}", existingFile.getId());
            // 创建新的文件记录，指向相同的物理文件
            FileInfo newFileInfo = createFileInfoFromExisting(existingFile, file, directoryId, description);
            fileInfoMapper.insert(newFileInfo);
            return convertToResponseDTO(newFileInfo);
        }
        
        // 生成存储文件名
        String originalFilename = file.getOriginalFilename();
        String storedFilename = FileUtil.generateStoredFilename(originalFilename);
        String extension = FileUtil.getFileExtension(originalFilename);
        String fileType = FileUtil.getFileType(extension);
        
        // 构建存储路径
        String directoryPath = directory.getPath();
        // 确保路径分隔符正确，处理Windows和Linux的差异
        String localDirectoryPath = fileStorageConfig.getUploadPath() + directoryPath.replace("/", File.separator);
        String fullFilePath = localDirectoryPath + File.separator + storedFilename;
        
        logger.debug("构建存储路径 - 目录路径: {}, 本地目录: {}, 完整路径: {}", 
            directoryPath, localDirectoryPath, fullFilePath);
        
        // 创建本地目录结构
        if (!FileUtil.createDirectoryIfNotExists(localDirectoryPath)) {
            throw new RuntimeException("创建本地目录失败：" + localDirectoryPath);
        }
        
        // 保存文件
        if (!FileUtil.saveUploadedFile(file, fullFilePath)) {
            throw new RuntimeException("保存文件失败：" + fullFilePath);
        }
        
        // 创建文件信息记录
        FileInfo fileInfo = new FileInfo();
        fileInfo.setOriginalName(originalFilename);
        fileInfo.setStoredName(storedFilename);
        fileInfo.setFilePath(fullFilePath);
        fileInfo.setFileSize(file.getSize());
        fileInfo.setFileType(fileType);
        fileInfo.setMimeType(file.getContentType());
        fileInfo.setMd5Hash(md5Hash);
        fileInfo.setDirectoryId(directoryId);
        fileInfo.setDescription(description);
        fileInfo.setDownloadCount(0);
        fileInfo.setCreatedAt(LocalDateTime.now());
        fileInfo.setUpdatedAt(LocalDateTime.now());
        fileInfo.setDeleted(0);
        
        // 保存到数据库
        fileInfoMapper.insert(fileInfo);
        
        logger.info("文件上传成功，文件ID：{}，存储路径：{}", fileInfo.getId(), fullFilePath);
        return convertToResponseDTO(fileInfo);
    }
    
    /**
     * 基于已存在的文件创建新的文件信息记录（用于秒传）
     */
    private FileInfo createFileInfoFromExisting(FileInfo existingFile, MultipartFile file, 
                                               Long directoryId, String description) {
        FileInfo newFileInfo = new FileInfo();
        newFileInfo.setOriginalName(file.getOriginalFilename());
        newFileInfo.setStoredName(existingFile.getStoredName());
        newFileInfo.setFilePath(existingFile.getFilePath());
        newFileInfo.setFileSize(file.getSize());
        newFileInfo.setFileType(existingFile.getFileType());
        newFileInfo.setMimeType(file.getContentType());
        newFileInfo.setMd5Hash(existingFile.getMd5Hash());
        newFileInfo.setDirectoryId(directoryId);
        newFileInfo.setDescription(description);
        newFileInfo.setDownloadCount(0);
        newFileInfo.setCreatedAt(LocalDateTime.now());
        newFileInfo.setUpdatedAt(LocalDateTime.now());
        newFileInfo.setDeleted(0);
        return newFileInfo;
    }
    
    @Override
    public List<FileResponseDTO> getFilesByDirectoryId(Long directoryId) {
        logger.info("查询目录下的文件列表，目录ID：{}", directoryId);
        
        List<FileInfo> fileInfos = fileInfoMapper.selectByDirectoryId(directoryId);
        return fileInfos.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public PageResponseDTO<FileResponseDTO> getFilesWithPage(Long current, Long size, Long directoryId) {
        logger.info("分页查询文件列表，当前页：{}，每页大小：{}，目录ID：{}", current, size, directoryId);
        
        Page<FileInfo> page = new Page<>(current, size);
        Page<FileInfo> result = fileInfoMapper.selectFilesWithPage(page, directoryId);
        
        List<FileResponseDTO> responseList = result.getRecords().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        
        PageResponseDTO<FileResponseDTO> pageResponse = new PageResponseDTO<>();
        pageResponse.setRecords(responseList);
        pageResponse.setTotal(result.getTotal());
        pageResponse.setSize(result.getSize());
        pageResponse.setCurrent(result.getCurrent());
        pageResponse.setPages(result.getPages());
        
        return pageResponse;
    }
    
    @Override
    public FileResponseDTO getFileById(Long fileId) {
        logger.info("查询文件详情，文件ID：{}", fileId);
        
        FileInfo fileInfo = fileInfoMapper.selectById(fileId);
        if (fileInfo == null) {
            throw new RuntimeException("文件不存在，ID：" + fileId);
        }
        
        return convertToResponseDTO(fileInfo);
    }
    
    @Override
    @Transactional
    public boolean deleteFile(Long fileId) {
        logger.info("删除文件，文件ID：{}", fileId);
        
        FileInfo fileInfo = fileInfoMapper.selectById(fileId);
        if (fileInfo == null) {
            logger.warn("文件不存在，ID：{}", fileId);
            return false;
        }
        
        // 软删除文件记录
        int result = fileInfoMapper.deleteById(fileId);
        
        // 检查是否还有其他记录指向同一个物理文件
        LambdaQueryWrapper<FileInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FileInfo::getMd5Hash, fileInfo.getMd5Hash())
               .ne(FileInfo::getId, fileId);
        long count = fileInfoMapper.selectCount(wrapper);
        
        // 如果没有其他记录指向该物理文件，则删除物理文件
        if (count == 0) {
            FileUtil.deleteFile(fileInfo.getFilePath());
            logger.info("删除物理文件：{}", fileInfo.getFilePath());
        } else {
            logger.info("保留物理文件，还有{}个记录指向该文件", count);
        }
        
        return result > 0;
    }
    
    @Override
    public boolean updateDownloadCount(Long fileId) {
        logger.info("更新文件下载次数，文件ID：{}", fileId);
        
        int result = fileInfoMapper.updateDownloadCount(fileId);
        return result > 0;
    }
    
    @Override
    public String getFullFilePath(Long fileId) {
        logger.debug("获取文件完整路径，文件ID：{}", fileId);
        
        FileInfo fileInfo = fileInfoMapper.selectById(fileId);
        if (fileInfo == null) {
            throw new RuntimeException("文件不存在，ID：" + fileId);
        }
        
        return fileInfo.getFilePath();
    }
    
    @Override
    public FileResponseDTO convertToResponseDTO(FileInfo fileInfo) {
        if (fileInfo == null) {
            return null;
        }
        
        FileResponseDTO responseDTO = new FileResponseDTO();
        responseDTO.setId(fileInfo.getId());
        responseDTO.setOriginalName(fileInfo.getOriginalName());
        responseDTO.setStoredName(fileInfo.getStoredName());
        responseDTO.setFilePath(fileInfo.getFilePath());
        responseDTO.setFileSize(fileInfo.getFileSize());
        responseDTO.setFileType(fileInfo.getFileType());
        responseDTO.setMimeType(fileInfo.getMimeType());
        responseDTO.setMd5Hash(fileInfo.getMd5Hash());
        responseDTO.setDirectoryId(fileInfo.getDirectoryId());
        responseDTO.setDescription(fileInfo.getDescription());
        responseDTO.setDownloadCount(fileInfo.getDownloadCount());
        responseDTO.setFileSizeFormatted(FileUtil.formatFileSize(fileInfo.getFileSize()));
        responseDTO.setCreatedAt(fileInfo.getCreatedAt());
        responseDTO.setUpdatedAt(fileInfo.getUpdatedAt());
        
        // 查询目录名称
        if (fileInfo.getDirectoryId() != null) {
            Directory directory = directoryMapper.selectById(fileInfo.getDirectoryId());
            if (directory != null) {
                responseDTO.setDirectoryName(directory.getName());
            }
        }
        
        return responseDTO;
    }
    
    @Override
    public Long countTotalFiles() {
        try {
            logger.info("开始统计总文件数量");
            Long count = fileInfoMapper.selectCount(new LambdaQueryWrapper<FileInfo>()
                    .eq(FileInfo::getDeleted, 0));
            logger.info("统计总文件数量完成，总数：{}", count);
            return count;
        } catch (Exception e) {
            logger.error("统计总文件数量失败", e);
            throw new RuntimeException("统计总文件数量失败", e);
        }
    }
    
    @Override
    public Long sumTotalFileSize() {
        try {
            logger.info("开始统计文件总大小");
            Long totalSize = fileInfoMapper.sumTotalFileSize();
            if (totalSize == null) {
                totalSize = 0L;
            }
            logger.info("统计文件总大小完成，总大小：{} 字节", totalSize);
            return totalSize;
        } catch (Exception e) {
            logger.error("统计文件总大小失败", e);
            throw new RuntimeException("统计文件总大小失败", e);
        }
    }
} 