2014.10.30

# 微博详情模块

## 目标

* bug: 查看微博详情评论部分与提醒模块中的评论部分耦合度太大，导致其它模型变更后，微博详情评论部分
显示内容不正确。
* enhancement: 微博详情，评论加载时要显示进度条
* enhancement: listview 使用 convertView, viewHolder 优化
* enhacement: 将微博项，进一步模块化，将 (头像，用户名，时间) 单独抽出，形成 weibo_header 布局。
从而在 weibo_full_item, comment_item, simple_comment_item 中都通过 include 复用。
* enhacement: 加载更多评论
* enhacement: 
* 通过自定义属性，实现按钮控件，以便更好地复用布局和代码。
* bug: 名字太长时(最长是15个汉字，30个字符)，会覆盖时间
* 评论字体：评论中，weibo_header 中的字体应该小一点，避免占用空间比评论内容大很多。
* 个人中心，只显示微博内容，不显示 header, footer
* bug: 微博评论数目太大时会折行
* enhacement: 个人中心的微博设计应该和时间线上的区分开，因为个人中心的微博都是自己发的，涉及
转发，评论的不多，所以，展示时，可以不将 weibo_footer 设计成按钮，而只设计成时间＋转发数＋评论数，
点开微博后，在顶部加转发按钮，底部加评论。

## 完成

* bugfix: 通过布局来区分微博详情评论，和提醒模块中的评论，前者没有转发部分，不需要显示原微博，因为原微博就在最上面，而提醒模块需要标识评论了哪条微博。代码中，检测布局中是否有某个模块，如果有，就显示相应内容，如果没有，则不显示内容。
* enhancement: 将微博分成 weibo_header, weibo_content, weibo_footer 三个独立的 layout ，以便
可以在这个粒度上复用。例如个人中心，只用到 weibo_content，评论只用到 weibo_header, weibo_content
* enhancement: 评论部分， header 需要字体，高度变小，如何与微博 header 复用？ 目前是单独提出一个 
comment_header
* enhancement: 为 WeiboControllor 添加新构造函数，将单条微博布局与 WeiboControllor 解耦合，使得
WeiboControllor 可以就用于不同的单条微博布局。
* refactor: 重构 StatusAdapter，添加 View Holder 优化，保存各个控件的 View，避免反复获取，
添加检测功能，如果某个控件不存在，就不用设置内容了，以便于 StatusAdapter 适用用不同控件的组合。
* bugfix: 调整用户名宽度，使得名字太长时(最长是15个汉字，30个字符)，自动换行
* enhacement: 加载更多评论
* 个人中心，只显示微博内容，不显示 header, footer
