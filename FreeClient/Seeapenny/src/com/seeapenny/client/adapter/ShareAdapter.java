package com.seeapenny.client.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.seeapenny.client.R;
import com.seeapenny.client.SeeapennyApp;
import com.seeapenny.client.list.ListItemsFactory;

import java.util.List;

/**
 * Created by Sony on 15.07.13.
 */
public class ShareAdapter extends ArrayAdapter<ShareUser> {

    private Activity activity;
    private LayoutInflater inflater;
    private SeeapennyApp app;

    protected ListItemsFactory<ShareUser> factory;


    public ShareAdapter(Activity activity, int textViewResourceId, List<ShareUser> objects) {
        super(activity, textViewResourceId, objects);
        this.activity = activity;
        inflater = activity.getLayoutInflater();
        app = SeeapennyApp.getInstance();
    }

    public void setFactory(ListItemsFactory<ShareUser> factory) {
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

        final ShareUser item = getItem(position);

        final ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.share_user_item, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.user_name);
            holder.image = (CheckBox) convertView.findViewById(R.id.checkbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final View row = convertView;

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                factory.setOnClickListener(item, row);
            }
        });

        holder.name.setText(item.getUserName());

        if (item.isSelected()) {
            holder.image.setSelected(true);
        } else {
            holder.image.setSelected(false);
        }

        return convertView;
    }

    class ViewHolder {
        TextView name;
        CheckBox image;
    }
}
