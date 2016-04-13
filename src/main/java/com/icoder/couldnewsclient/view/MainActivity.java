package com.icoder.couldnewsclient.view;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.icoder.couldnewsclient.R;
import com.icoder.couldnewsclient.StatusBarCompat;
import com.icoder.couldnewsclient.app.MyApplication;
import com.icoder.couldnewsclient.utils.PropertyAnimationUtils;
import com.icoder.couldnewsclient.view.fragment.NewsFragment;
import com.icoder.couldnewsclient.view.fragment.SettingFragment;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends FragmentActivity {
    @Bind(R.id.toolbar)
    RelativeLayout toolbar;
    @Bind(R.id.dl_drawer_layout)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.il_bottom_menu)
    ViewGroup mBottomMenu;

    @Bind(R.id.tv_nav_tab_news)
    TextView mTabNews;
    @Bind(R.id.tv_nav_tab_read)
    TextView mTabRead;
    @Bind(R.id.tv_nav_tab_va)
    TextView mTabVa;
    @Bind(R.id.tv_nav_tab_pc)
    TextView mTabPc;

    ArrayList<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication app = (MyApplication) getApplication();

        if(app.isNightMode())
            setTheme(R.style.AppTheme_night);
        else
            setTheme(R.style.AppTheme_day);
        setContentView(R.layout.activity_main);
//        StatusBarCompat.compat(this);

        fragments = new ArrayList<>();
        fragments.add(new NewsFragment());
        fragments.add(new SettingFragment());

        ButterKnife.bind(this);

        initActionBar();
        mTabNews.performClick();
    }

    public void dismissBottomMenu(){
        int y = (int) mBottomMenu.getTranslationY();
        PropertyAnimationUtils.translateYObject(mBottomMenu,mBottomMenu.getMeasuredHeight(),500);
        ObjectAnimator.ofFloat(mBottomMenu,"TranslationY", y, mBottomMenu.getMeasuredHeight() + y).setDuration(500).start();
//        ObjectAnimator.ofFloat(mBottomMenu,"alpha",1.f,0.f).setDuration(200).start();
    }

    public void showBottomMenu(){
        int y = (int) mBottomMenu.getTranslationY();
        ObjectAnimator.ofFloat(mBottomMenu,"TranslationY", y, y - mBottomMenu.getMeasuredHeight()).setDuration(500).start();
    }

    private void initActionBar(){
    }

    private void initBottomMenu(){
        mTabNews.setSelected(false);
        mTabRead.setSelected(false);
        mTabVa.setSelected(false);
        mTabPc.setSelected(false);
    }

    @OnClick({R.id.tv_nav_tab_news,R.id.tv_nav_tab_read,R.id.tv_nav_tab_va,R.id.tv_nav_tab_pc})
    public void selectedBottomMenu(View view){
        initBottomMenu();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        switch(view.getId()){
            case R.id.tv_nav_tab_news:
                mTabNews.setSelected(true);
                transaction.replace(R.id.rl_container,fragments.get(0));
                break;
            case R.id.tv_nav_tab_read:
                mTabRead.setSelected(true);
                break;
            case R.id.tv_nav_tab_va:
                mTabVa.setSelected(true);
                break;
            case R.id.tv_nav_tab_pc:
                mTabPc.setSelected(true);
                transaction.replace(R.id.rl_container,fragments.get(1));
                break;
        }
        transaction.commit();
    }
}