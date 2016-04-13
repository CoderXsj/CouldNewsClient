package com.icoder.couldnewsclient.entity;

import java.util.List;

public class GsonNews {
    public int showapi_res_code;
    public String showapi_res_error;
    public ResApiBody showapi_res_body;

    public static class ResApiBody{
        public int ret_code;
        public PageBean pagebean;

        public static class PageBean{
            public int allNum;
            public int allPages;
            public List<News> contentlist;
        }
    }
}
