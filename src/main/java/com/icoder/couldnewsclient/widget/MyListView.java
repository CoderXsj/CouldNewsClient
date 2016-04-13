package com.icoder.couldnewsclient.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class MyListView extends View{
    Paint paint;
    Rect rect = new Rect();

    String str = "#ABCDEFHIJKLMNOPQRSTUVWXYZ#";
    char[] letters;



    public MyListView(Context context) {
        this(context, null);
    }

    public MyListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        letters = str.toCharArray();
        initPaint();
    }

    private void initPaint(){
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
        paint.setStyle(Paint.Style.FILL);
        paint.setTypeface(Typeface.MONOSPACE);
    }

    /**
     * 把我希望的view的样子画到屏幕上
     * 如果要画画,就需要画布(canvas)
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        int i = 0;
        float height = getHeight()  * 1.0f / letters.length;
        for(char c : letters){
            String s = String.valueOf(c);
            paint.getTextBounds(s,0,1,rect);
            //一个框的宽度 / 2 - 字的宽度 / 2
            float x = getWidth() / 2 - rect.width() / 2;
            //一个框的高度 / 2 + 字的宽度 / 2 + 若干个框的高度
            float y = height / 2 + rect.height() / 2 + height * i;
            canvas.drawText(s,x,y,paint);
        }
    }

    private OnTouchListener listener;
    public static interface OnTouchListener{
        void onTouch(char letter);
    }

    public void setListener(OnTouchListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float height = getHeight()  * 1.0f / letters.length;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                int index = (int) (event.getY() / height);
                if(listener != null)
                    listener.onTouch(letters[index]);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return true;
    }
}