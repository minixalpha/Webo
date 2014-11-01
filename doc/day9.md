## 目标
### 重构程序

* 自定义 Application 生成全局 context
* 将 Weibo API 单独抽象成一个类，使用单例模式，供全局使用

###  完成发微博模块

###  设计新消息模块，侧栏模块 UI


## 完成

### 重构程序

* 自定义 Application 生成全局 context，全局都使用这个 context. (**有 bug**)
* 将 Weibo API 单独抽象成一个类，使用单例模式，供全局使用
* 清理无用代码

###  发微博

* 实时更新字数限制
* 字数超出时提示，不发送
* 字数为0，也不发送
* 无网络连接不发送
* 发送后使用系统通知提示已经发送，通过先发 notification，再 cancel，使得通知消息在顶部一闪而过，用户不用手动取消。
* 发微博的异步API回调时，使用系统通知提示发送成功或者失败
* 使用本地广播，在发微博成功后，由发微博的 fragment 向其所在 activity 发送一条广播，使这个 activity 切换到查看微博的　fragment
* View Pager 切换到写微博模块时，要自动打开软键盘，通过 EditText.requestFocus 获得焦点，再通过 INPUT_METHOD_SERVICE 服务打开软键盘。


###  完成新消息模块

* 使用两个动态 Fragment 嵌套在新消息Fragment下，通过两个按钮点击动态切换 Fragment，同时切换图标颜色。为评论和at设计两个新的 Adapter 类。

## 重构及Bug

1. 不能全局都使用同一个　contex ，例如　ViewWeiboFragment 中，　ListView 的　Adapter，　就需要使用　ViewWeiboFragment 的　Activity 
作为　contex, 而不能使用全局的 contex, 否则会引起　UI 的改变，这里出现的　bug是字体颜色发生了改变。全局的　contex 用于　Toast, share preferences 等等。

重构的时候，每重构到一个可运行的状态，就要检查下程序的状态是否改变，否则最后很难知道是哪里的改变导致程序出错。

2. 输入法的打开与关闭应该放在写微博模块中，切入时打开，切出时关闭，这样这个功能就独立于其它模块，不用 ViewPagerActivity 统一调度了。
