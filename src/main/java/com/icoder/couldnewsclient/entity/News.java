package com.icoder.couldnewsclient.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * 多条新闻对应一个NewsChannel因此channelId相当于外键
 */
public class News implements Parcelable{
    public String channelId;
    public String channelName;
    public String desc;
    public List<ImageInfo> imageurls;
    public String link;
    public String nid;
    public String pubDate;
    public String source;
    public String title;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(channelId);
        dest.writeString(channelName);
        dest.writeString(desc);
        dest.writeList(imageurls);
        dest.writeString(link);
        dest.writeString(nid);
        dest.writeString(pubDate);
        dest.writeString(source);
        dest.writeString(title);
    }

    public final static Parcelable.Creator<News> CREATOR = new Creator<News>() {
        @Override
        public News createFromParcel(Parcel source) {
            News news = new News();
            news.channelId = source.readString();
            news.channelName = source.readString();
            news.desc = source.readString();
            news.imageurls = new ArrayList<>();
            source.readList(news.imageurls,ImageInfo.class.getClassLoader());
            news.link = source.readString();
            news.nid = source.readString();
            news.pubDate = source.readString();
            news.source = source.readString();
            news.title = source.readString();

            return news;
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };
}