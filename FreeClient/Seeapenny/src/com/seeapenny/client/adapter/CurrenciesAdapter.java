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
 * Date: 20.06.13
 * Time: 2:16
 * To change this template use File | Settings | File Templates.
 */
public class CurrenciesAdapter extends ArrayAdapter<CurrencyItem> {

    private Activity activity;
    private LayoutInflater inflater;
    private SeeapennyApp app;

    protected ListItemsFactory<CurrencyItem> factory;

    private ImageView selectedImage;
    private List<CurrencyItem> currencyItems;

    public CurrenciesAdapter(Activity activity, int textViewResourceId, List<CurrencyItem> objects) {
        super(activity, textViewResourceId, objects);
        this.activity = activity;
        this.currencyItems = objects;
        inflater = activity.getLayoutInflater();
        app = SeeapennyApp.getInstance();
    }

    public void setFactory(ListItemsFactory<CurrencyItem> factory) {
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

        final CurrencyItem item = getItem(position);

        final ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.currency_item, null);
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
                for (CurrencyItem currencyItem : currencyItems) {
                    currencyItem.setSelected(false);
                }
                currencyItems.get(position).setSelected(true);
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
