# 配置指南

## 配置文件位置

FinalShell Clone 的配置文件存储位置：

- **Windows**: `%USERPROFILE%\.finalshell\`
- **macOS**: `~/Library/Application Support/FinalShell/`
- **Linux**: `~/.finalshell/`

## 配置文件结构

```
.finalshell/
├── config.json          # 主配置文件
├── client.json          # 客户端配置
├── folders.json         # 文件夹配置
├── conn/                # 连接配置目录
│   ├── conn1.json       # 连接配置文件
│   └── conn2.json
├── themes/              # 主题配置
├── keys/                # SSH密钥
└── logs/               # 日志文件
```

## 主要配置项

### 1. 应用配置 (config.json)

```json
{
  "theme": "dark",
  "language": "zh_CN",
  "fontSize": 12,
  "fontFamily": "Consolas",
  "autoSave": true,
  "windowState": {
    "x": 100,
    "y": 100,
    "width": 1200,
    "height": 800,
    "maximized": false
  },
  "recentConnections": [],
  "defaultCharset": "UTF-8",
  "scrollbackLines": 5000,
  "commandPrompt": true
}
```

### 2. 连接配置 (conn/*.json)

```json
{
  "id": "conn-uuid",
  "name": "服务器名称",
  "host": "192.168.1.100",
  "port": 22,
  "userName": "root",
  "password": "enc:encrypted_password",
  "privateKey": "/path/to/private/key",
  "type": 1,
  "charset": "UTF-8",
  "terminalType": "xterm-256color",
  "parentId": "folder-id",
  "proxyConfig": {
    "type": "none",
    "host": "",
    "port": 0,
    "user": "",
    "password": ""
  }
}
```

### 3. 文件夹配置 (folders.json)

```json
[
  {
    "id": "folder-uuid",
    "name": "生产环境",
    "parentId": "root",
    "expanded": true,
    "createTime": 1640995200000,
    "updateTime": 1640995200000
  }
]
```

## 环境变量配置

### 系统属性

| 属性 | 默认值 | 说明 |
|------|--------|------|
| `finalshell.config.dir` | 用户目录 | 配置文件目录 |
| `finalshell.log.level` | INFO | 日志级别 |
| `java.net.useSystemProxies` | false | 使用系统代理 |

### 启动参数

```bash
# 指定配置目录
java -Dfinalshell.config.dir=/custom/path -jar finalshell.jar

# 设置日志级别
java -Dfinalshell.log.level=DEBUG -jar finalshell.jar

# 最小化启动
java -jar finalshell.jar -min

# 指定更新服务器
java -Dupdate.check.url=https://example.com/update -jar finalshell.jar

# 指定控制服务器
java -Dcontrol.server.url=https://example.com/control -jar finalshell.jar
```

## 主题配置

### 内置主题

- `light` - 浅色主题
- `dark` - 深色主题
- `dracula` - Dracula 主题
- `monokai` - Monokai 主题
- `solarized` - Solarized 主题

### 自定义主题

在 `themes/` 目录创建 JSON 文件：

```json
{
  "name": "Custom Theme",
  "background": "#1e1e1e",
  "foreground": "#d4d4d4",
  "cursor": "#ffffff",
  "selection": "#264f78",
  "ansi": {
    "black": "#000000",
    "red": "#cd3131",
    "green": "#0dbc79",
    "yellow": "#e5e510",
    "blue": "#2472c8",
    "magenta": "#bc3fbc",
    "cyan": "#11a8cd",
    "white": "#e5e5e5"
  }
}
```

## 网络配置

### 代理设置

```json
{
  "proxyConfig": {
    "type": "http",        // http, socks4, socks5, none
    "host": "proxy.example.com",
    "port": 8080,
    "user": "proxy_user",
    "password": "proxy_pass",
    "authRequired": true
  }
}
```

### 跳板机配置

```json
{
  "jumpServer": {
    "enabled": true,
    "host": "jump.example.com",
    "port": 22,
    "user": "jump_user",
    "password": "jump_pass"
  }
}
```

## 安全配置

### SSH 密钥配置

1. 生成密钥对：
   ```bash
   ssh-keygen -t rsa -b 4096 -C "your_email@example.com"
   ```

2. 在连接配置中指定私钥路径：
   ```json
   {
     "privateKey": "/path/to/private/key",
     "passphrase": "key_passphrase"
   }
   ```

### 密码加密

连接密码使用 DES 加密存储，格式为 `enc:encrypted_data`。

**注意**：当前加密机制存在安全风险，建议：
- 使用 SSH 密钥认证
- 在安全环境中使用
- 定期更换密码

## 性能调优

### JVM 参数优化

```bash
# 内存设置
-Xms256m -Xmx1024m

# 垃圾回收优化
-XX:+UseG1GC -XX:MaxGCPauseMillis=200

# UI 渲染优化
-Dsun.java2d.opengl=true
-Dswing.aatext=true
-Dawt.useSystemAAFontSettings=on
```

### 应用配置优化

```json
{
  "scrollbackLines": 1000,     // 减少内存占用
  "autoSave": false,           // 关闭自动保存
  "logLevel": "WARN",          // 减少日志输出
  "cacheSize": {
    "icons": 100,
    "images": 50,
    "themes": 10
  }
}
```

## 故障排除

### 常见问题

1. **启动失败**
   - 检查 Java 版本 (需要 JDK 8+)
   - 检查配置文件权限
   - 查看日志文件

2. **连接失败**
   - 验证网络连通性
   - 检查防火墙设置
   - 确认代理配置

3. **中文乱码**
   - 设置正确的字符编码
   - 检查终端字体支持

### 日志配置

编辑 `logback.xml`：

```xml
<configuration>
  <appender name="FILE" class="ch.qos.logback.core.FileAppender">
    <file>logs/finalshell.log</file>
    <encoder>
      <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
    </encoder>
  </appender>
  
  <root level="INFO">
    <appender-ref ref="FILE" />
  </root>
</configuration>
```

## 备份与恢复

### 备份配置

```bash
# 备份整个配置目录
tar -czf finalshell-backup.tar.gz ~/.finalshell/

# 仅备份连接配置
cp -r ~/.finalshell/conn/ ~/backup/
```

### 恢复配置

```bash
# 恢复配置目录
tar -xzf finalshell-backup.tar.gz -C ~/

# 导入连接配置
通过应用菜单 -> 工具 -> 导入配置
```

## 高级配置

### 插件配置

```json
{
  "plugins": {
    "enabled": true,
    "directory": "plugins/",
    "loadOnStartup": ["essential-plugin.jar"]
  }
}
```

### 脚本配置

```json
{
  "scripts": {
    "engine": "javascript",
    "directory": "scripts/",
    "autorun": ["startup.js"]
  }
}
```

---

更多配置选项请参考应用内的设置对话框。
