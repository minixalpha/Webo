2014.11.1

## 目标

* 分离微博项按钮与数目，以便降低耦合，利于复用
* 微博详情：原微博要跟随评论ListView滑动，ListView添加Progress bar
* 进一步重构，将可以复用的布局，代码，模块都抽取出来，坚持 DRY

## 完成

* 分离微博项按钮与数目，以便降低耦合，利于复用
* 将微博详情中原微博的 header, body 抽出，成为一个单独的布局 weibo_detail_header, 将这个布局作为评论列表的 header，从而使其成为评论列表的一部分
* 为评论列表添加 Progressbar，通过将总体布局设置成 RelativeLayout， 为Progress bar 设置属性 ` android:layout_centerInParent="true"` ,
将 Progressbar 设置在屏幕中间。

## 思考

* ProgressBar 应该是与网络请求操作绑定在一定的，请求前显示，请求完成后消失，如果没有网络请求，就不用显示了。


