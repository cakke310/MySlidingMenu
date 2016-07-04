package com.demo.draglayout;

import java.util.Random;

import com.demo.draglayout.R;
import com.demo.draglayout.bean.Cheeses;
import com.demo.draglayout.view.DragLayout;
import com.demo.draglayout.view.InnerLinearLayout;
import com.demo.draglayout.view.DragLayout.OnDragStateChangeListener;
import com.demo.draglayout.view.DragLayout.State;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

    private ListView mLvMain;
    private ListView mLvLeft;
    private DragLayout mDl;
    private ImageView mIvHead;
    private InnerLinearLayout mIll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        mLvMain = (ListView) findViewById(R.id.lv_main);
        mLvLeft = (ListView) findViewById(R.id.lv_left);
        mDl = (DragLayout) findViewById(R.id.dl);
        mIvHead = (ImageView) findViewById(R.id.iv_head);
        mIll = (InnerLinearLayout) findViewById(R.id.ill);
        mIll.setDragLayout(mDl);
//        mLvMain.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
//                android.R.layout.simple_list_item_1, Cheeses.NAMES){
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//                TextView tv = (TextView) super.getView(position, convertView, parent);
//                tv.setTextColor(Color.BLACK);
//                return tv;
//            }
//        });
        mIvHead.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mDl.open();
				
			}
		});
        mLvLeft.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, Cheeses.sCheeseStrings));
        mDl.setOnDragStateChangeListener(new OnDragStateChangeListener() {
            
            @Override
            public void onOpen() {
                //mLvLeft.smoothScrollToPosition(new Random().nextInt(50));
            }
            
            @Override
            public void onDragging(float percent) {
//                ViewHelper.setAlpha(mIvHead, 1.0f-percent);
            }
            
            @Override
            public void onClose() {
                /*ObjectAnimator animator = ObjectAnimator.ofFloat(mIvHead, "translationX", 15f);
                animator.setDuration(500);
                animator.setInterpolator(new CycleInterpolator(4.0f));
                animator.start();*/
            }
        });
    }
}
