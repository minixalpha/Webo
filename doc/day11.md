##目标

0. 字体

1. 按钮

* 评论
* 转发
* 微博正文

2. 退出

3. 微博详情页

4. 富文本

5. 设置

* 图片开关,大小
* 提醒：新微博，新消息
* 刷新：自动，手动
* 缓存
* 关于
* 字体

6. Cache

Cache 中与用户相关的信息应该加用户 id，防止看到其它用户的缓存。

7. 刷新

第一次刷新时应该给出 progress bar，手动刷新时不给出 progress bar, 由 pullrefreshlistview 给 progress bar.

8. 刷新状态何时开始，何时结束

9. 获取关注人列表，并利用此列表清除广告

10. 首次加载太慢

11. 缓存问题，缓存什么时候刷新好呢？

## 完成

0. 字体和图标的深浅要一致，没找到合适的图标，一直失真

1. 退出：退出时给出提示，选确定才退出，退出时，回到登录界面，清除本地 token，用 LogoutAPI 向服务器发退出接口调用。

2. 缓存中，时间线，当前用户时间线，当前用户评论，均与登录用户相关，其 KEY 值中添加当前用户 ID.

3. 刷新， viewweibofragment 添加一个　progress bar，在初始化时显示出来，当 weibo controllor 添加完数据后，调用 viewweibofragement 的一个回调函数，
通知它，更新完成，viewweibofragment的回调函数会使 progress bar 消失。

4. 评论，转发功能：设计一个 activity: RetweetReplyActivity，通过不同参数进入 startactivity，进入转发或者评论，android:windowSoftInputMode="adjustResize"，　自动打开键盘，并且重新计算窗口布局。

5. 通过给RetweetReplyActivity添加 filter，使其可以得到评论，转发完成的信号，从而返回主界面。

6. 单条微博通过点击微博主体内容触发，新 start 一个 WeiboDetailsActivity，其中包含微博主体内容以及评论列表。

7. 重构评论模块, 评论模块在用户收到的评论列表，以及单条微博详情中都会用到，二者只是获取列表的方式不一样。可以单独提出一个类CommentView，告诉他如何获取评论，是哪个　listviwe, 
它就自动将一个 listview 设置好。可以将需要告诉他的东西抽象到一个接口ViewCommentHelper中，与之前重构的时间线，用户基本信息很相似。另外，不同的使用者有不同的缓存策略，经常需要
查看的需要使用缓存，不经常查看的不用缓存，刷新时更新就行，所以 ViewCommentHelper中还设计有缓存更新策略接口。

8. 设置 weibo_full_item 微博正文最小高度，否则当正文内容少时，按钮比正文还高。

9. 微博详情中，主要是评论内容，这时候，微博主体内容应该设计成单击放大的效果。

