package com.icoder.couldnewsclient.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.android.volley.toolbox.NetworkImageView;
import com.icoder.couldnewsclient.R;
import com.icoder.couldnewsclient.app.MyApplication;
import com.icoder.couldnewsclient.entity.News;
import com.icoder.couldnewsclient.widget.AutoRollLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class NewsAdapter extends Adapter<ViewHolder> {
    private static final int TYPE_ITEM_NORMAL = 0;
    private static final int TYPE_ITEM_MORE_PIC = 1;
    private static final int TYPE_FOOTER = 2;
    private static final int TYPE_HEADER = 3;

    private Context context;
    private List<News> data;
    LayoutInflater inflater;
    AutoRollLayout.OnItemClickListener listener;

    private List<? extends AutoRollLayout.IShowItem> items;

    public NewsAdapter(Context context, List<News> data) {
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return data.size() == 0 ? 0 : data.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else if (position == 0){
            return TYPE_HEADER;
        }

        News news = data.get(position - 1);
        if(news.imageurls != null && news.imageurls.size() >= 3){
            return TYPE_ITEM_MORE_PIC;
        } else{
            return TYPE_ITEM_NORMAL;
        }
    }

    private HeaderViewHolder header;
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM_NORMAL) {
            View view = inflater.inflate(R.layout.il_news_item, parent, false);
            return new ItemViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            View view = inflater.from(context).inflate(R.layout.il_item_foot, parent, false);
            return new FootViewHolder(view);
        } else if(viewType == TYPE_HEADER){
            View view = inflater.from(context).inflate(R.layout.il_autoroll_layout,parent,false);
            header = new HeaderViewHolder(view);
            header.autoRollLayout.setItem(items,MyApplication.getLoader());
            header.autoRollLayout.setOnItemClickListener(listener);
            return header;
        } else {
            View view = inflater.inflate(R.layout.il_news_item2, parent, false);
            return new ItemViewHolder2(view);
        }
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            //holder.tv.setText(data.get(position));
            News news = data.get(position - 1);
            ItemViewHolder ivh = (ItemViewHolder) holder;
            if(news.imageurls != null && news.imageurls.size() != 0) {
                ivh.niv_news_item_img.setVisibility(View.VISIBLE);
                ivh.niv_news_item_img.setImageUrl(news.imageurls.get(0).url, MyApplication.getLoader());
            }else{
                ivh.niv_news_item_img.setVisibility(View.GONE);
            }
            ivh.tv_news_item_desc.setText(news.desc);
            ivh.tv_news_item_title.setText(news.title);

            //设置事件
            ivh.tv_news_date.setText(formatTime(news.pubDate));
            ivh.tv_news_source.setText("来源:" + news.source);
            setNewsItemClickListener(ivh);
        } else if(holder instanceof ItemViewHolder2){
            News news = data.get(position - 1);
            ItemViewHolder2 ivh = (ItemViewHolder2) holder;

            ivh.niv_news_item_img1.setImageUrl(news.imageurls.get(0).url,MyApplication.getLoader());
            ivh.niv_news_item_img2.setImageUrl(news.imageurls.get(1).url,MyApplication.getLoader());
            ivh.niv_news_item_img3.setImageUrl(news.imageurls.get(2).url,MyApplication.getLoader());

            ivh.tv_news_item_title.setText(news.title);

            //设置事件
            ivh.tv_news_date.setText(formatTime(news.pubDate));
            ivh.tv_news_source.setText("来源:" + news.source);
            setNewsItemClickListener(ivh);
        }
    }

    private void setNewsItemClickListener(final ViewHolder holder){
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(holder.itemView, position);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = holder.getLayoutPosition();
                    onItemClickListener.onItemLongClick(holder.itemView, position);
                    return false;
                }
            });
        }
    }

    static class ItemViewHolder extends ViewHolder {
        NetworkImageView niv_news_item_img;
        TextView tv_news_item_title;
        TextView tv_news_item_desc;
        TextView tv_news_date;
        TextView tv_news_source;

        public ItemViewHolder(View view) {
            super(view);
            niv_news_item_img = (NetworkImageView) view.findViewById(R.id.niv_news_item_img);
            tv_news_item_title = (TextView) view.findViewById(R.id.tv_news_item_title);
            tv_news_item_desc = (TextView) view.findViewById(R.id.tv_news_item_desc);
            tv_news_date = (TextView) view.findViewById(R.id.tv_news_date);
            tv_news_source = (TextView) view.findViewById(R.id.tv_news_source);
        }
    }

    /**
     * 更多图片的Item
     */
    static class ItemViewHolder2 extends ViewHolder {
        NetworkImageView niv_news_item_img1;
        NetworkImageView niv_news_item_img2;
        NetworkImageView niv_news_item_img3;
        TextView tv_news_item_title;
        TextView tv_news_date;
        TextView tv_news_source;

        public ItemViewHolder2(View view) {
            super(view);
            niv_news_item_img1 = (NetworkImageView) view.findViewById(R.id.niv_news_item_img1);
            niv_news_item_img2 = (NetworkImageView) view.findViewById(R.id.niv_news_item_img2);
            niv_news_item_img3 = (NetworkImageView) view.findViewById(R.id.niv_news_item_img3);
            tv_news_item_title = (TextView) view.findViewById(R.id.tv_news_item_title);
            tv_news_date = (TextView) view.findViewById(R.id.tv_news_date);
            tv_news_source = (TextView) view.findViewById(R.id.tv_news_source);
        }
    }

    class FootViewHolder extends ViewHolder {

        public FootViewHolder(View view) {
            super(view);
        }
    }

    class HeaderViewHolder extends ViewHolder{
        AutoRollLayout autoRollLayout;
        public HeaderViewHolder(View view) {
            super(view);
            autoRollLayout = (AutoRollLayout) view.findViewById(R.id.arl_auto_roll_layout);
            autoRollLayout.setOnItemClickListener(listener);
        }
    }

    public void setHeaderItems(List<? extends AutoRollLayout.IShowItem> items){
        this.items = items;
    }

    public void setHeaderItemsListener(AutoRollLayout.OnItemClickListener listener){
        this.listener = listener;
    }

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat newFormatter = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat mFormatter = new SimpleDateFormat("HH:mm");
    /**
     * 格式化时间
     * @param time  事件字符串
     */
    private String formatTime(String time){
        try {
            Calendar now = Calendar.getInstance();
            Calendar newsTime = Calendar.getInstance();
            newsTime.setTime(sdf.parse(time));
            int nowYear = now.get(Calendar.YEAR);
            int nowDay = now.get(Calendar.DAY_OF_YEAR);
            int nowWeek = now.get(Calendar.WEEK_OF_YEAR);

            int newsYear = newsTime.get(Calendar.YEAR);
            int newsDay = newsTime.get(Calendar.DAY_OF_YEAR);
            int newsWeek = newsTime.get(Calendar.WEEK_OF_YEAR);

            //如果是同一天的话直接显示时间
            if(nowYear == newsYear && nowDay == newsDay){
                return "今天" + mFormatter.format(newsTime.getTime());
            }else if(nowYear == newsYear && nowDay - 1 == newsDay){
                return "昨天" + mFormatter.format(newsTime.getTime());
            }else if(nowYear == newsYear && nowDay - 2 == newsDay){
                return "前天" + mFormatter.format(newsTime.getTime());
            }else if(nowYear == newsYear && nowWeek == newsWeek){
                //同一周显示星期几
                switch(newsWeek){
                    case Calendar.MONDAY :
                        return "星期一" + mFormatter.format(newsTime.getTime());
                    case Calendar.TUESDAY:
                        return "星期二" + mFormatter.format(newsTime.getTime());
                    case Calendar.WEDNESDAY:
                        return "星期三" + mFormatter.format(newsTime.getTime());
                    case Calendar.THURSDAY:
                        return "星期四" + mFormatter.format(newsTime.getTime());
                    case Calendar.FRIDAY:
                        return "星期五" + mFormatter.format(newsTime.getTime());
                    case Calendar.SATURDAY:
                        return "星期六" + mFormatter.format(newsTime.getTime());
                    case Calendar.SUNDAY:
                        return "星期天" + mFormatter.format(newsTime.getTime());
                }
            }
            return newFormatter.format(newsTime.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}