# 文件上传功能API文档

## 功能概述

文件管理系统提供了完整的文件上传、管理功能，支持：
- 文件上传到指定目录
- 自动创建本地目录结构
- 文件MD5去重（秒传）
- 文件查询、删除
- 下载计数等功能

## 配置说明

### 应用配置（application.yml）

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 100MB        # 单个文件最大大小
      max-request-size: 100MB     # 单次请求最大大小
      enabled: true               # 启用文件上传

file:
  upload:
    path: ${user.home}/文档管理    # 文件上传根目录
    max-size: 104857600          # 文件大小限制（字节）
    allowed-types: "*"           # 允许的文件类型
```

### 本地存储结构

文件将按照数据库中的目录结构在本地创建对应的目录，例如：
```
/home/user/文档管理/
├── 文档资料/
│   ├── Word文档/
│   └── PDF文件/
├── 图片素材/
│   ├── 照片/
│   └── 截图/
└── 其他文件/
```

## API接口

## API接口总览

1. `POST /api/v1/files/upload` - 文件上传
2. `GET /api/v1/files/directory/{directoryId}` - 获取目录下的文件
3. `GET /api/v1/files/page` - 分页获取文件列表  
4. `GET /api/v1/files/{fileId}` - 获取文件详情
5. `GET /api/v1/files/download/{fileId}` - 下载文件（自动更新下载次数）
6. `DELETE /api/v1/files/{fileId}` - 删除文件
7. `PUT /api/v1/files/{fileId}/download` - 仅更新下载次数

### 1. 文件上传

**接口**: `POST /api/v1/files/upload`

**参数**:
- `file` (FormData): 上传的文件（必需）
- `directoryId` (Long): 目录ID（必需）
- `description` (String): 文件描述（可选）

**前端示例**:

```html
<!-- HTML表单 -->
<form id="uploadForm" enctype="multipart/form-data">
    <input type="file" id="fileInput" name="file" required>
    <input type="number" id="directoryId" name="directoryId" placeholder="目录ID" required>
    <input type="text" id="description" name="description" placeholder="文件描述（可选）">
    <button type="submit">上传文件</button>
</form>
```

```javascript
// JavaScript上传示例
document.getElementById('uploadForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const formData = new FormData();
    const fileInput = document.getElementById('fileInput');
    const directoryId = document.getElementById('directoryId').value;
    const description = document.getElementById('description').value;
    
    formData.append('file', fileInput.files[0]);
    formData.append('directoryId', directoryId);
    if (description) {
        formData.append('description', description);
    }
    
    try {
        const response = await fetch('/api/v1/files/upload', {
            method: 'POST',
            body: formData
        });
        
        const result = await response.json();
        if (result.success) {
            console.log('文件上传成功:', result.data);
            alert('文件上传成功！');
        } else {
            console.error('上传失败:', result.message);
            alert('上传失败: ' + result.message);
        }
    } catch (error) {
        console.error('上传出错:', error);
        alert('上传出错: ' + error.message);
    }
});
```

**响应示例**:
```json
{
    "success": true,
    "message": "文件上传成功",
    "data": {
        "id": 1,
        "originalName": "example.pdf",
        "storedName": "20241201143022_a1b2c3d4.pdf",
        "fileSize": 1048576,
        "fileType": "文档",
        "mimeType": "application/pdf",
        "md5Hash": "d41d8cd98f00b204e9800998ecf8427e",
        "directoryId": 2,
        "directoryName": "PDF文件",
        "description": "示例PDF文档",
        "downloadCount": 0,
        "fileSizeFormatted": "1.00 MB",
        "createdAt": "2024-12-01T14:30:22",
        "updatedAt": "2024-12-01T14:30:22"
    }
}
```

### 2. 获取目录下的文件列表

**接口**: `GET /api/v1/files/directory/{directoryId}`

**前端示例**:
```javascript
async function getFilesByDirectory(directoryId) {
    try {
        const response = await fetch(`/api/v1/files/directory/${directoryId}`);
        const result = await response.json();
        
        if (result.success) {
            console.log('文件列表:', result.data);
            displayFiles(result.data);
        } else {
            console.error('获取失败:', result.message);
        }
    } catch (error) {
        console.error('请求出错:', error);
    }
}

function displayFiles(files) {
    const fileList = document.getElementById('fileList');
    fileList.innerHTML = '';
    
    files.forEach(file => {
        const fileItem = document.createElement('div');
        fileItem.className = 'file-item';
        fileItem.innerHTML = `
            <h4>${file.originalName}</h4>
            <p>类型: ${file.fileType}</p>
            <p>大小: ${file.fileSizeFormatted}</p>
            <p>上传时间: ${new Date(file.createdAt).toLocaleString()}</p>
            <p>下载次数: ${file.downloadCount}</p>
            <button onclick="deleteFile(${file.id})">删除</button>
        `;
        fileList.appendChild(fileItem);
    });
}
```

### 3. 分页获取文件列表

**接口**: `GET /api/v1/files/page`

**参数**:
- `current` (Long): 当前页码，默认1
- `size` (Long): 每页大小，默认10
- `directoryId` (Long): 目录ID，可选

**前端示例**:
```javascript
async function getFilesWithPage(current = 1, size = 10, directoryId = null) {
    let url = `/api/v1/files/page?current=${current}&size=${size}`;
    if (directoryId) {
        url += `&directoryId=${directoryId}`;
    }
    
    try {
        const response = await fetch(url);
        const result = await response.json();
        
        if (result.success) {
            console.log('分页数据:', result.data);
            displayPagedFiles(result.data);
        } else {
            console.error('获取失败:', result.message);
        }
    } catch (error) {
        console.error('请求出错:', error);
    }
}

function displayPagedFiles(pageData) {
    // 显示文件列表
    displayFiles(pageData.records);
    
    // 显示分页信息
    const pageInfo = document.getElementById('pageInfo');
    pageInfo.innerHTML = `
        <p>第 ${pageData.current} 页，共 ${pageData.pages} 页</p>
        <p>总计 ${pageData.total} 个文件</p>
        <button onclick="getFilesWithPage(${pageData.current - 1})" 
                ${pageData.current <= 1 ? 'disabled' : ''}>上一页</button>
        <button onclick="getFilesWithPage(${pageData.current + 1})" 
                ${pageData.current >= pageData.pages ? 'disabled' : ''}>下一页</button>
    `;
}
```

### 4. 获取文件详情

**接口**: `GET /api/v1/files/{fileId}`

**前端示例**:
```javascript
async function getFileDetails(fileId) {
    try {
        const response = await fetch(`/api/v1/files/${fileId}`);
        const result = await response.json();
        
        if (result.success) {
            console.log('文件详情:', result.data);
            showFileDetails(result.data);
        } else {
            console.error('获取失败:', result.message);
        }
    } catch (error) {
        console.error('请求出错:', error);
    }
}
```

### 5. 删除文件

**接口**: `DELETE /api/v1/files/{fileId}`

**前端示例**:
```javascript
async function deleteFile(fileId) {
    if (!confirm('确定要删除这个文件吗？')) {
        return;
    }
    
    try {
        const response = await fetch(`/api/v1/files/${fileId}`, {
            method: 'DELETE'
        });
        const result = await response.json();
        
        if (result.success) {
            alert('文件删除成功！');
            // 刷新文件列表
            location.reload();
        } else {
            alert('删除失败: ' + result.message);
        }
    } catch (error) {
        alert('删除出错: ' + error.message);
    }
}
```

### 6. 下载文件

**接口**: `GET /api/v1/files/download/{fileId}`

**功能**: 下载文件并自动更新下载次数

**前端示例**:
```javascript
// 方式一：直接打开下载链接
function downloadFile(fileId) {
    window.open(`/api/v1/files/download/${fileId}`, '_blank');
}

// 方式二：使用fetch下载
async function downloadFileWithFetch(fileId, filename) {
    try {
        const response = await fetch(`/api/v1/files/download/${fileId}`);
        
        if (response.ok) {
            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = filename || 'download';
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
            document.body.removeChild(a);
            
            console.log('文件下载成功');
        } else {
            console.error('下载失败:', response.status);
        }
    } catch (error) {
        console.error('下载出错:', error);
    }
}
```

### 7. 更新下载次数

**接口**: `PUT /api/v1/files/{fileId}/download`

**功能**: 仅更新下载次数，不下载文件

**前端示例**:
```javascript
async function updateDownloadCount(fileId) {
    try {
        const response = await fetch(`/api/v1/files/${fileId}/download`, {
            method: 'PUT'
        });
        const result = await response.json();
        
        if (result.success) {
            console.log('下载次数更新成功');
        }
    } catch (error) {
        console.error('更新失败:', error);
    }
}
```

## 完整的HTML示例页面

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>文件管理系统</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .container { max-width: 800px; margin: 0 auto; }
        .upload-form { background: #f5f5f5; padding: 20px; border-radius: 5px; margin-bottom: 20px; }
        .file-item { border: 1px solid #ddd; padding: 15px; margin: 10px 0; border-radius: 5px; }
        .file-item h4 { margin: 0 0 10px 0; color: #333; }
        .file-item p { margin: 5px 0; color: #666; }
        button { background: #007bff; color: white; border: none; padding: 8px 16px; border-radius: 4px; cursor: pointer; }
        button:hover { background: #0056b3; }
        button:disabled { background: #ccc; cursor: not-allowed; }
        .delete-btn { background: #dc3545; }
        .delete-btn:hover { background: #c82333; }
    </style>
</head>
<body>
    <div class="container">
        <h1>文件管理系统</h1>
        
        <!-- 文件上传表单 -->
        <div class="upload-form">
            <h2>上传文件</h2>
            <form id="uploadForm" enctype="multipart/form-data">
                <p>
                    <label for="fileInput">选择文件:</label><br>
                    <input type="file" id="fileInput" name="file" required>
                </p>
                <p>
                    <label for="directoryId">目录ID:</label><br>
                    <input type="number" id="directoryId" name="directoryId" value="1" required>
                </p>
                <p>
                    <label for="description">文件描述:</label><br>
                    <input type="text" id="description" name="description" placeholder="可选">
                </p>
                <button type="submit">上传文件</button>
            </form>
        </div>
        
        <!-- 查询文件 -->
        <div>
            <h2>查询文件</h2>
            <p>
                <label for="queryDirectoryId">目录ID:</label>
                <input type="number" id="queryDirectoryId" value="1">
                <button onclick="loadFiles()">查询</button>
                <button onclick="loadAllFiles()">查询所有文件</button>
            </p>
        </div>
        
        <!-- 分页信息 -->
        <div id="pageInfo"></div>
        
        <!-- 文件列表 -->
        <div id="fileList"></div>
    </div>

    <script>
        // 文件上传
        document.getElementById('uploadForm').addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const formData = new FormData();
            const fileInput = document.getElementById('fileInput');
            const directoryId = document.getElementById('directoryId').value;
            const description = document.getElementById('description').value;
            
            if (!fileInput.files[0]) {
                alert('请选择文件');
                return;
            }
            
            formData.append('file', fileInput.files[0]);
            formData.append('directoryId', directoryId);
            if (description) {
                formData.append('description', description);
            }
            
            try {
                const response = await fetch('/api/v1/files/upload', {
                    method: 'POST',
                    body: formData
                });
                
                const result = await response.json();
                if (result.success) {
                    alert('文件上传成功！');
                    document.getElementById('uploadForm').reset();
                    loadFiles(); // 刷新文件列表
                } else {
                    alert('上传失败: ' + result.message);
                }
            } catch (error) {
                alert('上传出错: ' + error.message);
            }
        });

        // 加载指定目录的文件
        async function loadFiles() {
            const directoryId = document.getElementById('queryDirectoryId').value;
            if (!directoryId) {
                alert('请输入目录ID');
                return;
            }
            
            try {
                const response = await fetch(`/api/v1/files/directory/${directoryId}`);
                const result = await response.json();
                
                if (result.success) {
                    displayFiles(result.data);
                } else {
                    alert('查询失败: ' + result.message);
                }
            } catch (error) {
                alert('查询出错: ' + error.message);
            }
        }

        // 加载所有文件（分页）
        async function loadAllFiles(current = 1) {
            try {
                const response = await fetch(`/api/v1/files/page?current=${current}&size=10`);
                const result = await response.json();
                
                if (result.success) {
                    displayFiles(result.data.records);
                    displayPagination(result.data);
                } else {
                    alert('查询失败: ' + result.message);
                }
            } catch (error) {
                alert('查询出错: ' + error.message);
            }
        }

        // 显示文件列表
        function displayFiles(files) {
            const fileList = document.getElementById('fileList');
            if (files.length === 0) {
                fileList.innerHTML = '<p>暂无文件</p>';
                return;
            }
            
            fileList.innerHTML = files.map(file => `
                <div class="file-item">
                    <h4>${file.originalName}</h4>
                    <p><strong>类型:</strong> ${file.fileType}</p>
                    <p><strong>大小:</strong> ${file.fileSizeFormatted}</p>
                    <p><strong>目录:</strong> ${file.directoryName || '未知'}</p>
                    <p><strong>上传时间:</strong> ${new Date(file.createdAt).toLocaleString()}</p>
                    <p><strong>下载次数:</strong> ${file.downloadCount}</p>
                                         ${file.description ? `<p><strong>描述:</strong> ${file.description}</p>` : ''}
                     <button onclick="downloadFile(${file.id})">下载文件</button>
                     <button class="delete-btn" onclick="deleteFile(${file.id})">删除</button>
                     <button onclick="updateDownloadCount(${file.id})">更新下载次数</button>
                </div>
            `).join('');
        }

        // 显示分页信息
        function displayPagination(pageData) {
            const pageInfo = document.getElementById('pageInfo');
            pageInfo.innerHTML = `
                <p>第 ${pageData.current} 页，共 ${pageData.pages} 页，总计 ${pageData.total} 个文件</p>
                <button onclick="loadAllFiles(${pageData.current - 1})" 
                        ${pageData.current <= 1 ? 'disabled' : ''}>上一页</button>
                <button onclick="loadAllFiles(${pageData.current + 1})" 
                        ${pageData.current >= pageData.pages ? 'disabled' : ''}>下一页</button>
            `;
        }

        // 删除文件
        async function deleteFile(fileId) {
            if (!confirm('确定要删除这个文件吗？')) {
                return;
            }
            
            try {
                const response = await fetch(`/api/v1/files/${fileId}`, {
                    method: 'DELETE'
                });
                const result = await response.json();
                
                if (result.success) {
                    alert('文件删除成功！');
                    loadAllFiles(); // 刷新列表
                } else {
                    alert('删除失败: ' + result.message);
                }
            } catch (error) {
                alert('删除出错: ' + error.message);
            }
        }

        // 下载文件
        function downloadFile(fileId) {
            // 直接打开下载链接
            window.open(`/api/v1/files/download/${fileId}`, '_blank');
            
            // 延迟一秒后刷新列表以显示更新的下载次数
            setTimeout(() => {
                loadAllFiles();
            }, 1000);
        }

        // 更新下载次数
        async function updateDownloadCount(fileId) {
            try {
                const response = await fetch(`/api/v1/files/${fileId}/download`, {
                    method: 'PUT'
                });
                const result = await response.json();
                
                if (result.success) {
                    alert('下载次数更新成功！');
                    loadAllFiles(); // 刷新列表
                } else {
                    alert('更新失败: ' + result.message);
                }
            } catch (error) {
                alert('更新出错: ' + error.message);
            }
        }

        // 页面加载时查询所有文件
        window.onload = function() {
            loadAllFiles();
        };
    </script>
</body>
</html>
```

## 注意事项

1. **文件大小限制**: 默认最大100MB，可在配置文件中修改
2. **目录结构**: 上传时会自动创建对应的本地目录结构
3. **文件去重**: 相同MD5的文件会执行秒传，节省存储空间
4. **安全性**: 建议在生产环境中添加文件类型校验和用户权限控制
5. **存储路径**: 文件存储在配置的根目录下，按目录结构组织
6. **跨域配置**: Controller已配置CORS，支持前端调用

## 测试建议

1. 先通过目录管理接口创建几个测试目录
2. 使用上述HTML页面测试文件上传功能
3. 验证本地目录结构是否正确创建
4. 测试文件查询、删除等功能
5. 验证相同文件的秒传功能 