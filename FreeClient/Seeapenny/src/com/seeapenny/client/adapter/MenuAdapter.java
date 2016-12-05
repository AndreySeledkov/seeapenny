package com.seeapenny.client.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.seeapenny.client.R;
import com.seeapenny.client.SeeapennyApp;

import java.util.List;

/**
 * Created by Sony on 06.08.13.
 */
public class MenuAdapter extends ArrayAdapter<DrawerMenu> {

    private Activity activity;
    private LayoutInflater inflater;
    private SeeapennyApp app;

    private List<DrawerMenu> drawerMenuList;

    public MenuAdapter(Activity activity, int textViewResourceId, List<DrawerMenu> objects) {
        super(activity, textViewResourceId, objects);
        this.activity = activity;
        this.drawerMenuList = objects;
        inflater = activity.getLayoutInflater();
        app = SeeapennyApp.getInstance();
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

        final DrawerMenu item = getItem(position);

        final ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.drawer_list_item, null);
            holder = new ViewHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

//        final View row = convertView;
//
//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                for (CurrencyItem currencyItem : currencyItems) {
//                    currencyItem.setSelected(false);
//                }
//                currencyItems.get(position).setSelected(true);
//                notifyDataSetChanged();
//
//                factory.setOnClickListener(item, row);
//            }
//        });

        holder.title.setText(item.getTitle());
        holder.icon.setImageResource(item.getResId());

//        if (item.isSelected()) {
//            holder.image.setSelected(true);
//            selectedImage = holder.image;
//        } else {
//            holder.image.setSelected(false);
//        }

        return convertView;
    }

    class ViewHolder {
        TextView title;
        ImageView icon;


    }
}