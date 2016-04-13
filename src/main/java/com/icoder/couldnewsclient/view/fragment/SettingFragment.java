package com.icoder.couldnewsclient.view.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.icoder.couldnewsclient.R;
import com.icoder.couldnewsclient.adapter.SettingAdapter;
import com.icoder.couldnewsclient.app.MyApplication;
import com.icoder.couldnewsclient.entity.SettingItem;
import com.icoder.couldnewsclient.utils.SPUtils;
import com.zcw.togglebutton.ToggleButton;

import java.util.ArrayList;

public class SettingFragment extends BaseFragment{
    RecyclerView recyclerView;
    SettingAdapter adapter;

    String wifiAutoOffline = "WI-FI下自动离线";
    String wifiDownloadPic = "非WI-FI下不下载图片";
    String nightMode = "夜间模式";

    @Override
    protected void initView() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        ArrayList<SettingItem> items = initSettingItems();
        adapter = new SettingAdapter(getActivity(),items);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new SettingAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                if (view instanceof ToggleButton) {
                    ToggleButton btn = (ToggleButton) view;
                    btn.toggle(true);
                }
            }
        });

        adapter.setOnToggleButtonListener(mOnToggleChangedListener);


    }

    private ToggleButton.OnToggleChanged mOnToggleChangedListener = new ToggleButton.OnToggleChanged() {
        @Override
        public void onToggle(View view,boolean on) {
            ToggleButton btn = (ToggleButton) view;
            if(btn.tag.equals(wifiAutoOffline)){                   //WI-FI下自动离线
                SPUtils.put(getActivity(),"isWifiAutoOffline",on);
            } else if(btn.tag.equals(wifiDownloadPic)){            //非WI-FI下不下载图片
                SPUtils.put(getActivity(),"isWifiDownloadPic",on);
            }
        }
    };

    private ArrayList<SettingItem> initSettingItems() {
        MyApplication app = (MyApplication) MyApplication.getContext();

        boolean isWifiAutoOffline = (boolean) SPUtils.get(getActivity(), "isWifiAutoOffline",false);
        boolean isWifiDownloadPic = (boolean) SPUtils.get(getActivity(), "isWifiDownloadPic",false);

        ArrayList<SettingItem> settings = new ArrayList<>();
        settings.add(new SettingItem("帐号设置",SettingItem.TYPE_ITEM_TITLE));
        settings.add(new SettingItem("个人设置",SettingItem.TYPE_ITEM_NORMAL));
        settings.add(new SettingItem("绑定其他平台",SettingItem.TYPE_ITEM_NORMAL));
        settings.add(new SettingItem("系统设置",SettingItem.TYPE_ITEM_TITLE));
        settings.add(new SettingItem(wifiAutoOffline,SettingItem.TYPE_ITEM_TEXT_BTN,isWifiAutoOffline));
        settings.add(new SettingItem(wifiDownloadPic,SettingItem.TYPE_ITEM_TEXT_BTN,isWifiDownloadPic));
        settings.add(new SettingItem(nightMode,SettingItem.TYPE_ITEM_TEXT_BTN,app.isNightMode()));
        settings.add(new SettingItem("正文字号","中号字",SettingItem.TYPE_ITEM_TEXT_TEXT));
        settings.add(new SettingItem("手动清理缓存","64.77M",SettingItem.TYPE_ITEM_TEXT_TEXT));
        settings.add(new SettingItem("其他设置",SettingItem.TYPE_ITEM_TITLE));
        settings.add(new SettingItem("检查更新",SettingItem.TYPE_ITEM_NORMAL));
        settings.add(new SettingItem("关于",SettingItem.TYPE_ITEM_NORMAL));
        return settings;
    }

    @Override
    protected int inflateFragmentById() {
        return R.layout.frag_setting;
    }
}