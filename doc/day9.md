## 目标

1. 重构程序

* 自定义 Application 生成全局 context
* 将 Weibo API 单独抽象成一个类，使用单例模式，供全局使用


2. 完成发微博模块

3. 设计新消息模块，侧栏模块 UI


## 完成

1.　重构程序

* 自定义 Application 生成全局 context，全局都使用这个 context. (**有 bug**)
* 将 Weibo API 单独抽象成一个类，使用单例模式，供全局使用
* 清理无用代码

2. 发微博

* 实时更新字数限制
* 字数超出时提示，不发送
* 无网络连接不发送
* 发送后使用系统通知提示已经发送
* 发微博的异步API回调时，使用系统通知提示发送成功或者失败
* 使用本地广播，在发微博成功后，由发微博的 fragment 向其所在 activity 发送一条广播，使这个 activity 切换到查看微博的　fragment


3. 完成新消息模块

* 使用两个动态 Fragment 嵌套在新消息Fragment下，通过两个按钮点击动态切换 Fragment. 为评论和at设计两个新的 Adapter 类

## Bug

1. 不能全局都使用同一个　contex ，例如　ViewWeiboFragment 中，　ListView 的　Adapter，　就需要使用　ViewWeiboFragment 的　Activity 
作为　contex, 而不能使用全局的 contex, 否则会引起　UI 的改变，这里出现的　bug是字体颜色发生了改变。全局的　contex 用于　Toast, share preferences 等等。

重构的时候，每重构到一个可运行的状态，就要检查下程序的状态是否改变，否则最后很难知道是哪里的改变导致程序出错。


response:{"comments":[{"created_at":"Tue Oct 07 20:42:48 +0800 2014","id":3763139219216762,"text":"来看只能我来了","source":"<a href=\"http://weibo.com/\" rel=\"nofollow\">微博 weibo.com</a>","user":{"id":1664695495,"idstr":"1664695495","class":1,"screen_name":"_Iambda","name":"_Iambda","province":"11","city":"8","location":"北京 海淀区","description":"请不要超过70个字","url":"","profile_image_url":"http://tp4.sinaimg.cn/1664695495/50/39999625666/1","profile_url":"u/1664695495","domain":"","weihao":"","gender":"m","followers_count":68,"friends_count":388,"pagefriends_count":0,"statuses_count":299,"favourites_count":6,"created_at":"Thu Apr 14 12:42:35 +0800 2011","following":false,"allow_all_act_msg":false,"geo_enabled":false,"verified":false,"verified_type":-1,"remark":"","ptype":0,"allow_all_comment":true,"avatar_large":"http://tp4.sinaimg.cn/1664695495/180/39999625666/1","avatar_hd":"http://tp4.sinaimg.cn/1664695495/180/39999625666/1","verified_reason":"","verified_trade":"","verified_reason_url":"","verified_source":"","verified_source_url":"","follow_me":false,"online_status":1,"bi_followers_count":62,"lang":"zh-cn","star":0,"mbtype":0,"mbrank":0,"block_word":0,"block_app":0,"credit_score":-1},"mid":"3763139219216762","idstr":"3763139219216762","status":{"created_at":"Tue Oct 07 11:31:34 +0800 2014","id":3763000496676483,"mid":"3763000496676483","idstr":"3763000496676483","text":"fuubo也挺好","source":"<a href=\"http://app.weibo.com/t/feed/1Ua4Xz\" rel=\"nofollow\">Fuubo</a>","source_type":1,"favorited":false,"truncated":false,"in_reply_to_status_id":"","in_reply_to_user_id":"","in_reply_to_screen_name":"","pic_urls":[],"geo":null,"user":{"id":5309864355,"idstr":"5309864355","class":1,"screen_name":"xk_android","name":"xk_android","province":"11","city":"1","location":"北京 东城区","description":"","url":"","profile_image_url":"http://tp4.sinaimg.cn/5309864355/50/0/1","profile_url":"u/5309864355","domain":"","weihao":"","gender":"m","followers_count":1,"friends_count":33,"pagefriends_count":0,"statuses_count":6,"favourites_count":0,"created_at":"Sun Sep 28 17:05:18 +0800 2014","following":false,"allow_all_act_msg":false,"geo_enabled":true,"verified":false,"verified_type":-1,"remark":"","ptype":0,"allow_all_comment":true,"avatar_large":"http://tp4.sinaimg.cn/5309864355/180/0/1","avatar_hd":"http://tp4.sinaimg.cn/5309864355/180/0/1","verified_reason":"","verified_trade":"","verified_reason_url":"","verified_source":"","verified_source_url":"","follow_me":false,"online_status":1,"bi_followers_count":0,"lang":"zh-cn","star":0,"mbtype":0,"mbrank":0,"block_word":0,"block_app":0,"credit_score":0},"reposts_count":0,"comments_count":0,"attitudes_count":0,"mlevel":0,"visible":{"type":0,"list_id":0},"darwin_tags":[]},"pic_ids":[],"floor_num":2}],"hasvisible":false,"previous_cursor":0,"next_cursor":0,"total_number":1,"interval":0}
