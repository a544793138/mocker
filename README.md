# Moker

HTTP 模拟服务端构建器。可以快速启动一个 HTTP 服务端，在指定路由和响应头下返回指定的内容。

## 项目简介

Mocker 是一个基于 Java 的 HTTP 模拟服务器工具，允许开发者快速创建和配置自定义的 HTTP
路由，支持设置状态码、响应头、内容类型和响应体等。适用于接口测试、前后端联调等场景。

## 功能特性

- 🎯 **快速启动**：一键启动 HTTP 服务器
- 📝 **可视化配置**：图形化界面管理路由配置
- 🔧 **灵活配置**：支持多种 HTTP 方法（GET、POST、PUT、DELETE、PATCH、HEAD、OPTIONS）
- 📋 **响应头管理**：Postman 风格的响应头配置界面
- 📊 **实时日志**：查看所有请求的详细信息
- 🎨 **JSON 格式化**：支持 JSON 响应的格式化和紧凑化
- 🔄 **路由编辑**：支持添加、编辑、删除路由，双击快速编辑
- 💾 **响应保存**：所有配置自动保存到内存中

## 项目结构

```
moker/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── tjwoods/
│                   ├── engine/
│                   │   └── HttpServerEngine.java    # HTTP 服务器引擎
│                   ├── gui/
│                   │   ├── MainFrame.java              # 主窗口界面
│                   │   └── LogWindow.java             # 日志窗口
│                   ├── model/
│                   │   ├── RouteConfig.java            # 路由配置模型
│                   │   └── ServerConfig.java           # 服务器配置模型
│                   ├── util/
│                   │   └── JsonUtil.java              # JSON 工具类
│                   └── Main.java                      # 程序入口
├── pom.xml                                      # Maven 项目配置文件
├── start                                         # Linux/Mac 启动脚本
└── test_server.bat                               # Windows 启动脚本
```

## 环境要求

- Java 8 或更高版本
- Maven 3.6 或更高版本（可选，用于编译）

## 快速开始

### 方式一：使用启动脚本

**Windows:**

```bash
双击运行 test_server.bat
```

**Linux/Mac:**

```bash
chmod +x start
./start
```

### 方式二：使用 Maven

```bash
# 编译项目
mvn compile

# 运行程序
mvn exec:java -Dexec.mainClass="com.tjwoods.Main"
```

### 方式三：直接运行 JAR

```bash
# 打包项目
mvn package

# 运行 JAR 文件
java -jar target/moker-1.0-SNAPSHOT.jar
```

## 使用说明

### 1. 配置服务器

- **端口号**：在顶部"服务器配置"区域设置监听端口（默认：8080）
- **启动/停止**：点击"启动服务器"或"停止服务器"按钮控制服务器状态
- **查看日志**：点击"📝 日志"按钮打开/关闭请求日志窗口

### 2. 添加路由

1. 点击"添加路由"按钮
2. 配置路由参数：
    - **HTTP 方法**：选择请求方法（GET、POST、PUT、DELETE 等）
    - **路径**：设置路由路径（例如：`/api/user`）
    - **状态码**：设置 HTTP 响应状态码（例如：200、404、500）
    - **内容类型**：设置响应内容类型（例如：`application/json`）
    - **响应头**：配置自定义响应头（支持多个）
        - 在表格中填写 Key 和 Value
        - 点击"+ 添加响应头"添加新行
        - 点击"删除"按钮删除对应的响应头
    - **响应内容**：编辑响应体内容
        - 支持文本和 JSON 格式
        - 使用"格式化 JSON"美化 JSON 格式
        - 使用"紧凑化 JSON"压缩 JSON 格式
3. 点击"确定"保存路由

### 3. 编辑路由

- **方式一**：选中路由后点击"编辑路由"按钮
- **方式二**：双击路由表格中的任意记录快速编辑

### 4. 删除路由

1. 选中要删除的路由
2. 点击"删除路由"按钮
3. 确认删除操作

### 5. 测试路由

启动服务器后，使用任何 HTTP 客户端测试配置的路由：

**示例：使用 curl**

```bash
curl http://localhost:8080/api/user
```

**示例：使用浏览器**

```
http://localhost:8080/api/user
```

**示例：使用 Postman**

```
GET http://localhost:8080/api/user
```

## 使用示例

### 示例 1：返回 JSON 数据

1. 添加路由：
    - 方法：GET
    - 路径：`/api/user`
    - 状态码：200
    - 内容类型：`application/json`
    - 响应头：无
    - 响应内容：
      ```json
      {
        "id": 1,
        "name": "张三",
        "email": "zhangsan@example.com"
      }
      ```

2. 启动服务器
3. 测试：`curl http://localhost:8080/api/user`

### 示例 2：带自定义响应头的路由

1. 添加路由：
    - 方法：POST
    - 路径：`/api/login`
    - 状态码：201
    - 内容类型：`application/json`
    - 响应头：
        - Key: `X-Custom-Header` → Value: `custom-value`
        - Key: `X-Request-ID` → Value: `12345`
    - 响应内容：
      ```json
      {
        "status": "success",
        "message": "登录成功"
      }
      ```

2. 启动服务器
3. 测试：`curl -X POST http://localhost:8080/api/login`

### 示例 3：模拟错误响应

1. 添加路由：
    - 方法：GET
    - 路径：`/api/error`
    - 状态码：500
    - 内容类型：`application/json`
    - 响应内容：
      ```json
      {
        "error": "Internal Server Error",
        "message": "服务器内部错误"
      }
      ```

2. 启动服务器
3. 测试：`curl http://localhost:8080/api/error`

## 技术栈

- **编程语言**：Java 8
- **构建工具**：Maven
- **GUI 框架**：Swing
- **HTTP 服务器**：内置的 `com.sun.net.httpserver.HttpServer`

## 注意事项

1. 同一路径和 HTTP 方法只能配置一个路由，重复添加会提示错误
2. 修改路由的路径或方法时，会自动检查是否与其他路由冲突
3. 响应头中的空行在保存时会被自动过滤
4. 服务器启动后，端口输入框会被禁用，需要先停止服务器才能修改端口
5. 所有路由配置在内存中，程序重启后需要重新配置

## 许可证

本项目采用 [Apache License 2.0](LICENSE) 开源许可证。

```
Copyright 2026 tjwoods

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## 作者

Created with ❤️ by tjwoods

