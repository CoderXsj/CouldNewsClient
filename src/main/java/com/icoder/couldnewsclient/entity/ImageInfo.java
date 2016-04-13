package com.icoder.couldnewsclient.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tarena on 2016/4/1.
 */
public class ImageInfo implements Parcelable{
    public int height;
    public int width;
    public String url;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(height);
        dest.writeInt(width);
        dest.writeString(url);
    }

    public static final Parcelable.Creator<ImageInfo> CREATOR = new Creator<ImageInfo>() {
        @Override
        public ImageInfo createFromParcel(Parcel source) {
            ImageInfo info = new ImageInfo();
            info.height = source.readInt();
            info.width = source.readInt();
            info.url = source.readString();
            return info;
        }

        @Override
        public ImageInfo[] newArray(int size) {
            return new ImageInfo[size];
        }
    };
}
