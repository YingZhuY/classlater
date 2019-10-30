# BOMB！

## 小注意点

* GDB：GNU项目调试器（The GNU Project Debugger）
* 汇编指令的操作后缀："b"(byte，8位)、"s"(single，32位浮点数)、"w"(word，16位)、"l"(long，32位整型或64位浮点型)、"q"(quad，64位)、"t"(ten bytes，80位浮点型)
* 前缀：寄存器以"%"为前缀，常数以"$"为前缀
* 推荐一个图形界面反汇编工具 `IDA`，我最后一个 phase_6 的整个循环跳转过程是结合 IDA 的 `Graph view` 分析的，有流程图的话思路就很清晰
* [在线进制转换器](https://tool.oschina.net/hexconvert) may help~
* [推荐阅读](http://heather.cs.ucdavis.edu/~matloff/UnixAndC/CLanguage/Debug.html)
* [推荐参考](https://blog.csdn.net/shiyuqing1207/article/details/45875051)
* p.s. 本实验分析过程对应 bombs.rar 压缩包的 No.36

## bomb36 拆弹过程记录

vim 进入 bomb.c 查看程序逻辑

    The PERPETRATOR takes no responsibility for damage, frustration, insanity, bug-eyes, carpal-tunnel syndrome, loss of sleep, or other harm to the VICTIM. ~~~Aha, sense of humor. :joy: (发现 GitHub markdown 还有[小表情](https://www.webfx.com/tools/emoji-cheat-sheet/))
    无参运行时 bomb 会从 stdin 命令行读取，此外，输入参数可为文件，换行隔开。
    bomb 初始化后通过 6 个 phase_i 函数检验输入行是否合法，模拟 6 个拆弹过程

### phase_1 

`gdb bomb` 进入调试环境，先用 `disassemble phase_1` 看下 phase\_1 的内部结构，注意到在 call <strings\_not\_equal> 进行 test 等于判断后会触发 <explode\_bomb> ，然后初步知道了应该设置断点的几个位置。

![](https://github.com/Yudreamy/classlater/blob/master/csapp/datalab2/picture/phase_1.PNG)

下一步设置断点，以推断 call <explode\_bomb> 的条件，依次设置`b phase_1`, `b phase_2`, `b strings_not_equal`, `b explode_bomb`，然后直接 run。通过上图的分析可知，+6~+17 为 phase\_1 导入输入文本及目标答案过程，则可依次试探该部分寄存器内容，得到思路。

![](https://github.com/Yudreamy/classlater/blob/master/csapp/datalab2/picture/phase_1r.PNG)

![](https://github.com/Yudreamy/classlater/blob/master/csapp/datalab2/picture/phase_1try.PNG)

### phase_2

phase\_2 通过试探寄存器内容得不到有效信息，看到 <read\_six\_numbers> 进入查看其代码逻辑，设 0xc(%ebp) 内容为 x ，则 %eax=x+0x14，%edx=x+0x10，%ecx=x+0xc， %ebx=x+0x8， %esi=x+0x4，然后依次放进 0x1c(%esp)（第一个数），0x18(%esp)（第二个数），0x14(%esp)（第三个数），0x10(%esp)（第四个数），0xc(%esp)（第五个数）中，%eax再次设为 x，放进 0x8(%esp)（第六个数），常数 $0x8049d25 放进 0x4(%esp)，0x8(%ebp) 放进 %eax，赋给 (%esp)。此处完成28字节（0x1c）的初始化。\<sscanf@plt> 操作后比较 %eax 和常数 0x5，小于等于的话会引爆炸弹。然后根据 phase\_2 的反汇编码进行逐行分析，具体分析过程见下图。
```
(gdb) x/s 0x8049d25 # 输入格式，<read_six_numbers>中
0x8049d25:      "%d %d %d %d %d %d"
```
![](https://github.com/Yudreamy/classlater/blob/master/csapp/datalab2/picture/phase_2.PNG)

### phase_3

跟上题一样，观察到 \<sscanf@plt> 处，可以查看相应位置的输入格式要求
```
(gdb) x/s 0x8049a27
0x8049a27:      "%d %c %d"
```
继续查看反汇编代码观察特征，注意到第一处 <explode\_bomb> 的触发条件为 -0x8(%ebp)>2 不成立，联系上题，进而更加确定输入元素不足三个时炸弹爆炸的猜想。发现代码都是 mov-mov-判断-jump 的结构，最后都是得到并比较 %eax 的值，属于 switch-case 分支结构，部分关键点分析如下。
```
   0x08048c42 <+70>:    mov    %eax,-0x24(%ebp)
   0x08048c45 <+73>:    cmpl   $0x7,-0x24(%ebp)
   # 要求第一个数小于等于 7，之后可以依次输入不大于 7 的数，然后直接读取 %eax 最后的值即可
   0x08048c49 <+77>:    ja     0x8048d11 <phase_3+277>
   
   0x08048c4f <+83>:    mov    -0x24(%ebp),%edx
   0x08048c52 <+86>:    mov    0x8049a30(,%edx,4),%eax  # 对应跳转表内容
   0x08048c59 <+93>:    jmp    *%eax

(gdb) x/20xw 0x8049a30  # 打印跳转表
0x8049a30:      0x08048c5b      0x08048c77      0x08048c93      0x08048ca8
0x8049a40:      0x08048cbd      0x08048cd2      0x08048ce7      0x08048cfc
0x8049a50:      0x00006425      0x21776f57      0x756f5920      0x20657627
0x8049a60:      0x75666564      0x20646573      0x20656874      0x72636573
0x8049a70:      0x73207465      0x65676174      0x00000021      0x73736162

# 比如输入的第一个数为1
(gdb) i r
eax            0x804a900           134523136
(gdb) x/s 0x804a900     # 即为我们的输入
0x804a900 <input_strings+160>:  "1 7 8"
(gdb) p/x *(int *)(0x8049a30+4)
$4 = 0x8048c77
# 输入的是第一个数是1，所以跳转地址为（0x8049a30+4）处存的地址，跳转到该地址处查看

   0x08048c7b <+127>:   mov    -0x10(%ebp),%eax #第一个数存储在
   0x08048c7e <+130>:   cmp    $0xbb,%eax
   0x08048c83 <+135>:   je     0x8048d1a <phase_3+286>
   0x08048c89 <+141>:   call   0x80496fa <explode_bomb>

# %eax 部分即为我们的输入数，%eax 最后的比较值为 0xbb，即第二个目标数值为 187
# 接着会跳转到 0x8048d1a <phase_3+286> 部分，加断点`b *0x08048d1e`，继续查看汇编码

   0x08048d1a <+286>:   movzbl -0x11(%ebp),%eax     # 载入我们的输入字符
   0x08048d1e <+290>:   cmp    %al,-0x1(%ebp)       # 低八位 %al，与目标位置比较
   0x08048d21 <+293>:   je     0x8048d28 <phase_3+300>
   0x08048d23 <+295>:   call   0x80496fa <explode_bomb>
   0x08048d28 <+300>:   leave

# 注意到 <+290> 处的条件判断 %al,-0x1(%ebp) 是否相等，分别查看 %al 与 -0x1(%ebp) 的值，即可得到此时的目标字符为 i
(gdb) print $al
$9 = 55 # 字符 '7' 的ASCII（0x37）对应的十进制数
(gdb) x ($ebp-1)
0xffffd207:     "i8\322\377\377\377\212\004\b"  # 找到目标字符 i
# for example，另一种显示方法
# 0xffffd207:     0xffd23875    此时取最低字节即 0x75 ,对应 ASCII u
```
以上为输入的是第一个数是 1 时的整个分析过程，我们可以使用同样的方法对其它情况进行分析，得到的结果如下：
```
# 给出所有合法输入
0-0x08048c5b-0x14c-332-c => 0 c 332
1-0x08048c77-0xbb -187-i => 1 i 187
2-0x08048c93-0x98 -152-m => 2 m 152
3-0x08048ca8-0x1e9-489-j => 3 j 489
4-0x08048cbd-0xfa -250-f => 4 f 250
5-0x08048cd2-0x389-905-s => 5 s 905
6-0x08048ce7-0x21d-541-e => 6 e 541
7-0x08048cfc-0x2b6-694-u => 7 u 694
```

### phase_4

根据上边的思路，对 phase\_4 的分析如下图

![](https://github.com/Yudreamy/classlater/blob/master/csapp/datalab2/picture/phase_4.PNG)

可以看出，phase\_4 在对输入进行简单的判断后，传入 \<func4> 进行处理，再将返回值与 0x1055 比较，相等即通过。所以重点是对函数 \<func4> 过程的理解。进入\<func4>查看，从函数调用本身可以看出这是一个递归函数。对 \<func4> 逐步分析如下图，即可得到 phase\_4 的答案 18。

![](https://github.com/Yudreamy/classlater/blob/master/csapp/datalab2/picture/func4.PNG)

### phase_5

查看 phase\_5 部分的反汇编代码

![](https://github.com/Yudreamy/classlater/blob/master/csapp/datalab2/picture/phase_5.PNG)

由上图可知，循环对 i 依次累和，直到 i==52 时退出；循环进行 6 次，每次将 j 位置对应的数组元素追加给 i 。我们可以推知循环的功能即，按给定的 6 个输入的后四位作为索引，查找对应数组元素，累和等于 52 即通过验证。显示一下数组元素

```
(gdb) x/16xw 0x804a5c0      # 打印数组元素
0x804a5c0 <array.2511>: 0x00000002      0x0000000a      0x00000006      0x00000001
0x804a5d0 <array.2511+16>:      0x0000000c      0x00000010      0x00000009      0x00000003
0x804a5e0 <array.2511+32>:      0x00000004      0x00000007      0x0000000e      0x00000005
0x804a5f0 <array.2511+48>:      0x0000000b      0x00000008      0x0000000f      0x0000000d
```
也可以进一步验证我们分析的代逻辑是否正确，比如我们随意输入 567890，设置断点 `b *0x08048e02`，转到断点处单步运行查看，发现 %eax 此时就是我们的第一个输入 '5'对应索引的数组值 0x00000010

![](https://github.com/Yudreamy/classlater/blob/master/csapp/datalab2/picture/phase_5try.PNG)

```
# 之后也可以依次单步观察循环过程
Breakpoint 3, 0x08048e02 in phase_5 ()
(gdb) i r
eax            0x9                 9

Breakpoint 3, 0x08048e02 in phase_5 ()
(gdb) i r
eax            0x3                 3

Breakpoint 3, 0x08048e02 in phase_5 ()
(gdb) i r
eax            0x4                 4

Breakpoint 3, 0x08048e02 in phase_5 ()
(gdb) i r
eax            0x7                 7

Breakpoint 3, 0x08048e02 in phase_5 ()
(gdb) i r
eax            0x2                 2
```

最后考虑整体结果：找到一个组合 0、1、4、5、6、7（ $\because$ 0x2+0xa+0xc+0x10+0x9+0c3=0x34=52），即 014567、714506、541067、@!$%&'、PADEFG、pqtufw......均可通过验证；当然可以考虑其他组合（元素索引亦可重用），很多，这里不一一列出了。

### phase_6

进入最后一个环节啦！

phase_6 的反汇编代码太长，慢慢的一部分一部分分析
```
   0x08048e35 <+26>:    call   0x80490a0 <read_six_numbers> # 读入 6 个数
   # -0x8(%ebp) 置为 0
   0x08048e8b <+112>:   cmpl   $0x5,-0x8(%ebp)  # 判断 -0x8(%ebp) 与 5 的大小
   0x08048e43 <+40>:    mov    -0x8(%ebp),%eax  # <= 时进入
   0x08048e46 <+43>:    mov    -0x28(%ebp,%eax,4),%eax  #取数组对应元素
   0x08048e4a <+47>:    test   %eax,%eax
   0x08048e4c <+49>:    jle    0x8048e5a <phase_6+63>   # <=0 时爆炸
   
   0x08048e55 <+58>:    cmp    $0x6,%eax        # >0 时取数组对应元素与 6 比较
   0x08048e58 <+61>:    jle    0x8048e5f <phase_6+68>
   0x08048e5a <+63>:    call   0x80496fa <explode_bomb> # >6 时引爆炸弹（说明0<对应数组元素<=6）
   
   0x08048e5f <+68>:    mov    -0x8(%ebp),%eax
   0x08048e62 <+71>:    inc    %eax             # %eax++
   0x08048e63 <+72>:    mov    %eax,-0x4(%ebp)  # 进入下个元素 -0x4(%ebp)
   # 注意此处 %eax++ 后赋给 -0x4(%ebp)，通过 -0x4(%ebp) 即可遍历数组后边的元素
   0x08048e82 <+103>:   cmpl   $0x5,-0x4(%ebp)  # 比较此元素与 5 大小
   # 此处可知循环进行5次
   
   0x08048e68 <+77>:    mov    -0x8(%ebp),%eax
   0x08048e6b <+80>:    mov    -0x28(%ebp,%eax,4),%edx
   0x08048e6f <+84>:    mov    -0x4(%ebp),%eax
   0x08048e72 <+87>:    mov    -0x28(%ebp,%eax,4),%eax
   0x08048e76 <+91>:    cmp    %eax,%edx        # 比较上述两个数组元素的大小
   0x08048e78 <+93>:    jne    0x8048e7f <phase_6+100>      # 回到 <+103> 循环
   0x08048e7a <+95>:    call   0x80496fa <explode_bomb>     # 相等时引爆炸弹
```
此处先停一停总结一下，以上部分得到的信息是，目标数组 -0x28(%ebp,%eax,4) 元素要求 (0,6]；通过循环比较第一个数与数组后边元素，循环进行 5 次，要求后边 5 个数和第一个数不相等，否则爆炸。
```
   # 循环 5 次比较完后
   0x08048e88 <+109>:   incl   -0x8(%ebp)       # -0x8(%ebp)++
   0x08048e8b <+112>:   cmpl   $0x5,-0x8(%ebp)
   0x08048e8f <+116>:   jle    0x8048e43 <phase_6+40>
   # 再次进入 <+112> 处的外层循环，完成此时 -0x8(%ebp) 与数组后边元素不相等的判断
   # 外层循环目的：控制数组中内容两两不同

   # 跳出外层循环，此时 -0x8(%ebp)>5
   0x08048e91 <+118>:   movl   $0x0,-0x8(%ebp)      # 再次赋值为 0
   0x08048e98 <+125>:   jmp    0x8048ece <phase_6+179>
   
   0x08048ece <+179>:   cmpl   $0x5,-0x8(%ebp)
   0x08048ed2 <+183>:   jle    0x8048e9a <phase_6+127>  # -0x8(%ebp)<=5 时
   
   0x08048e9a <+127>:   mov    -0x10(%ebp),%eax
   0x08048e9d <+130>:   mov    %eax,-0xc(%ebp)      # 将 -0x10(%ebp) 赋给 -0xc(%ebp)
   0x08048ea0 <+133>:   movl   $0x1,-0x4(%ebp)
   0x08048eb5 <+154>:   mov    -0x8(%ebp),%eax
   0x08048eb8 <+157>:   mov    -0x28(%ebp,%eax,4),%eax  # 还是那个数组
   0x08048ebc <+161>:   cmp    -0x4(%ebp),%eax      # 比较数组元素和 1
   0x08048ebf <+164>:   jg     0x8048ea9 <phase_6+142>
   # 大于 1 时
   0x08048ea9 <+142>:   mov    -0xc(%ebp),%eax
   0x08048eac <+145>:   mov    0x8(%eax),%eax       # %eax=%eax+0x8
   0x08048eaf <+148>:   mov    %eax,-0xc(%ebp)      # 存回-0xc(%ebp)
   0x08048eb2 <+151>:   incl   -0x4(%ebp)           # 循环计数++
   0x08048eb5 <+154>:   mov    -0x8(%ebp),%eax
   0x08048eb8 <+157>:   mov    -0x28(%ebp,%eax,4),%eax  # %eax记录数组 -0x8(%ebp) 处值
   0x08048ebc <+161>:   cmp    -0x4(%ebp),%eax          # 值大于循环计数时
   0x08048ebf <+164>:   jg     0x8048ea9 <phase_6+142>  # 继续该循环

   # <=1 时
   0x08048ec1 <+166>:   mov    -0x8(%ebp),%edx
   0x08048ec4 <+169>:   mov    -0xc(%ebp),%eax
   0x08048ec7 <+172>:   mov    %eax,-0x40(%ebp,%edx,4)  # 将 %eax 的值存入一个新的数组
   0x08048ecb <+176>:   incl   -0x8(%ebp)
   0x08048ece <+179>:   cmpl   $0x5,-0x8(%ebp)      # 比较循环因子 -0x8(%ebp)<=5，以上过程循环 5 次
   0x08048ed2 <+183>:   jle    0x8048e9a <phase_6+127>
   0x08048ed4 <+185>:   mov    -0x40(%ebp),%eax     # 完成一系列赋值
   0x08048ed7 <+188>:   mov    %eax,-0x10(%ebp)
   0x08048eda <+191>:   mov    -0x10(%ebp),%eax
   0x08048edd <+194>:   mov    %eax,-0xc(%ebp)
   0x08048ee0 <+197>:   movl   $0x1,-0x8(%ebp)
   0x08048ee7 <+204>:   jmp    0x8048f02 <phase_6+231>

   # 之后的部分跳转较多，就不往上贴了，关键点在于 <+275> 处的比较
   0x08048f2e <+275>:   cmp    %eax,%edx
   0x08048f30 <+277>:   jge    0x8048f37 <phase_6+284>
   0x08048f32 <+279>:   call   0x80496fa <explode_bomb>
   # %edx>=%eax 时程序继续跳转，否则引爆炸弹。知此新数组整体降序
   
   # 代码的最后部分知，循环计数 -0x8(%ebp)>4 时退出，phase_6 解决
   0x08048f40 <+293>:   incl   -0x8(%ebp)
   0x08048f43 <+296>:   cmpl   $0x4,-0x8(%ebp)
   0x08048f47 <+300>:   jle    0x8048f21 <phase_6+262>
   0x08048f49 <+302>:   leave
   0x08048f4a <+303>:   ret

```
再用 GDB 试探分析
```
Good work!  On to the next...       # 输入
1 4 3 5 2 6
(gdb) i r
eax            0x6                 6
ebp            0xffffd1f8          0xffffd1f8
(gdb) x/s 0xffffd1d0                # -0x28(%ebp)处对应我们输入的第一个数
0xffffd1d0:     "\001"
(gdb) x/s 0xffffd1d4                # -0x24(%ebp)处对应第二个，依次类推
0xffffd1d4:     "\004"
(gdb) x/x 0x804a63c                 # 查看汇编代码中最开始处出现的一个立即数的内容
0x804a63c <node1>:      0x0000037d
```
由此可知第一个数组，-0x28(%ebp,%eax,4)中，存储的是我们输入的 6 个数。此外，注意到 0x804a63c 后带有修饰 <node1>，上网查找资料，联系到结构体，所以可以得到结论，这是一个典型的链表结构。结合上边分析可知，我们输入的 6 个数指向 6 个 node 节点数顺序，以满足新数组整体降序的要求。
```
# node 节点的结构
struct node{
    int data;
    int index;
    node* next;
}
# 我们知 <node1> 的地址是立即数 0x804a63c，可依据 node 结构按图索骥地把剩下的套出来
(gdb) x/x 0x804a63c             # node1 data
0x804a63c <node1>:      0x0000037d
(gdb) x/x (0x804a63c+4)         # node1 index
0x804a640 <node1+4>:    0x00000001
(gdb) x/x (0x804a63c+8)         # node2 address
0x804a644 <node1+8>:    0x0804a630
(gdb) x/x 0x0804a630            # node2 data
0x804a630 <node2>:      0x00000113
(gdb) x/x (0x0804a630+8)        # node3 address
0x804a638 <node2+8>:    0x0804a624
(gdb) x/x 0x0804a624            # node3 data
0x804a624 <node3>:      0x0000028d
(gdb) x/x (0x0804a624+8)
0x804a62c <node3+8>:    0x00000000
(gdb) x/x 0x00000000
0x0:    Cannot access memory at address 0x0
# 至此线索断了，发现 node3 往下 0x00000000 处地址内容不可读
# 观察前边得到的节点地址，0x804a63c <node1>、0x804a630 <node2>、0x804a624 <node3>
# 发现相邻节点的地址数差 12，根据这个特征往下试探 0x804a618、0x804a60c、0x804a600
(gdb) x/x 0x804a618
0x804a618 <node4>:      0x0000015b
(gdb) x/x 0x804a60c
0x804a60c <node5>:      0x00000292
(gdb) x/x 0x804a600
0x804a600 <node6>:      0x000000ef
```
至此可得 node 链所有值：0x37d（node1）、0x113（node2）、0x28d（node3）、0x15b（node4）、0x292（node5）、0xef（node6），降序排序后为 1 5 3 4 2 6，即为 phase_6答案。整个验证过程如下:

![](https://github.com/Yudreamy/classlater/blob/master/csapp/datalab2/picture/result.PNG)

还有最后的隐藏关卡-the secret phase，这里不列出解决过程，具体可参考最上边给出的 [推荐参考](https://blog.csdn.net/shiyuqing1207/article/details/45875051)，最后结果如下

![](https://github.com/Yudreamy/classlater/blob/master/csapp/datalab2/picture/secret_try.PNG)

![](https://github.com/Yudreamy/classlater/blob/master/csapp/datalab2/picture/secret.PNG)

### 总结
- [x] phase\_1 设断点和查看寄存器内容
- [x] phase\_2 循环
- [x] phase\_3 switch-case，跳转表
- [x] phase\_4 递归
- [x] phase\_5 数组
- [x] phase\_6 循环嵌套和结构体
- [x] the secret phase 递归和二叉查找树