package com.seeapenny.client.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.seeapenny.client.General;
import com.seeapenny.client.R;
import com.seeapenny.client.SeeapennyApp;
import com.seeapenny.client.list.ListItemsFactory;

import java.util.ArrayList;


public class ExpandableGoodByCategoryAdapter extends BaseExpandableListAdapter {

    private LayoutInflater mInflater;
    protected ListItemsFactory<String> factoryShopList;
    protected ListItemsFactory<Good> factoryGood;
    private SeeapennyApp app;

    private boolean isShowImage;
    private boolean isShowNote;


    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    private Context context;
    private ArrayList<String> groups;
    private ArrayList<ArrayList<Good>> children;


    public ExpandableGoodByCategoryAdapter(Context context, ArrayList<String> groups,
                                           ArrayList<ArrayList<Good>> children, ListItemsFactory<String> factoryShopList, ListItemsFactory<Good> factoryGood) {
        this.context = context;
        this.factoryShopList = factoryShopList;
        this.factoryGood = factoryGood;
        this.groups = groups;
        this.children = children;

        app = SeeapennyApp.getInstance();

        isShowImage = app.getProductImage();
        isShowNote = app.getProductNote();

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void removeAllItems() {
        groups.clear();
        children.clear();
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return groups.isEmpty();
    }

    public void refreshAllItems(ArrayList<String> groups,
                                ArrayList<ArrayList<Good>> children) {
        this.groups = groups;
        this.children = children;
        notifyDataSetChanged();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return children.get(groupPosition).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {

        final ViewHolderChild holder;

        final Good good = (Good) getChild(groupPosition, childPosition);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.good_item, parent, false);

            holder = new ViewHolderChild();

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
            holder = (ViewHolderChild) convertView.getTag();
        }


        final View row = convertView;

        convertView.setEnabled(true);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                factoryGood.setOnClickListener(good, row);
            }
        });

        if (isShowImage) {
            holder.goodImage.setVisibility(View.VISIBLE);
            General.tryDownloadWaitableImage(holder.goodImage, null, good.getSmallUrl());
        } else {
            holder.goodImage.setVisibility(View.GONE);
        }

        holder.goodName.setText(good.getName());

        switch (good.getState()) {
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

        if (good.isPriority()) {
            holder.important.setVisibility(View.VISIBLE);
        } else {
            holder.important.setVisibility(View.GONE);
        }

        if (good.getQuantity() != 0) {
            if (good.getMeasure() > 0) {
                String[] strings = convertView.getResources().getStringArray(R.array.measure_array);
                holder.goodQuantity.setText(" (" + String.valueOf(good.getQuantity()) + " " + strings[good.getMeasure()] + ")");
            } else {
                holder.goodQuantity.setText(" (" + String.valueOf(good.getQuantity()) + ")");
            }
        } else {
            holder.goodQuantity.setText(null);
        }


        if (good.getNote() != null && good.getNote().length() != 0 && isShowNote) {
            holder.goodNote.setText(good.getNote());
            holder.goodNote.setVisibility(View.VISIBLE);
        } else {
            holder.goodNote.setText(null);
            holder.goodNote.setVisibility(View.GONE);
        }

        holder.goodPrice.setText(String.valueOf(good.getPrice() * good.getQuantity()) + " " + app.getCurrency());


        if (good.getCategoryId() > 0) {
//            holder.goodCategory.setText(goodResponse.getGoodCategory().getCategory());   //todo null pointer why?
            holder.categoryIcon.setImageResource(good.getGoodCategoryImageId());
        } else {
            holder.categoryIcon.setImageResource(good.getGoodCategoryImageId());
//            holder.goodCategory.setText(activity.getResources().getString(R.string.without_category));
        }

        holder.goodMark.setEnabled(true);
        holder.goodMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                goodResponse.setState(goodResponse.getState() > 0 ? 0 : 1);
                factoryGood.setOnClickListener(good, holder.goodMark);
            }
        });

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return children.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final ViewHolderGroups holder;

        final String categoryName = (String) getGroup(groupPosition);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.category_group_item, null);

            holder = new ViewHolderGroups();

            holder.textCategory = (TextView)convertView.findViewById(R.id.categoryTitle);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolderGroups) convertView.getTag();
        }

        holder.textCategory.setText(categoryName);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {

    }

    @Override
    public void onGroupCollapsed(int groupPosition) {

    }

    static class ViewHolderChild {
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

    static class ViewHolderGroups {
        TextView textCategory;
    }
}

