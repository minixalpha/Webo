Webo
====

Webo 是新浪微博第三方客户端


## UI 设计

* 初始界面

![login](screenshots-mini/login.png)

* 主界面

![main](screenshots-mini/main.png)

* 写微博

![wirte](screenshots-mini/wirte.png)


* 新消息

![new_msg](screenshots-mini/new_msg.png)

* 转发

![retweet](screenshots-mini/retweet.png)

* 评论

![comment](screenshots-mini/comment.png)

* 微博详情

![weibo_detail](screenshots-mini/weibo_detail.png)

* 侧栏

![left_menu](screenshots-mini/left_menu.png)

* 主页

![homepage](screenshots-mini/homepage.png)

* 搜索

![search_user](screenshots-mini/search_user.png)

* 设置

![configure](screenshots-mini/configure.png)

* 退出

![logout](screenshots-mini/logout.png)

## 技术

* PullRefreshListView

* View Pager

* Menu Drawer

* 图片缓存

* 通知

这个 App 是国庆节期间写的，是我刚刚学习 android 的练习，每天的开发内容都有记录，在 doc 目录下。

## DONE

* 登录
* 下拉刷新微博
* 写微博
* 获取新消息提醒，包含新评论，有人at了我
* 评论
* 转发
* 查看自己的主页
* 搜索用户
* 退出

## TODO

* 设置仅完成了UI设计，功能还没有完成
* ~~刷新微博到底部时还没有完成加载更早的微博的功能，仅有下拉刷新最新微博~~
* 关注功能
* 消息自动提醒
* ~~按钮图标的设计不够精细~~
* 仅仅在小米上测试，其它机器没有完成适配
* 代码还需要重构，包括有代码可以复用的地方，功能的划分
* 资源文件还需要整理
* ~~加载效率，listivew 的效率还可以再提升~~
* 通过配置文件来设置参数
* 区分 Debug 版本和 Release 版本
