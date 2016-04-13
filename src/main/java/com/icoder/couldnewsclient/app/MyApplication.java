package com.icoder.couldnewsclient.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.icoder.couldnewsclient.entity.ChannelInfo;
import com.icoder.couldnewsclient.entity.GsonNews;
import com.icoder.couldnewsclient.entity.NewsChannel;
import com.icoder.couldnewsclient.utils.BitmapDiskFileUtils;
import com.icoder.couldnewsclient.utils.ConstantParams;
import com.icoder.couldnewsclient.utils.L;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by tarena on 2016/3/29.
 */
public class MyApplication extends Application{
    //是否是夜间模式
    private boolean isNightMode = false;
    private static Context context;
    private static RequestQueue queue;
    private static ImageLoader loader;
    //频道条目信息
    public static List<ChannelInfo> channelList;

    public boolean isNightMode() {
        return isNightMode;
    }

    public void setIsNightMode(boolean isNightMode) {
        this.isNightMode = isNightMode;
    }

    public static Context getContext() {
        return context;
    }

    public static RequestQueue getQueue() {
        return queue;
    }

    public static ImageLoader getLoader() {
        return loader;
    }

    public static List<ChannelInfo> getChannelList() {
        return channelList;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = this;
        //设置Volley不进行缓存
        queue = Volley.newRequestQueue(this, 0);

        final BitmapDiskFileUtils instance = BitmapDiskFileUtils.getInstance();
        loader = new ImageLoader(queue, new ImageLoader.ImageCache() {
            private LruCache<String, Bitmap> cache = new LruCache<>(1024 * 3);

            @Override
            public void putBitmap(String url, Bitmap value) {
                //写入内存中
                cache.put(url, value);
                //写入硬盘中
                instance.writeBitmap(url, value);
            }

            @Override
            public Bitmap getBitmap(String url) {
                Bitmap bitmap;
                //不在内存中
                if((bitmap = cache.get(url)) == null){
                    //从硬盘中拿
                    bitmap = instance.readBitmap(url);
                }

                return bitmap;
            }
        });

        init();
        //首先从xml文件中读取channel是否存在
        SharedPreferences sp = getSharedPreferences("init", MODE_PRIVATE);
        boolean isHaveChannel = sp.getBoolean("isHaveChannel", false);

        //如果存在频道xml文件从中读取，否则从网络端下载
        if(isHaveChannel){
            loadChannelFromFile();
        }else{
            loadChannelFromInternet();
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("isHaveChannel",true);
            editor.commit();
        }
    }

    private void loadChannelFromFile() {
        SharedPreferences sp = getSharedPreferences("news_channel", MODE_PRIVATE);
        channelList = new ArrayList<>();
        Map<String, ?> maps = sp.getAll();
        for(Map.Entry<String,?> entry : maps.entrySet()){
            ChannelInfo channelInfo = new ChannelInfo();
            channelInfo.channelId = entry.getKey();
            channelInfo.name = (String) entry.getValue();
            channelList.add(channelInfo);
        }
    }

    /**
     * 加载新闻频道从网络端
     */
    private void loadChannelFromInternet(){
        final SharedPreferences sp = getSharedPreferences("news_channel", MODE_PRIVATE);
        String url = "http://apis.baidu.com/showapi_open_bus/channel_news/channel_news";
        final ArrayMap<String, String> mHeaders = new ArrayMap<>();
        mHeaders.put("apikey", ConstantParams.API_KEY);
        StringRequest req = new StringRequest(Request.Method.GET,url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                Gson gson = new Gson();
                NewsChannel newsChannel = gson.fromJson(s, NewsChannel.class);
                channelList = newsChannel.showapi_res_body.channelList;
                saveChannelToLocal(sp);
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        }){
            //因为get请求需要带有apikey参数因此需要重写这个方法
            public Map<String, String> getHeaders() {
                return mHeaders;
            }
        };
        MyApplication.getQueue().add(req);
    }

    /**
     * 保存新闻频道到本地端
     * @param sp
     */
    private void saveChannelToLocal(SharedPreferences sp){
        SharedPreferences.Editor editor = sp.edit();
        for(ChannelInfo channelInfo : channelList){
            editor.putString(channelInfo.channelId,channelInfo.name);
        }
        editor.commit();
    }

    private void init(){
        L.d("tedu","test...");
        String url = "http://apis.baidu.com/showapi_open_bus/channel_news/search_news";
        final ArrayMap<String, String> mHeaders = new ArrayMap<>();
        mHeaders.put("apikey", ConstantParams.API_KEY);
        StringRequest req = new StringRequest(Request.Method.GET,url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                Gson gson = new Gson();
                GsonNews news = gson.fromJson(s, GsonNews.class);
                L.d("tedu","" + news);
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        }){
            //因为get请求需要带有apikey参数因此需要重写这个方法
            public Map<String, String> getHeaders() {
                return mHeaders;
            }
        };
        MyApplication.getQueue().add(req);
    }
}
