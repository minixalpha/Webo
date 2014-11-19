2014.11.14

## 目标

1. 解决listview 的 item_weibo 中的 textview 不响应整个 item 的 onclickitem 事件
2. 解决listview 中的 gridview 中非图片部分不响应整个 item 的 onclickitem 事件
3. 去除当前微博点击事件，转发微博点击后进入转发微博详情，评论按钮在没有评论时，进入发表评论页面，在
有评论时，进入评论列表页面，同时在底部添加编辑框，方便快速发表评论。
4. 转发中的单个图片控件和图片显示大小的关系还是没计算好
5. item_weibo 图片部分点开是图片大图，其它部分点开是相应微博详情。
6. 如果图片一时显示不出来，就不要占用位置
7. 图片问题不能快速显示

## 完成

1. 去除 item_weibo 的 textview 在设置链接时调用的 setMovementMethod
2. 把图片控件的宽度设置成与图片实际大小一致
4. 单个图片时，gridview 大小等于默认图片大小与图片实际大小中，长宽较小者
5. 在微博项外面加一层 layout，为此 layout 设置点击事件，点击时进入微博详情。再为图片设置点击事件，
点击时进入大图模式。
7. 不再使用Universal-ImageLoader loadImage 方式自己显示图片，直接使用 displayImage，同时，使用
ImageLoader 的 DisplayImageOptions 中的 postProcessor，在图片加载进入内存，但还没有显示时，执行
一段代码，这段代码此时已经可以获取要显示的图片的真实大小，此时可以调用图片控件的 post 方法，根据图片
的真实大小调整图片控件的大小，注意一定要调用 post 方法，而不能直接调整图片控件大小，因为非 UI 线程是
不能改变 UI的。

## 教程

1. 在文本中添加链接形式(表现为蓝色)，并去掉下划线

```
	// 在文本中添加链接形式(表现为蓝色)，并去年下划线
	private static void addLinkedContentWithoutUnderline(TextView tv,
			String content) {
		String addHrefContent = Utils.getHrefLink(content);
		Spannable sp = (Spannable) Html.fromHtml(addHrefContent);
		URLSpan[] urls = sp.getSpans(0, sp.length(), URLSpan.class);
		for (URLSpan url : urls) {

			int start = sp.getSpanStart(url);
			int end = sp.getSpanEnd(url);
			sp.removeSpan(url);

			WeiboSpan myURLSpan = new WeiboSpan(url.getURL());
			sp.setSpan(myURLSpan, start, end, 0);
		}
		tv.setText(sp);
	}
```
