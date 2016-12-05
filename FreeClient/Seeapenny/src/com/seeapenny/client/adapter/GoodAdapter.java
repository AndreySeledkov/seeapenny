package com.seeapenny.client.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Vibrator;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;

import com.seeapenny.client.General;
import com.seeapenny.client.R;
import com.seeapenny.client.SeeapennyApp;
import com.seeapenny.client.list.ListHandler;
import com.seeapenny.client.list.ListItemsFactory;
import com.seeapenny.client.server.GoodResponse;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Sony
 * Date: 11.02.13
 * Time: 0:07
 * To change this template use File | Settings | File Templates.
 */
public class GoodAdapter extends ListHandler<Good> {

    private boolean isShowImage;
    private boolean isShowNote;
    private SeeapennyApp app;
    private LayoutInflater mInflater;

    public GoodAdapter(View view, Activity activity, ListItemsFactory<Good> factory) {
        super(view, activity, factory);
        app = SeeapennyApp.getInstance();
        isShowImage = app.getProductImage();
        isShowNote = app.getProductNote();

        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        final Good goodResponse = getItem(position);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.good_item, parent, false);

            holder = new ViewHolder();

            holder.mainView = convertView.findViewById(R.id.main_view);
            holder.viewGood = convertView.findViewById(R.id.view_good);


            holder.goodName = (TextView) convertView.findViewById(R.id.good_name);
            holder.goodQuantity = (TextView) convertView.findViewById(R.id.good_quantity);
            holder.goodImage = (ImageView) convertView.findViewById(R.id.good_image);
            holder.goodNote = (TextView) convertView.findViewById(R.id.good_note);
            holder.goodPrice = (TextView) convertView.findViewById(R.id.good_price);

            holder.category = convertView.findViewById(R.id.category);
            holder.categoryIcon = (ImageView) convertView.findViewById(R.id.category_icon);
            holder.goodCategory = (TextView) convertView.findViewById(R.id.good_category);
            holder.important = (ImageView) convertView.findViewById(R.id.important);
            holder.goodMark = (ImageView) convertView.findViewById(R.id.mark);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        final View row = convertView;

        convertView.setEnabled(true);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                factory.setOnClickListener(goodResponse, row);
            }
        });

        if (isShowImage) {
            holder.goodImage.setVisibility(View.VISIBLE);
            General.tryDownloadWaitableImage(holder.goodImage, null, goodResponse.getSmallUrl());
        } else {
            holder.goodImage.setVisibility(View.GONE);
        }

        holder.goodName.setText(goodResponse.getName());

        switch (goodResponse.getState()) {
            case 0:
                holder.goodName.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
                holder.goodName.setTextColor(Color.BLACK);
                holder.goodMark.setBackgroundResource(R.drawable.item_ok_good_dis);
                break;
            case 1:
                holder.goodName.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                holder.goodName.setTextColor(Color.GRAY);
                holder.goodMark.setBackgroundResource(R.drawable.item_ok_good_en);
                break;
        }

        if (goodResponse.isPriority()) {
            holder.important.setVisibility(View.VISIBLE);
        } else {
            holder.important.setVisibility(View.GONE);
        }

        if (goodResponse.getQuantity() != 0) {
            if (goodResponse.getMeasure() > 0) {
                String[] strings = convertView.getResources().getStringArray(R.array.measure_array);
                holder.goodQuantity.setText(" (" + String.valueOf(goodResponse.getQuantity()) + " " + strings[goodResponse.getMeasure()] + ")");
            } else {
                holder.goodQuantity.setText(" (" + String.valueOf(goodResponse.getQuantity()) + ")");
            }
        } else {
            holder.goodQuantity.setText(null);
        }


        if (goodResponse.getNote() != null && goodResponse.getNote().length() != 0 && isShowNote) {
            holder.goodNote.setText(goodResponse.getNote());
            holder.goodNote.setVisibility(View.VISIBLE);
        } else {
            holder.goodNote.setText(null);
            holder.goodNote.setVisibility(View.GONE);
        }

        holder.goodPrice.setText(String.valueOf(goodResponse.getPrice() * goodResponse.getQuantity()) + " " + app.getCurrency());


        if (goodResponse.getCategoryId() > 0) {
//            holder.goodCategory.setText(goodResponse.getGoodCategory().getCategory());   //todo null pointer why?
            holder.categoryIcon.setImageResource(goodResponse.getGoodCategoryImageId());
        } else {
            holder.categoryIcon.setImageResource(goodResponse.getGoodCategoryImageId());
//            holder.goodCategory.setText(activity.getResources().getString(R.string.without_category));
        }

        holder.goodMark.setEnabled(true);
        holder.goodMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                goodResponse.setState(goodResponse.getState() > 0 ? 0 : 1);
                factory.setOnClickListener(goodResponse, holder.goodMark);
            }
        });

        return convertView;
    }

    public void removeItem(Good item) {
//        listView.removeFooterView(waitItem);


        for (Good i : items) {
            if (i.getId() == item.getId()) {
                items.remove(i);
                break;
            }
        }

        notifyDataSetChanged();

        totalCount--;

        requestMode = false;
    }

    public void clearAllSelected(Good good) {
        good.setSelected(false);
        notifyDataSetChanged();
    }

    public void refreshAllItems(List<Good> items) {
        super.refreshAllItems(items);
        isShowImage = app.getProductImage();
        isShowNote = app.getProductNote();
    }

    static class ViewHolder {
        View mainView;

        ImageView goodImage;
        TextView goodName;
        TextView goodQuantity;
        TextView goodNote;
        View viewGood;
        TextView goodPrice;
        View category;
        ImageView categoryIcon;
        TextView goodCategory;
        ImageView important;
        ImageView goodMark;


    }

}

