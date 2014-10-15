

## 目标

* ListView

完成 ListView 模块，并集成到主界面中


做了一天，ListView 基本的功能是有了，但是自己造轮子效果确实不太好，还有四天就 deadline了，开始用开源组件吧。

* 重构 ViewActivity

现在所有微博时间线上的逻辑都在 ViewActivity 时，包括 UI 的处理，Weibo SDK 初始化，数据获取。

现在把除了时间线 UI 之外的逻辑都分离到 WeiboControllor 类中。


## 目标完成

1. 集成开源 ListView 组件 [Android-PullToRefresh](https://github.com/chrisbanes/Android-PullToRefresh) 
2. 重构了 ViewActivity, 使得　ViewActivity 中只包含 UI 相关的初始化工作, 将刷新微博主要操作逻辑放在 TimelineController 类中，
获取微博后的回调接口类放在　TimelineCallback 中。




