2012.10.28

## 目标

* 正确显示微博中的图片，包括微博配图，多图
* 点击图片，显示原图，充满整个屏幕

## 完成

* 正确显示微博中的图片，包括微博配图，多图


## 思考

* 图片显示模式: 假如一个图片很长，那么用什么方式来显示呢？也就是说 ImageView.ScaleType，要设置成什么呢？
  - CENTER: 图片不会进行拉伸，如果 ImageView 比实际图片大，那么按实际图片显示，如果比实际图片小，那么只显示实际图片一部分。
  - CENTER_CROP: 按比例将图片拉伸，一直到图片的长宽都大于等于 ImageView 为止。可能只显示图片一部分。
  - CENTER_INSIDE: 按比例将图片拉伸，一直到图片的长宽都小于等于 ImageView 为止。会显示整个图片。

多图模式下，应该拉伸，保证九宫格中充满每个格，图片不用显示全。

单图模式下，应该显示整个图片，否则图片信息损失太多，用户无法判断图片信息量，可能就不会点开查看了。

## 教程

* 动态设置 GridView 布局

```java
int rowN = (pic_urlss.size() - 1) / 3 + 1;
ViewGroup.LayoutParams params = gridView.getLayoutParams();
params.height = 155 * rowN;
gridView.setLayoutParams(params);
```
