之前用过 Ubuntu 和 Kali，都是在 VMware 中用下载的 *.iso镜像安装的虚拟机。发现虚拟机会不定时出现网络之类的错误，后来是因为在虚拟机上弄 Anaconda，Tensorflow 什么的跑 Tensorflow 程序，经常出现一些解决不了的环境 error，然后经常反复卸载重装，弄得很烦，占用内存又多，就全删了。

这次实验要用 Linux，其实不太想去重装虚拟机，就想着找即用的在线Linux虚拟机（找到的 Chrome Linux虚拟机插件很不好用，开不了机还很慢），然后就发现了[JSLinux](https://bellard.org/jslinux/)，一个用JavaScript编写的PC/x86模拟器，可以很方便地使用 Linux命令行，很好用，给出它的 [Technical Notes](https://bellard.org/jslinux/tech.html) 和 [Frequently Asked Questions](https://bellard.org/jslinux/faq.html)，具体使用[参考网站](https://www.ostechnix.com/run-linux-operating-systems-browser/)

JSLinux上传输文件很方便，make也很方便，甚至不用提前装 GCC，哈哈哈，like this

![](./JSLinux.png)

Learn to enjoy coding ~