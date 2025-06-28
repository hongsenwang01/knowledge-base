-- 文件管理系统数据库初始化脚本
-- 创建时间: 2024-01-01

-- 创建数据库
CREATE DATABASE IF NOT EXISTS file_manager DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE file_manager;

-- 目录表
CREATE TABLE IF NOT EXISTS directories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '目录ID',
    name VARCHAR(255) NOT NULL COMMENT '目录名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父目录ID，0表示根目录',
    path VARCHAR(1000) NOT NULL COMMENT '目录路径',
    description TEXT COMMENT '目录描述',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标志，0未删除，1已删除',
    INDEX idx_parent_id (parent_id),
    INDEX idx_path (path(255)),
    INDEX idx_deleted (deleted),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='目录表';

-- 文件信息表
CREATE TABLE IF NOT EXISTS file_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '文件ID',
    original_name VARCHAR(255) NOT NULL COMMENT '原始文件名',
    stored_name VARCHAR(255) NOT NULL COMMENT '存储文件名',
    file_path VARCHAR(1000) NOT NULL COMMENT '文件存储路径',
    file_size BIGINT NOT NULL COMMENT '文件大小（字节）',
    file_type VARCHAR(50) COMMENT '文件类型',
    mime_type VARCHAR(100) COMMENT 'MIME类型',
    md5_hash VARCHAR(32) COMMENT '文件MD5哈希值',
    directory_id BIGINT NOT NULL DEFAULT 0 COMMENT '所属目录ID',
    description TEXT COMMENT '文件描述',
    download_count INT DEFAULT 0 COMMENT '下载次数',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标志，0未删除，1已删除',
    INDEX idx_directory_id (directory_id),
    INDEX idx_original_name (original_name),
    INDEX idx_file_type (file_type),
    INDEX idx_md5_hash (md5_hash),
    INDEX idx_deleted (deleted),
    INDEX idx_created_at (created_at),
    UNIQUE KEY uk_stored_name (stored_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件信息表';

-- 文件目录关系表（支持文件在多个目录中）
CREATE TABLE IF NOT EXISTS file_directory_relations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关系ID',
    file_id BIGINT NOT NULL COMMENT '文件ID',
    directory_id BIGINT NOT NULL COMMENT '目录ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标志，0未删除，1已删除',
    INDEX idx_file_id (file_id),
    INDEX idx_directory_id (directory_id),
    INDEX idx_deleted (deleted),
    UNIQUE KEY uk_file_directory (file_id, directory_id, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件目录关系表';

-- 添加外键约束
ALTER TABLE file_directory_relations 
ADD CONSTRAINT fk_file_directory_file 
FOREIGN KEY (file_id) REFERENCES file_info(id) ON DELETE CASCADE;

ALTER TABLE file_directory_relations 
ADD CONSTRAINT fk_file_directory_directory 
FOREIGN KEY (directory_id) REFERENCES directories(id) ON DELETE CASCADE;

-- 插入根目录
INSERT INTO directories (id, name, parent_id, path, description) 
VALUES (1, '根目录', 0, '/', '系统根目录') 
ON DUPLICATE KEY UPDATE name = name;

-- 插入常用目录结构
INSERT INTO directories (name, parent_id, path, description) VALUES
-- 一级目录
('文档资料', 1, '/文档资料', '存放各类文档文件'),
('图片素材', 1, '/图片素材', '存放图片、照片等素材文件'),
('视频文件', 1, '/视频文件', '存放视频、影片等文件'),
('音频文件', 1, '/音频文件', '存放音乐、录音等音频文件'),
('压缩包', 1, '/压缩包', '存放各类压缩文件'),
('程序软件', 1, '/程序软件', '存放应用程序、安装包等'),
('工作文件', 1, '/工作文件', '存放工作相关文件'),
('个人文件', 1, '/个人文件', '存放个人相关文件'),
('临时文件', 1, '/临时文件', '存放临时文件'),
('其他文件', 1, '/其他文件', '存放其他类型文件'),

-- 文档资料子目录
('Word文档', 2, '/文档资料/Word文档', '存放Word文档文件'),
('Excel表格', 2, '/文档资料/Excel表格', '存放Excel表格文件'),
('PPT演示', 2, '/文档资料/PPT演示', '存放PowerPoint演示文件'),
('PDF文件', 2, '/文档资料/PDF文件', '存放PDF格式文件'),
('文本文件', 2, '/文档资料/文本文件', '存放纯文本文件'),
('设计文档', 2, '/文档资料/设计文档', '存放设计类文档'),
('技术文档', 2, '/文档资料/技术文档', '存放技术类文档'),

-- 图片素材子目录
('照片', 3, '/图片素材/照片', '存放生活照片'),
('截图', 3, '/图片素材/截图', '存放屏幕截图'),
('素材图', 3, '/图片素材/素材图', '存放设计素材图片'),
('头像', 3, '/图片素材/头像', '存放头像图片'),
('背景图', 3, '/图片素材/背景图', '存放背景图片'),
('图标', 3, '/图片素材/图标', '存放图标文件'),

-- 视频文件子目录
('教学视频', 4, '/视频文件/教学视频', '存放教学类视频'),
('娱乐视频', 4, '/视频文件/娱乐视频', '存放娱乐类视频'),
('工作视频', 4, '/视频文件/工作视频', '存放工作相关视频'),
('短视频', 4, '/视频文件/短视频', '存放短视频文件'),

-- 音频文件子目录
('音乐', 5, '/音频文件/音乐', '存放音乐文件'),
('录音', 5, '/音频文件/录音', '存放录音文件'),
('有声书', 5, '/音频文件/有声书', '存放有声书文件'),
('播客', 5, '/音频文件/播客', '存放播客文件'),

-- 程序软件子目录
('安装包', 6, '/程序软件/安装包', '存放软件安装包'),
('绿色软件', 6, '/程序软件/绿色软件', '存放免安装软件'),
('手机应用', 6, '/程序软件/手机应用', '存放手机APP文件'),
('插件扩展', 6, '/程序软件/插件扩展', '存放浏览器插件等扩展'),

-- 工作文件子目录
('项目文档', 7, '/工作文件/项目文档', '存放项目相关文档'),
('会议记录', 7, '/工作文件/会议记录', '存放会议记录文件'),
('报告总结', 7, '/工作文件/报告总结', '存放各类报告总结'),
('合同协议', 7, '/工作文件/合同协议', '存放合同协议文件'),
('财务资料', 7, '/工作文件/财务资料', '存放财务相关资料'),

-- 个人文件子目录
('简历', 8, '/个人文件/简历', '存放个人简历'),
('证件照', 8, '/个人文件/证件照', '存放证件照片'),
('个人资料', 8, '/个人文件/个人资料', '存放个人相关资料'),
('学习笔记', 8, '/个人文件/学习笔记', '存放学习笔记'),
('收藏文件', 8, '/个人文件/收藏文件', '存放收藏的文件'); 