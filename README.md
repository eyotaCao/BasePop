安卓弹窗基类，可以适配全面屏手势，基于自定义view，功能与xpopup类似。

使用：implementation 'com.github.Ablexq:HttpHelper:1.0.3'

优点:提升性能；基于view利于更改，可以完全自定义样式；全面屏手势下可以全屏展示，沉浸感更好；修复了xpopup的一些展示问题。

缺点：不基于dialog，不拥有安卓dialog某些特性，比如弹窗显示acrivity停止渲染，但经过对比性能相差不大，而且dialog会出现导航栏不显示的情况，如果需要基于dialog可以自行更改。
#
使用示例：
##
1、中心弹窗
###
1、继承基类
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
2、在activity中使用
```
Center center=new Center(this);
center.show();
```
##
2、图片弹窗
###
1、直接使用
```
     ImageDialog imageDialog=new ImageDialog(this);
            imageDialog.setSrcView(binding.popCenter7).setLoadImage(view1 -> {
                view1.setImageResource(R.mipmap.ic_launcher);
            }).show();
```
