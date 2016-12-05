package com.seeapenny.client.activity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Gallery;

/**
 * Created by IntelliJ IDEA.
 * User: Sony
 * Date: 17.04.13
 * Time: 22:27
 * To change this template use File | Settings | File Templates.
 */
public class GalleryCustom extends Gallery {

    public GalleryCustom(Context ctx, AttributeSet attrSet) {
        super(ctx, attrSet);
    }

    @SuppressWarnings("unused")
    private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
        return e2.getX() > e1.getX();
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return true;
    }
}