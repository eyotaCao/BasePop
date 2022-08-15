# 一、简介与使用：
安卓弹窗基类，可以适配全面屏手势，基于自定义view，功能与xpopup类似。

使用：
```
implementation 'com.github.EyoutaCao:BasePop:1.0.3'
```

优点:提升性能；基于view利于更改，可以完全自定义样式；全面屏手势下可以全屏展示，沉浸感更好；修复了xpopup的一些展示问题，原生dialog在全面屏手势下会出现导航栏不显示的情况，basePop可以全屏展示，当弹窗有EditText时可以实现弹窗自动移动到软键盘之上，拥有更好的交互体验。

缺点：不基于dialog，不具有安卓dialog某些特性，比如弹窗显示acrivity停止渲染。但如果需要基于dialog可以自行更改。

demo app可以下载体验:
```
https://www.pgyer.com/Mgqc
```

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
import com.example.basepop.basepop.base.BasePopCenter2;
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
#
更多使用可以查看本项目app文件中mainactivity的使用示例。
有问题可直接qq交流：957652774
