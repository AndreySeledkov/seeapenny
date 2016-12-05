package com.seeapenny.client.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.seeapenny.client.R;
import com.seeapenny.client.SeeapennyApp;
import com.seeapenny.client.list.ListItemsFactory;
import com.seeapenny.client.server.ShopListResponse;

import java.util.List;

/**
 * Created by Sony on 12.07.13.
 */
public class CopyShopListAdapter extends ArrayAdapter<ShopList> {

    private Activity activity;
    private LayoutInflater inflater;
    private SeeapennyApp app;

    protected ListItemsFactory<ShopList> factory;

    private List<ShopList> currencyItems;

    public CopyShopListAdapter(Activity activity, int textViewResourceId, List<ShopList> objects) {
        super(activity, textViewResourceId, objects);
        this.activity = activity;
        this.currencyItems = objects;
        inflater = activity.getLayoutInflater();
        app = SeeapennyApp.getInstance();
    }

    public void setFactory(ListItemsFactory<ShopList> factory) {
        this.factory = factory;
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();

        final ShopList item = getItem(position);

        final ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.copy_shop_list_item, null);
            holder = new ViewHolder();
            holder.row = convertView.findViewById(R.id.row);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final View row = convertView;

        holder.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                factory.setOnClickListener(item, row);
            }
        });

        holder.name.setText(item.getName());

        return convertView;
    }

    class ViewHolder {
        View row;
        TextView name;
    }
}
