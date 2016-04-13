package com.icoder.couldnewsclient.utils;

import android.content.Context;
import android.content.res.TypedArray;

import com.icoder.couldnewsclient.R;

/**
 * 主题工具类
 * Created by tarena on 2016/3/31.
 */
public class ThemeUtils {

    public static int[] getThemeColor(Context context,int... colorId){
        int[] colors = new int[colorId.length];

        TypedArray array = context.getTheme().obtainStyledAttributes(colorId);

        for(int i = 0;i < colors.length;i++){
            colors[i] = array.getColor(i,0x000000);
        }

        array.recycle();
        return colors;
    }

}
