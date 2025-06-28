package com.example.filemanager;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 文件管理系统启动类
 * 
 * @author system
 * @since 2024-01-01
 */
@SpringBootApplication
@MapperScan("com.example.filemanager.mapper")
public class FileManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileManagerApplication.class, args);
    }

} 