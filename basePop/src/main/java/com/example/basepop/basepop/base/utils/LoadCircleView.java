package com.example.basepop.basepop.base.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.basepop.R;


public class LoadCircleView extends View {
    private Paint paint_bg, paint_normal;
    private int bg_color, normal_color;
    private Resources resources;
    private float arc_width;
    private int width, height;

    private int progress;

    public LoadCircleView(Context context) {
        this(context, null);
    }

    public LoadCircleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        resources = context.getResources();
        bg_color = resources.getColor(R.color.color33181D40);
        normal_color = resources.getColor(R.color.colorFFFFFF);
        init(context);
    }

    private void init(Context context) {
        paint_bg = new Paint();
        paint_bg.setStyle(Paint.Style.STROKE);
        paint_bg.setAntiAlias(true);
        paint_bg.setAlpha(255);

        paint_normal = new Paint();
        paint_normal.setStyle(Paint.Style.STROKE);
        paint_normal.setAntiAlias(true);
        paint_normal.setStrokeCap(Paint.Cap.ROUND);
        paint_normal.setAlpha(255);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        arc_width = (float) (width * 0.083);
        paint_normal.setColor(normal_color);
        paint_bg.setColor(bg_color);
        paint_normal.setStrokeWidth(arc_width);
        paint_bg.setStrokeWidth(arc_width);
        RectF rectF = new RectF(arc_width,arc_width,width-arc_width,height-arc_width);
        canvas.drawArc(rectF,progress-90, 360, false, paint_bg);
        canvas.drawArc(rectF,-90, progress, false, paint_normal);


    }

    public int getBg_color() {
        return bg_color;
    }

    public void setBg_color(int bg_color) {
        this.bg_color = bg_color;
    }

    public int getNormal_color() {
        return normal_color;
    }

    public void setNormal_color(int normal_color) {
        this.normal_color = normal_color;
    }

    public void setProgress(int p) {
        this.progress = p;
        postInvalidate();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }
}
