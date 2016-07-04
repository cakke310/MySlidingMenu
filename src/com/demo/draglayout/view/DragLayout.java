package com.demo.draglayout.view;

import com.demo.draglayout.util.EvaluateUtil;
import com.nineoldandroids.view.ViewHelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class DragLayout extends FrameLayout {
    private ViewDragHelper mDragHelper;
    // 提供信息, 接收事件
    private ViewDragHelper.Callback mCallback = new Callback() {
        // 是否捕获(处理)子View, 当触摸到某个子View时, 这个方法会被调用
        // child: 当前触摸到的子View
        // pointerId: 多点触摸的id, 用不到
        // 返回值决定了是否处理
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            // if(child == mMainContent) {
            // return true;
            // }else if(child == mLeftContent) {
            // return false;
            // }
            return child == mMainContent || child == mLeftContent;
        }

        // 修正View的水平位置
        // child: 整在处理的子View
        // left: ViewDragHelper建议的View的left值, left值决定了View的水平位置
        // dx: ViewDragHelper建议的left值和当前View实际的left值的差值.
        // 返回值决定View最终的left值
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            System.out.println("clampViewPositionHorizontal left: " + left + " dx: " + dx
                    + " oldLeft: " + child.getLeft());
            if (child == mMainContent) {
                left = fixLeft(left);
            }
            return left;
        }

        private int fixLeft(int left) {
            if (left < 0) {
                return 0;
            } else if (left > mDragRange) {
                return mDragRange;
            }
            return left;
        };

        // 不影响View的水平拖拽范围, 影响View松手后的动画时长
        // 如果页面中有ListView等可以滑动的控件, 那么这个方法必须返回一个大于0的值, 否则水平方向无法拖动
        // 返回实际的拖拽范围即可
        public int getViewHorizontalDragRange(View child) {
            return mDragRange;
        };

        // View的位置发生改变时调用
        // changedView: 位置发生改变的View
        // left: View当前的位置(位置已经发生改变)
        // dx: View当前的left值和位置改变之前的left值的差值
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            System.out.println("onViewPositionChanged left: " + left + " dx: " + dx + " oldLeft: "
                    + changedView.getLeft());
            if (changedView == mLeftContent) {
                mLeftContent.layout(0, 0, mWidth, mHeight);
                int newLeft = mMainContent.getLeft() + dx;
                newLeft = fixLeft(newLeft);
                mMainContent.layout(newLeft, 0, newLeft + mWidth, mHeight);
            }
            // 根据主面板的left值做动画
            dispathDragState(mMainContent.getLeft());
            invalidate(); // 解决2.3.3 无法拖动的bug
        };

        // 当松手的时候调用
        // xvel: 松手时水平方向的速度, 向着x轴正方形松手, 速度为正, 向着x轴负方向松手, 速度为负
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            System.out.println("onViewReleased xvel: " + xvel);
            if (xvel == 0.0f && mMainContent.getLeft() < mDragRange * 0.5f) {
                close();
            } else if (xvel < 0.0f) {
                close();
            } else {
                open();
            }
        };
    };
    private ViewGroup mLeftContent;
    private ViewGroup mMainContent;
    private int mHeight;
    private int mWidth;
    private int mDragRange;

    public enum State {
        CLOSE, OPEN, DRAGGING
    }

    private State mState = State.CLOSE;

    public interface OnDragStateChangeListener {
        void onOpen();

        void onClose();

        void onDragging(float percent);
    }

    private OnDragStateChangeListener mOnDragStateChangeListener;

    public State getState() {
        return mState;
    }

    public void setState(State state) {
        mState = state;
    }

    public OnDragStateChangeListener getOnDragStateChangeListener() {
        return mOnDragStateChangeListener;
    }

    public void setOnDragStateChangeListener(OnDragStateChangeListener onDragStateChangeListener) {
        mOnDragStateChangeListener = onDragStateChangeListener;
    }

    // new 出来
    public DragLayout(Context context) {
        this(context, null);
    }

    public void dispathDragState(int left) {
        float percent = left * 1.0f / mDragRange;
        animViews(percent);
        State preState = mState;
        mState = updateState(percent);
        // if(mState == State.CLOSE) {
        // // mOnDragStateChangeListener.onClose();
        // }else if(mState == State.OPEN) {
        // // mOnDragStateChangeListener.onOpen();
        // }
        if (mOnDragStateChangeListener != null) {
            mOnDragStateChangeListener.onDragging(percent);
            if (mState != preState) {
                if (mState == State.CLOSE) {
                    mOnDragStateChangeListener.onClose();
                } else if (mState == State.OPEN) {
                    mOnDragStateChangeListener.onOpen();
                }
            }
        }
    }

    private State updateState(float percent) {
        if (percent == 0.0f) {
            return State.CLOSE;
        } else if (percent == 1.0f) {
            return State.OPEN;
        } else {
            return State.DRAGGING;
        }
    }

    private void animViews(float percent) {
        // 1.8.1. 缩放(主面板, 左面板)
        // 1.0f - 0.8f >>> percent 0.0f - 1.0f
        // mMainContent.setScaleX(1.0f+(0.8f-1.0f)*percent);
        // mMainContent.setScaleY(1.0f+(0.8f-1.0f)*percent);
//        ViewHelper.setScaleX(mMainContent, 1.0f + (0.8f - 1.0f) * percent);
//        ViewHelper.setScaleY(mMainContent, 1.0f + (0.8f - 1.0f) * percent);
        // 0.5f -1.0f
//        ViewHelper.setScaleX(mLeftContent, EvaluateUtil.evaluateFloat(percent, 0.5f, 1.0f));
//        ViewHelper.setScaleY(mLeftContent, EvaluateUtil.evaluateFloat(percent, 0.5f, 1.0f));
        // 1.8.2. 位移(左面板)
        ViewHelper.setTranslationX(mLeftContent,
                EvaluateUtil.evaluateFloat(percent, -mWidth * 0.5f, 0.0f));
        // 1.8.3. 透明度(左面板)
//        ViewHelper.setAlpha(mLeftContent, EvaluateUtil.evaluateFloat(percent, 0.0f, 1.0f));
        // 1.8.4. 亮度(背景)
//        getBackground().setColorFilter(
//                (Integer) EvaluateUtil.evaluateArgb(percent, Color.BLACK, Color.TRANSPARENT),
//                Mode.SRC_OVER);
    }

    public void open() {
        open(true);
    }

    public void open(boolean isSmooth) {
        int finalLeft = mDragRange;
        if (isSmooth) {
            mDragHelper.smoothSlideViewTo(mMainContent, finalLeft, 0);
            invalidate();
        } else {
            mMainContent.layout(finalLeft, 0, finalLeft + mWidth, mHeight);
        }
    }

    protected void close() {
        close(true);
    }

    public void close(boolean isSmooth) {
        int finalLeft = 0;
        if (isSmooth) {
            // "触发"一个平滑动画, 并且计算第一帧
            mDragHelper.smoothSlideViewTo(mMainContent, finalLeft, 0);
            invalidate();
        } else {
            mMainContent.layout(finalLeft, 0, finalLeft + mWidth, mHeight);
        }
    }

    // 在布局文件里配置
    public DragLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    // 这个构造方法外部不调用, 需要另外两个构造方法调用
    public DragLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        // forParent: 要监视的父View
        // cb: Callback to provide information and receive events
        mDragHelper = ViewDragHelper.create(this, mCallback);
    }

    // 让ViewDragHelper 处理触摸事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }

    // 让 ViewDragHelper 决定是否拦截事件
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    // 所有的子View都被加进来之后调用
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() < 2) {
            throw new RuntimeException("You must have at least 2 child views");
        }
        if (!(getChildAt(0) instanceof ViewGroup) || !(getChildAt(1) instanceof ViewGroup)) {
            throw new RuntimeException("Your child view must be ViewGroup");
        }
        mLeftContent = (ViewGroup) getChildAt(0);
        mMainContent = (ViewGroup) getChildAt(1);
    }

    // @Override
    // protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    // super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    //
    // }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = getMeasuredHeight();
        mWidth = getMeasuredWidth();
        mDragRange = (int) (mWidth * 0.6f);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }
}
