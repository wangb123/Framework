package org.wbing.base.ui;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 王冰 on 2017/4/25.
 */

public abstract class WAdapter<Data, Binding extends ViewDataBinding> extends RecyclerView.Adapter<WHolder<Binding>> {

    private List<Data> list;
    private WHolder.OnHolderClickListener<Binding> onHolderClickListener;
    private WHolder.OnHolderLongClickListener<Binding> onHolderLongClickListener;

    @Override
    public WHolder<Binding> onCreateViewHolder(ViewGroup parent, int viewType) {
        WHolder<Binding> holder = new WHolder<>(LayoutInflater.from(parent.getContext()).inflate(holderLayout(viewType), parent, false));
        if (onHolderClickListener != null) holder.setOnHolderClickListener(onHolderClickListener);
        if (onHolderLongClickListener != null) holder.setOnHolderLongClickListener(onHolderLongClickListener);
        initHolder(holder, viewType);
        return holder;
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void setOnHolderClickListener(WHolder.OnHolderClickListener<Binding> onHolderClickListener) {
        this.onHolderClickListener = onHolderClickListener;
    }

    public void setOnHolderLongClickListener(WHolder.OnHolderLongClickListener<Binding> onHolderLongClickListener) {
        this.onHolderLongClickListener = onHolderLongClickListener;
    }

    public Data getItem(int position) {
        if (getItemCount() > position)
            return list.get(position);
        return null;
    }

    public void setList(List<Data> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public List<Data> getList() {
        return list;
    }

    public void addItem(Data data) {
        if (this.list == null) this.list = new ArrayList<>();
        this.list.add(data);
        notifyItemInserted(this.list.size() - 1);
    }

    public void addItem(Data data, int position) {
        if (this.list == null) this.list = new ArrayList<>();
        this.list.add(position, data);
        notifyItemInserted(position);
    }

    public void addItems(List<Data> datas) {
        if (datas == null || datas.isEmpty()) return;
        if (this.list == null || this.list.isEmpty()) {
            setList(datas);
            return;
        }
        this.list.addAll(datas);
        notifyItemRangeChanged(this.list.size() - datas.size(), datas.size());
    }

    public void addItems(List<Data> datas, int position) {
        if (datas == null || datas.isEmpty()) return;
        if (this.list == null || this.list.isEmpty()) {
            setList(datas);
            return;
        }
        if (this.list.size() - 1 < position) {
            addItems(datas);
            return;
        }
        this.list.addAll(position, datas);
        notifyItemRangeChanged(position, datas.size());
    }

    public void remove(int position) {
        if (getItemCount() == 0 || position < 0 || position > getItemCount() - 1) return;
        this.list.remove(position);
        notifyItemRemoved(position);
    }

    public void remove(Data data) {
        if (getItemCount() == 0) return;
        remove(this.list.indexOf(data));
    }

    public void removeAll() {
        if (getItemCount() == 0) return;
        this.list.clear();
        notifyDataSetChanged();
    }

    protected void initHolder(WHolder<Binding> holder, int viewType) {

    }

    public abstract int holderLayout(int viewType);


    public static class SimpleAdapter<Data, Binding extends ViewDataBinding> extends WAdapter<Data, Binding> {
        int holderLayout;
        int variableId;

        public SimpleAdapter(int variableId, int holderLayout) {
            this.variableId = variableId;
            this.holderLayout = holderLayout;
        }

        @Override
        public int holderLayout(int viewType) {
            return holderLayout;
        }

        @Override
        public void onBindViewHolder(WHolder<Binding> holder, int position) {
            holder.fill(variableId, getItem(position));
        }
    }

}
