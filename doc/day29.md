2014.11.13

## 目标

1. 解决微博项不能整体点击问题

2. 转发中的单个图片控件和图片显示大小的关系还是没计算好


## 完成

1. listview 的 item_weibo 最上层的 LinearLayout 添加 `android:descendantFocusability="blocksDescendants"`， item_weibo 中的 imageview 控件添加 
`android:focusable=false`。
