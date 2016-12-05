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

import com.seeapenny.client.R;
import com.seeapenny.client.SeeapennyApp;
import com.seeapenny.client.list.ListItemsFactory;
import com.seeapenny.client.server.GoodResponse;
import com.seeapenny.client.server.ShopListResponse;

import java.util.ArrayList;


public class ExpandableShopListAdapter extends BaseExpandableListAdapter {

    private LayoutInflater mInflater;
    protected ListItemsFactory<ShopList> factoryShopList;
    protected ListItemsFactory<Good> factoryGood;
    private SeeapennyApp app;


    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    private Context context;
    private ArrayList<ShopList> groups;
    private ArrayList<ArrayList<Good>> children;


    public ExpandableShopListAdapter(Context context, ArrayList<ShopList> groups,
                                     ArrayList<ArrayList<Good>> children, ListItemsFactory<ShopList> factoryShopList, ListItemsFactory<Good> factoryGood) {
        this.context = context;
        this.factoryShopList = factoryShopList;
        this.factoryGood = factoryGood;
        this.groups = groups;
        this.children = children;

        app = SeeapennyApp.getInstance();

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItem(ShopList shopList) {
        groups.add(0, shopList);

        ArrayList<Good> goods = new ArrayList<Good>();
        children.add(0, goods);
        notifyDataSetChanged();
    }

    public void removeAllItems() {
        groups.clear();
        children.clear();
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return groups.isEmpty();
    }

    public void refreshAllItems(ArrayList<ShopList> groups,
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
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final Good good = (Good) getChild(groupPosition, childPosition);
        final ShopList shopList = (ShopList) getGroup(groupPosition);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.shop_list_child_item, parent, false);
            holder = new ViewHolderChild();

            holder.content = convertView.findViewById(R.id.content);
            holder.mark = convertView.findViewById(R.id.mark);

            holder.goodName = (TextView) convertView.findViewById(R.id.good_name);
            holder.goodQuantity = (TextView) convertView.findViewById(R.id.good_quantity);

            holder.category = convertView.findViewById(R.id.category);
            holder.categoryIcon = (ImageView) convertView.findViewById(R.id.category_icon);
            holder.goodCategory = (TextView) convertView.findViewById(R.id.good_category);
            holder.goodPrice = (TextView) convertView.findViewById(R.id.good_price);

            holder.totalPrice = (TextView) convertView.findViewById(R.id.totalPrice);
            holder.totalView = convertView.findViewById(R.id.totalView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolderChild) convertView.getTag();
        }

        if (childPosition == getChildrenCount(groupPosition) - 1) {
            holder.totalView.setVisibility(View.VISIBLE);
        } else {
            holder.totalView.setVisibility(View.GONE);
        }

        holder.goodName.setText(good.getName());
        holder.totalPrice.setText(app.getResources().getString(R.string.totalPrice, String.valueOf(shopList.getTotalPrice()) + " " + app.getCurrency()));

        switch (good.getState()) {
            case 0:
                holder.goodName.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
                holder.goodName.setTextColor(Color.BLACK);
                holder.mark.setBackgroundResource(R.drawable.item_ok_good_dis);
                break;
            case 1:
                holder.goodName.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                holder.goodName.setTextColor(Color.GRAY);
                holder.mark.setBackgroundResource(R.drawable.item_ok_good_en);
                break;
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

        if (good.getCategoryId() > 0) {
//            holder.goodCategory.setText(goodResponse.getGoodCategory().getCategory()); ///todo null pointer
            holder.categoryIcon.setImageResource(good.getGoodCategoryImageId());
        } else {
            holder.categoryIcon.setImageResource(good.getGoodCategoryImageId());
//            holder.goodCategory.setText(context.getResources().getString(R.string.without_category));
        }


        holder.goodPrice.setText(String.valueOf(good.getPrice() * good.getQuantity()) + " " + app.getCurrency());

        holder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                factoryGood.setOnClickListener(good, holder.content);
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

        final ShopList shopList = (ShopList) getGroup(groupPosition);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.shop_list_group_item, null);

            holder = new ViewHolderGroups();


            holder.listShareLayout = convertView.findViewById(R.id.list_share_layout);
            holder.lastEditLayout = convertView.findViewById(R.id.last_edit_layout);

            holder.actual = (ImageView) convertView.findViewById(R.id.actual);
            holder.textShare = (TextView) convertView.findViewById(R.id.text_share);
            holder.listTitle = (TextView) convertView.findViewById(R.id.listTitle);
            holder.listTime = (TextView) convertView.findViewById(R.id.listTime);
            holder.listContent = convertView.findViewById(R.id.listContent);
            holder.goToGoods = convertView.findViewById(R.id.go_to_goods);
            holder.count = (TextView) convertView.findViewById(R.id.count);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolderGroups) convertView.getTag();
        }

        if (groupPosition == 0) {
            holder.actual.setBackgroundResource(R.drawable.actual_expanded_selector);
        } else {
            holder.actual.setBackgroundResource(R.drawable.not_actual_expanded_selector);
        }

        holder.actual.setSelected(shopList.isSelected());
        holder.actual.setEnabled(true);
        holder.actual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shopList.setSelected(!holder.actual.isSelected());
                holder.actual.setSelected(!holder.actual.isSelected());

                holder.actual.setTag(groupPosition);
                factoryShopList.setOnClickListener(shopList, holder.actual);
            }
        });


        if (shopList.getOwnerUser() != null && shopList.getOwnerUser().getId() != app.getOwnerID()) {
            holder.lastEditLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    factoryShopList.setOnClickListener(shopList, holder.lastEditLayout);
                }
            });
        } else {
            holder.lastEditLayout.setOnClickListener(null);
        }


        holder.listTitle.setText(shopList.getName());
        holder.listTime.setText(app.formatElapsed(shopList.getLastModifiedTime()));

        holder.listContent.setEnabled(true);
        holder.listContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                factoryShopList.setOnClickListener(shopList, holder.listContent);
            }
        });

        holder.goToGoods.setEnabled(true);
        holder.goToGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                factoryShopList.setOnClickListener(shopList, holder.goToGoods);
            }
        });

        holder.count.setEnabled(true);
        holder.count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.goToGoods.performClick();
            }
        });

        //todo переделать
        holder.count.setText(shopList.getCountLeftGoods() + "/" + getChildrenCount(groupPosition));

        if (shopList.isShared()) {

//            holder.textShare.setText(String.valueOf(shopListResponse.getUserShareList().size()));
            holder.listShareLayout.setVisibility(View.VISIBLE);
            holder.listShareLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    factoryShopList.setOnClickListener(shopList, holder.listShareLayout);
                }
            });
        } else {
            holder.listShareLayout.setVisibility(View.GONE);
        }

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

    static class ViewHolderChild {
        View separatorTop;
        View separatorBottom;
        TextView goodName;
        TextView goodQuantity;

        View category;
        ImageView categoryIcon;
        TextView goodCategory;
        TextView goodPrice;
        TextView totalPrice;
        View totalView;

        View content;

        View mark;
    }

    static class ViewHolderGroups {
        View listShareLayout;
        View lastEditLayout;
        ImageView actual;

        TextView textShare;
        TextView listTitle;
        TextView listTime;
        View goToGoods;
        TextView count;
        View listContent;
    }
}

