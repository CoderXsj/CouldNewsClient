package com.icoder.couldnewsclient.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.icoder.couldnewsclient.app.MyApplication;
import com.icoder.couldnewsclient.entity.ImageInfo;
import com.icoder.couldnewsclient.entity.News;
import com.icoder.couldnewsclient.utils.MD5;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by tarena on 2016/4/5.
 */
public class NewsDb {
    private static NewsDb newsDb;
    private NewsDbHelper helper;


    private NewsDb(){
        helper = new NewsDbHelper(MyApplication.getContext(),"tb_news.db",null,1);
    }

    public static NewsDb getInstance(){
        if(newsDb == null){
            synchronized(NewsDb.class){
                if(newsDb == null) {
                    newsDb = new NewsDb();
                }
            }
        }
        return newsDb;
    }

    /**
     * 根据频道ID查询的页数和查询页数的大小
     * @param page
     * @param pageSize
     * @return
     */
    public List<News> queryNews(String channelId,int page,int pageSize){
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query(true, "tb_news", null, null, null, null, null, "pub_date desc", "");
        List<News> newses = new ArrayList<>();

        while(cursor.moveToNext()){
            News news = new News();
            news.link = cursor.getString(cursor.getColumnIndex("link"));
            news.channelId = cursor.getString(cursor.getColumnIndex("channel_id"));
            news.channelName = cursor.getString(cursor.getColumnIndex("channel_name"));
            news.desc = cursor.getString(cursor.getColumnIndex("desc"));
            news.imageurls = splitImageUrls(cursor.getString(cursor.getColumnIndex("image_urls")));
            news.nid = cursor.getString(cursor.getColumnIndex("_id"));
            news.pubDate = cursor.getString(cursor.getColumnIndex("pub_date"));
            news.source = cursor.getString(cursor.getColumnIndex("source"));
            news.title = cursor.getString(cursor.getColumnIndex("title"));
            newses.add(news);
        }

        cursor.close();
        db.close();

        return newses;
    }

    /**
     * 查询单个音乐
     * @return
     */
    public boolean queryNews(String id){
        if(id == null || id.equals(""))
            return false;

        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query(true, "tb_news", null, "_id = ?", new String[]{id}, null, null, null, null);

        if(cursor.moveToNext())
            return true;

        return false;
    }

    public void insertNews(List<News> newses){
        SQLiteDatabase db = helper.getWritableDatabase();
        for(News news: newses) {
            if(!queryNews(MD5.GetMD5Code(news.link))) {
                ContentValues values = new ContentValues();
                values.put("link", news.link);
                values.put("channel_id", news.channelId);
                values.put("channel_name", news.channelName);
                values.put("desc", news.desc);
                values.put("channel_name", news.channelName);
                values.put("image_urls", makeImageUrls(news.imageurls));
                values.put("link", news.link);
                values.put("_id", MD5.GetMD5Code(news.link));
                values.put("pub_date", news.pubDate);
                values.put("source", news.source);
                values.put("title", news.title);
                db.insert("tb_news", null, values);
            }
        }
        db.close();
    }

    /**
     * 存在数据库中的图片中的链接以 #pic# 进行连接
     * @return
     */
    private List<ImageInfo> splitImageUrls(String urls){
        List<ImageInfo> infos = null;
        if(urls != null) {
            String[] split = urls.split("#pic#");
            if(split != null) {
                infos = new ArrayList<>();
                for (String str : split) {
                    ImageInfo info = new ImageInfo();
                    info.url = str;
                    infos.add(info);
                }
            }
        }
        return infos;
    }

    private String makeImageUrls(List<ImageInfo> infos){
        StringBuilder sb = new StringBuilder();

        for(ImageInfo info : infos){
            sb.append(info.url).append("#pic#");
        }

        return sb.length() == 0 ? sb.toString():sb.toString().substring(0, sb.length() - 5);
    }
}