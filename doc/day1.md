# 新浪微博第三方客户端　Webo 开发手记

2014.09.28

## 新建　Android Project

### 版本
minimum version: 4.0, target version: 4.2, maximum version: 4.4

### 新建 Android Project 

建立一个　Hello World Project 测试一下环境

## 下载并调试新浪微博　API

* 从　[weibo_android_sdk](https://github.com/sinaweibosdk/weibo_android_sdk) 下载项目
* 阅读文档　微博Android平台SDK文档V2.5.0.pdf
* 在　Eclipse 里导入　demo-src 中的两个项目，按文档中说的安装到手机上，并调试使用

## 实现一个基本的登录，发微博功能

### 授权

* 开发者注册：在微博开放平台注册应用，获取 App Key，设置默认回调页
* 添加测试用户：没有审核的应用只能使用测试用户调用接口，在我的应用->SimpleWebo->测试信息　中添加测试用户。
* 授权: SSO 授权使用官方微博客户端，不适用于第三方微博客户端使用，Code 授权又有安全隐患，所以这里使用　Web 授权方式
* 源代码中按注册好的信息设置　App Key，回调页

### UI

* 隐藏标题栏

由于使用Web授权时，使用的UI是SDK中包含的，为了配合这个UI，隐藏标题栏。

不过默认的　UI 非常丑，要怎么修改呢？




