package com.icoder.couldnewsclient.widget;

import android.content.Context;
import android.graphics.PointF;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 *	问题描述
 *	ViewPager嵌套ViewPager的时候,父层的ViewPager会自动拦截滑动的事件,因此需要重写父控件中的OnTouchEvent方法,调用其
 *	父控件的requestDisallowInterceptTouchEvent()方法,让父控件不拦截触屏事件
 */
public class CustomViewPager extends ViewPager{

	public CustomViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomViewPager(Context context) {
		super(context);
	}

	//	这是一个float类型的点
	private PointF point = new PointF();
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		int count = getChildCount();
		if(count > 1){
			getParent().requestDisallowInterceptTouchEvent(true);
		}

		switch(ev.getAction()){
			case MotionEvent.ACTION_DOWN:
				point.x = ev.getX();
				point.y = ev.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				break;
			case MotionEvent.ACTION_UP:
				//如果点击的偏移量不超过8.0f就判定为点击了
				if(PointF.length(Math.abs(point.x - ev.getX()), point.y - ev.getY()) < 8.0f){
					return true;
				}
				break;
		}
		return super.onTouchEvent(ev);
	}

	@Override
	public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
		super.requestDisallowInterceptTouchEvent(disallowIntercept);
	}
}