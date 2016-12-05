package com.seeapenny.client.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.seeapenny.client.R;

/**
 * Created by IntelliJ IDEA.
 * User: Sony
 * Date: 09.06.13
 * Time: 23:49
 * To change this template use File | Settings | File Templates.
 */
public class MeasureAdapter extends ArrayAdapter<String> {

    private Activity activity;
    LayoutInflater inflater;

    public MeasureAdapter(Activity activity, int textViewResourceId, String[] objects) {
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

        final String measure = getItem(position);

        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.measure_item, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.title);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(measure);

        return convertView;
    }

    class ViewHolder {
        TextView title;
    }

}

