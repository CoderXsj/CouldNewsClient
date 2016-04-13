package com.icoder.couldnewsclient.entity;

public class SettingItem {
    public static final int TYPE_ITEM_NORMAL = 0;          //普通的只有文本的item
    public static final int TYPE_ITEM_TEXT_BTN = 1;        //带有文本和button的item
    public static final int TYPE_ITEM_TITLE = 2;           //标题类型的item
    public static final int TYPE_ITEM_TEXT_TEXT = 3;       //文本和文本类型

    public SettingItem(String title, int type) {
        this.title = title;
        this.type = type;
    }

    public SettingItem(String title, int type, boolean status) {
        this.title = title;
        this.type = type;
        this.status = status;
    }

    public SettingItem(String title, String info,int type) {
        this.title = title;
        this.info = info;
        this.type = type;
    }

    public String title;
    public String info;
    public int type;
    public boolean status;
}
