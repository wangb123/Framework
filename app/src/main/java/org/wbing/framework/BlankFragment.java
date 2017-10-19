package org.wbing.framework;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


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
    RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        recyclerView= view.findViewById(R.id.content);


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new RecyclerView.Adapter<MainActivity.Holder>() {

            @Override
            public MainActivity.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new MainActivity.Holder(new TextView(parent.getContext()));
            }

            @Override
            public void onBindViewHolder(MainActivity.Holder holder, int position) {
                holder.fill(position);
                Log.e("tag", position + "");
            }

            @Override
            public int getItemCount() {
                return 100;
            }
        });

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
