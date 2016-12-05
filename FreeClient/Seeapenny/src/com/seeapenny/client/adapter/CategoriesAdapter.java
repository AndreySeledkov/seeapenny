package com.seeapenny.client.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.seeapenny.client.R;
import com.seeapenny.client.bean.GoodCategory;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Sony
 * Date: 04.05.13
 * Time: 3:52
 * To change this template use File | Settings | File Templates.
 */
public class CategoriesAdapter extends ArrayAdapter<GoodCategory> {

    private Activity activity;
    LayoutInflater inflater;

    public CategoriesAdapter(Activity activity, int textViewResourceId, List<GoodCategory> objects) {
        super(activity, textViewResourceId, objects);
        this.activity = activity;
        inflater = activity.getLayoutInflater();
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

        final GoodCategory goodCategory = getItem(position);

        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.categories_item, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.title);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(goodCategory.getCategory());

        return convertView;
    }

    class ViewHolder {
        TextView title;
    }

}
