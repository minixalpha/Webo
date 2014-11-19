2014.11.18

## 目标

1. bug: 主页，搜索，设置里 Action Bar 返回上一层时闪退
2. refactor: 重构了 setText，使得设置各种 Text 基本功能保持一致，在基本功能基础上功能可以叠加
3. refactor: 重构微博中与at相关的代码，现在的代码像面条一样和各种模块耦合，性能又低
4. enhancement: 默认空白图片与图片控件大小不吻合


## 完成

1. bugfix: 闪退原因是重构时修改了 Activity 的名字，但是 AndroidManifest.xml 中的 PARENT_ACTIVITY 并
没有相应改变。

2. 重构了 setText，使得设置各种 setText 基本功能保持一致，在基本功能基础上功能可以叠加

3. 仅仅将 `at XXX` 设置成可点击的就好，当用户实际点击时，再搜索相应用户，而不用一遇到 `at XXX` 就
马上搜索，这样避免了无谓的搜索，提高了性能。另外，通过对微博HTML页面代码的观察，可以通过昵称直接
访问到用户的主页，假如用户昵称为 abc ，则在 at 信息中，可以通过 `http://weibo.com/n/abc?from=feed&loc=at` 来访问 abc 的主页
