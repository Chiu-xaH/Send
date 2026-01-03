# 计算机网络课程设计
## 选题
局域网传输（题号2）
## 组员：
1. 2023***529 界面与交互设计、项目的骨架搭建、文本和文件的收发功能、发现设备功能
2. 2023***155 待定
3. 2023***601 待定

## 需求
局域网内发送、接收文本或文件

## 设计参考
以 LocalSend 的功能为原型设计，未参考其源代码（只是一个课设，只需要实现基本功能即可）

## 技术调研

### 网络层
- 设备查找：UDP
- 数据传输：TCP

### 界面层
- 跨平台适配：Compose Multiplatform

### 逻辑层
- 跨平台适配：Kotlin Multiplatform
- 跨设备传输：Java(java.net)，以 Json 为载体

## 总代码行数
2426行（不算空白行）

## 项目目录简介
![img.png](src/img.png)

## 编译运行
1. [安装 Android Studio](https://developer.android.com/studio)
2. 用 Android Studio 打开此项目并等待构建完成（构建时必须使用 Clash 的 Tun 模式）
3. 以 Debug 环境运行
- 在计算机上运行
```bash
./gradlew composeApp:run
```
- 在 Android 设备上运行：选择 composeApp 上面的运行图标

![img.png](src/run.png)


## 后续完善
1. 发送提速 
2. 安卓端低版本权限适配 
3. 安卓端选择文件的适配 
4. 完成后支持打开文件 
5. 预估时间、速度显示