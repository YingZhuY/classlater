# HTML and CSS

环境：VMware-workstation-full-15.5.1，ubuntu-18.04.2-desktop-amd64，Apache/2.4.29，MySQL5.7.28，PHP7.2.24

## zoobar 显示

这里先解决一下上次 HTTPS 最后的遗留问题，上次已经实现了输入 localhost 可以直接显示地址栏前边带锁的网页，但是用 root 作为我们数据库 zoo 的用户会导致使用网页连接不上数据库的 error，因为 root 的密码任意都可以，此时我们需要新建一个用户并给予其管理指定数据库的权限。
```
root@ubuntu# mysql -u root -p                       # 先进入 root 用户
mysql> CREATE USER 'zoo'@'localhost' IDENTIFIED BY '12345';             # 创建新用户 zoo ，12345 是其密码
Query OK, 0 rows affected (0.10 sec)

mysql> exit
Bye
root@ubuntu# mysql -u zoo -p                        # 尝试用 zoo 登录
Enter password: 
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 5                       # ok，说明登录成功
Server version: 5.7.28-0ubuntu0.18.04.4 (Ubuntu)
root@ubuntu# mysql -u root -p
# exit 退出后切回到 root 用户，给 zoo 授予对目标数据库的权限
mysql> GRANT all ON zoo.Person TO 'zoo'@'localhost'
    -> ;                                            # zoo.Person zoo 数据库名，Person table 名
Query OK, 0 rows affected (0.00 sec)
root@ubuntu:# mysql -u zoo -p                       # 换到 zoo 用户
mysql> use zoo;
Reading table information for completion of table and column names
You can turn off this feature to get a quicker startup with -A

Database changed                                    # 修改成功
mysql> exit
Bye
root@ubuntu:# service mysql restart                 # 重启数据库服务
```
刷新就能看到网站可以用了。

## 简单的 HTML/CSS 攻击

zoobar 网站上有几个基本的信息：user、zoobars、profile，每个新用户注册后 zoobars 都为10，攻击要求通过管理个人 profile 信息，使他人或你自己 view 自己的个人资料时，zoobars看起来更多。

### 添加 $<img>$ 便签

首先看一下 view 过程对应的代码，位于 myzoo 文件夹下的 users.php。
```
<div id="profileheader"><!-- user data appears here --></div>
<?php 
  $selecteduser = $_GET['user'];                                # 记录我们输入的查询对象 user
  $sql = "SELECT Profile, Username, Zoobars FROM Person " . 
         "WHERE Username='$selecteduser'";                      # 构建查询语句
  $rs = $db->executeQuery($sql);                                # 执行查询，结果放在 result，rs
  $rs = mysqli_fetch_array($rs);                                # 以数字数组和关联数组的形式获取结果行
    if ($rs){                                                   # rs 存在时分别赋给 profile、username 和 zoobars
   	$profile = $rs["Profile"];
	$username = $rs["Username"];
	$zoobars = $rs["Zoobars"];
	echo "<div class=profilecontainer><b>Profile</b>";
    $allowed_tags = 
      '<a><br><b><h1><h2><h3><h4><i><img><li><ol><p><strong><table>' .
      '<tr><td><th><u><ul><em><span>';                          # 此处是允许的标签，我们看到了<img>
    $profile = strip_tags($profile, $allowed_tags);
    $disallowed =                                               # 不允许的属性
      'javascript:|window|eval|setTimeout|setInterval|target|'.
      'onAbort|onBlur|onChange|onClick|onDblClick|'.
      'onDragDrop|onError|onFocus|onKeyDown|onKeyPress|'.
      'onKeyUp|onLoad|onMouseDown|onMouseMove|onMouseOut|'.
      'onMouseOver|onMouseUp|onMove|onReset|onResize|'.
      'onSelect|onSubmit|onUnload';
    $profile = preg_replace("/$disallowed/i", " ", $profile);   # 不允许的属性都被替换成了空格，不会产生作用
    echo "<p id=profile>$profile</p></div>";
  } else if($selecteduser) {  // user parameter present but user not found
    echo '<p class="warning" id="baduser">Cannot find that user.</p>';
  }
  $zoobars = ($zoobars > 0) ? $zoobars : 0;
  echo "<span id='zoobars' class='$zoobars'/>";	
?><script type="text/javascript">                               # 最后网页上的打印显示
  var total = eval(document.getElementById('zoobars').className);
  function showZoobars(zoobars) {
    document.getElementById("profileheader").innerHTML =
      "<?php echo $selecteduser ?>'s zoobars:" + zoobars;
    if (zoobars < total) {                                      # 延时显示
      setTimeout("showZoobars(" + (zoobars + 1) + ")", 100);
    }
  }
  if (total > 0) showZoobars(0);  // count up to total
</script>
```
接下来构造 $<img>$ 便签，增加几个简单的位置属性就可以了。

### 结果

![](https://github.com/Yudreamy/classlater/blob/master/web/exercise2/picture/resulta1.PNG)

![](https://github.com/Yudreamy/classlater/blob/master/web/exercise2/picture/resulta2.PNG)


## 简单的 SQL 注入

通过对比和别人的思路发现还可以 SQL 注入，不过 SQL 注入改的就是实际的 zoobars 不是表面上看起来的了。

### 对 zoobars

查看设置 profile 部分的代码
```
<?php
  if($_POST['profile_submit']) {  // Check for profile submission
    $profile = $_POST['profile_update'];
    $sql = "UPDATE Person SET Profile='$profile' ".
           "WHERE PersonID=$user->id";
    $db->executeQuery($sql);  // Overwrite profile in database
  }
  $sql = "SELECT Profile FROM Person WHERE PersonID=$user->id";       # 可以针对此处进行SQL 注入
  $rs = $db->executeQuery($sql);
  $rs = mysqli_fetch_array($rs);
  echo $rs["Profile"];
?>
```

![](https://github.com/Yudreamy/classlater/blob/master/web/exercise2/picture/resultb1.PNG)

![](https://github.com/Yudreamy/classlater/blob/master/web/exercise2/picture/resultb2.PNG)

### 对登录

![](https://github.com/Yudreamy/classlater/blob/master/web/exercise2/picture/resultc1.PNG)

![](https://github.com/Yudreamy/classlater/blob/master/web/exercise2/picture/resultc2.PNG)

## 两个HTML

这个是另外的两个 HTML+CSS，可以看 GitHub 上边的 CS142，资料很多，这里就不赘述了，放两张效果图。

![](https://github.com/Yudreamy/classlater/blob/master/web/exercise2/picture/resultd1.PNG)

![](https://github.com/Yudreamy/classlater/blob/master/web/exercise2/picture/resultd2.PNG)

# Reference：

- [x] [MySQL 创建用户与授权](https://www.jianshu.com/p/d7b9c468f20d)
