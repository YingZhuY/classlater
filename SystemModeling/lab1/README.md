# ATM-Uppaal

环境：Win10，uppaal-4.1.19，java-11.0.2

## uppaal 安装

uppaal 的安装很简单，解压缩下载的 uppaal-4.1.19.zip，找到 `uppaal.jar` 的目录，命令行输入 `java -jar 文件全目录` 即可安装，我的是 `java -jar \UST\uppaal-4.1.19\uppaal.jar`，安装后它会自己打开。

## 使用方法

下一步就是导入文件，文件-打开模型-找到 atm.xml -打开；验证器-导入 atm.q；atm.xml 和 atm.q 都是已经提供了的。

![](https://github.com/Yudreamy/classlater/blob/master/SystemModeling/lab1/picture/open_ATM.PNG)

先熟悉一下工具中的各个文件，atm.xml 提供了 Eric, Bank, ATM 的模型：Eri 与 ATM 互动，从 ATM 中提取现金；ATM 又与银行通信，以确保银行正确跟踪 Eric 银行帐户的余额。所有信号如下，可以通过点击加号里边的声明查看。
```
// Channels between the customer Eric and the ATM 
chan bank_card,      
     request, // request for 10 euro by Eric to the ATM
     cash;

// Channels between the ATM and the bank 
chan ask_permission, // request to pay out 10 euro 
     OK, 
     not_OK;
```

![](https://github.com/Yudreamy/classlater/blob/master/SystemModeling/lab1/picture/process.PNG)

最初 Eric 口袋没有现金，自动取款机 ATM 的收银台中有200欧元，Eric的银行账户中有80元余额。Uppaal 使用状态图作为模型，除了描述我们要分析的系统图表外，我们还需要描述一些所需的属性，这些属性称为查询。注意到我们的 atm.q 文件中表示了系统的两个属性：即 Eric 始终有80元（该模型不包括他花钱的可能性），并且系统不应死锁。进入验证器验证这两个属性，可以看到结果——两个都不满足。

![](https://github.com/Yudreamy/classlater/blob/master/SystemModeling/lab1/picture/verify.PNG)

## 通过属性验证

接下来的工作是尝试修改文件使验证属性满足，先把它跑起来。根据提示设置 选项-诊断路径-最短，接下来可以让它随机模拟，类似于单步的过程，找出问题的源头，记得打开所有的 `+`。

![](https://github.com/Yudreamy/classlater/blob/master/SystemModeling/lab1/picture/inconsistent.PNG)

由上图可以看出 Eric 的 cash_in_pocket 和 Bank 的 balance 之间存在不一致的现象，很容易看到是 Bank 的 `balance := balance - 1` 出了问题，改成每次减 10。再看另一个属性，找到什么时候发生死锁。

![](https://github.com/Yudreamy/classlater/blob/master/SystemModeling/lab1/picture/deadlock.PNG)

可以看到，此时系统状态已经不发生变化了，整个卡住了（注意到左上角使能迁移出现 deadlock 状态）。看中间的状态栏，此时银行 balance 不足了，ATM不支付现金处 Eric 就会卡住，Bank 也会卡住。需要修改 Eric，加一个临时节点和一条到退卡的边，在此之前在项目声明中 Channels between the customer Eric and the ATM 中添加 `not_cash`。

![](https://github.com/Yudreamy/classlater/blob/master/SystemModeling/lab1/picture/not_cash.PNG)

结果如下

![](https://github.com/Yudreamy/classlater/blob/master/SystemModeling/lab1/picture/ok.PNG)


## ATM 的 相关属性

题目要求把 ATM 的 in_till 改为 30（位于编辑器 ATM 的声明中），运行时发现由于没有对 in_till 正负的控制，在 ATM 中没钱时程序还会继续，产生不合理的情况。

![](https://github.com/Yudreamy/classlater/blob/master/SystemModeling/lab1/picture/only30.PNG)

之后，我们修改 ATM 的模型，只需要加一条带 guard 守卫条件的边到退卡就行，当然这里应该也会用到我们上边创建的 not_cash 信号量，所以置于其前。同时 Bank 中也应该接收 not_cash2/cash2 信号控制余额减不减 10（注意把 `not_cash2` 及 `cash2` 加到 Channels between the ATM and the bank 声明中，not_cash 不能重复定义）。

![](https://github.com/Yudreamy/classlater/blob/master/SystemModeling/lab1/picture/guard.PNG)

验证器中添加属性 `A[] (ATM.READY) imply ATM.in_till >= 0`，然后重新验证修改后三个属性是否满足。验证成功！

![](https://github.com/Yudreamy/classlater/blob/master/SystemModeling/lab1/picture/result.PNG)


# Reference：

- [x] [ATM requirement](https://www.cs.ru.nl/E.Poll/teaching/Uppaal/)
