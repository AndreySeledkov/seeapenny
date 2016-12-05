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

public class LanguageAdapter extends ArrayAdapter<LanguageItem> {

    private Activity activity;
    private LayoutInflater inflater;
    private SeeapennyApp app;

    protected ListItemsFactory<LanguageItem> factory;

    private ImageView selectedImage;
    private List<LanguageItem> languageItems;

    public LanguageAdapter(Activity activity, int textViewResourceId, List<LanguageItem> objects) {
        super(activity, textViewResourceId, objects);
        this.activity = activity;
        this.languageItems = objects;
        inflater = activity.getLayoutInflater();
        app = SeeapennyApp.getInstance();
    }

    public void setFactory(ListItemsFactory<LanguageItem> factory) {
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

        final LanguageItem item = getItem(position);

        final ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.language_item, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final View row = convertView;

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (LanguageItem languageItem : languageItems) {
                    languageItem.setSelected(false);
                }
                languageItems.get(position).setSelected(true);
                notifyDataSetChanged();

                factory.setOnClickListener(item, row);
            }
        });

        holder.name.setText(item.getName());

        if (item.isSelected()) {
            holder.image.setSelected(true);
            selectedImage = holder.image;
        } else {
            holder.image.setSelected(false);
        }

        return convertView;
    }

    class ViewHolder {
        TextView name;
        ImageView image;


    }
}