# 计算机网络课程设计
## 选题
局域网传输（题号2）
## 组员：
1. 2023***529
2. 2023***155 
3. 2023***601

界面与交互设计、项目的骨架搭建、文本和文件的收发功能、发现设备功能

## 需求
局域网内发送、接收文本或文件

## 设计参考
以 LocalSend 的功能为原型设计，未参考其源代码

## 技术调研

### 网络层
- 设备查找：UDP
- 数据传输：TCP

### 界面层
- 跨平台适配：Compose Multiplatform

### 逻辑层
- 跨平台适配：Kotlin Multiplatform
- 跨设备传输：Java(java.net)，以 Json 为载体

## 项目目录简介
![img.png](src/img.png)

## 编译运行
1. [安装 Android Studio](https://developer.android.com/studio)
2. 用 Android Studio 打开此项目并等待构建完成（构建时必须使用 Clash 的 Tun 模式）
3. 以 Debug 环境运行
- 在 PC 上运行
```bash
./gradlew composeApp:run
```
- 在 Android 设备上运行：选择 composeApp 上面的运行图标

![img.png](src/run.png)

4. 打包

- 打包 PC 版

```bash
./gradlew composeApp:packageDistributionForCurrentOS 
```

- 打包 Android Debug 版

![img.png](src/debug.png)

## 后续完善（优先级高到低排序）
1. UI层：PC 端支持拖拽文件
2. 整体：多文件、文件夹的发送（文件夹借助多文件发送的方法去实现）
3. 整体：多接收者并行发送
4. 逻辑层：先行发送文件元数据，接收方可选是否接收文件（目前是元数据和文件一起发送，微调代码应该就可以实现）
5. 逻辑层：目前的中止传输方式是强制关闭连接，可以优化改造成更优雅的方式告知对方关闭连接
6. UI层：引入全局转场动画体系
7. 整体：支持Android端分享、打开方式
8. 整体：软件自我更新