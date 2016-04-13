package com.icoder.couldnewsclient.utils;

import android.support.v4.util.ArrayMap;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.icoder.couldnewsclient.app.MyApplication;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class HttpUtils {
    private HttpUtils(){
    }

    public interface OnResponseListener {
        void onSuccess(Object object);
        void onFiled(String msg);
    }

    public static void getJsonBean(String url, final OnResponseListener listener,final Class clazz){
        StringRequest req = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Gson gson = new Gson();
                Object obj = gson.fromJson(s, clazz);
                listener.onSuccess(obj);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                String msg = volleyError.getMessage();
                listener.onFiled(msg);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final ArrayMap<String, String> mHeaders = new ArrayMap<>();
                mHeaders.put("apikey", ConstantParams.API_KEY);
                return mHeaders;
            }
        };
        MyApplication.getQueue().add(req);
    }

    public static void getHtml(String url,final OnResponseListener listener){
        StringRequest req = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                listener.onSuccess(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                listener.onFiled(volleyError.getMessage());
            }
        });
        MyApplication.getQueue().add(req);
    }
}
