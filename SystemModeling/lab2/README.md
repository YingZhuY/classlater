# RobotArm-Uppaal

环境：Win10，uppaal-4.1.19，java-11.0.2

## 目标描述

本次的目标是设计 Telerobot Control (TC) 远程控制机器人系统的简单版本。TC 有两个主要部分：sensor glove 和 remote RobotArm，sensor glove 进行准确的测量并把传感器数据发送到 remote RobotArm，使其执行与 sensor glove 一样的动作。TC 的两个特点：常用于危险环境中（like 引爆炸弹/处理危险化学品）、要求迅速反应。

* Grip action component：收集传感器数据，发送 grip 命令给 Controler
* Controler component：触发动作（接收 grip 命令，Send 将控制指令发送到远程机器人；Update 收到 update 命令后更新显示
* remote Robot component：通过网络协议栈接收 Send 命令；Update 向 Controler 发回更新命令

给出一个远程机器人的控制过程，定义了其中的一个组件（controller），需要大家给出另外2个组件的自动机模型。详细要求如下：

![](https://github.com/Yudreamy/classlater/blob/master/SystemModeling/lab2/picture/require.PNG)

有了上次 lab1 基础操作的熟悉，这个就比较简单了，主要是注意时钟设置。

![](https://github.com/Yudreamy/classlater/blob/master/SystemModeling/lab2/picture/process.PNG)

![](https://github.com/Yudreamy/classlater/blob/master/SystemModeling/lab2/picture/result.PNG)


# Reference：

- [x] [Uppaal 官网](http://www.uppaal.org/)
- [x] [Uppaal 网页说明](https://www.it.uu.se/research/group/darts/uppaal/help.php?file=Introduction.shtml)
- [ ] [可以看看别人解决更复杂问题的处理过程](http://wonggwan.cn/2017/09/23/%E7%81%AB%E8%BD%A6%E8%BF%87%E6%A1%A5%E9%97%AE%E9%A2%98/)
