package com.icoder.couldnewsclient.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.icoder.couldnewsclient.R;
import com.icoder.couldnewsclient.entity.SettingItem;
import com.icoder.couldnewsclient.utils.DensityUtils;
import com.icoder.couldnewsclient.utils.ThemeUtils;
import com.zcw.togglebutton.ToggleButton;

import java.util.ArrayList;

/**
 * Created by tarena on 2016/4/7.
 */
public class SettingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<SettingItem> settings;
    private LayoutInflater inflater;
    private int textColor;                  //文本的颜色
    private int titleTextColor;             //标题文本的颜色
    private int itemHeight;

    public SettingAdapter(Context context,ArrayList<SettingItem> settings){
        this.context = context;
        this.settings = settings;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        int[] ids = new int[]{R.attr.textColor,R.attr.titleColor};
        int[] themeColor = ThemeUtils.getThemeColor(context, ids);
        textColor = themeColor[0];
        titleTextColor = themeColor[1];

        itemHeight = context.getResources().getDimensionPixelSize(R.dimen.dimen_40);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch(viewType){
            case SettingItem.TYPE_ITEM_NORMAL:      //普通的文本的item
                TextView itemTextView = new TextView(context);
                viewHolder = new TextHolder(itemTextView);
                break;
            case SettingItem.TYPE_ITEM_TEXT_BTN:    //带有text和Button的item
                View text_btn_item = inflater.inflate(R.layout.il_setting_text_btn_item, viewGroup, false);
                viewHolder = new TextButtonHolder(text_btn_item);
                break;
            case SettingItem.TYPE_ITEM_TITLE:       //标题的item
                TextView itemTitleView = new TextView(context);
                viewHolder = new SettingTitleHolder(itemTitleView);
                break;
            case SettingItem.TYPE_ITEM_TEXT_TEXT:
                View text_text_item = inflater.inflate(R.layout.il_setting_text_text_item, viewGroup, false);
                viewHolder = new TextTextHolder(text_text_item);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        SettingItem item = settings.get(position);

        if(viewHolder instanceof TextHolder){
            TextHolder holder = (TextHolder) viewHolder;
            holder.tv.setText(item.title);
            holder.tv.setTag(position);
            holder.tv.setOnClickListener(mClickListener);
        } else if(viewHolder instanceof TextButtonHolder){
            TextButtonHolder holder = (TextButtonHolder) viewHolder;
            holder.title.setText(item.title);
            holder.button.setTag(position);
            holder.button.setOnClickListener(mClickListener);
            if(item.status)
                holder.button.setToggleOn();
            else
                holder.button.setToggleOff();
            holder.button.tag = item.title;
            if(mOnToggleChangedListener != null)
                holder.button.setOnToggleChanged(mOnToggleChangedListener);
        } else if(viewHolder instanceof SettingTitleHolder){
            SettingTitleHolder holder = (SettingTitleHolder) viewHolder;
            holder.tv.setText(item.title);
        } else if(viewHolder instanceof  TextTextHolder){
            TextTextHolder holder = (TextTextHolder) viewHolder;
            holder.title.setText(item.title);
            holder.info.setText(item.info);
        }
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(onItemClickListener != null)
                onItemClickListener.onItemClickListener(v, (Integer) v.getTag());
        }
    };
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
    private OnItemClickListener onItemClickListener;

    ToggleButton.OnToggleChanged mOnToggleChangedListener;
    public void setOnToggleButtonListener(ToggleButton.OnToggleChanged mOnToggleChangedListener) {
        this.mOnToggleChangedListener = mOnToggleChangedListener;
    }

    public interface OnItemClickListener{
        void onItemClickListener(View view,int position);
    }

    @Override
    public int getItemCount() {
        return settings.size();
    }

    @Override
    public int getItemViewType(int position) {
        return settings.get(position).type;
    }

    /**
     * 只有文本的viewHolder
     */
    class TextHolder extends RecyclerView.ViewHolder{
        TextView tv;

        public TextHolder(View itemView) {
            super(itemView);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,itemHeight);

            tv = (TextView) itemView;
            tv.setTextColor(textColor);
            tv.setTextSize(DensityUtils.sp2px(context, 11));
            tv.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            tv.setPadding(DensityUtils.dp2px(context, 10), 0, 0, 0);
            tv.setLayoutParams(lp);
        }
    }

    /**
     * Text带button带Button的viewHolder
     */
    class TextButtonHolder extends RecyclerView.ViewHolder{
        TextView title;
        ToggleButton button;

        public TextButtonHolder(View itemView) {
            super(itemView);

            button = (ToggleButton) itemView.findViewById(R.id.tb_button);
            title = (TextView) itemView.findViewById(R.id.tv_title);
        }
    }

    /**
     * 设置的标题的Holder
     */
    class SettingTitleHolder extends RecyclerView.ViewHolder{
        TextView tv;

        public SettingTitleHolder(View itemView) {
            super(itemView);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,itemHeight);

            tv = (TextView) itemView;
            tv.setTextColor(titleTextColor);
            tv.setTextSize(DensityUtils.sp2px(context, 8));
            tv.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            tv.setPadding(DensityUtils.dp2px(context, 10), 0, 0, 0);
            tv.setLayoutParams(lp);
        }
    }

    /**
     * 文本加文本的ViewHolder
     */
    class TextTextHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView info;

        public TextTextHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            info = (TextView) itemView.findViewById(R.id.tv_info);
            Drawable drawable = context.getResources().getDrawable(R.mipmap.biz_vote_org_arrow);
            drawable.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
            info.setCompoundDrawablePadding(10);
            info.setCompoundDrawables(null,null,drawable,null);
        }
    }
}