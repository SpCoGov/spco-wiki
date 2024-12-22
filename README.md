# spco-wiki

[![JDK-21+](https://img.shields.io/badge/JDK-21+-blue)](https://adoptium.net)
[![GitHub issues](https://img.shields.io/github/issues/SpCoGov/spco-wiki)](https://github.com/SpCoGov/spco-wiki/issues)
[![GitHub pull requests](https://img.shields.io/github/issues-pr/SpCoGov/spco-wiki)](https://github.com/SpCoGov/spco-wiki/pulls)
[![License](https://img.shields.io/github/license/SpCoGov/spco-wiki)](https://github.com/SpCoGov/spco-wiki/blob/master/LICENSE)

**spco-wiki** 是一个用 Java 编写的开源库，旨在简化与基于 MediaWiki 的 Wiki 平台 API 的交互流程。通过使用本库，您可以轻松调用 MediaWiki 提供的 API 进行常见的操作，例如获取页面内容、编辑页面等。

## 特性

- **简单易用**：提供直观的 API，降低调用 MediaWiki 接口的复杂性。
- **高效**：自动处理登录、会话管理和请求构建。
- **灵活扩展**：支持自定义参数和扩展接口。
- **错误处理**：对常见的错误情况进行封装和提示，减少开发调试时间。

## 安装

### 使用 Maven

在项目的 `pom.xml` 文件中添加以下依赖：

```xml
<dependency>
    <groupId>top.spco.spcobot.wiki</groupId>
    <artifactId>spco-wiki</artifactId>
    <version>0.1.2</version>
</dependency>
```

### 使用 Gradle

在项目的 `build.gradle` 文件中添加以下内容：

```groovy
implementation group: 'top.spco.spcobot.wiki', name: 'spco-wiki', version: '0.1.2'
```