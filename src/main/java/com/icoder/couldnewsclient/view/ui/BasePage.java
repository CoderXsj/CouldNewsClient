package com.icoder.couldnewsclient.view.ui;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import com.icoder.couldnewsclient.R;
import com.icoder.couldnewsclient.entity.ChannelInfo;
import android.support.v7.widget.RecyclerView;

/**
 *
 *  BasePager将会成为 普通新闻 NewsPager 和 组图PicturePager（包括图标、语音）pager的爹

 发现NewsPager 和 PicturePager 之间的共同点：
 1 refreshListView
 2 下拉刷新、上拉下载更多
 3 下载数据需要的HttpUtils、加载图片的bitmaputils

 Basepager也具有init方法：
 加载布局（含有listview）

 子类去重写init方法（第一句话：super.init() ）
 在去下载数据
 NewsPager需要对listView指定自己的adapter
 NewsPager对listview添加轮播图的头
 ——————
 PicturePager需要对listView指定自己的adapter

 Basepager也具有getView方法：



 不同之处：
 1 NewsPager有轮播图，PicturePager没有轮播图
 2 adapter，具体是adapte的getview方法
 */
public abstract class BasePage {
    protected Context context;
    protected View rootView;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected RecyclerView recyclerView;
    protected ChannelInfo channelInfo;

    public void initView(Context context,ChannelInfo channelInfo){
        rootView = View.inflate(context, R.layout.il_news_page,null);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        this.channelInfo = channelInfo;
        this.context = context;
    }

    public void onResume(){
    }

    public void onPause(){
    }

    public void onStop(){
    }

    public View getRootView(){
        return rootView;
    }
}