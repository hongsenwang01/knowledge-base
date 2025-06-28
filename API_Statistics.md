# 文件管理系统 - 统计接口 API 文档

## 概述

统计接口提供系统的统计信息，包括总文件夹数量、总文件数量、文件总大小等数据。

## 接口列表

### 1. 获取系统统计信息

**接口地址：** `GET /statistics`

**请求参数：** 无

**响应格式：**
```json
{
    "code": 200,
    "message": "获取统计信息成功",
    "data": {
        "totalDirectories": 5,
        "totalFiles": 23,
        "totalFileSize": 1048576,
        "totalFileSizeFormatted": "1.00 MB"
    }
}
```

**响应字段说明：**
- `totalDirectories`: 总文件夹数量
- `totalFiles`: 总文件数量  
- `totalFileSize`: 文件总大小（字节）
- `totalFileSizeFormatted`: 文件总大小（格式化显示）

## 前端调用示例

### JavaScript/Ajax 调用
```javascript
// 获取统计信息
function getStatistics() {
    return fetch('/statistics', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.code === 200) {
            console.log('统计信息:', data.data);
            return data.data;
        } else {
            throw new Error(data.message || '获取统计信息失败');
        }
    })
    .catch(error => {
        console.error('获取统计信息失败:', error);
        throw error;
    });
}

// 使用示例
getStatistics().then(stats => {
    console.log(`总文件夹: ${stats.totalDirectories}`);
    console.log(`总文件: ${stats.totalFiles}`);  
    console.log(`文件总大小: ${stats.totalFileSizeFormatted}`);
});
```

### Vue.js 组件示例
```vue
<template>
    <div class="statistics-panel">
        <h3>系统统计</h3>
        <div class="stats-grid">
            <div class="stat-item">
                <div class="stat-label">总文件夹数</div>
                <div class="stat-value">{{ statistics.totalDirectories }}</div>
            </div>
            <div class="stat-item">
                <div class="stat-label">总文件数</div>
                <div class="stat-value">{{ statistics.totalFiles }}</div>
            </div>
            <div class="stat-item">
                <div class="stat-label">文件总大小</div>
                <div class="stat-value">{{ statistics.totalFileSizeFormatted }}</div>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: 'StatisticsPanel',
    data() {
        return {
            statistics: {
                totalDirectories: 0,
                totalFiles: 0,
                totalFileSize: 0,
                totalFileSizeFormatted: '0 B'
            }
        }
    },
    mounted() {
        this.loadStatistics();
    },
    methods: {
        async loadStatistics() {
            try {
                const response = await fetch('/statistics');
                const result = await response.json();
                if (result.code === 200) {
                    this.statistics = result.data;
                } else {
                    console.error('获取统计信息失败:', result.message);
                }
            } catch (error) {
                console.error('请求失败:', error);
            }
        }
    }
}
</script>

<style scoped>
.statistics-panel {
    padding: 20px;
    background: #f5f5f5;
    border-radius: 8px;
    margin: 20px 0;
}

.stats-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 15px;
    margin-top: 15px;
}

.stat-item {
    background: white;
    padding: 15px;
    border-radius: 6px;
    text-align: center;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.stat-label {
    font-size: 14px;
    color: #666;
    margin-bottom: 8px;
}

.stat-value {
    font-size: 24px;
    font-weight: bold;
    color: #333;
}

.stat-subtitle {
    font-size: 14px;
    opacity: 0.8;
}
</style>
```

## 完整测试页面

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>文件管理系统 - 统计信息</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
            background-color: #f0f2f5;
        }
        
        .header {
            text-align: center;
            margin-bottom: 30px;
            color: #333;
        }
        
        .statistics-container {
            background: white;
            border-radius: 10px;
            padding: 30px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            margin-bottom: 30px;
        }
        
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin: 20px 0;
        }
        
        .stat-card {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 25px;
            border-radius: 10px;
            text-align: center;
            transition: transform 0.3s ease;
        }
        
        .stat-card:hover {
            transform: translateY(-5px);
        }
        
        .stat-card.directories {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        }
        
        .stat-card.files {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
        }
        
        .stat-card.size {
            background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
        }
        
        .stat-title {
            font-size: 16px;
            margin-bottom: 10px;
            opacity: 0.9;
        }
        
        .stat-value {
            font-size: 36px;
            font-weight: bold;
            margin-bottom: 5px;
        }
        
        .controls {
            text-align: center;
            margin: 20px 0;
        }
        
        .btn {
            background: #4CAF50;
            color: white;
            border: none;
            padding: 12px 24px;
            border-radius: 6px;
            cursor: pointer;
            font-size: 16px;
            margin: 0 10px;
            transition: background-color 0.3s;
        }
        
        .btn:hover {
            background: #45a049;
        }
        
        .btn.refresh {
            background: #2196F3;
        }
        
        .btn.refresh:hover {
            background: #1976D2;
        }
        
        .loading {
            text-align: center;
            color: #666;
            font-style: italic;
        }
        
        .error {
            color: #f44336;
            text-align: center;
            background: #ffebee;
            padding: 15px;
            border-radius: 6px;
            margin: 20px 0;
        }
        
        .last-updated {
            text-align: center;
            color: #666;
            font-size: 14px;
            margin-top: 20px;
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>📊 文件管理系统统计</h1>
        <p>实时查看系统的文件和目录统计信息</p>
    </div>
    
    <div class="statistics-container">
        <div class="controls">
            <button class="btn refresh" onclick="loadStatistics()">🔄 刷新统计</button>
        </div>
        
        <div id="loading" class="loading" style="display: none;">
            正在加载统计信息...
        </div>
        
        <div id="error" class="error" style="display: none;"></div>
        
        <div id="statsGrid" class="stats-grid">
            <div class="stat-card directories">
                <div class="stat-title">📁 总文件夹数</div>
                <div class="stat-value" id="totalDirectories">-</div>
                <div class="stat-subtitle">个文件夹</div>
            </div>
            
            <div class="stat-card files">
                <div class="stat-title">📄 总文件数</div>
                <div class="stat-value" id="totalFiles">-</div>
                <div class="stat-subtitle">个文件</div>
            </div>
            
            <div class="stat-card size">
                <div class="stat-title">💾 文件总大小</div>
                <div class="stat-value" id="totalFileSize">-</div>
                <div class="stat-subtitle" id="totalFileSizeBytes">0 字节</div>
            </div>
        </div>
        
        <div id="lastUpdated" class="last-updated"></div>
    </div>

    <script>
        // 页面加载时自动获取统计信息
        document.addEventListener('DOMContentLoaded', function() {
            loadStatistics();
        });

        // 加载统计信息
        async function loadStatistics() {
            const loadingEl = document.getElementById('loading');
            const errorEl = document.getElementById('error');
            const statsGridEl = document.getElementById('statsGrid');
            
            try {
                // 显示加载状态
                loadingEl.style.display = 'block';
                errorEl.style.display = 'none';
                statsGridEl.style.opacity = '0.5';
                
                const response = await fetch('/statistics', {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                });
                
                const result = await response.json();
                
                if (result.code === 200) {
                    displayStatistics(result.data);
                    updateLastUpdatedTime();
                } else {
                    throw new Error(result.message || '获取统计信息失败');
                }
                
            } catch (error) {
                console.error('获取统计信息失败:', error);
                errorEl.textContent = '获取统计信息失败: ' + error.message;
                errorEl.style.display = 'block';
            } finally {
                loadingEl.style.display = 'none';
                statsGridEl.style.opacity = '1';
            }
        }

        // 显示统计信息
        function displayStatistics(stats) {
            document.getElementById('totalDirectories').textContent = stats.totalDirectories || 0;
            document.getElementById('totalFiles').textContent = stats.totalFiles || 0;
            document.getElementById('totalFileSize').textContent = stats.totalFileSizeFormatted || '0 B';
            document.getElementById('totalFileSizeBytes').textContent = `${stats.totalFileSize || 0} 字节`;
        }

        // 更新最后更新时间
        function updateLastUpdatedTime() {
            const now = new Date();
            const timeString = now.toLocaleString('zh-CN');
            document.getElementById('lastUpdated').textContent = `最后更新: ${timeString}`;
        }

        // 格式化数字（添加千分位分隔符）
        function formatNumber(num) {
            return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
        }
    </script>
</body>
</html>
```

## 错误处理

### 常见错误响应

1. **系统错误**
```json
{
    "code": 500,
    "message": "获取统计信息失败",
    "data": null
}
```

### 错误处理建议

1. **网络错误处理**：添加重试机制
2. **数据验证**：检查返回的数据格式
3. **用户友好提示**：显示具体的错误信息
4. **降级处理**：如果无法获取统计信息，显示默认值

## 性能优化建议

1. **缓存策略**：统计数据可以考虑短期缓存（1-5分钟）
2. **异步加载**：统计信息可以在页面其他内容加载完成后再加载
3. **定时刷新**：可以设置定时器自动刷新统计信息
4. **分页统计**：如果数据量大，考虑分页统计或采样统计

## 使用场景

1. **系统概览**：在仪表板页面展示系统概况
2. **存储管理**：监控存储空间使用情况
3. **用户报告**：生成系统使用报告
4. **容量规划**：为系统扩容提供数据支持 