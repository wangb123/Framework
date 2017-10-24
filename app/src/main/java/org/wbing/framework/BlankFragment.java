package org.wbing.framework;

import android.content.Context;
import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.wbing.base.ui.WAdapter;
import org.wbing.base.view.recyclerview.OnLoadMoreListener;
import org.wbing.base.view.recyclerview.PageRecyclerView;

import java.util.ArrayList;


public class BlankFragment extends Fragment implements IPageListFragment {

    private OnFragmentInteractionListener mListener;

    public BlankFragment() {
    }


    public static BlankFragment newInstance(int pos) {
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putInt("pos", pos);
        fragment.setArguments(args);
        return fragment;
    }

    int pos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pos = getArguments().getInt("pos");
        }
    }

    PageRecyclerView recyclerView;

    WAdapter<String, ViewDataBinding> adapter = new WAdapter.SimpleAdapter<>(0, R.layout.item);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        recyclerView = view.findViewById(R.id.content);
        recyclerView.loadMoreFinish(true, true);
//        recyclerView.setEnableLoadMore(true);
        recyclerView.showLoadMoreView(true);
        recyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadNMore(View view) {
//                recyclerView.loadMoreFinish(false, false);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.addItems(new ArrayList<String>() {{
            add("1");
            add("2");
            add("3");
            add("45");
            add("123");
            add("123");
            add("123");
        }});

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public RecyclerView getListView() {
        return recyclerView;
    }

    @Override
    public void refreshData() {
        if (mListener != null) {
            mListener.onFragmentRefreshComplete(null);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentRefreshComplete(Intent intent);
    }
}
