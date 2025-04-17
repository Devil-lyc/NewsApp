# Node.js API 接口文档

## 基本信息
- 基本URL：https://ceqjrjwgmeqy.sealoshzh.site
- 文档URL：https://ceqjrjwgmeqy.sealoshzh.site/api-docs

## 认证方式
所有需要认证的接口都需要在请求头中添加 `Authorization` 字段，值为 `Bearer {token}`，其中 `{token}` 是用户登录后获取的令牌。

## 接口列表

### 1. 用户认证

#### 1.1 用户注册
- **接口URL**：`/api/auth/register`
- **方法**：POST
- **描述**：注册新用户
- **请求体**：
  ```json
  {
    "username": "用户名",  // 必填，3-20个字符
    "email": "邮箱",      // 必填，有效的邮箱地址
    "password": "密码"    // 必填，至少6个字符
  }
  ```
- **响应**：
  ```json
  {
    "success": true,
    "message": "注册成功",
    "data": {
      "user": {
        "id": "用户ID",
        "username": "用户名",
        "email": "邮箱"
      },
      "token": "JWT令牌"
    }
  }
  ```

#### 1.2 用户登录
- **接口URL**：`/api/auth/login`
- **方法**：POST
- **描述**：用户登录获取令牌
- **请求体**：
  ```json
  {
    "email": "邮箱",    // 必填
    "password": "密码"  // 必填
  }
  ```
- **响应**：
  ```json
  {
    "success": true,
    "message": "登录成功",
    "data": {
      "user": {
        "id": "用户ID",
        "username": "用户名",
        "email": "邮箱"
      },
      "token": "JWT令牌"
    }
  }
  ```

### 2. 用户信息管理

#### 2.1 获取用户信息
- **接口URL**：`/api/users/profile`
- **方法**：GET
- **描述**：获取当前用户的个人信息
- **认证**：需要
- **响应**：
  ```json
  {
    "success": true,
    "data": {
      "user": {
        "_id": "用户ID",
        "username": "用户名",
        "email": "邮箱",
        "avatar": "头像URL",
        "bio": "个人简介",
        "createdAt": "创建时间",
        "updatedAt": "更新时间"
      }
    }
  }
  ```

#### 2.2 更新用户信息
- **接口URL**：`/api/users/profile`
- **方法**：PUT
- **描述**：更新当前用户的个人信息
- **认证**：需要
- **请求体**：
  ```json
  {
    "username": "新用户名",  // 可选，3-20个字符
    "avatar": "头像URL",    // 可选，头像图片URL
    "bio": "个人简介"       // 可选，最多200个字符
  }
  ```
- **响应**：
  ```json
  {
    "success": true,
    "message": "用户信息更新成功",
    "data": {
      "user": {
        "_id": "用户ID",
        "username": "用户名",
        "email": "邮箱",
        "avatar": "头像URL",
        "bio": "个人简介",
        "createdAt": "创建时间",
        "updatedAt": "更新时间"
      }
    }
  }
  ```

#### 2.3 修改密码
- **接口URL**：`/api/users/change-password`
- **方法**：PUT
- **描述**：修改当前用户的密码
- **认证**：需要
- **请求体**：
  ```json
  {
    "currentPassword": "当前密码",  // 必填
    "newPassword": "新密码"         // 必填，至少6个字符
  }
  ```
- **响应**：
  ```json
  {
    "success": true,
    "message": "密码修改成功"
  }
  ```

### 3. 收藏管理

#### 3.1 添加收藏
- **接口URL**：`/api/favorites`
- **方法**：POST
- **描述**：添加新收藏
- **认证**：需要
- **请求体**：
  ```json
  {
    "title": "收藏标题",            // 必填，1-100个字符
    "url": "收藏URL",              // 必填，有效的URL
    "description": "收藏描述",      // 可选，最多500个字符
    "source_name": "来源名称",      // 可选
    "publishedAt": "发布时间",      // 可选，发布日期时间
    "category": "分类",            // 可选
    "imageUrl": "图片URL",         // 可选，有效的URL
    "author": "作者",              // 可选
    "newsId": "新闻ID"             // 可选，新闻标识符
  }
  ```
- **响应**：
  ```json
  {
    "success": true,
    "message": "收藏添加成功",
    "data": {
      "favorite": {
        "_id": "收藏ID",
        "user": "用户ID",
        "title": "收藏标题",
        "description": "收藏描述",
        "url": "收藏URL",
        "source_name": "来源名称",
        "publishedAt": "发布时间",
        "category": "分类",
        "imageUrl": "图片URL",
        "author": "作者",
        "newsId": "新闻ID",
        "createdAt": "创建时间",
        "updatedAt": "更新时间"
      }
    }
  }
  ```

#### 3.2 获取收藏列表
- **接口URL**：`/api/favorites`
- **方法**：GET
- **描述**：获取当前用户的收藏列表，支持分页和筛选
- **认证**：需要
- **查询参数**：
  - `category`：按分类筛选
  - `source_name`：按来源名称筛选
  - `author`：按作者筛选
  - `page`：页码，默认1
  - `limit`：每页数量，默认10
- **响应**：
  ```json
  {
    "success": true,
    "data": {
      "favorites": [
        {
          "_id": "收藏ID",
          "user": "用户ID",
          "title": "收藏标题",
          "description": "收藏描述",
          "url": "收藏URL",
          "source_name": "来源名称",
          "publishedAt": "发布时间",
          "category": "分类",
          "imageUrl": "图片URL",
          "author": "作者",
          "newsId": "新闻ID",
          "createdAt": "创建时间",
          "updatedAt": "更新时间"
        }
      ],
      "pagination": {
        "total": 总数量,
        "page": 当前页码,
        "limit": 每页数量,
        "pages": 总页数
      }
    }
  }
  ```

#### 3.3 获取单个收藏详情
- **接口URL**：`/api/favorites/{id}`
- **方法**：GET
- **描述**：获取单个收藏的详细信息
- **认证**：需要
- **路径参数**：
  - `id`：收藏ID
- **响应**：
  ```json
  {
    "success": true,
    "data": {
      "favorite": {
        "_id": "收藏ID",
        "user": "用户ID",
        "title": "收藏标题",
        "description": "收藏描述",
        "url": "收藏URL",
        "source_name": "来源名称",
        "publishedAt": "发布时间",
        "category": "分类",
        "imageUrl": "图片URL",
        "author": "作者",
        "newsId": "新闻ID",
        "createdAt": "创建时间",
        "updatedAt": "更新时间"
      }
    }
  }
  ```

#### 3.4 更新收藏
- **接口URL**：`/api/favorites/{id}`
- **方法**：PUT
- **描述**：更新收藏的信息
- **认证**：需要
- **路径参数**：
  - `id`：收藏ID
- **请求体**：
  ```json
  {
    "title": "更新的标题",           // 可选，1-100个字符
    "url": "更新的URL",              // 可选，有效的URL
    "description": "更新的描述",      // 可选，最多500个字符
    "source_name": "更新的来源名称",   // 可选
    "publishedAt": "更新的发布时间",   // 可选
    "category": "更新的分类",         // 可选
    "imageUrl": "更新的图片URL",      // 可选，有效的URL
    "author": "更新的作者",           // 可选
    "newsId": "更新的新闻ID"          // 可选
  }
  ```
- **响应**：
  ```json
  {
    "success": true,
    "message": "收藏更新成功",
    "data": {
      "favorite": {
        "_id": "收藏ID",
        "user": "用户ID",
        "title": "更新后的标题",
        "description": "更新后的描述",
        "url": "更新后的URL",
        "source_name": "更新后的来源名称",
        "publishedAt": "更新后的发布时间",
        "category": "更新后的分类",
        "imageUrl": "更新后的图片URL",
        "author": "更新后的作者",
        "newsId": "更新后的新闻ID",
        "createdAt": "创建时间",
        "updatedAt": "更新时间"
      }
    }
  }
  ```

#### 3.5 删除收藏
- **接口URL**：`/api/favorites/{id}`
- **方法**：DELETE
- **描述**：删除收藏
- **认证**：需要
- **路径参数**：
  - `id`：收藏ID
- **响应**：
  ```json
  {
    "success": true,
    "message": "收藏删除成功"
  }
  ```

## 错误处理

所有API接口在遇到错误时都会返回一个统一格式的响应：

```json
{
  "success": false,
  "message": "错误描述",
  "errors": [
    {
      "msg": "错误详情",
      "param": "错误参数",
      "location": "错误位置"
    }
  ]
}
```

### 常见HTTP状态码

- `200`: 请求成功
- `201`: 创建成功
- `400`: 请求无效
- `401`: 未授权
- `404`: 资源不存在
- `409`: 资源冲突
- `500`: 服务器内部错误

## 使用示例

### 用户注册示例

**请求**:
```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{"username":"测试用户","email":"test@example.com","password":"password123"}' \
  https://ceqjrjwgmeqy.sealoshzh.site/api/auth/register
```

**响应**:
```json
{
  "success": true,
  "message": "注册成功",
  "data": {
    "user": {
      "id": "67ff5a1648ee09837b210ac6",
      "username": "测试用户",
      "email": "test@example.com"
    },
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

### 添加收藏示例

**请求**:
```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -d '{
    "title": "测试收藏",
    "url": "https://example.com",
    "description": "这是一个测试收藏",
    "source_name": "示例网站",
    "publishedAt": "2024-04-16T09:11:36.285Z",
    "category": "文章",
    "imageUrl": "https://example.com/image.jpg",
    "author": "示例作者",
    "newsId": "news12345"
  }' \
  https://ceqjrjwgmeqy.sealoshzh.site/api/favorites
```

**响应**:
```json
{
  "success": true,
  "message": "收藏添加成功",
  "data": {
    "favorite": {
      "_id": "67ff74481e2e43d98d67fc4a",
      "user": "67ff5a1648ee09837b210ac6",
      "title": "测试收藏",
      "description": "这是一个测试收藏",
      "url": "https://example.com",
      "source_name": "示例网站",
      "publishedAt": "2024-04-16T09:11:36.285Z",
      "category": "文章",
      "imageUrl": "https://example.com/image.jpg",
      "author": "示例作者",
      "newsId": "news12345",
      "createdAt": "2024-04-16T09:11:36.285Z",
      "updatedAt": "2024-04-16T09:11:36.285Z"
    }
  }
}
``` 