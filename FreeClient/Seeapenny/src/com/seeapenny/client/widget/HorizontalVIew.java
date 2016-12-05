package com.seeapenny.client.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * Created by IntelliJ IDEA.
 * User: Sony
 * Date: 06.06.13
 * Time: 22:29
 * To change this template use File | Settings | File Templates.
 */
public class HorizontalVIew extends LinearLayout implements GestureDetector.OnGestureListener {
    private GestureDetector mGestureDetector;
    private ListView mListView;

    public HorizontalVIew(Context context) {
        super(context);
        mGestureDetector = new GestureDetector(this);
        mGestureDetector.setIsLongpressEnabled(false);
    }

    public HorizontalVIew(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGestureDetector = new GestureDetector(this);
        mGestureDetector.setIsLongpressEnabled(false);
    }

    @Override
    public boolean onDown(MotionEvent arg0) {
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        //empty
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        int scrollWidth = mListView.getWidth() - this.getWidth();
        if ((this.getScrollX() >= 0) && (this.getScrollX() <= scrollWidth) && (scrollWidth > 0)) {
            int moveX = (int)distanceX;
            if (((moveX + this.getScrollX()) >= 0) && ((Math.abs(moveX) + Math.abs(this.getScrollX())) <= scrollWidth)) {
                this.scrollBy(moveX, 0);
            }
            else {
                if (distanceX >= 0) {
                    this.scrollBy(scrollWidth - Math.max(Math.abs(moveX), Math.abs(this.getScrollX())), 0);
                }
                else {
                    this.scrollBy(-Math.min(Math.abs(moveX), Math.abs(this.getScrollX())), 0);
                }
            }
        }
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        //empty
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        mGestureDetector.onTouchEvent(ev);
        return true;
    }
}
