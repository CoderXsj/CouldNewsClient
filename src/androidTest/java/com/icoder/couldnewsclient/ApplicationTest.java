package com.icoder.couldnewsclient;

import android.app.Application;
import android.support.v4.util.ArrayMap;
import android.test.ApplicationTestCase;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.icoder.couldnewsclient.app.MyApplication;
import com.icoder.couldnewsclient.entity.GsonNews;
import com.icoder.couldnewsclient.entity.NewsChannel;
import com.icoder.couldnewsclient.utils.ConstantParams;
import com.icoder.couldnewsclient.utils.L;

import java.util.Map;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testGson(){
    }
}