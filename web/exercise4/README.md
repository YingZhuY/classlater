# 阿里云安全恶意程序检测

## 实验要求

1.	比赛网址： [https://tianchi.aliyun.com/competition/entrance/231694/information](https://tianchi.aliyun.com/competition/entrance/231694/information)
2.	赛题背景：恶意软件是一种被设计用来对目标计算机造成破坏或者占用目标计算机资源的软件，传统的恶意软件包括蠕虫、木马等，这些恶意软件严重侵犯用户合法权益，甚至将为用户及他人带来巨大的经济或其他形式的利益损失。近年来随着虚拟货币进入大众视野，挖矿类的恶意程序也开始大量涌现，黑客通过入侵恶意挖矿程序获取巨额收益。当前恶意软件的检测技术主要有特征码检测、行为检测和启发式检测等，配合使用机器学习可以在一定程度上提高泛化能力，提升恶意样本的识别率。
3.	赛题说明：数据来自 Windows 可执行程序经过沙箱程序模拟运行后的 API 指令序列，经过脱敏处理，其中恶意文件的类型有感染型病毒、木马程序、挖矿程序、DDOS 木马、勒索病毒等，数据总计 6 亿条。
4.  具体见比赛网址。

## 实验环境

Window10，python3，numpy，pandas，sklearn，contextlib，xgboost

## 解决方案学习

第一次处理这样的实验，先看看燕姐分享的几个解决方案学习一下，这里看的是 [第三届阿里云安全算法挑战赛_i_hate_mcdonalds团队_亚军解决方案](https://tianchi.aliyun.com/forum/postDetail?spm=5176.12586969.1002.3.57e77a2a3AJuuf&postId=33296)。

用户层应用程序通过调用操作系统 API 执行应用程序的命令，可以实现网络连接，文件读写，数据库读写等操作，因此，API 调用序列具有一定的代表性，在判定恶意软件中起到重要作用。模型需要根据文件经过沙箱程序模拟运行后的 API 指令序列，判断该文件是否为恶意文件，是的话并指出是哪类恶意文件。由此可以清楚地划分为两个阶段：二分类 -> 是否为恶意文件；多分类 -> 识别恶意文件类型。检测流程如下图。

![](https://github.com/Yudreamy/classlater/blob/master/web/exercise4/picture/model.PNG)

![](https://github.com/Yudreamy/classlater/blob/master/web/exercise4/picture/feature.PNG)

该团队将 TF-IDF 特征输入进 Naive Bayesian + Logistic Regression（NBLR）来预测样本是否为恶意文件，将其概率作为新特征。将基础统计特征 + NBLR 概率特征共同输入到 xgboost(XGB)或LightGBM(LGB)，训练模型。最后的结果由多模型加权融合得到，权重参数根据线下交叉验证获得。GitHub 上代码 [https://github.com/DeanNg/3rd_security_competition](https://github.com/DeanNg/3rd_security_competition)，准备下载代码下来跑跑试试的，但是它没有提供当时的数据集，运行不起来。

## test

课程实验指定的网站是 [阿里云安全恶意程序检测](https://tianchi.aliyun.com/competition/entrance/231694/information)。在上边代码的基础上改了一些数据处理，然后把 v3 部分的基于返回值进行分析的部分全部去掉，最后结果如下。

![](https://github.com/Yudreamy/classlater/blob/master/web/exercise4/picture/result1.PNG)

![](https://github.com/Yudreamy/classlater/blob/master/web/exercise4/picture/result2.PNG)

![](https://github.com/Yudreamy/classlater/blob/master/web/exercise4/picture/result3.PNG)

# Reference：

- [x] [第三届阿里云安全算法挑战赛_i_hate_mcdonalds团队_亚军解决方案](https://tianchi.aliyun.com/forum/postDetail?spm=5176.12586969.1002.3.57e77a2a3AJuuf&postId=33296)
- [x] [GitHub 上代码](https://github.com/DeanNg/3rd_security_competition)