package com.seeapenny.client.list;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import com.seeapenny.client.R;

import java.util.ArrayList;
import java.util.List;

public abstract class ListHandler<I> extends BaseAdapter {

    protected ListView listView;

    protected int totalCount;
    protected List<I> items;
    protected boolean requestMode;

    protected ListItemsFactory<I> factory;

    protected LayoutInflater inflater;
    protected Activity activity;

    public ListHandler(View view, Activity activity, ListItemsFactory<I> factory) {
        this.activity = activity;
        this.factory = factory;
        create(view, new ArrayList<I>());
    }

    public void create(View view) {

//        create(view, new ArrayList<I>());
    }

    public void create(View view, List<I> items) {
        this.items = items;

        inflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null) {
            listView = (ListView) activity.findViewById(R.id.list);
        } else {
            listView = (ListView) view.findViewById(R.id.list);
        }

        if (items.size() != 0) {
            this.totalCount = items.size();
        }

        listView.setAdapter(this);

        if (items.size() == 0) {
            createMoreItems(0);
        }

    }

    public void takeMoreItems(int totalCount, List<I> items) {
//        listView.removeFooterView(waitItem);

        if (items.size() == 0) {
            totalCount = this.items.size();
        }

        if (totalCount > 0) {
            this.totalCount = totalCount;
            this.items.addAll(items);
            notifyDataSetChanged();
        }

        requestMode = false;
    }

    public void takeMoreItems(int totalCount, I items) {
//        listView.removeFooterView(waitItem);

        if (totalCount > 0) {
            this.totalCount = totalCount;
            this.items.add(0, items);
            notifyDataSetChanged();
        }

        requestMode = false;
    }

    public void refreshItems(int totalCount) {
//        listView.removeFooterView(waitItem);

        if (totalCount > 0) {
            this.totalCount = totalCount;
            notifyDataSetChanged();
        }

        requestMode = false;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        listView.setOnItemClickListener(listener);
    }

    public ListView getListView() {
        return listView;
    }

    public List<I> getAllItems() {
        return items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public I getItem(int position) {
        final int count = items.size();
        if (totalCount > count && position == count - 1 && !requestMode) {
//            listView.addFooterView(waitItem);
            createMoreItems(count);
        }
        return items.get(position);
    }

    protected void createMoreItems(int position) {
        factory.createMoreItems(this, position);
        requestMode = true;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }


    public void clear() {
        factory = null;
        setOnItemClickListener(null);
        items.clear();
        notifyDataSetChanged();


    }

    public void refreshAllItems(List<I> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void removeAllItems() {
        items.clear();
        totalCount = 0;
        notifyDataSetChanged();
    }
}