2014.11.7

## 目标

* 完成设置模块
* bug: 刷新旧微博时，把原来微博也清空了，原来微博是要保留的。刷新新微博时，原来微博可以不保留。


## 完成

* 使用 Preference 完成设置模块中类似按钮的功能
* 使用监听器模式，在设置中的控件状态改变时，得到通知，从而设置系统状态。
* 在其它 Activity 中，通过 PreferenceManager 获取某个控件的状态
* 将各个状态的获取统一到 com.minixalpha.webo.data.Configure 类中
* 改变字体大小
* bugfix: 请求旧微博时，保留原有微博
* 完成中图，小图，无图的切换


## Q&A

Q: 如何在设置字体大小后，可以立即看到字体改变后的效果
A: 设置模块实现 onSharedPreferenceChanged, 在设置改变时， 设置 Setting 类的一个标记位，
在字体所在模块的 onResume 中，检测到 Setting 类的标记位被设置，就调用  notifyDataSetChanged，
然后将 Setting 类的标记位重置。
