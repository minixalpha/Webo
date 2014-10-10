## 目标

1. 完成对微博正文中链接， at 链接的处理

2. 严重 bug: 无缓存时，ViewWeiboActivity不显示内容，但是退出后，缓存后，再进入就显示了内容。

3. 各种图片及内容缓存要早点获取，不要等 item 进入屏幕再去获取，那样体验不好。

4. 开始设计新模块，设置 menu，侧栏。

## 完成

1. 
通过正则表达式提取出链接文本，并替换成可href链接。
通过正则表达式提取出所有 at, 通过 searchAPK 得到 at　名字的　uid, 替换成包含主页链接的 href 格式。

2. bug 的原因是 listview 的 height 应该设置为　match_parent，而不是　wrap_content，否则第一次显示时因为高度为0,
即使有数据也不显示。第二次显示有了数据才会显示全。好奇怪。。。　

3. 找到开源项目[ActionBarSherlock](https://github.com/JakeWharton/ActionBarSherlock) [Android-ViewPagerIndicator](https://github.com/JakeWharton/Android-ViewPagerIndicator) 实现菜单栏的　menu, 　开源项目　[MenuDrawer](https://github.com/SimonVT/android-menudrawer) 实现侧栏
