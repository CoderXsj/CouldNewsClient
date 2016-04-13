package com.icoder.couldnewsclient.widget;

import android.animation.LayoutTransition;
import android.content.ClipData;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import com.icoder.couldnewsclient.R;
import com.icoder.couldnewsclient.utils.ThemeUtils;

import java.util.List;

/**
 * GridLayout这里面有一个坑:
 * GridLayout没有对
 */
public class DragLayout extends GridLayout {
	public static final int COL_NUM = 3;		//4列
	public static int HORIZONTAL_MARGIN = 8;
	public static int VERTICAL_MARGIN = 10;

	//时候允许拖拽
	private boolean isAllowDrag = true;
	private Rect[] childRects;
	private View dragedView;

	public void setIsAllowDrag(boolean isAllowDrag){
		this.isAllowDrag = isAllowDrag;
	}

	public static interface OnItemClickListener{
		public void onItemClick(View view, GridItem item);
	}

	private OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(itemClickListener != null)
				itemClickListener.onItemClick(v, (GridItem)v.getTag());
		}
	};

	private OnItemClickListener itemClickListener;
	public void setItemClickListener(OnItemClickListener itemClickListener) {
		this.itemClickListener = itemClickListener;
	}


	public DragLayout(Context context) {
		this(context, null);
	}

	public DragLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DragLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	/**
	 * 这里只是写了一段文本,但是为了扩展,将子项写成一个接口
	 */
	public static interface GridItem{
		String getItemInfo();

		Drawable getBackgroundDrawable();

		int getTextColor();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		int count = getChildCount();

		if(isAllowDrag){
			for(int i = 0;i < count;i++){
				getChildAt(i).setOnClickListener(clickListener);
				getChildAt(i).setOnLongClickListener(longClickListener);
			}
			setOnDragListener(dragListener);
		}
	}

	private void init(){
		//设置列数
		setColumnCount(COL_NUM);
		//设置改变布局奇偶
		setLayoutTransition(new LayoutTransition());

		int[] themeColor = ThemeUtils.getThemeColor(getContext(), R.attr.containerBackground);
		setBackgroundColor(themeColor[0]);
	}

	public void setItems(final List<? extends GridItem> items){
		post(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < items.size(); i++) {
					GridItem item = items.get(i);

					TextView tv = new TextView(getContext());
					tv.setText(item.getItemInfo());
					tv.setTag(item);
					tv.setGravity(Gravity.CENTER);
					tv.setPadding(10, 10, 10, 10);
					tv.setBackgroundResource(R.drawable.drag_item_selector);
					tv.setTextColor(item.getTextColor());
//			tv.setOnLongClickListener(longClickListener);
					LayoutParams lp = getLayoutParamsByIndex(i);
					tv.setLayoutParams(lp);
					addView(tv, i);
				}
			}
		});
	}

	private PointF point = new PointF();
	Rect rect = new Rect();
	/**
	 * 对于这个方法的返回值
	 * 下面是android开发文档中一段内容
	 *  Next, the system sends a drag event with action type ACTION_DRAG_STARTED to the drag event listeners
	 *  for all the View objects in the current layout. To continue to receive drag events, including a possible drop event,
	 *  a drag event listener must return true. This registers the listener with the system. Only registered listeners continue 
	 * 	to receive drag events. At this point, listeners can also change the appearance of their View object to show that the 
	 *  listener can accept a drop event.
	 If the drag event listener returns false, then it will not receive drag events for the current operation until the
	 system sends a drag event with action type ACTION_DRAG_ENDED. By sending false, the listener tells the system that
	 it is not interested in the drag operation and does not want to accept the dragged data.
	 上面的意思大概是说如果你要是这个方法return true；表示你对拖拽事件感兴趣,系统会源源不断的发送事件给你
	 如果是返回false表示你对这个拖拽事件不感兴趣,系统在不会给你发送多余的消息直到ACTION_DRAG_ENDED事件发生为止
	 */
	int index;
	private OnDragListener dragListener = new OnDragListener() {
		@Override
		public boolean onDrag(View v, DragEvent event) {
			switch (event.getAction()) {
				case DragEvent.ACTION_DRAG_STARTED:
					initRect();
					if (dragedView != null) {
						dragedView.setAlpha(0.0f);
					}
					break;
				case DragEvent.ACTION_DRAG_LOCATION:
					index = findTouchViewIndex(event);
					if (dragedView != null && index >= 0 && dragedView != getChildAt(index)) {
						removeView(dragedView);
						addView(dragedView, index);
						dragedView.setVisibility(View.INVISIBLE);
						dragedView.setAlpha(0.0f);
					}
					break;
				case DragEvent.ACTION_DRAG_ENDED:
					initRect();
					if (dragedView != null) {
						dragedView.setVisibility(View.VISIBLE);
						dragedView.setAlpha(1.0f);
					}
					break;
			}
			return true;
		}
	};

	private int findTouchViewIndex(DragEvent event) {
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			if (childRects[i].contains((int) event.getX(),
					(int) event.getY())) {
				return i;
			}
		}
		return -1;
	}

	private void initRect() {
		int childCount = getChildCount();
		childRects = new Rect[childCount];
		for (int i = 0; i < childCount; i++) {
			View child = getChildAt(i);
			childRects[i] = new Rect(child.getLeft(), child.getTop(),
					child.getRight(), child.getBottom());
		}
	}


	private OnLongClickListener longClickListener = new OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			dragedView = v;
			GridItem item = (GridItem) v.getTag();
			ClipData.Item i = new ClipData.Item(item.getItemInfo());
			ClipData data = new ClipData(item.getItemInfo(), new String[]{"text/plain"}, i);
			startDrag(data,new DragShadowBuilder(v), null, 0);
			return true;
		}
	};

	private LayoutParams getLayoutParamsByIndex(int index){
		DisplayMetrics dm = getResources().getDisplayMetrics();
//		Spec rowSpec = GridLayout.spec(index / COL_NUM);
//		Spec colSpec = GridLayout.spec(index % COL_NUM);
//		LayoutParams lp = new LayoutParams(rowSpec, colSpec);
		LayoutParams lp = new LayoutParams();
		lp.width = (dm.widthPixels - (COL_NUM * 2 + 1) * HORIZONTAL_MARGIN) / COL_NUM;
		lp.height = LayoutParams.WRAP_CONTENT;
		lp.leftMargin = lp.rightMargin = HORIZONTAL_MARGIN;
		lp.topMargin = lp.bottomMargin = VERTICAL_MARGIN;

		return lp;
	}
}