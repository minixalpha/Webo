2014.10.29

## 目标

* 点击图片，显示原图，充满整个屏幕
* 多图模式下，右滑显示下一个图片，即 View Pager 模式


## 完成

* 可以完成充满整个屏幕，但不会调整图片位置，导致图片从中间部分开始显示
* 使用 PhotoView 使得点开的图片可以放大缩小
* 最终使用了 MATRIC 默认模式
* 多图模式下，默认是缩略图，需要通过对 URL 的变换，找到对应的中图: `thumbnailPicURL.replace("thumbnail", "bmiddle");`
* 多图模式下，使用 View Pager，左右滑切换图片
* 解决按钮图片不清晰问题:使用 ImageView, Text 组合模拟按钮，替换 Button+drawableLeft 的方式
* 将转发，评论，赞的图片，文字 style 统一到 values/style 下
* 通过 padding, margin 调整 转发，评论，赞的位置（ugly, 怎么设计 UI 才又漂亮，又高效？)
