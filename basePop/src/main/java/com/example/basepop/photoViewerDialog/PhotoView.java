package com.example.basepop.photoViewerDialog;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * 大图预览
 */
@SuppressWarnings("unused")
public class PhotoView extends AppCompatImageView {
    public PhotoViewAttacker attacker;
    private ScaleType pendingScaleType;

    public PhotoView(Context context) {
        this(context, null);
    }

    public PhotoView(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public PhotoView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        init();
    }

    private void init() {
        attacker = new PhotoViewAttacker(this);
        //We always pose as a Matrix scale type, though we can change to another scale type
        //via the attacher
        super.setScaleType(ScaleType.MATRIX);
        //apply the previously applied scale type
        if (pendingScaleType != null) {
            setScaleType(pendingScaleType);
            pendingScaleType = null;
        }
    }

    /**
     * Get the current {@link PhotoViewAttacker} for this view. Be wary of holding on to references
     * to this attacher, as it has a reference to this view, which, if a reference is held in the
     * wrong place, can cause memory leaks.
     *
     * @return the attacher.
     */
    public PhotoViewAttacker getAttacker() {
        return attacker;
    }

    @Override
    public ScaleType getScaleType() {
        return attacker.getScaleType();
    }

    @Override
    public Matrix getImageMatrix() {
        return attacker.getImageMatrix();
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        attacker.setOnLongClickListener(l);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        attacker.setOnClickListener(l);
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (attacker == null) {
            pendingScaleType = scaleType;
        } else {
            attacker.setScaleType(scaleType);
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        // setImageBitmap calls through to this method
        if (attacker != null) {
            attacker.update();
        }
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        if (attacker != null) {
            attacker.update();
        }
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        if (attacker != null) {
            attacker.update();
        }
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        boolean changed = super.setFrame(l, t, r, b);
        if (changed) {
            attacker.update();
        }
        return changed;
    }

    public void setRotationTo(float rotationDegree) {
        attacker.setRotationTo(rotationDegree);
    }

    public void setRotationBy(float rotationDegree) {
        attacker.setRotationBy(rotationDegree);
    }

    public boolean isZoomable() {
        return attacker.isZoomable();
    }

    public void setZoomable(boolean zoomable) {
        attacker.setZoomable(zoomable);
    }

    public RectF getDisplayRect() {
        return attacker.getDisplayRect();
    }

    public void getDisplayMatrix(Matrix matrix) {
        attacker.getDisplayMatrix(matrix);
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean setDisplayMatrix(Matrix finalRectangle) {
        return attacker.setDisplayMatrix(finalRectangle);
    }

    public void getSuppMatrix(Matrix matrix) {
        attacker.getSuppMatrix(matrix);
    }

    public boolean setSuppMatrix(Matrix matrix) {
        return attacker.setDisplayMatrix(matrix);
    }

    public float getMinimumScale() {
        return attacker.getMinimumScale();
    }

    public float getMediumScale() {
        return attacker.getMediumScale();
    }

    public float getMaximumScale() {
        return attacker.getMaximumScale();
    }

    public float getScale() {
        return attacker.getScale();
    }

    public void setAllowParentInterceptOnEdge(boolean allow) {
        attacker.setAllowParentInterceptOnEdge(allow);
    }

    public void setMinimumScale(float minimumScale) {
        attacker.setMinimumScale(minimumScale);
    }

    public void setMediumScale(float mediumScale) {
        attacker.setMediumScale(mediumScale);
    }

    public void setMaximumScale(float maximumScale) {
        attacker.setMaximumScale(maximumScale);
    }

    public void setScaleLevels(float minimumScale, float mediumScale, float maximumScale) {
        attacker.setScaleLevels(minimumScale, mediumScale, maximumScale);
    }

    public void setOnMatrixChangeListener(OnMatrixChangedListener listener) {
        attacker.setOnMatrixChangeListener(listener);
    }

    public void setOnPhotoTapListener(OnPhotoTapListener listener) {
        attacker.setOnPhotoTapListener(listener);
    }

    public void setOnOutsidePhotoTapListener(OnOutsidePhotoTapListener listener) {
        attacker.setOnOutsidePhotoTapListener(listener);
    }

    public void setOnViewTapListener(OnViewTapListener listener) {
        attacker.setOnViewTapListener(listener);
    }

    public void setOnViewDragListener(OnViewDragListener listener) {
        attacker.setOnViewDragListener(listener);
    }

    public void setScale(float scale) {
        attacker.setScale(scale);
    }

    public void setScale(float scale, boolean animate) {
        attacker.setScale(scale, animate);
    }

    public void setScale(float scale, float focalX, float focalY, boolean animate) {
        attacker.setScale(scale, focalX, focalY, animate);
    }

    public void setZoomTransitionDuration(int milliseconds) {
        attacker.setZoomTransitionDuration(milliseconds);
    }

    public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener onDoubleTapListener) {
        attacker.setOnDoubleTapListener(onDoubleTapListener);
    }

    public void setOnScaleChangeListener(OnScaleChangedListener onScaleChangedListener) {
        attacker.setOnScaleChangeListener(onScaleChangedListener);
    }

    public void setOnSingleFlingListener(OnSingleFlingListener onSingleFlingListener) {
        attacker.setOnSingleFlingListener(onSingleFlingListener);
    }

    public interface OnPhotoTapListener {
        void onPhotoTap(ImageView view, float x, float y);
    }

    public interface OnScaleChangedListener {
        void onScaleChange(float scaleFactor, float focusX, float focusY);
    }

    public interface OnSingleFlingListener {
        boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY);
    }

    public interface OnViewTapListener {
        void onViewTap(View view, float x, float y);
    }

    public interface OnOutsidePhotoTapListener {
        void onOutsidePhotoTap(ImageView imageView);
    }

    public interface OnMatrixChangedListener {
        void onMatrixChanged(RectF rect);
    }

    public interface OnViewDragListener {
        void onDrag(float dx, float dy);
    }


}
