package com.icoder.couldnewsclient.utils;

import android.animation.ObjectAnimator;
import android.view.View;

/**
 * Created by tarena on 2016/3/31.
 */
public class PropertyAnimationUtils {

    /**
     *
     * @param view      要位移的对象
     */
    public static void translateYObject(View view,int distance,int duration){
        int y = (int) view.getTranslationY();
        ObjectAnimator.ofFloat(view,"TranslationY",y,y + distance).setDuration(duration).start();
    }
}