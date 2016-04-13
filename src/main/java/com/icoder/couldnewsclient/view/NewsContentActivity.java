package com.icoder.couldnewsclient.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.VideoView;

import com.android.volley.toolbox.NetworkImageView;
import com.icoder.couldnewsclient.R;
import com.icoder.couldnewsclient.app.MyApplication;
import com.icoder.couldnewsclient.entity.ImageInfo;
import com.icoder.couldnewsclient.entity.News;
import com.icoder.couldnewsclient.utils.ConstantParams;
import com.icoder.couldnewsclient.utils.HttpUtils;
import com.icoder.couldnewsclient.utils.L;
import com.icoder.couldnewsclient.utils.ThemeUtils;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.edu.hfut.dmic.htmlbot.contentextractor.ContentExtractor;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by tarena on 2016/4/2.
 */
public class NewsContentActivity extends Activity {
    @Bind(R.id.iv_menu)
    ImageView iv_back;
    @Bind(R.id.toolbar)
    RelativeLayout toolbar;
    @Bind(R.id.tv_news_content_cont)
    TextView tvContent;
    @Bind(R.id.tv_news_text_content_title)
    TextView tvTextTitle;
    @Bind(R.id.tv_news_video_content_title)
    TextView tvVideoTitle;
    //如果是正常的页面的画显示文本信息就好了
    @Bind(R.id.rl_news_text_content)
    ScrollView mTextContent;
    //如果是video页面的画显示视频信息.
    @Bind(R.id.rl_news_video_content)
    RelativeLayout mVideoContent;
    @Bind(R.id.vp_news_content_images)
    ViewPager mImageViewPager;

    private News news;
    NewsAsyncTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication app = (MyApplication) getApplication();

        if(app.isNightMode())
            setTheme(R.style.AppTheme_night);
        else
            setTheme(R.style.AppTheme_day);

        setContentView(R.layout.activity_news_content);
        ButterKnife.bind(this);

        initActionBar();
        news = getIntent().getParcelableExtra("news");
        task = new NewsAsyncTask();
        task.execute(news.link);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        task.cancel(true);
        task = null;
        super.onDestroy();
    }

    private void initActionBar(){
    }

    class NewsAsyncTask extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... params) {
            try {
                StringBuilder sb = new StringBuilder();
                URL mUrl = new URL(params[0]);
                Log.d("NewsContentActivity",params[0]);
                HttpURLConnection conn = (HttpURLConnection) mUrl.openConnection();
                //设置访问端是Chrome浏览器发送的请求，欺骗服务器返回PC段数据给我们
                conn.setRequestProperty("user-agent", "Chrome");
                conn.setRequestProperty("Accept-Language","zh-CN");
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.connect();
                if(conn.getResponseCode() == 200){
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
                    String str;
                    while((str = br.readLine()) != null){
                        sb.append(str);
                    }
                    return sb.toString();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String html) {
            try {
                if(news.link.indexOf("video") == -1) {
                    mTextContent.setVisibility(View.VISIBLE);
                    String content;
                    content = ContentExtractor.getContentByHtml(html);
                    tvTextTitle.setText(news.title);
                    tvContent.setText(content);
                    if(news.imageurls != null && news.imageurls.size() != 0){
                        setNewsImage();
                        L.d("tedu", "visable");
                    }else{
                        L.d("tedu","gone");
                        mImageViewPager.setVisibility(View.GONE);
                    }
                }else{
                    mVideoContent.setVisibility(View.VISIBLE);
                    tvVideoTitle.setText(news.title);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setNewsImage(){
        mImageViewPager.setVisibility(View.VISIBLE);
        List<ImageInfo> imageUrls = news.imageurls;
        mImageViewPager.setAdapter(new NewsImageAdapter(this,imageUrls));
    }

    @OnClick({R.id.iv_menu})
    public void back(View view){
        finish();
        overridePendingTransition(R.anim.translate_in_right,R.anim.translate_out_right);
    }

    class NewsImageAdapter extends PagerAdapter{
        private List<ImageInfo> imageUrls;
        private Context context;

        public NewsImageAdapter(Context context,List<ImageInfo> imageUrls){
            this.imageUrls = imageUrls;
            this.context = context;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            NetworkImageView niv = new NetworkImageView(context);
            niv.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
            container.addView(niv);
            niv.setImageUrl(imageUrls.get(position).url,MyApplication.getLoader());

            return niv;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return imageUrls.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}