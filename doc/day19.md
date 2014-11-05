2014.11.2

## 目标

* 解决用户信息模块的复用问题，在左栏菜单及个人中心，均有用户信息界面，二者由于背景颜色不同，所以
只是字体颜色不同
* 将相同的属性 style 化，提高复用度，以便修改时统一修改

## 完成

* 无法通过设置 layout 的 theme 或者 style 设置整个 layout 中控件的字体。设置 style 只有两种方式，将 style 设置到 textview，或者将 theme 设置到 application 或者 activity。

## 教程

* 在 res/values/styles.xml 中定义 style，可以应用到控件的 style 或者 theme 中

```
    <style name="ConfTitle">
        <item name="android:textStyle">bold</item>
        <item name="android:textSize">15sp</item>
        <item name="android:paddingLeft">10dp</item>
        <item name="android:paddingTop">5dp</item>
    </style>
```
