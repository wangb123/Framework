package org.wbing.base.ui;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.Arrays;
import java.util.Iterator;

/**
 * 基础的ViewHolder，维护点击事件
 * Created by 王冰 on 2017/4/25.
 */

public class WHolder<Binding extends ViewDataBinding> extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    Binding binding;
    OnHolderClickListener<Binding> onHolderClickListener;//holder的点击事件
    WHolder.OnHolderLongClickListener<Binding> onHolderLongClickListener;
    OnHolderChildClickListener<Binding> onHolderChildClickListener;//holder孩子的点击事件

    public WHolder(View itemView) {
        super(itemView);
        binding = DataBindingUtil.bind(itemView);
    }

    public WHolder(View itemView, OnHolderClickListener<Binding> onHolderClickListener) {
        this(itemView);
        setOnHolderClickListener(onHolderClickListener);
    }

    public void setOnHolderClickListener(OnHolderClickListener<Binding> onHolderClickListener) {
        this.onHolderClickListener = onHolderClickListener;
        itemView.setOnClickListener(this);
    }

    void setOnHolderLongClickListener(OnHolderLongClickListener<Binding> onHolderLongClickListener) {
        this.onHolderLongClickListener = onHolderLongClickListener;
        itemView.setOnLongClickListener(this);
    }

    public void setOnHolderChildClickListener(OnHolderChildClickListener<Binding> onHolderChildClickListener, View... views) {
        this.onHolderChildClickListener = onHolderChildClickListener;

        Iterator<View> iterator = Arrays.asList(views).iterator();
        while (iterator.hasNext()) {
            iterator.next().setOnClickListener(this);
        }
    }

    public Binding getBinding() {
        return binding;
    }

    public void fill(int variableId, Object data) {
        binding.setVariable(variableId, data);
    }

    @Override
    public void onClick(View view) {
        if (onHolderClickListener != null) {
            if (view == binding.getRoot()) {
                onHolderClickListener.onClick(this);
                return;
            }
        }
        if(onHolderChildClickListener!=null){
            onHolderChildClickListener.onChildClick(this, view);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        return onHolderLongClickListener != null && onHolderLongClickListener.onLongClick(this);
    }

    public interface OnHolderChildClickListener<Binding extends ViewDataBinding> {
        void onChildClick(WHolder<Binding> holder, View view);
    }

    public interface OnHolderClickListener<Binding extends ViewDataBinding> {
        void onClick(WHolder<Binding> holder);
    }

    public interface OnHolderLongClickListener<Binding extends ViewDataBinding> {
        boolean onLongClick(WHolder<Binding> holder);
    }

}
