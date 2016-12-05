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
import com.seeapenny.client.list.ListItemsFactory;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Sony
 * Date: 04.05.13
 * Time: 3:52
 * To change this template use File | Settings | File Templates.
 */
public class ListNameAdapter extends ArrayAdapter<String> {

    private Activity activity;
    LayoutInflater inflater;
    protected ListItemsFactory<String> factory;


    public ListNameAdapter(Activity activity, int textViewResourceId, List<String> objects) {
        super(activity, textViewResourceId, objects);
        this.activity = activity;
        inflater = activity.getLayoutInflater();
    }

    public void setFactory(ListItemsFactory<String> factory) {
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

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();

        final String item = getItem(position);

        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_name_item, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.remove = (ImageView) convertView.findViewById(R.id.remove);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final View row = convertView;

        holder.name.setText(item);
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                factory.setOnClickListener(item, row);
            }
        });

        return convertView;
    }

    class ViewHolder {
        TextView name;
        ImageView remove;
    }

}
