package com.icoder.couldnewsclient.view.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
//import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.icoder.couldnewsclient.R;
import com.icoder.couldnewsclient.adapter.NewsAdapter;
import com.icoder.couldnewsclient.db.NewsDb;
import com.icoder.couldnewsclient.entity.ChannelInfo;
import com.icoder.couldnewsclient.entity.GsonNews;
import com.icoder.couldnewsclient.entity.News;
import com.icoder.couldnewsclient.utils.ConstantParams;
import com.icoder.couldnewsclient.utils.HttpUtils;
import com.icoder.couldnewsclient.utils.L;
import com.icoder.couldnewsclient.view.NewsContentActivity;
import com.icoder.couldnewsclient.view.ui.BasePage;
import com.icoder.couldnewsclient.widget.AutoRollLayout;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class NewsPage extends BasePage {
    //下拉刷新
    public static final int MODE_PUSH_DOWN = 0;
    //上拉加载
    public static final int MODE_PUSH_UP = 1;

    private  AutoRollLayout autoRollLayout;
    //保存已经存在的news
    private LinkedList<News> news;
    //轮播图的NewsItem
    private ArrayList<NewsAutoItem> items;
    //一个已读news的集合保存着news的id
    private ArrayList<String> readNews;
    private NewsAdapter adapter;

    //记录page页的
    private int pageCount = 1;

    private LinearLayoutManager layoutManager;
    //是否正在加载数据
    private boolean isLoading;
    private NewsDb newsDb;

    @Override
    public void initView(final Context context,ChannelInfo channelInfo) {
        super.initView(context,channelInfo);
        news = new LinkedList<>();
        readNews = new ArrayList<>();
        items = new ArrayList<>();

        //设置下拉监听
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageCount = 1;
                sendRequest(MODE_PUSH_DOWN);
            }
        });
        //设置下拉刷新的progress的进度条的颜色
        swipeRefreshLayout.setColorSchemeResources(R.color.main_red_color);

        // 这句话是为了，第一次进入页面的时候显示加载进度条
        adapter = new NewsAdapter(context,news);
        adapter.setHeaderItemsListener(onItemClickListener);
        adapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                News item = news.get(position - 1);

                Activity activity = (Activity) context;
                Intent intent = new Intent(activity, NewsContentActivity.class);
                intent.putExtra("news", item);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.translate_in_left, R.anim.translate_out_left);
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setOnScrollListener(mScrollListener);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        newsDb = NewsDb.getInstance();

        sendRequest(MODE_PUSH_DOWN);
    }

    @Override
    public void onResume() {
        autoRollLayout.setIsAutoRoll(true);
    }

    @Override
    public void onPause(){
        autoRollLayout.setIsAutoRoll(false);
    }

    @Override
    public void onStop() {
        autoRollLayout.setIsAutoRoll(false);
    }

    private void sendRequest(final int mode) {
        try {
            String url = ConstantParams.API_SEARCH_NEWS_ITEMS + "?channelId=" + channelInfo.channelId + "&" + "channelName=" + URLEncoder.encode(channelInfo.name, "UTF-8") + "&page=" + pageCount;
            Log.d("tedu",url);
            HttpUtils.getJsonBean(url, new HttpUtils.OnResponseListener() {
                @Override
                public void onSuccess(Object object) {
                    if(mode == MODE_PUSH_DOWN) {
                        news.clear();
                    }
                    pageCount++;
                    //当进行下拉刷新的时候,将新产生的新闻添加到现有新闻的头部
                    GsonNews newNews = (GsonNews) object;
                    List<News> addNews = newNews.showapi_res_body.pagebean.contentlist;
                    news.addAll(addNews);
//                    adapter.notifyItemRangeInserted(lastSize,news.size() - 1);
                    adapter.notifyDataSetChanged();
                    newsDb.insertNews(news);
                    //下拉刷新时，设置头条新闻
                    if(mode == MODE_PUSH_DOWN){
                        getTopNews(news);
                    }

                    //设置让swipRefreshLayout不进行下拉刷新
                    swipeRefreshLayout.setRefreshing(false);
                    isLoading = false;
                }

                @Override
                public void onFiled(String msg) {
                    Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
                }
            }, GsonNews.class);
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }
    }

    /**
     *  只有下拉刷新的时候才会更新topNews
     */
    private void getTopNews(List<News> newses){
        items.clear();

        for(News news : newses){
            if(news.imageurls != null && news.imageurls.size() > 0){
                items.add(new NewsAutoItem(news));
            }
        }

        adapter.setHeaderItems(items);
    }

    //进行上拉加载
    private RecyclerView.OnScrollListener mScrollListener = new OnScrollListener(){
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
            if (lastVisibleItemPosition + 1 == adapter.getItemCount()) {

                boolean isRefreshing = swipeRefreshLayout.isRefreshing();
                if (isRefreshing) {
                    adapter.notifyItemRemoved(adapter.getItemCount());
                    return;
                }
                if (!isLoading) {
                    isLoading = true;
                    //请求数据
                    sendRequest(MODE_PUSH_UP);
                }
            }
        }
    };

    class NewsAutoItem implements AutoRollLayout.IShowItem {
        private News news;

        public News getNews() {
            return news;
        }

        public NewsAutoItem(News news){
            this.news = news;
        }

        @Override
        public String getImageUrl() {
            return news.imageurls.get(0).url;
        }

        @Override
        public String getTitle() {
            return null;
        }

        @Override
        public int getDefaultImageId() {
            return 0;
        }

        @Override
        public int getErrorImageId() {
            return 0;
        }
    }

    private AutoRollLayout.OnItemClickListener onItemClickListener = new AutoRollLayout.OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            News item = items.get(position).getNews();
            Activity activity = (Activity) context;
            L.d("tede","" + activity);
            Intent intent = new Intent(activity,NewsContentActivity.class);
            intent.putExtra("news",item);
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.translate_in_left,R.anim.translate_out_left);
        }
    };
}