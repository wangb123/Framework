package org.wbing.base.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.wbing.base.utils.ToastUtils;

import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by 王冰 on 2017/4/25.
 */

public abstract class WFragment<Binding extends ViewDataBinding> extends Fragment implements Action1<Intent> {

    private Binding binding;
    public Subscription subscription, actionSubscription;
    protected boolean isInit = false;
    protected boolean isLoad = false;

    public WFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (subIntent())
            actionSubscription = WActivity.rxRegister(this);
        if (getArguments() != null)
            getParams(getArguments());
    }

    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (layoutId() == 0) return super.onCreateView(inflater, container, savedInstanceState);
        binding = DataBindingUtil.inflate(inflater, layoutId(), container, false);
        onCreateView(savedInstanceState);
        isInit = true;
        isCanLoadData();
        return binding.getRoot();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isCanLoadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isInit = false;
        isLoad = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        resetSubscription();
        if (actionSubscription!=null&&!actionSubscription.isUnsubscribed())
            actionSubscription.unsubscribe();
    }

    protected boolean subIntent() {
        return false;
    }

    @Override
    public void call(Intent wAction) {

    }

    /**
     * 当视图初始化并且对用户可见的时候去真正的加载数据
     */
    protected abstract void lazyLoad();

    /**
     * 当视图已经对用户不可见并且加载过数据，如果需要在切换到其他页面时停止加载数据，可以覆写此方法
     */
    protected void stopLoad() {
    }

    /**
     * 是否可以加载数据
     * 可以加载数据的条件：
     * 1.视图已经初始化
     * 2.视图对用户可见
     */
    private void isCanLoadData() {
        if (!isInit) {
            return;
        }

        if (isLoad) {
            return;
        }

        if (getUserVisibleHint()) {
            lazyLoad();
            isLoad = true;
        } else {
            if (isLoad) {
                stopLoad();
            }
        }
    }

    protected void getParams(Bundle args) {

    }

    protected void resetSubscription() {
        if (subscription != null && subscription.isUnsubscribed()) subscription.unsubscribe();
    }

    public Binding getBinding() {
        return binding;
    }

    protected final void showError(String error) {
        if (TextUtils.isEmpty(error)) error = "发生未知错误";
        ToastUtils.showMessage(error);
//        Snackbar.make(binding.getRoot(),
//                error,
//                BaseTransientBottomBar.LENGTH_SHORT)
//                .show();
    }

    protected abstract void onCreateView(Bundle savedInstanceState);

    protected abstract int layoutId();

}
