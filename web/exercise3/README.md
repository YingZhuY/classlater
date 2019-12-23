# CSRF & XSS

## 实验要求

1.	在 zoobar 网站上展示并防御 CSRF 攻击。请注意在防御时的粒度问题，防止所有人的 token 都一样；以及刷新太快，正常操作都失败。
2.	在 zoobar 网站上展示并防御 XSS 攻击。请注意实现 cookie 窃取、以及 xss 蠕虫。
3.	在 zoobar 网站上展示并防御点击劫持攻击；或者在其他网站上实现点击劫持攻击。

## 实验环境

VMware-workstation-full-15.5.1，ubuntu-18.04.2-desktop-amd64，Apache2.4.29，MySQL5.7.28，PHP7.2.24，kali-linux-2019-4-vmware-amd64，Mozilla Firefox 71.0


## Apache 设置多站点 https

首先我们需要创建两个网站，一个通过 https://localhost 访问 zoobar，一个通过 https://www.zoobar.com 访问 zoobar，此时需要在 Apache 服务器上搭建多个站点，运行多个不同的网站。在 Apache 的配置文件中，/etc/apache2/sites-available 保存所有可用站点的配置文件，/etc/apache2/sites-enabled 为已启用站点信息，sites-enabled 目录存放指向 sites-available 的符号链接。这样，如果需要配置多个虚拟主机，把每个虚拟主机配置都放在 sites-available 下，当 sites-enabled 下建立一个指向某个虚拟主机的配置文件的链接时，该网站就被启用，如果要关闭的话，只需要删除相应符号链接即可，不用去改配置文件。

```
@ubuntu:/etc/apache2/sites-available$ ls                # 查看刚开机时的配置文件，此时为 sites-available 目录
000-default.conf  default-ssl.conf  lab-ssl.conf  myzoo.conf
@ubuntu:/etc/apache2/sites-available$ sudo cp 000-default.conf csrf.conf            # 新建 csrf.conf
[sudo] password for vic: 
@ubuntu:/etc/apache2/sites-available$ ls
000-default.conf  csrf.conf  default-ssl.conf  lab-ssl.conf  myzoo.conf
@ubuntu:/etc/apache2/sites-available$ sudo vim csrf.conf                            # 配置 csrf.conf

ServerName www.zoobar.com
ServerAdmin csrf@localhost
DocumentRoot /var/www/csrf

# 在 sites-enabled 目录下建立符号链接
@ubuntu:/etc/apache2/sites-enabled$ sudo ln -s /etc/apache2/sites-available/csrf.conf /etc/apache2/sites-enabled/csrf.conf

# 将 myzoo 更名为 csrf 放入 /var/www 目录下
# 修改 /etc/apache2/apache2.conf 文件
# 修改 /etc 下的 hosts 文件

@ubuntu:/etc$ cat hosts
127.0.0.1	localhost
127.0.1.1	ubuntu

# The following lines are desirable for IPv6 capable hosts
::1     ip6-localhost ip6-loopback
fe00::0 ip6-localnet
ff00::0 ip6-mcastprefix
ff02::1 ip6-allnodes
ff02::2 ip6-allrouters
@ubuntu:/etc$ sudo vim hosts

127.0.0.2       www.zoobar.com

@ubuntu:/etc$ sudo service apache2 restart                          # 重启 apache2 服务
Enter passphrase for SSL/TLS keys for 127.0.1.1:443 (RSA): *****
```
对了，还需要给 www.zoobar.com 网站配置 SSL 以及建新的数据库修改几个文件，具体步骤参照之前 [exercise1](https://github.com/Yudreamy/classlater/tree/master/web/exercise1)。 Apache 多站点 https 配置参考最后的 reference，关键就是在 ssl 配置文件中的多个 VirtualHost。顺序：配置服务器-使用 CA 对服务器证书签名-得到证书-创建编辑 csrf-ssl.conf 配置文件，注意此时的标头 `<VirtualHost 127.0.0.2:443>`-重启 ssl、apache2-进行数据库操作，创建 csrf 用户及相应表格，权限设置-修改 /var/www/csrf 中的相关文件。以下给出全部的 csrf-ssl.conf 配置文件。

```
# csrf-ssl.conf 配置文件

<IfModule mod_ssl.c>
	<VirtualHost 127.0.0.2:443>
		ServerAdmin csrf@localhost
                ServerName www.zoobar.com

		DocumentRoot /var/www/csrf

		# Available loglevels: trace8, ..., trace1, debug, info, notice, warn,
		# error, crit, alert, emerg.
		# It is also possible to configure the loglevel for particular
		# modules, e.g.
		#LogLevel info ssl:warn

		ErrorLog ${APACHE_LOG_DIR}/error.log
		CustomLog ${APACHE_LOG_DIR}/access.log combined

		# For most configuration files from conf-available/, which are
		# enabled or disabled at a global level, it is possible to
		# include a line for only one particular virtual host. For example the
		# following line enables the CGI configuration for this host only
		# after it has been globally disabled with "a2disconf".
		#Include conf-available/serve-cgi-bin.conf

		#   SSL Engine Switch:
		#   Enable/Disable SSL for this virtual host.
		SSLEngine on

		#   A self-signed (snakeoil) certificate can be created by installing
		#   the ssl-cert package. See
		#   /usr/share/doc/apache2/README.Debian.gz for more info.
		#   If both key and certificate are stored in the same file, only the
		#   SSLCertificateFile directive is needed.
		SSLCertificateFile	/home/vic/myCA/csrf_crt.pem
		SSLCertificateKeyFile /home/vic/myCA/csrf_key.pem

		#   Server Certificate Chain:
		#   Point SSLCertificateChainFile at a file containing the
		#   concatenation of PEM encoded CA certificates which form the
		#   certificate chain for the server certificate. Alternatively
		#   the referenced file can be the same as SSLCertificateFile
		#   when the CA certificates are directly appended to the server
		#   certificate for convinience.
		#SSLCertificateChainFile /etc/apache2/ssl.crt/server-ca.crt

		#   Certificate Authority (CA):
		#   Set the CA certificate verification path where to find CA
		#   certificates for client authentication or alternatively one
		#   huge file containing all of them (file must be PEM encoded)
		#   Note: Inside SSLCACertificatePath you need hash symlinks
		#		 to point to the certificate files. Use the provided
		#		 Makefile to update the hash symlinks after changes.
		#SSLCACertificatePath /etc/ssl/certs/
		#SSLCACertificateFile /etc/apache2/ssl.crt/ca-bundle.crt

		#   Certificate Revocation Lists (CRL):
		#   Set the CA revocation path where to find CA CRLs for client
		#   authentication or alternatively one huge file containing all
		#   of them (file must be PEM encoded)
		#   Note: Inside SSLCARevocationPath you need hash symlinks
		#		 to point to the certificate files. Use the provided
		#		 Makefile to update the hash symlinks after changes.
		#SSLCARevocationPath /etc/apache2/ssl.crl/
		#SSLCARevocationFile /etc/apache2/ssl.crl/ca-bundle.crl

		#   Client Authentication (Type):
		#   Client certificate verification type and depth.  Types are
		#   none, optional, require and optional_no_ca.  Depth is a
		#   number which specifies how deeply to verify the certificate
		#   issuer chain before deciding the certificate is not valid.
		#SSLVerifyClient require
		#SSLVerifyDepth  10

		#   SSL Engine Options:
		#   Set various options for the SSL engine.
		#   o FakeBasicAuth:
		#	 Translate the client X.509 into a Basic Authorisation.  This means that
		#	 the standard Auth/DBMAuth methods can be used for access control.  The
		#	 user name is the `one line' version of the client's X.509 certificate.
		#	 Note that no password is obtained from the user. Every entry in the user
		#	 file needs this password: `xxj31ZMTZzkVA'.
		#   o ExportCertData:
		#	 This exports two additional environment variables: SSL_CLIENT_CERT and
		#	 SSL_SERVER_CERT. These contain the PEM-encoded certificates of the
		#	 server (always existing) and the client (only existing when client
		#	 authentication is used). This can be used to import the certificates
		#	 into CGI scripts.
		#   o StdEnvVars:
		#	 This exports the standard SSL/TLS related `SSL_*' environment variables.
		#	 Per default this exportation is switched off for performance reasons,
		#	 because the extraction step is an expensive operation and is usually
		#	 useless for serving static content. So one usually enables the
		#	 exportation for CGI and SSI requests only.
		#   o OptRenegotiate:
		#	 This enables optimized SSL connection renegotiation handling when SSL
		#	 directives are used in per-directory context.
		#SSLOptions +FakeBasicAuth +ExportCertData +StrictRequire
		<FilesMatch "\.(cgi|shtml|phtml|php)$">
				SSLOptions +StdEnvVars
		</FilesMatch>
		<Directory /usr/lib/cgi-bin>
				SSLOptions +StdEnvVars
		</Directory>

		#   SSL Protocol Adjustments:
		#   The safe and default but still SSL/TLS standard compliant shutdown
		#   approach is that mod_ssl sends the close notify alert but doesn't wait for
		#   the close notify alert from client. When you need a different shutdown
		#   approach you can use one of the following variables:
		#   o ssl-unclean-shutdown:
		#	 This forces an unclean shutdown when the connection is closed, i.e. no
		#	 SSL close notify alert is send or allowed to received.  This violates
		#	 the SSL/TLS standard but is needed for some brain-dead browsers. Use
		#	 this when you receive I/O errors because of the standard approach where
		#	 mod_ssl sends the close notify alert.
		#   o ssl-accurate-shutdown:
		#	 This forces an accurate shutdown when the connection is closed, i.e. a
		#	 SSL close notify alert is send and mod_ssl waits for the close notify
		#	 alert of the client. This is 100% SSL/TLS standard compliant, but in
		#	 practice often causes hanging connections with brain-dead browsers. Use
		#	 this only for browsers where you know that their SSL implementation
		#	 works correctly.
		#   Notice: Most problems of broken clients are also related to the HTTP
		#   keep-alive facility, so you usually additionally want to disable
		#   keep-alive for those clients, too. Use variable "nokeepalive" for this.
		#   Similarly, one has to force some clients to use HTTP/1.0 to workaround
		#   their broken HTTP/1.1 implementation. Use variables "downgrade-1.0" and
		#   "force-response-1.0" for this.
		# BrowserMatch "MSIE [2-6]" \
		#		nokeepalive ssl-unclean-shutdown \
		#		downgrade-1.0 force-response-1.0

	</VirtualHost>
</IfModule>

# vim: syntax=apache ts=4 sw=4 sts=4 sr noet


////////////////////////////////////////////分割线
# 此处为 ssl 的配置过程
@ubuntu:/etc/apache2/sites-available$ sudo vim csrf-ssl.conf 
[sudo] password for vic: 
@ubuntu:/etc/apache2/sites-available$ sudo a2dissite csrf-ssl.conf
Site csrf-ssl disabled.
To activate the new configuration, you need to run:
  systemctl reload apache2
@ubuntu:/etc/apache2/sites-available$ sudo a2ensite csrf-ssl.conf
Enabling site csrf-ssl.
To activate the new configuration, you need to run:
  systemctl reload apache2
@ubuntu:/etc/apache2/sites-available$ sudo service apache2 restart
Enter passphrase for SSL/TLS keys for 127.0.1.1:443 (RSA): *****
Enter passphrase for SSL/TLS keys for www.zoobar.com:443 (RSA): *****

# 要输两个服务器主机的 passphrase
```

![](https://github.com/Yudreamy/classlater/blob/master/web/exercise3/picture/zoobar_com.PNG)

至此我们的两个网站就设置好了。

## CSRF 攻击

CSRF（Cross-Site Request Forgery），即跨站请求伪造，攻击者通过浏览器本地 cookie 盗用你的身份，以你的名义发送请求，对服务器来说这个请求是完全合法（当前用户已通过站点认证，该站点无法区分受害者发送的合法请求和受害者发送的伪造请求）的，从而就会在你不知情下完成一个攻击设计的恶意操作。以下通过 Zoobars 转账模拟。

cookie 是网站为了辨别用户身份而存储在用户本地终端上的数据（通常经过加密）。这里先总结一下 cookie 相关特点：1. cookie 存储在用户本地浏览器上；2. 记录状态信息（例如在线商店购物车中添加的商品），用户浏览活动（特定按钮、登录或访问过的网站），还可以用于记住用户先前在表单字段输入的任意信息（名称、地址、密码、信用卡号等）；3. cookie 与个人身份有关，身份验证 cookie 是 Web 服务器用来了解用户是否已登录以及他们使用哪个帐户登录的最常用方法。看下此时的 cookie(document 类下)。

![](https://github.com/Yudreamy/classlater/blob/master/web/exercise3/picture/cookie.PNG)

用户 Alice 登录 zoobar 网站后，网站就会给他设置一个 cookie，然后 Alice 每次请求一个新网页时，浏览器都会自动把 cookie 带上，帮浏览器识别 Alice，以进行查询和转账操作。先执行下正常的转账，Alice 给 Bob 转一个币，注意到此时浏览器的一个 POST 请求 https://www.zoobar.com/transfer.php ，此时的头部就有我们之前的 cookie，看下此时的 Params 以及 Response。

![](https://github.com/Yudreamy/classlater/blob/master/web/exercise3/picture/POST.PNG)

![](https://github.com/Yudreamy/classlater/blob/master/web/exercise3/picture/Params.PNG)

![](https://github.com/Yudreamy/classlater/blob/master/web/exercise3/picture/Response.PNG)

攻击者可以通过编辑 Params 中的三个参数来构造一个攻击网页，然后在自己的 profile 描述信息中编辑一个指向攻击网站的按钮。如果 Alice 通过攻击者的链接点到了攻击网站，即使 Alice 首先关闭了页面或者浏览器，只要他没有 logout，他的 cookie 就还没有过期，那么他访问攻击者网站的时候，他的 zoobar 就会被偷走。现在开始构造攻击网站，按照同样的方法创建一个域名为 `http://www.test.com` 的网站，编辑 /var/www/test 目录下的 `index.html` 文件，然后登入 localhost 的 csrf 用户，将其 profile 设置为 `<a href="http://www.test.com" target="view_window">CLICK ME!</a>`，等待用户点击。以下为攻击效果。

![](https://github.com/Yudreamy/classlater/blob/master/web/exercise3/picture/test-csrf.PNG)

![](https://github.com/Yudreamy/classlater/blob/master/web/exercise3/picture/transfered.PNG)

## CSRF 防御

### Referer check

可以注意到之前的 Request Headers 中有 referer 项，referer 项表示当前的请求是从哪个网站发出去的。我们可以使用代码来验证 referer，当发出网站非法时禁止该操作。
```
<?php
        $ref = ($_SERVER['HTTP_REFERER']);
        $tmp1 = substr($ref,strpos($ref,'//')+2);
        $tmp2 = substr($tmp1,0,strpos($tmp1,'/'));

        if($ref !='www.zoobar.com' &&  $ref !='localhost'&& $tmp2 != 'localhost' && $tmp2 != 'www.zoobar.com')
                die("Hotlinking not permitted!");
    
?>
```

将以上内容加入 myzoo 的 transfer.php 中，重启 lab-ssl.conf 和 apache2，点击 csrf profile 按钮及正常转账给 abc的执行结果如下。

![](https://github.com/Yudreamy/classlater/blob/master/web/exercise3/picture/referer-false.PNG)

![](https://github.com/Yudreamy/classlater/blob/master/web/exercise3/picture/referer-true.PNG)

这种方法有缺陷，用户可以在浏览器设置在发出请求的时候不带Referrer这个选项（由 2 设为 0），这样，用户的正常转账请求也会被隔断；另外，攻击者也可以通过安装插件很方便地修改 referer，这样也达不到防御效果。

### 验证码

通过添加验证码操作强制用户在执行转账操作时进行校验，但是考虑到用户友好，验证码只能作为一种辅助手段，不能作为主要解决方案。

### Anti CSRF Token

通过添加令牌 token 可以提高攻击者伪造请求的难度，如果请求中包括一些攻击者不能容易获取的信息，那么攻击自然不能成功。这个信息应该用户独特且短时间不会过期。即我们尝试使用 Anti CSRF Token。

* 用户登录网站，服务端生成一个 Token，放在用户的 Session
```
# auth.php
<?php session_start(); ?>
  function _updateToken($token) {
    $arr = array($this->username, $token);
    $cookieData = base64_encode(serialize($arr));
    $_SESSION["csrf"] = md5(uniqid(mt_rand(),true));
	
    setcookie($this->cookieName, $cookieData, time() + 31104000);
  }
```
* 在页面表单附带上 Token 参数，为了不影响用户，可以设置 type=hidden
```
# transfer.php
<form method=POST name=transferform
...
<input type=hidden name=csrf value=<?php echo "\"".$_SESSION["csrf"]."\"" ?>>
</form>
```
* 用户提交请求时，表单中的这一参数会自动提交， 服务端验证表单中的 Token 是否与用户 Session 中的 Token 一致，一致为合法请求，不是则非法请求
```
# transfer.php
  if($_POST['submission'] && ($_SESSION["csrf"]==$_POST["csrf"])) 
# 正常转账请求的 Token 即 $_POST["csrf"] 与 Session 中可以完成匹配进入 if 体，进行 Token 更新。
# 而用按钮伪造的请求通过不了 Token 验证。
```
* 每次提交，Token 值可以更新
```
# transfer.php
    $_SESSION["csrf"] = md5(uniqid(mt_rand(),true));
    $recipient = $_POST['recipient'];
```
完成上述修改后，重启 csrf-ssl.conf 和 apache2，设置的 token 名为 `csrf`，会在每次登录及进行正常转账时更新。效果如下。

![](https://github.com/Yudreamy/classlater/blob/master/web/exercise3/picture/token1.PNG)

![](https://github.com/Yudreamy/classlater/blob/master/web/exercise3/picture/token2.PNG)

![](https://github.com/Yudreamy/classlater/blob/master/web/exercise3/picture/token3.PNG)

![](https://github.com/Yudreamy/classlater/blob/master/web/exercise3/picture/token-false.PNG)

## XSS



# Reference：

- [x] [Apache 设置多站点 https 参考 1](https://blog.51cto.com/guoxh/2114630)
- [x] [Apache 设置多站点 https 参考 2](https://www.digitalocean.com/community/tutorials/how-to-create-a-self-signed-ssl-certificate-for-apache-in-ubuntu-18-04)
- [x] [Ubuntu 下 Apache 配置域名](https://www.jianshu.com/p/81be4732af9f)
- [x] [CSRF & XSS](https://www.familyhealth.top/?p=660)
- [x] [课程知乎](https://zhuanlan.zhihu.com/p/51874839)