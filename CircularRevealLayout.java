package com.wayww.away.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by wayww on 2017/3/20.
 * github: https://github.com/covetcode
 */

public class CircularRevealLayout extends FrameLayout {
    private static final int DEFAULT_ANIMATION_DURATION = 500;
    private ValueAnimator mAnimator;
    private Path mCirclePath;

    private float mAnimationValue;

    //显示区域的外界矩形
    private RectF mVisibleRectF;

    //动画的起点
    private int mCenterX;
    private int mCenterY;

    private int mFarthest;

    public CircularRevealLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public CircularRevealLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircularRevealLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mCirclePath = new Path();
        mVisibleRectF = new RectF();
    }

    /**
     * 开始揭示动画
     * @param x 动画开始位置的x值
     * @param y 动画开始位置的y值
     */
    public void revealLayout(int x, int y){
        mCenterX = x;
        mCenterY = y;
        computeFarthest();
        computeVisibleRegion();
        if (mAnimator == null) {
            mAnimator = ValueAnimator.ofFloat(0,1);
            mAnimator.setDuration(DEFAULT_ANIMATION_DURATION);
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mAnimationValue = (float) animation.getAnimatedValue();
                    postInvalidate((int) (mVisibleRectF.left*mAnimationValue),(int) (mVisibleRectF.top*mAnimationValue)
                            ,(int) (mVisibleRectF.right*mAnimationValue),(int) (mVisibleRectF.bottom*mAnimationValue));

                }
            });
        }
        mAnimator.start();
    }

    public void hideLayout(){
        mAnimator.reverse();
    }
    public void hideLayout(int x,int y){
        mCenterX = x;
        mCenterY = y;
        computeFarthest();
        computeVisibleRegion();
        mAnimator.reverse();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (mAnimationValue == 0){
            return;
        }
        canvas.save();
        mCirclePath.reset();
        mCirclePath.addCircle(mCenterX,mCenterY,mFarthest*mAnimationValue, Path.Direction.CW);
        canvas.clipPath(mCirclePath);
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mAnimationValue == 0){
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void computeFarthest(){
        int farthestX = (getWidth() - mCenterX) * (getWidth() - mCenterX) > mCenterX * mCenterX
                ? getWidth() - mCenterX : mCenterX;
        int farthestY = (getHeight() - mCenterY) * (getHeight() - mCenterY) > mCenterY * mCenterY
                ? getHeight() - mCenterY : mCenterY;
        mFarthest = (int) Math.sqrt(farthestX*farthestX+farthestY*farthestY);
    }

    private void computeVisibleRegion(){
        mVisibleRectF.left = mCenterX-mFarthest;
        mVisibleRectF.right = mCenterX+mFarthest;
        mVisibleRectF.top = mCenterY-mFarthest;
        mVisibleRectF.bottom = mCenterY+mFarthest;
    }

}
