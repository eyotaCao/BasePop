# 一、简介与使用：
安卓弹窗基类，可以适配全面屏手势，基于自定义view，功能与xpopup类似。

使用：
最新版：
[![](https://jitpack.io/v/EyoutaCao/BasePop.svg)](https://jitpack.io/#EyoutaCao/BasePop)


优点:提升性能；完全基于自定义view利于更改和扩展；全面屏手势下可以全屏展示，沉浸感更好；修复了xpopup的一些展示问题，原生dialog在全面屏手势下会出现导航栏不显示的情况，basePop可以全屏展示，当弹窗有EditText时可以实现弹窗自动移动到软键盘之上，拥有更好的交互体验。代码简洁，调起弹窗可以只要一行代码。



demo app可以下载体验: [demo](https://www.pgyer.com/Mgqc)


## 一些方法:
```
    //开启弹窗
    public void show()
    
    //关闭弹窗
    public void disminss()
    
    //设置弹窗监听回调
    public BasePopAttach setPopListener(MyPopLis myPopLis)
    
    //顶部依附弹窗需设置依附的在activity上的view
    public BasePopTop atView(View view)
    
    //设置没有阴影的背景点击可穿透
    public BasePopTop setClickThrough(boolean clickThrough)

    //仅在顶部弹窗模式且依附view会移动的情况下设置true
    public BasePopTop setMove(boolean move)
    
    //设置弹窗最大高度
    public BasePopTop setMaxHeight(int max)
    
    //设置弹窗是否可拖拽
    public BasePopTop setConScrollAble(boolean conScrollAble)
    
    //设置内容居中
    public BasePopTop setContentCenter(boolean contentCenter) 
    
    //依附弹窗设置依附的view
    public BasePopAttach setAttachView(View mAttachView) 
    
    //依附弹窗设置开启动画样式
    public BasePopAttach setAnimType(Animate animType)

    //设置x轴偏移量
    public BasePopAttach setOffsetX(int offsetX)
    
    //设置编辑框自动弹起
    public BasePopBottom setAutoEdit(boolean autoEdit)
    ...
```

# 二、使用示例：
## 1、中心弹窗
### 1.1、继承基类
```
import android.app.Activity;
import androidx.annotation.NonNull;
package com.example.basepop.BasePopCenter;
import com.example.cwjmodels.R;

public class Center extends BasePopCenter2 {
    public Center(@NonNull Activity context) {
        super(context);
    }

    @Override
    protected void onCreate() {
        super.onCreate();

    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_center_normal;
    }
}
```
### 1.2、在activity中使用
```
Center center=new Center(this);
center.show();
```
## 2、图片弹窗
### 2.1、直接使用
```
ImageDialog imageDialog=new ImageDialog(this);
//1：自定义图片加载
imageDialog.setSrcView(binding.popCenter7)
.setLoadImage(view1 -> {
     view1.setImageResource(R.mipmap.ic_launcher);
}).show();
//2：默认图片加载（使用Glide获取网络图片）
imageDialog.setSrcView(binding.popCenter7).setUrl("...").show();
```
## 3、其他弹窗
### 3.1、类名
```
1、中心弹框 从底部弹出：BasepopCenter
1、依附于某个view弹窗：BasePopAttach
1、底部弹框 有输入框自动弹起：BasePopBottom
1、中心弹框  中心弹出动画：BasePopCenter2
1、中心弹框  中心弹出动画 有编辑框自动弹起 从底部弹出：BasePopCenterEdit
1、头部弹框：BasePopTop
```
#
更多使用可以查看本项目app文件中mainactivity的使用示例。
有问题可直接qq交流：957652774
