package com.icoder.couldnewsclient.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.NetworkImageView;
import java.util.ArrayList;
import java.util.List;
import com.icoder.couldnewsclient.R;
import com.icoder.couldnewsclient.utils.L;

/**
 * 轮播图布局 A.显示图片 B。显示圆点 C.定时滚动 D.设置标题 E.对外曝露方法，来控制滚动 F。对外曝露方法。来获取显示的当前角标
 *
 * 使用handler机制来控制轮播图的顺序控制
 *
 * 轮播图的个数是两个的时候，发生一个Bug这个bug产生的原因是:
 * 因为我们轮播图是一个VIewPager，
 * 轮播图的原理就是:让ViewPager中的Item的数量设置一个Integer的极大值
 * 然后让轮播图的当前位置设置在这个极大值的中间位置上()这样不管用户使劲往左画或者往右划都不会出现溢出的问题
 *
 * Bug的产生原因发生在只有两张轮播图的时候
 * 因为我们把ViewPager设置在中间位置上
 * 所以ViewPager一次加载3个ImageView
 * 那么我们只有两个ImageView对象
 * 这时候我们没有在实例化方法中写下如下代码
 *
 * 			ViewParent vp = iv.getParent();
 if(vp != null){
 ViewGroup vg = (ViewGroup) vp;
 vg.removeView(iv);
 }

 产生了一个异常,异常的意思是ImageView对象已经存在了一个parent，你需要调用removeView方法移除后再进行添加
 那么为什么我们会出现这个问题呢?
 因为我们只有两个Image对象
 这里我们需要一次加载3个ImageView
 比如：
 position: 9999 10000 10001
 10000是我们ViewPager的当前显示item的position角标
 9999 %2 = 1
 10001 % 2 = 1
 他们同时取出同一个ImageView对象
 那么比如我们先为9999号位置已经添加了这个ImageView
 那么我们在10001号位置添加ImageView时就会产生这个异常

 解决方案是:
 如果items的个数是两个的时候

 */

public class AutoRollLayout extends FrameLayout {

	private CustomViewPager viewPager;
	private LinearLayout dotContainer;

	//	private ImageLoader imageLoader;
	private List<? extends IShowItem> items;
	//圆点图片集合
	private List<View> views;
	private OnItemClickListener onItemClickListener;
	//使用handler来
	private Handler handler;
	private int ROLL_TIME = 5000;
	private boolean isAutoRoll = false;
	private ArrayList<ImageView> images;

	public boolean isAutoRoll() {
		return isAutoRoll;
	}

	public void setIsAutoRoll(boolean isAutoRoll) {
		this.isAutoRoll = isAutoRoll;
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	List<View> rollViews;

	public static interface IShowItem {
		/**
		 * 获取Image的Url地址
		 *
		 * @return imageUrl地址
		 */
		String getImageUrl();

		/**
		 * 获取显示的标题
		 *
		 * @return 标题字符串
		 */
		String getTitle();

		/**
		 * 获取默认的图片资源id
		 * @return
		 */
		int getDefaultImageId();

		/**
		 * 获取错误图片的imageId
		 */
		int getErrorImageId();
	}

	public static interface OnItemClickListener{
		/**
		 * 当ViewPager上的图片被点击的时候进行触发
		 * @param position		被电击的
		 */
		public void onItemClick(int position);
	}

	public AutoRollLayout(Context context) {
		this(context, null);
	}

	public AutoRollLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AutoRollLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		L.d("AutoRollLayout","before");
		// 将布局文件映射到自己的本身上
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.autoroll_layout, this,true);

		L.d("AutoRollLayout","....");
		viewPager = (CustomViewPager) view.findViewById(R.id.vp_container);
		dotContainer = (LinearLayout) view.findViewById(R.id.ll_dot_container);

//		imageLoader = ImageLoader.getInstance();
		// 初始化对象
//		imageLoader.init(ImageLoaderConfiguration.createDefault(getContext()));

		viewPager.setOnPageChangeListener(mOnPageChangeListener);
		viewPager.setOnTouchListener(mOnTouchEventListener);

		handler = new Handler();
		handler.postDelayed(task, ROLL_TIME);
		viewPager.setAdapter(adapter);
	}

	/**
	 * 对外暴露接口
	 * @param items		显示的item;
	 * 首先逻辑是这样的
	 * 1.判断是否存在缓存图片文件,如果有则使用缓存文件,没有则使用网络下载
	 * 2.网络不通的时候使用默认图片
	 * 3.网络通畅的时候,从网络下载图片
	 */
	public void setItem(List<? extends IShowItem> items,ImageLoader loader){
		if(items == null)
			throw new NullPointerException("items is not null");
		this.items = items;
		dotContainer.removeAllViews();
		views = new ArrayList<View>(items.size());
		images = new ArrayList<ImageView>(items.size());
		for(int i = 0;i < items.size();i++){
			View view = new View(getContext());
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int)getResources().getDimension(R.dimen.dot_size),(int)getResources().getDimension(R.dimen.dot_size));
			lp.leftMargin = 10;
			lp.rightMargin = 10;
			view.setLayoutParams(lp);
			view.setBackgroundResource(R.drawable.dot_selector);
			//增加圆点图片
			views.add(view);
			dotContainer.addView(view);

			IShowItem iShowItem = items.get(i);
			NetworkImageView niv = new NetworkImageView(getContext());
			niv.setScaleType(ScaleType.FIT_XY);
			if(iShowItem.getDefaultImageId() > 0)
				niv.setDefaultImageResId(iShowItem.getDefaultImageId());
			if(iShowItem.getErrorImageId() > 0)
				niv.setErrorImageResId(iShowItem.getErrorImageId());
			niv.setImageUrl(iShowItem.getImageUrl(), loader);

			images.add(niv);
		}

		//选中第一个页面
		int page = (Integer.MAX_VALUE - Integer.MAX_VALUE % this.items.size())/this.items.size()/2 - items.size() + 1;
		setViewEnable(page);
		viewPager.setCurrentItem(page);
		adapter.notifyDataSetChanged();
	}

	/**
	 * 初始化圆形按钮
	 */
	private void initViews(){
		for(int i = 0;i < views.size();i++){
			views.get(i).setEnabled(false);
		}
	}

	private void setViewEnable(int position){
//		if(position < 0 || position >= views.size())
//			throw new IllegalArgumentException("position is wrong..");
		position %= items.size();
		if(position < 0){
			position += items.size();
		}
		initViews();
		views.get(position).setEnabled(true);
	}

	private CustomViewPager.OnPageChangeListener mOnPageChangeListener = new CustomViewPager.OnPageChangeListener() {
		@Override
		public void onPageSelected(int position) {
			setViewEnable(position);
		}

		@Override
		public void onPageScrolled(int position, float positionOffset,int positionOffsetPixels) {
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}
	};

	private PagerAdapter adapter = new PagerAdapter() {
//		public Object instantiateItem(ViewGroup container, int position) {
//			position %= items.size();
//			if(position < 0){
//				position += items.size();
//			}
//			Log.d("tedu", ",,,position " + position);
//			 
//			ViewPager的绘制过程是比如我要显示viewPager中的第一个view对象的时候他需要要把两侧滑动的imageView对象都需要获取出来
//			比如我第一次初始化的时候是		position : 0		那么他需要把  postion + 1 和position - 1上的view都实例化出来,那么就会调用PagerAdapter中的
//			instantiateItem方法,也就是position 为 0 -1 1
//			而这里的我们可以通过position则获取在指定位置上的ImageView
		//	通过container.getChildAt(position)我获取的是viewPager上的原有的ImageView对象
		//	因此我之前犯了一个逻辑上的错误我把getChildAt方法误认为是可以获取指定位置上的imageView对象,
		//	而实际上则是我们获取的是指定在ViewPager上positoin的view对象
		//	这里的的数据源我们需要理清楚....数据源是相当于已经准备好了的4个图片对象集合,然后我们通过position获取该位置上的图片
		//	这里我把数据源看作是viewPager，而这里我们不能直接获取imageView对象，因此我们显示imageView的时候乱了

//			ImageView iv = (ImageView) container.getChildAt(position);
//			if(iv == null){
//				iv = new ImageView(getContext());
//				iv.setScaleType(ScaleType.FIT_XY);
//				imageLoader.displayImage(items.get(position).getImageUrl(), iv);
//				container.addView(iv);
//			}
//			
//			return iv;
//		}

		public Object instantiateItem(ViewGroup container, int position) {
			position %= items.size();
			if(position < 0){
				position += items.size();
			}

			ImageView iv = images.get(position);
			ViewParent vp = iv.getParent();
			if(vp != null){
				ViewGroup vg = (ViewGroup) vp;
				vg.removeView(iv);
			}
			container.addView(iv);

			return iv;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public int getCount() {
			//因为要实现轮播因此采用第一个image实际上是最后要url显示的image,最后一个是第一个要显示的image
			return items == null ? 0 : Integer.MAX_VALUE;
		}

		public void destroyItem(ViewGroup container, int position, Object object) {
			ImageView iv = (ImageView) object;
			container.removeView(iv);
		}
	};

	private GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener(){
		@Override
		public boolean onSingleTapUp(MotionEvent e) {

			if(onItemClickListener != null)
				onItemClickListener.onItemClick(viewPager.getCurrentItem() % items.size());

			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,float velocityY) {
			handler.removeCallbacks(task);		//让轮播图不进行自动滚动

			return super.onFling(e1, e2, velocityX, velocityY);
		}
	});

	/**
	 * 可以不用继承ViewPager就可以使用这个OnTouchListener来代替触发的事件   
	 */
	private OnTouchListener mOnTouchEventListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			//将事件交给手势判定器进行判定.
			gestureDetector.onTouchEvent(event);

			switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:			//用户按下
					handler.removeCallbacks(task);		//让轮播图不进行自动滚动
					break;
				case MotionEvent.ACTION_UP:				//当用户手抬起来了,就让轮播图继续的轮播
					handler.postDelayed(task, ROLL_TIME);
					break;
			}

			return false;
		}
	};

	private Runnable task = new Runnable(){
		@Override
		public void run() {
			//避免重复执行任务
			handler.removeCallbacks(task);

			if(!isAutoRoll)
				return;

			int nowPosition = viewPager.getCurrentItem();
			int nextPosition = nowPosition + 1;
			if(nextPosition == items.size()){
				nextPosition = 0;
			}

			viewPager.setCurrentItem(nextPosition);

			handler.postDelayed(task, ROLL_TIME);
		}
	};
}