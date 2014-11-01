2014.10.31

## 目标

* refactor: WeiboItemAdapter, CommentsAdapter, 使用 View Holder 提升效率，统一获取，使用 view.
* 单个图片中，根据微博中图片的实际长宽，来设定 GridView 的长宽。
* 定制包含自定义属性的控件，以达到复用效果
* 分离微博项按钮与数目，以便降低耦合，利于复用

## 完成

* refactor: WeiboItemAdapter, CommentsAdapter, 使用 View Holder 提升效率，统一获取，使用 view.
* 单个图片中，根据微博中图片的实际长宽，来设定 GridView 的长宽。
* 定制包含自定义属性的控件，以达到复用效果
* enhacement: 个人中心的微博设计添加成时间＋转发数＋评论数
* 调整时间线上微博底部按钮中图片与数字的布局。


## Q&A

Q: 使用 View Holder 后， 第一条微博的配图在第三条微博上也出现了，而第三条微博本来是没有陪图的。
A: 问题的关键在于，有没有完全更新数据，避免上一次的缓存的数据在这一次显示出来。找到没有更新数据
的点，看看是什么导致数据没有更新的。

## Turorial

### 自定义属性

1. 建立 values/attrs.xml

2. 自定义属性

```
<declare-styleable name="DecorateTextView">
     <attr name="prefix" format="string" />
     <attr name="suffix" format="string" />
</declare-styleable>
```

3. 自定义相关类

```java
public class DecorateTextView extends TextView {
...
}
```

4. 定义控件

```
        <com.minixalpha.controls.DecorateTextView
            android:id="@+id/repost_count"
            style="@style/weiboBtnText"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            decorate:prefix="转发 ("
            decorate:suffix=")" />
```


