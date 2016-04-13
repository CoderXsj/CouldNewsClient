package com.icoder.couldnewsclient.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NewsDbHelper extends SQLiteOpenHelper{

    public NewsDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE tb_news(" +
                            "channel_id varchar(20)," +
                            "channel_name varchar(20)," +
                            "desc text," +
                            "image_urls varchar(200)," +
                            "link varchar(30)," +
                            "_id varchar(30) primary key," +
                            "pub_date varchar(30)," +
                            "source varchar(20)," +
                            "title varchar(20)" +
                        ");";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
