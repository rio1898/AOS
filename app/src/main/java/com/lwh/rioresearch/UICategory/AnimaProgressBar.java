package com.lwh.rioresearch.UICategory;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.lwh.rioresearch.R;

/**
 * 进度条
 *
 * @author xiaxl1
 */

public class AnimaProgressBar extends View {

    private static final String TAG = "AnimaProgressBar";

    // 动画时间
    private final int ANIMA_TIME = 300;
    // 总共的级别
    private final int TOTAL_PROGRESS = 100;

    /**
     * UI
     */
    // ---画笔---
    // 画笔
    private Paint mPaint = null;
    private PaintFlagsDrawFilter mPaintFlagsDrawFilter = new PaintFlagsDrawFilter(
            0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);


    /**
     * 数据
     */
    // View的宽
    private int mViewWidth = 0;
    // View的高
    private int mViewHeight = 0;


    // 进度条高度
    private float mProgressHeight;
    // 进度条padding
    private float mProgressPaddingLeftRight;
    // 进度条颜色
    private int mProgressColor;
    // 进度条背景颜色
    private int mProgressBgColor;
    // 进度条圆角大小
    private float mProgressRadius;
    // 当前的显示进度
    private float mCurrentProgress = 1;
    // progress header
    private Bitmap mProgressMaskerBmp = null;
    private NinePatch mProgressMaskerNP = null;
    /**
     * 进度条进度
     */
    // 总共的级别
    private int mTotalProgress = TOTAL_PROGRESS;


    /**
     * #############################构造方法###################################
     */
    public AnimaProgressBar(final Context context) {
        this(context, null);
    }

    public AnimaProgressBar(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimaProgressBar(final Context context, final AttributeSet attrs,
                            final int defStyle) {
        super(context, attrs, defStyle);
        // #########加载att################
        final TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.AnimaProgressBar);
        //
        if (attributes != null) {
            try {
                // 进度高度
                setProgressHeight(attributes.getDimension(R.styleable.AnimaProgressBar_progress_height, 0));
                // 进度padding
                setProgressPaddingLeftRight(attributes.getDimension(R.styleable.AnimaProgressBar_progress_padding_left_right, 0));
                // 进度颜色
                setProgressColor(attributes.getColor(R.styleable.AnimaProgressBar_progress_color, Color.RED));
                // 背景颜色
                setProgressBgColor(attributes.getColor(R.styleable.AnimaProgressBar_progress_bg_color, Color.CYAN));
                // 圆角大小
                setProgressRadius(attributes.getDimension(R.styleable.AnimaProgressBar_progress_radius, 10));
                // 进度
                setCurrentProgress(attributes.getFloat(R.styleable.AnimaProgressBar_progress, 0));
                //蒙板图片
                int maskId = attributes.getResourceId(R.styleable.AnimaProgressBar_progress_masker, 0);
                if (maskId != 0) {
                    setProgressMaskerBmp(BitmapFactory.decodeResource(context.getResources(), maskId));
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                attributes.recycle();
            }
        }
        //
        initPaint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        /*
         * 数据
		 */
        mViewWidth = this.getMeasuredWidth();
        mViewHeight = this.getMeasuredHeight();
        // 打一个补丁
        if (this.mProgressHeight == 0) {
            this.mProgressHeight = mViewWidth;
        }
    }


    private void initPaint() {
        /*
         * 画笔
		 */
        //
        mPaint = new Paint();
        // 抗锯齿处理
        mPaint.setAntiAlias(true);
        mPaintFlagsDrawFilter = new PaintFlagsDrawFilter(0,
                Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 抗锯齿用
        canvas.setDrawFilter(mPaintFlagsDrawFilter);

        // 进度条左右padding
        float progressPaddingLeftRight = getProgressPaddingLeftRight();
        // 高度
        float progressHeight = getProgressHeight();
        // 每一段线段的长度
        float lineSegmentLength = (mViewWidth - 2 * progressPaddingLeftRight) / (float) mTotalProgress;
        // 当前进度
        float progressEndX = progressPaddingLeftRight + lineSegmentLength * getCurrentProgress();

        // ######### 绘制圆角矩形 #########
        // 绘制背景
        mPaint.setColor(getProgressBgColor());
        float top = (mViewHeight - progressHeight) / 2f;
        RectF bgRectf = new RectF(progressPaddingLeftRight, top,
                mViewWidth - progressPaddingLeftRight, top + progressHeight
        );
        canvas.drawRoundRect(bgRectf, getProgressRadius(), getProgressRadius(), mPaint);
        // 绘制进度
        mPaint.setColor(getProgressColor());
        RectF progressRectf = new RectF(progressPaddingLeftRight, top,
                progressEndX, top + progressHeight
        );
        canvas.drawRoundRect(progressRectf, getProgressRadius(), getProgressRadius(), mPaint);

        // 绘制蒙板
        if (mProgressMaskerNP != null) {
            // 每一段线段的长度
            lineSegmentLength = mViewWidth / (float) mTotalProgress;
            // 当前进度
            progressEndX = lineSegmentLength * getCurrentProgress();
            //
            mProgressMaskerNP.draw(canvas,
                    //
                    new Rect(0, 0,
                            (int) progressEndX, mViewHeight), null);

        }
    }


    private ObjectAnimator mProgressAnimator;
    private boolean mIsAnimating = false;

    /**
     * 设置显示的进度，有动画
     *
     * @param targetProgress
     */
    public void setProgressWithAnima(float targetProgress) {
        //
        if (targetProgress <= mCurrentProgress) {
            setCurrentProgress(targetProgress);
            return;
        }
        if (mIsAnimating) {
            mProgressAnimator.cancel();
        }
        mProgressAnimator = ObjectAnimator.ofFloat(this, "currentProgress", mCurrentProgress,
                targetProgress);
        mProgressAnimator.setDuration(ANIMA_TIME);
        mProgressAnimator
                .setInterpolator(new AccelerateDecelerateInterpolator());

        mProgressAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                mIsAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mIsAnimating = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        mProgressAnimator.start();
    }


    //##################################################################################

    /**
     * 获取当前的显示级别
     *
     * @return
     */
    public float getCurrentProgress() {
        return mCurrentProgress;
    }

    /**
     * 设置显示级别
     *
     * @param progress
     */
    public void setCurrentProgress(float progress) {
        // 当前的级别
        this.mCurrentProgress = progress;

        // 刷新界面
        this.invalidate();
    }


    public int getProgressColor() {
        return mProgressColor;
    }

    public void setProgressColor(int progressColor) {
        this.mProgressColor = progressColor;
    }

    public int getProgressBgColor() {
        return mProgressBgColor;
    }

    public void setProgressBgColor(int progressBgColor) {
        this.mProgressBgColor = progressBgColor;
    }

    public float getProgressRadius() {
        return mProgressRadius;
    }

    public void setProgressRadius(float progressRadius) {
        this.mProgressRadius = progressRadius;
    }

    public Bitmap getProgressHeaderMaskBmp() {
        return mProgressMaskerBmp;
    }

    public void setProgressMaskerBmp(Bitmap progressHeaderMask) {
        this.mProgressMaskerBmp = progressHeaderMask;
        if (progressHeaderMask != null) {
            this.mProgressMaskerNP = new NinePatch(mProgressMaskerBmp,
                    mProgressMaskerBmp.getNinePatchChunk(), null);
        }

    }


    public float getProgressHeight() {
        return mProgressHeight;
    }

    public void setProgressHeight(float mProgressHeight) {
        this.mProgressHeight = mProgressHeight;
    }

    public float getProgressPaddingLeftRight() {
        return mProgressPaddingLeftRight;
    }

    public void setProgressPaddingLeftRight(float mProgressPaddingLeftRight) {
        this.mProgressPaddingLeftRight = mProgressPaddingLeftRight;
    }
}