package com.demo.draglayout.view;

import com.demo.draglayout.view.DragLayout.State;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class InnerLinearLayout extends LinearLayout {

    private DragLayout mDragLayout;

    public InnerLinearLayout(Context context) {
        super(context);
    }

    public InnerLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDragLayout(DragLayout layout) {
        mDragLayout = layout;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mDragLayout != null) {
            if (mDragLayout.getState() == State.OPEN) {
                return true;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mDragLayout != null) {
            if (mDragLayout.getState() == State.OPEN && event.getAction() == MotionEvent.ACTION_UP) {
                mDragLayout.close(true);
            }
            return true;
        }
        return super.onTouchEvent(event);
    }
}
