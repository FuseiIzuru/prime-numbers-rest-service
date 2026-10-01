# 素数 REST 服务（Java + Maven）

这是一个课程实验项目：服务器保存数字是否为素数的结果；客户端先向服务器查询。如果服务器还没有记录，客户端就在本地计算，再把结果交给服务器保存。

## 一、准备软件

你需要：

- IntelliJ IDEA
- JDK 17 或更新版本
- IDEA 自带的 Maven 支持（通常不需要单独安装 Maven）
- 第一次加载项目时可以连接互联网，让 Maven 下载构建所需文件

## 二、把项目放到电脑上

如果你还没有下载项目：

1. 打开这个 GitHub 页面：<https://github.com/FuseiIzuru/prime-numbers-rest-service>
2. 点击绿色的 **Code** 按钮。
3. 选择 **Download ZIP**，下载后解压到一个容易找到的位置。

如果你已经用 Git 把仓库克隆到电脑，就直接找到它的文件夹，不需要再下载。

## 三、用 IDEA 打开项目

1. 启动 IntelliJ IDEA。
2. 在欢迎页点击 **Open**；如果已经打开了别的项目，点击 **File → Open**。
3. 选择刚解压或克隆出来的 `prime-numbers-rest-service` 文件夹，点击 **OK**。
4. 如果 IDEA 问是否信任项目，选择 **Trust Project**。
5. 如果 IDEA 问是否以 Maven 项目打开，选择 **Open as Project** 或 **Load Maven Project**。
6. 等待右下角的加载进度结束。第一次可能需要等一会儿，因为 Maven 要下载一些构建文件。

项目里最重要的文件是 `pom.xml`。它告诉 Maven 怎样编译项目。Java 源代码在 `src/main/java/edu/example/primes/` 文件夹里。

## 四、先编译一次

1. 在 IDEA 右侧找到 **Maven** 工具窗口。如果看不到，点击 **View → Tool Windows → Maven**。
2. 展开项目名称，再展开 **Lifecycle**。
3. 双击 **clean**，等它运行结束。
4. 再双击 **package**，等它运行结束。
5. 如果底部显示 `BUILD SUCCESS`，说明编译成功。

如果看到红色报错，先不要改代码。把报错截图发给我，我会陪你一起排查。若报错提到下载失败，通常是 Maven 软件源或网络问题；这和你有没有 IDEA 不一样，也不一定是代码错了。

## 五、启动服务器

1. 在左侧项目文件区，依次展开 `src → main → java → edu.example.primes`。
2. 找到 `PrimeNumbersServer.java`。
3. 打开文件，找到 `main` 左边的绿色三角形，点击它。
4. 选择 **Run 'PrimeNumbersServer.main()'**。
5. 下方运行窗口出现类似 `Prime numbers REST service listening at http://localhost:8080/...` 的文字，就表示服务器启动了。
6. **先不要关闭这个运行窗口**。服务器需要一直开着，客户端才能连接它。

## 六、启动客户端并输入数字

1. 回到左侧项目文件区，打开 `PrimeNumbersClient.java`。
2. 点击 `main` 左边的绿色三角形，选择 **Run 'PrimeNumbersClient.main()'**。
3. IDEA 会打开另一个运行窗口。点击窗口底部的输入区域，输入 `17`，按回车。
4. 第一次输入 `17`，客户端会说服务器还没有记录，然后自行判断并保存结果。
5. 再输入一次 `17`，客户端应显示这是服务器里已有的记录。
6. 试试 `18`、`1`、`2` 和 `-7`，看看不同数字的结果。
7. 输入 `quit` 并回车，可以结束客户端。服务器窗口可点击红色停止按钮关闭。

如果 IDEA 没有让你在运行窗口里输入内容，点窗口后再试。仍然不行就把画面截图发给我。

## 七、建议检查的情况

- `17`：第一次计算并保存；第二次从服务器读取。
- `18`：不是素数，也应保存下来。
- `1` 和 `-7`：都不是素数。
- `2`：是素数。
- 服务器停止后重新启动，再查询 `17`：仍能读到之前结果，说明文件保存有效。

服务器的数据文件会在运行时自动创建：`data/numbers.txt`。它已经加入 Git 忽略列表，不会把你电脑里的运行记录提交到仓库。

## 八、提交作业时的截图

建议准备三张截图：

1. IDEA 中服务器启动成功的窗口。
2. 客户端第一次查询 `17`，显示本地计算并保存。
3. 客户端第二次查询 `17`，显示使用服务器已有记录。

简短中英报告在 [REPORT_EN_ZH.md](REPORT_EN_ZH.md)。

## REST 接口（了解即可）

- `GET /api/numbers/17`：向服务器询问 17 是否已经记录。
- `PUT /api/numbers/17`：把客户端算出的结果交给服务器保存，内容形如 `{"prime":true}`。
- 如果服务器没见过这个数字，会返回 `404`，客户端就知道要自己计算。

本项目使用 Java 自带的 HTTP 服务端和客户端，所以不需要另外购买 API 额度，也不需要 OpenAI API 密钥。

