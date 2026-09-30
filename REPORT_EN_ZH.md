# Short Report / 简短报告

## English

This project implements a small REST web service for recording whether signed 64-bit integers are prime. The server stores classifications in a tab-separated text file and exposes `GET /api/numbers/{number}` to retrieve a record and `PUT /api/numbers/{number}` with a JSON `prime` boolean to save one. An unknown number produces HTTP 404, which tells the client to calculate locally. The console client uses trial division up to the square root, then submits the result with PUT. Repeating a query demonstrates that the result is served from the server's stored records. The implementation uses Java 17, Maven, the JDK HTTP server and HTTP client, and no third-party runtime libraries.

## 中文

本项目实现了一个小型 REST Web 服务，用于记录有符号 64 位整数是否为素数。服务器将分类保存到制表符分隔的文本文件中，并提供 `GET /api/numbers/{number}` 查询记录，以及携带 JSON `prime` 布尔值的 `PUT /api/numbers/{number}` 保存记录。服务器未收录该数字时返回 HTTP 404，客户端收到后在本地计算。控制台客户端使用试除法检查到平方根，再通过 PUT 提交结果。重复查询可以展示服务器直接返回已保存的分类。本项目使用 Java 17、Maven 和 JDK 自带的 HTTP 服务端与客户端，没有第三方运行时依赖。
