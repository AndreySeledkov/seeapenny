package com.seeapenny.client.list;

import android.view.View;

public interface ListItemsFactory<I> {

    public void createMoreItems(ListHandler<I> handler, int position);

    public void setOnClickListener(I item, View view);

}
