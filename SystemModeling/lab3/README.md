# lab3

环境：Win10，uppaal-4.1.19，java-11.0.2

## 目标

理解位置不变量 invariants，约束 guards 和验证查询语言 query language。

## 实验过程

首先按照 PPT 第三页的两张图构建模型和相关 Observer、Test 两个模板。Test 中是一个 loop 自循环，控制在满足 x>=2 条件时发出 reset！ 同步信号，x是一个时钟；Observer 中初始位置 idle 接收 reset 信号转换到下一个 location -taken，然后完成置时钟为 0 回到 idle。

### 理解

invariant 意为不变的限定条件，是满足特定条件的表达式，为布尔值，仅引用时钟、整数变量和常量，作用是在这个 location 里，如果不满足这个条件，系统就会陷入 timelock。<br />

guard 为守卫条件，同样是满足特定条件的表达式，为布尔值，仅引用时钟、整数变量和常量（或这些类型的数组），如果不满足这个条件，transition 就不会运行。<br />

query language 主要目的是验证模型及需求规范，将需求规范以一种形式明确且机器可读的语言来表达，由路径公式（见下图）和状态公式（比如 i==7 或者 deadlock 死锁状态）组成。

![](https://github.com/Yudreamy/classlater/blob/master/SystemModeling/lab3/picture/path.PNG)

```
A[] p：对于每个执行路径 p 保留路径的所有状态
A<> p：对于所有的单个执行路径，p 保持该路径的至少一个状态
E<> p：有一个执行路径 p，其中 p 的最终状态保持不变
E[] p：有一条执行路径 p，其中 p 包含该路径的所有状态
q–> p：任何以 q 保持的状态开始的路径后来都会到达 p 保持的状态
```

对于此题中的验证查询语言，`A[] Observer.taken imply x>=2` 当 x>=2 时将发生 x 的重置，此查询意味着对所有的可达状态，位于 Observer.taken 时有 x>=2。`E<> Observer.idle and x>3` 此属性要求可能达到 Observer.idle 位置并且 x>3，也就是说，此属性检查是否能在两次 reset 间至少延迟 3 个时间单位。由于没有 invariant 控制不变的限定条件，即 x∈[2,+∞)，将此处的 3 改成 30000 结果也是一样的，两条件都可满足。模型模拟图及验证结果图如下。

![](https://github.com/Yudreamy/classlater/blob/master/SystemModeling/lab3/picture/moni1.PNG)

![](https://github.com/Yudreamy/classlater/blob/master/SystemModeling/lab3/picture/result1.PNG)

接下来，我们修改第一个模型，给 location loop 添加不变量 x<=3，即系统不允许停留在超过 3 个时间单位的状态，所以，taken 转移及时钟重置有个上界 3。下一步，修改此时的验证查询语言，上边的 `A[] Observer.taken imply x>=2` 和 `E<> Observer.idle and x>3` 这两个解释过了就先不说了，`E<> Observer.idle and x>2` 表明 x>2 有可能发生转换，`A[] Observer.taken imply (x>=2 and x<=3)` 表示转换的时间延迟都在 2 到 3 之间，`A[] Observer.idle imply x<=3` 限制上限为 3。此时，条件 `E<> Observer.idle and x>3` 不被满足，模型模拟图及验证结果图如下。

![](https://github.com/Yudreamy/classlater/blob/master/SystemModeling/lab3/picture/moni2.PNG)

![](https://github.com/Yudreamy/classlater/blob/master/SystemModeling/lab3/picture/result2.PNG)

现在，如果删除不变量 x<=3 而把守卫条件改为 x>=2 && x<=3，结果会发生变化吗？运行结果发现，系统可能会执行与以前相同的转换，但可能发生死锁（条件 x<=3 不一定成立，在 3 个时间单位后不进行转换系统就会卡住）。关键点在于 loop 中 x<=3 是 guards 守卫条件，该约束不一定满足，当其不被满足时，transition 就不会运行，就进入死锁状态。模型模拟图及验证结果图如下。

![](https://github.com/Yudreamy/classlater/blob/master/SystemModeling/lab3/picture/moni3.PNG)

![](https://github.com/Yudreamy/classlater/blob/master/SystemModeling/lab3/picture/result3.PNG)
