package com.seeapenny.client.widget;

import android.content.Context;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import com.seeapenny.client.General;
import com.seeapenny.client.R;

public class ClickableToast implements OnClickListener {
   
   private TextView contentView;
   private volatile PopupWindow popup;
   private int duration;
   private OnClickListener listener;
   
   public ClickableToast(Context context) {
      contentView = new TextView(context);
      contentView.setGravity(Gravity.CENTER);
      contentView.setBackgroundResource(R.drawable.audio_dialog_shape);
      
      contentView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.white_arrow, 0);
      int padding = General.dpToPx(context.getResources(), 15);
      contentView.setCompoundDrawablePadding(padding);
      contentView.setPadding(padding, padding, padding, padding);
      int minHeight = General.dpToPx(context.getResources(), 48);
      contentView.setMinHeight(minHeight);
      
      popup = new PopupWindow(contentView);
      popup.setAnimationStyle(R.style.clickable_toast);
      popup.setFocusable(false);
      popup.setTouchable(true);
   }
   
   public void setText(String text) {
      contentView.setText(text);
      contentView.setTextColor(0xFFFFFFFF);
      contentView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
   }
   
   public void setOnClickListener(OnClickListener listener) {
      this.listener = listener;
      contentView.setOnClickListener(this);
   }
   
   @Override
   public void onClick(View view) {
      listener.onClick(view);
      dismiss();
   }
   
   public void setOnDismissListener(OnDismissListener listener) {
      popup.setOnDismissListener(listener);
   }
   
   public void setDuration(int duration) {
      this.duration = duration;
   }
   
   public void show(View anchor, int maxW, int maxH, int gravity) {
      contentView.measure(MeasureSpec.makeMeasureSpec(maxW * 8 / 10, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(maxH, MeasureSpec.AT_MOST));
      int w = contentView.getMeasuredWidth();
      int h = contentView.getMeasuredHeight();
      // Log.d("ClickableToast", "w: " + w + " h: " + h);
      
      int hGravity = gravity & Gravity.HORIZONTAL_GRAVITY_MASK;
      int x = 0;
      if (hGravity == Gravity.LEFT) {
         x = 0;
      } else if (hGravity == Gravity.CENTER_HORIZONTAL) {
         x = (maxW - w) / 2;
      } else if (hGravity == Gravity.RIGHT) {
         x = maxW - w;
      }
      
      int vGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;
      int y = 0;
      if (vGravity == Gravity.TOP) {
         y = -maxH;
      } else if (vGravity == Gravity.CENTER_VERTICAL) {
         y = -maxH + (maxH - h) / 2;
      } else if (vGravity == Gravity.BOTTOM) {
         y = -h;
      }
      
      popup.setWidth(w);
      popup.setHeight(h);
      
      final View fa = anchor;
      final int fx = x;
      final int fy = y;
      fa.post(new Runnable() {
         @Override
         public void run() {
            popup.showAsDropDown(fa, fx, fy);   //must be called after all activity lifecycle events
         }
      });
      
      Handler handler = new Handler();
      handler.postDelayed(new Runnable() {
         @Override
         public void run() {
            dismiss();
         }
      }, duration);
   }
   
   public void dismiss() {
      if (popup != null && popup.isShowing()) {
//         try {
            popup.dismiss();
//         } catch (IllegalArgumentException iaex) {
//            //do nothing, bug at Android framework
//         }
      }
      popup = null;
   }
   
}
