# HTTPS

环境：VMware-workstation-full-15.5.1，ubuntu-18.04.2-desktop-amd64，kali-linux-2019-4-vmware-amd64，Apache/2.4.29，OpenSSL1.1.1，MySQL5.7.28，PHP7.2.24

> 注意：安装 Kali 期间可以给用户设置一个密码，但如果用的是 live、i386、amd64、VMware 或 ARM 镜像是，默认账户 root，默认密码 toor。

## 安装
Ubuntu 上安装 Apache2
```
sudo apt-get install apache2
```

配置文件目录
```
@ubuntu:~$ cd /etc/apache2
@ubuntu:/etc/apache2$ tree -L 1         # apache2 配置目录
.
├── apache2.conf                        # 全局配置
├── conf-available                      # 一般配置文件
├── conf-enabled
├── envvars                             # 环境变量
├── magic
├── mods-available                      # 已安装模块
├── mods-enabled                        # 已启用模块
├── ports.conf                          # httpd 服务端口信息
├── sites-available                     # 可用站点信息
└── sites-enabled                       # 已启用站点信息

vim apache2.conf                # 修改 apache2.conf 配置文件
/directory                      # vim 中查找 directory，n 查找下一个，N 查找上一个
```

## 配置证书

由于申请证书是要收费的，我们可以自建 CA（myCA目录）

### CA

```
@ubuntu:~$ cd && mkdir -p myCA/signedcerts && mkdir myCA/private && cd myCA        # 签名证书及私钥
# 配置 myCA 相关参数文件
@ubuntu:~/myCA$ echo '01'>serial && touch index.txt && touch index.txt.attr
# 创建编辑 CA 配置文件 caconfig.cnf
@ubuntu:~/myCA$ vim caconfig.cnf
# 设置环境变量，生成 CA 证书及密钥
# CA 证书后缀 .pem，密钥位于 private 目录下
@ubuntu:~/myCA$ export OPENSSL_CONF=~/myCA/caconfig.cnf
@ubuntu:~/myCA$ openssl req -x509 -newkey rsa:2048 -out cacert.pem -outform PEM -days 1825
Generating a RSA private key
.........................................+++++
..................................................................................................................+++++
writing new private key to '/home/vic/myCA/private/cakey.pem'
Enter PEM pass phrase:
Verifying - Enter PEM pass phrase:
-----
```

### 服务器

```
# 创建编辑服务器配置文件 exampleserver.cnf
@ubuntu:~/myCA$ vim ~/myCA/exampleserver.cnf
@ubuntu:~/myCA$ export OPENSSL_CONF=~/myCA/exampleserver.cnf
@ubuntu:~/myCA$ openssl req -newkey rsa:1024 -keyout tempkey.pem -keyform PEM -out tempreq.pem -outform PEM
Generating a RSA private key
..................+++++
...........+++++
writing new private key to 'tempkey.pem'
Enter PEM pass phrase:
Verifying - Enter PEM pass phrase:
-----
@ubuntu:~/myCA$ ls
cacert.pem    exampleserver.cnf  index.txt.attr serial          tempkey.pem
caconfig.cnf  index.txt          private        signedcerts     tempreq.pem
@ubuntu:~/myCA$ mv tempkey.pem server_key.pem           # 备份服务器密钥，记住此时的 PEM pass phrase
```

### 使用 CA 对服务器证书签名

```
@ubuntu:~/myCA$ export OPENSSL_CONF=~/myCA/caconfig.cnf
@ubuntu:~/myCA$ openssl ca -in tempreq.pem -out server_crt.pem
Using configuration from /home/vic/myCA/caconfig.cnf
Enter pass phrase for /home/vic/myCA/private/cakey.pem:
Check that the request matches the signature
Signature ok
The Subject's Distinguished Name is as follows
commonName            :ASN.1 12:'localhost'
stateOrProvinceName   :ASN.1 12:'JS'
countryName           :PRINTABLE:'CN'
emailAddress          :IA5STRING:'XXXX@XXX.com'
organizationName      :ASN.1 12:'XXX'
organizationalUnitName:ASN.1 12:'XXX'
Certificate is to be certified until Dec 10 12:57:21 2024 GMT (1825 days)
Sign the certificate? [y/n]:y


1 out of 1 certificate requests certified, commit? [y/n]y
Write out database with 1 new entries
Data Base Updated
@ubuntu:~/myCA$ rm -f tempreq.pem
@ubuntu:~/myCA$ ls                  # 可以看到此时生成的服务器证书
cacert.pem         index.txt.attr      serial          tempkey.pem
caconfig.cnf       index.txt.attr.old  serial.old
exampleserver.cnf  index.txt.old       server_crt.pem
index.txt          private             signedcerts
@ubuntu:~/myCA$ cd private
@ubuntu:~/myCA/private$ ls
cakey.pem
```

### SSL

```
# 创建编辑 ssl 配置文件 lab-ssl.conf
@ubuntu:/etc/apache2/sites-available$ sudo vim lab-ssl.conf
@ubuntu:/etc/apache2/sites-available$ sudo a2ensite lab-ssl.conf
Enabling site lab-ssl.
To activate the new configuration, you need to run:
  systemctl reload apache2
@ubuntu:/etc/apache2/sites-available$ systemctl reload apache2
==== AUTHENTICATING FOR org.freedesktop.systemd1.manage-units ===
Authentication is required to reload 'apache2.service'.
Authenticating as: Ubuntu,,, (vic)
Password: 
==== AUTHENTICATION COMPLETE ===
@ubuntu:/etc/apache2/sites-available$ sudo a2enmod ssl          # 启动 ssl
Considering dependency setenvif for ssl:
Module setenvif already enabled
Considering dependency mime for ssl:
Module mime already enabled
Considering dependency socache_shmcb for ssl:
Enabling module socache_shmcb.
Enabling module ssl.
See /usr/share/doc/apache2/README.Debian.gz on how to configure SSL and create self-signed certificates.
To activate the new configuration, you need to run:
  systemctl restart apache2
@ubuntu:/etc/apache2/sites-available$ sudo systemctl restart apache2
Enter passphrase for SSL/TLS keys for 127.0.1.1:443 (RSA): *****            # 输入服务器 PEM 密码短语
# 每次启动或重启时都需要输入，出于安全原因，server.key 文件中的 RSA 私钥以加密格式存储；需要密码来解密，以便读取和解析。
```

### 结果

![](https://github.com/Yudreamy/classlater/blob/master/web/exercise1/picture/result1.PNG)

浏览器不信任这个网站，在浏览器的 about:preferences#privacy 中导入我们生成的 cacert.pem 即可

![](https://github.com/Yudreamy/classlater/blob/master/web/exercise1/picture/result2.PNG)


# MyZoo

### 安装 mysql 及 php

`sudo apt-get install mysql-server mysql-common mysql-client` 安装 mysql 相关，但是安装过程中没有要求输入用户名和密码的地方。查看文件 `/etc/mysql/debian.cnf` 中 MySql 默认用户名和密码。
```
# Automatically generated for Debian scripts. DO NOT TOUCH!
[client]
host     = localhost
user     = debian-sys-maint               # 用户名，not root
password = YQcKAS1WTs3dvaVo               # 密码，复杂
socket   = /var/run/mysqld/mysqld.sock
[mysql_upgrade]
host     = localhost
user     = debian-sys-maint
password = YQcKAS1WTs3dvaVo
socket   = /var/run/mysqld/mysqld.sock
```

通过上面得到的默认用户和密码进入 mysql

```
@ubuntu:~$ sudo netstat -tap | grep mysql
[sudo] password for vic: 
tcp        0      0 localhost:mysql         0.0.0.0:*               LISTEN      2381/mysqld         
@ubuntu:~$ cd /etc/mysql
@ubuntu:/etc/mysql$ ls
conf.d      debian-start  my.cnf.fallback  mysql.conf.d
debian.cnf  my.cnf        mysql.cnf
@ubuntu:/etc/mysql$ sudo vim debian.cnf
@ubuntu:/etc/mysql$ mysql -u debian-sys-maint -p
Enter password: 
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 2
Server version: 5.7.28-0ubuntu0.18.04.4 (Ubuntu)

Copyright (c) 2000, 2019, Oracle and/or its affiliates. All rights reserved.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql> update mysql.user set authentication_string=password('12345') where user='debian-sys-maint'and Host = 'localhost';
Query OK, 1 row affected, 1 warning (0.32 sec)
Rows matched: 1  Changed: 1  Warnings: 1                # 修改成功

mysql> q
    -> ^Cexit
exit
^C
mysql> exit
Bye
# 将 debian-sys-maint 的密码设置为 12345
@ubuntu:/etc/mysql$ sudo service mysql restart          # 重启 mysql
vic@ubuntu:/etc/mysql$ mysql -u debian-sys-maint -p
Enter password:                                         # 以新密码进入，成功
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 3
Server version: 5.7.28-0ubuntu0.18.04.4 (Ubuntu)
```
`ALTER USER 'root'@'localhost' IDENTIFIED BY 'yourpassword';` 语句也可以达到修改相应密码的目的，`select * from mysql.user;` 语句可以查看 mysql 用户信息，这里看到的 password 是加密的。此外，可用 root 用户登录数据库，用户名为 root，密码为空，直接回车。

```
@ubuntu:~$ php -v                             # 查看有无安装 php

Command 'php' not found, but can be installed with:

sudo apt install php7.2-cli
sudo apt install hhvm
# 安装 php 相关
@ubuntu:~$ sudo apt install php7.2 php7.2-cli php7.2-common php7.2-gd php7.2-mysql libapache2-mod-php7.2
Reading package lists... Done
Building dependency tree       
Reading state information... Done
The following additional packages will be installed:
  php-common php7.2-json php7.2-opcache php7.2-readline
Suggested packages:
  php-pear
...
# 期间会让你输入两次之前的 passphrase
Creating config file /etc/php/7.2/cli/php.ini with new version
Setting up libapache2-mod-php7.2 (7.2.24-0ubuntu0.18.04.1) ...

Creating config file /etc/php/7.2/apache2/php.ini with new version
Module mpm_event disabled.
Enabling module mpm_prefork.
apache2_switch_mpm Switch to prefork
Enter passphrase for SSL/TLS keys for 127.0.1.1:443 (RSA): *****
apache2_invoke: Enable module php7.2
Enter passphrase for SSL/TLS keys for 127.0.1.1:443 (RSA): *****
Setting up php7.2 (7.2.24-0ubuntu0.18.04.1) ...
Processing triggers for man-db (2.8.3-2ubuntu0.1) ...
@ubuntu:~$ php -v                                 # 安装成功
PHP 7.2.24-0ubuntu0.18.04.1 (cli) (built: Oct 28 2019 12:07:07) ( NTS )
Copyright (c) 1997-2018 The PHP Group
Zend Engine v3.2.0, Copyright (c) 1998-2018 Zend Technologies
    with Zend OPcache v7.2.24-0ubuntu0.18.04.1, Copyright (c) 1999-2018, by Zend Technologies
```

### 配置 mysql 建数据库

```
root@ubuntu# mysql -u root -p                     # 进入 root 账户
mysql> create database zoo;
Query OK, 1 row affected (0.00 sec)
mysql> use zoo;
Database changed
mysql> create table Person(PersonID int primary key auto_increment, Password varchar(100),Salt varchar(100),Username varchar(100),Token varchar(100),Zoobars int default 10, Profile varchar(5000));
Query OK, 0 rows affected (0.54 sec)
mysql> exit
Bye
```

将 php7_myzoo.zip 解压缩之后的 myzoo 文件夹拷贝到 `/var/www`，修改 includes 下的 database.class.php 文件中 db_pass。然后创建并修改 apache2 配置文件 /etc/apache2/sites-available/myzoo.conf，将 `DocumentRoot /var/www/html` 修改为 `DocumentRoot /var/www/myzoo`。
```
root@ubuntu:/var/www/myzoo/includes# vim database.class.php
root@ubuntu:/var/www/myzoo/includes# cd /etc/apache2/sites-available
root@ubuntu:/etc/apache2/sites-available# ls
000-default.conf  default-ssl.conf  lab-ssl.conf
root@ubuntu:/etc/apache2/sites-available# cp 000-default.conf myzoo.conf
root@ubuntu:/etc/apache2/sites-available# ls
000-default.conf  default-ssl.conf  lab-ssl.conf  myzoo.conf
root@ubuntu:/etc/apache2/sites-available# vim 000-default.conf
root@ubuntu:/etc/apache2/sites-available# a2dissite 000-default.conf            # 禁用默认站点配置
Site 000-default disabled.
To activate the new configuration, you need to run:
  systemctl reload apache2
root@ubuntu:/etc/apache2/sites-available# a2ensite myzoo.conf                   # 启用 myzoo
Enabling site myzoo.
To activate the new configuration, you need to run:
  systemctl reload apache2
root@ubuntu:/etc/apache2/sites-available# service apache2 restart               # 重启 apache
Enter passphrase for SSL/TLS keys for 127.0.1.1:443 (RSA): *****
```

### 结果

![](https://github.com/Yudreamy/classlater/blob/master/web/exercise1/picture/result3.PNG)


# Reference：

- [ ] [Kali Linux 中文文档](https://wizardforcel.gitbooks.io/kali-linux-doc/content/index.html)
- [x] [https 搭建详细参考1](https://www.cnblogs.com/libaoquan/p/7965873.html)
- [x] [https 搭建详细参考2](https://blog.csdn.net/teivos/article/details/78619972)
- [x] [另一种做法](https://www.cnblogs.com/IT--Loding/p/6071855.html)
- [x] [ubuntu 安装 mysql 时未提示输入密码解决方法](https://blog.csdn.net/sinat_21302587/article/details/76870457)
