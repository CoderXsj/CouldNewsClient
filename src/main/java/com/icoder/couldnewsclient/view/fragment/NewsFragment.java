package com.icoder.couldnewsclient.view.fragment;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.util.LruCache;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import com.icoder.couldnewsclient.R;
import com.icoder.couldnewsclient.app.MyApplication;
import com.icoder.couldnewsclient.entity.ChannelInfo;
import com.icoder.couldnewsclient.utils.ThemeUtils;
import com.icoder.couldnewsclient.view.ui.BasePage;
import com.icoder.couldnewsclient.view.MainActivity;
import com.icoder.couldnewsclient.view.ui.NewsPage;
import com.icoder.couldnewsclient.widget.DragLayout;
import com.icoder.couldnewsclient.widget.PagerSlidingTabStrip;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 新闻Fragment
 */
public class NewsFragment extends BaseFragment implements View.OnClickListener{
    @Bind(R.id.psts_page_sliding_strip)
    PagerSlidingTabStrip mPageSlideTabTrip;
    @Bind(R.id.lazy_view_pager)
    ViewPager mLazyViewPager;
    @Bind(R.id.ib_news_group_item_edit)
    ImageButton mIbGroupEdit;
    //这个布局包装DragLayout
    ScrollView scrollView;

    //存储着所有的列值
    List<ChannelInfo> channelInfos;
    //设置页面缓存
    LruCache<String,BasePage> channelInfoMap;
    String showNewsId;
    PopupWindow window;


    private ViewPager.OnPageChangeListener mOnPagerChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            mPageSlideTabTrip.setTabTextSelected(position);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset,int positionOffsetPixels) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    @Override
    protected void initView() {
        ButterKnife.bind(this, getRootView());

        channelInfos = MyApplication.getChannelList();
        mIbGroupEdit.setOnClickListener(this);
        float totalMemory = Runtime.getRuntime().maxMemory()/1024/1024;
        channelInfoMap = new LruCache<>((int) (totalMemory / 6));
        initHeaderPageTabTrip();
        initDragLayout();
        initPopWindow();
    }

    @Override
    protected int inflateFragmentById() {
        return R.layout.frag_news;
    }

    /**
     * 初始化header tab 的菜单
     */
    private void initHeaderPageTabTrip() {
        mLazyViewPager.setAdapter(new NewsPagerAdapter());
        //设置分割线
        mPageSlideTabTrip.setDividerColor(ThemeUtils.getThemeColor(getContext(),R.attr.containerBackground)[0]);
        //设置指示器的高度
        mPageSlideTabTrip.setIndicatorHeight(4);
        //设置指示器的颜色
        mPageSlideTabTrip.setIndicatorColorResource(R.color.main_red_color);
        mPageSlideTabTrip.setTabBackground(R.drawable.transparent_press_bg_selector);
        //设置下划线的高度
        mPageSlideTabTrip.setUnderlineHeight(0);
        //是否均分整个去昂提，调用这个方法需要在setViewPager这个方法之前
//        mPageSlideTabTrip.setShouldExpand(true);
        //设置左右内外边距
        mPageSlideTabTrip.setTabPaddingLeftRight(30);
        mPageSlideTabTrip.setTabTextSelectedColorReceource(R.color.main_red_color);
        mPageSlideTabTrip.setTabTextSize(14);
        mPageSlideTabTrip.setViewPager(mLazyViewPager);
        mPageSlideTabTrip.setOnPageChangeListener(mOnPagerChangeListener);
    }

    private boolean isShowEditGroupItemFragment = false;
    @Override
    public void onClick(View v) {
//        ViewGroup vp = (ViewGroup) getRootView();
        MainActivity activity = (MainActivity) getActivity();
        if(!isShowEditGroupItemFragment){
            scrollView.scrollTo(0,0);
//            vp.addView(scrollView);
            // 在底部显示
//            window.showAtLocation(mIbGroupEdit, Gravity.NO_GRAVITY, 0, (int) mIbGroupEdit.getTranslationY());
            window.showAsDropDown(mIbGroupEdit, 0, 0);
            activity.dismissBottomMenu();
            isShowEditGroupItemFragment = true;
        }else {
            activity.showBottomMenu();
//            vp.removeView(scrollView);
            window.dismiss();
            mLazyViewPager.setVisibility(View.VISIBLE);
            isShowEditGroupItemFragment = false;
        }
    }

    protected void initDragLayout() {
        Context ctx = getContext();
        scrollView = new ScrollView(ctx);
        scrollView.setLayoutParams(mLazyViewPager.getLayoutParams());

        TypedArray array = ctx.getTheme().obtainStyledAttributes(new int[]{R.attr.containerBackground, R.attr.textColor, R.attr.selectorBtn});
        int containerBackground = array.getColor(0, 0xFF00FF);
        int textColor = array.getColor(1, 0xFF00FF);
        Drawable backgroundDrawable = array.getDrawable(2);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        DragLayout drag = new DragLayout(ctx);
        drag.setLayoutParams(lp);
        drag.setBackgroundColor(containerBackground);
        List<ChannelInfo> channelList = MyApplication.getChannelList();
        ArrayList<Item> items = new ArrayList<>();
        for(int i = 0;i < channelList.size();i++){
            ChannelInfo channelInfo = channelList.get(i);
            items.add(new Item(channelInfo.name,textColor,backgroundDrawable));
        }
        drag.setItems(items);

        scrollView.addView(drag);
    }

    private void initPopWindow(){
        window = new PopupWindow(scrollView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
//        window.setFocusable(true);
        window.setOutsideTouchable(true);
//        window.setAnimationStyle(R.style.mypopwindow_anim_style);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(showNewsId != null){
            channelInfoMap.get(showNewsId).onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(showNewsId != null){
            channelInfoMap.get(showNewsId).onStop();
        }
    }

    @Override
    public void onResume() {
        super.onStart();
        if(showNewsId != null){
            channelInfoMap.get(showNewsId).onResume();
        }
    }

    class Item implements DragLayout.GridItem{
        private Drawable backgroundDrawable;
        private int textColor;
        private String itemInfo;

        public Item(String itemInfo,int textColor,Drawable backgroundDrawable){
            this.itemInfo = itemInfo;
            this.textColor = textColor;
            this.backgroundDrawable = backgroundDrawable;
        }
        @Override
        public String getItemInfo() {
            return itemInfo;
        }

        @Override
        public Drawable getBackgroundDrawable() {
            return backgroundDrawable;
        }

        @Override
        public int getTextColor() {
            return textColor;
        }
    }

    class NewsPagerAdapter extends PagerAdapter{
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //先拿到这个是哪一个频道的信息
            ChannelInfo channelInfo = channelInfos.get(position);
            BasePage page = channelInfoMap.get(channelInfo.channelId);
            if(page == null){
                //如果page不存在就new出来
                page = new NewsPage();
                NewsPage newsPage = (NewsPage) page;
                newsPage.initView(getActivity(), channelInfo);
            }
            container.addView(page.getRootView());

            return page.getRootView();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }

        @Override
        public int getCount() {
            return channelInfos.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return channelInfos.get(position).name;
        }
    }
}