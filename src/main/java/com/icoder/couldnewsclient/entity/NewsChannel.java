package com.icoder.couldnewsclient.entity;

import java.util.List;

/**
 * 新闻栏目实体类
 * Created by tarena on 2016/3/29.
 */
public class NewsChannel {
    public int showapi_res_code;                //
    public String showapi_res_error;            //新闻错误代码
    public ChannelList showapi_res_body;

    public static class ChannelList{       //新闻showapi实体类
        public List<ChannelInfo> channelList;   //新闻频道条目
        public int ret_code;
        public int totalNum;                    //新闻频道总数


    }
}