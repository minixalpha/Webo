2014.11.4

## 目标

* 将 body_weibo 的各个部分 padding, margin 统一设置，使得外观统一
* 将相同的属性 style 化，提高复用度，以便修改时统一修改
* 设计一个合理的 Progress Bar，包括位置，出现时机，消失时机


## 完成

* 将 body_weibo 的各个部分 padding, margin 统一设置，使得外观统一
* 完成全部布局 style 化，今后再添加控件，首先就想着把 style 作的通用
* Progress Bar 出现时机: List中没有内容，正在加载时。消失时机：List 中的内容加载完成。位置：ListView
的中间。并且仅仅出现在自动刷新时，手动刷新时，PullToRefresh 已经有 Progress Bar 了。
* 将Progress Bar 与 ListView 绑定在一起，通过代码动态显示，消失，不再
在每个需要 ListView 的 layout 中都添加 Progress Bar

