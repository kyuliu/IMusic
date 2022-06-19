package com.example.imusic.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.imusic.R;
import com.example.imusic.util.LogUtil;


/**
 * 作者：Stran on 2017/3/27 16:55
 * 描述：${自定义ProgressBar}
 * 邮箱：strangermy@outlook.com
 *
 * @author Stran
 */
public class MusicProgressView
        extends LinearLayout {
    private ImageView mIvIcon;

    private boolean isProgressEnable = true;
    private int mMax = 100;
    private int mProgress;
    private RectF mOval;
    private RectF rectFRing;
    private Paint mPaint;
    private Paint grayPaint;
    /**
     *
     */
    private boolean useCenter = false;
    private float startAngle = -90;

    public MusicProgressView(Context context) {
        this(context, null);

    }

    @Override
    public void setGravity(int gravity) {
        super.setGravity(gravity);
    }

    private void init() {
        int strokeWidth = 6;
        grayPaint = new Paint();
        grayPaint.setColor(Color.parseColor("#ccdcdbdb"));
        grayPaint.setStyle(Paint.Style.STROKE);
        grayPaint.setAntiAlias(true);
        grayPaint.setStrokeWidth(strokeWidth);

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor("#E64040"));


    }

    public MusicProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //挂载布局

        View view = View.inflate(context, R.layout.music_progress_bar, this);
        mIvIcon = view.findViewById(R.id.music_progress_bar);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    /**
     * 在ViewGroup上绘制东西的时候往往重写的是dispatchDraw()
     *
     * @param canvas c
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        //绘制图标和文字
        super.dispatchDraw(canvas);
        int bottom = mIvIcon.getBottom();
        int top = mIvIcon.getTop();
        int left = mIvIcon.getLeft();
        int right = mIvIcon.getRight();
        drawGrayRing(canvas, left, top, right, bottom);
        drawProgress(canvas, left, top, right, bottom);
    }

    private void drawGrayRing(Canvas canvas, int left, int top, int right, int bottom) {
        if (rectFRing == null) {

            rectFRing = new RectF(left, top, right, bottom);
        }

        float sweepAngle = mMax * 1.0f / mMax * 360;
        canvas.drawArc(rectFRing, startAngle, sweepAngle, useCenter, grayPaint);

    }

    private void drawProgress(Canvas canvas, int left, int top, int right, int bottom) {
        if (isProgressEnable) {
            if (mOval == null) {
                mOval = new RectF(left, top, right, bottom);
            }
            float sweepAngle = mProgress * 1.0f / mMax * 360;
            canvas.drawArc(mOval, startAngle, sweepAngle, useCenter, mPaint);
        }

    }


    /**
     * 设置是否允许进度
     *
     * @param progressEnable p
     */
    public void setProgressEnable(boolean progressEnable) {
        isProgressEnable = progressEnable;
    }

    /**
     * 设置进度的最大值
     *
     * @param max m
     */
    public void setMax(int max) {
        mMax = max;
    }

    /**
     * 设置进度的当前值
     *
     * @param progress p
     */
    public void setProgress(int progress) {
        mProgress = progress;
        postInvalidate();
    }

    /**
     * 修改图标的内容
     */
    public void setIcon(int resId) {
        mIvIcon.setImageResource(resId);
    }
}
