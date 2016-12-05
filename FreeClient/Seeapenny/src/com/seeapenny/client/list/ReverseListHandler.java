package com.seeapenny.client.list;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.seeapenny.client.R;

import java.util.ArrayList;
import java.util.List;

public abstract class ReverseListHandler<I> extends ListHandler<I> {

    public ReverseListHandler(View view, Activity activity, ListItemsFactory<I> factory) {
        super(view, activity, factory);
    }

    public void create(Activity activity, View footerView, int emptyListStringId) {
        items = new ArrayList<I>();

        inflater = activity.getLayoutInflater();

//        waitItem = inflater.inflate(R.layout.loading, null);
//        waitItem.setOnClickListener(null);

        listView = (ListView) activity.findViewById(R.id.list);
        listView.setStackFromBottom(true);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);

        if (footerView != null) {
            listView.addFooterView(footerView);
        }

        listView.setAdapter(this);

        createMoreItems(0);
    }

    public void takeMoreItems(int totalCount, List<I> items) {
        if (items.size() == 0) {
            totalCount = this.items.size();
        }

        if (totalCount > 0) {
            this.totalCount = totalCount;
            this.items.addAll(items);
        }

        notifyDataSetChanged();

        listView.setSelectionFromTop(items.size(), 0);

        requestMode = false;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        listView.setOnItemClickListener(listener);
    }

    public ListView getListView() {
        return listView;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public I getItem(int position) {
        final int count = items.size();
        if (totalCount > count && position == 0 && !requestMode) {
            notifyDataSetChanged();

            createMoreItems(count);

            return null;
        }
        return items.get(items.size() - position - 1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
