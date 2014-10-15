## 目标

### 设计侧栏 UI，完成相应功能

* 用户信息
* 主页
* 搜索
* 设置
* 退出

### 为新浪微博 android 补充 API

目前还没有获取指定用户最新微博的 android API，根据开放平台提供的接口进行补充

### 重构用户信息布局和代码

用户主页与侧栏都需要有用户信息，这一部分布局，代码都可以复用，把布局单独放在　user_info_layout.xml 中，谁需要谁就 include.
把获取用户信息的代码抽出来成为 UserInfoView 类，给它一个包含用户信息布局的 view，它会自动当当前用户的信息设置上去。

###　重构时间线相关的代码

时间线有两条，一个是用户关注人的时间线，一个是用户自己发的微博的时间线。时间线相关的代码是可以复用的,
给一个 listview，给出获取数据的方式，重构出的模块就应该自动用这些数据来设置 listview。

### 搜索模块

## 完成

* 完成侧栏UI设计
* 完成用户信息获取与缓存
* 完成主页切换，使用　nav up, 并把 parent activity(viewpager activity) 设置成　singleTop，将 menu drawer 设计成返回时自动收回
* 完成用户信息布局和代码的重构
* 完成重构时间线相关代码
* 搜索模块使用　SearchView 完成，将 SearchActivity 设置成 singleTop ，并添加 filter，处理搜索请求。
* 使用 SearchAPI 获取搜索用户名的结果，展示在 listview 中。并异步请求用户信息，使用图片缓存，加载头像。
