## 目标

1. 使用开源项目[ActionBarSherlock](https://github.com/JakeWharton/ActionBarSherlock) [Android-ViewPagerIndicator](https://github.com/JakeWharton/Android-ViewPagerIndicator) 实现菜单栏的　menu, 　开源项目　[MenuDrawer](https://github.com/SimonVT/android-menudrawer) 实现侧栏

2. 完成发微博模块


## 完成

1. 添加了顶栏图标，设计选中未选中状态，引入侧栏。

2. 使用 View Pager 完成了滑动切换　Fragment，并改变　Menu Item 图标状态。

3. 完成发微博模块的 UI 设计


## 教程: com.minixalpha.webo.ViewPagerActivity

1. Action Bar 图标状态切换：Action bar 上的图标主要有三个：新微博，写微博，新消息。其中一个为选中状态，呈黑色，其它两个是灰色。在初始化 viewpager 时，可以为 viewpager
设置事件响应函数 onPageSelected，当选择了新的 pager 时，做什么动作。此时可以根据选择的 page ，设置三个图标的状态。状态的改变是通过替换图标来实现的。

2. View Pager 的使用

* 新建 ViewPagerActivity，布局为 `activity_view_pager.xml`，其中包含一个 View Pager

```
<?xml version="1.0" encoding="utf-8"?>
<android.support.v4.view.ViewPager xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/view_pager"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```

* 初始化 ViewPager：　通过 findViewById 获取 ViewPager, 通过　setOnPageChangeListener　设置　ViewPager 事件响应函数， 新建一个　PagerAdapter，添加　Tab, 并
通过　setAdapter 将 ViewPager 与此　PagerAdapter 关联。

3. 侧栏 MenuDrawer 的使用

* 将　MenuDrawer attach 到当前 Activity
* 通过 setMenuView 设置侧栏布局
* 通过 setContentView 设置内容区布局
