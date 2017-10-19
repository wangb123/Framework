package org.wbing.framework;

import android.support.v7.widget.RecyclerView;

/**
 * Created by ermu on 17/3/14.
 */
public interface IPageListFragment {

    RecyclerView getListView();

    void refreshData();

}
