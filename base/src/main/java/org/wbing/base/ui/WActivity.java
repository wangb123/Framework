package org.wbing.base.ui;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.util.LongSparseArray;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import org.wbing.base.utils.RxBus;
import org.wbing.base.utils.ToastUtils;

import java.lang.ref.WeakReference;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by 王冰 on 2017/8/16.
 */

public abstract class WActivity<Binding extends ViewDataBinding> extends AppCompatActivity implements View.OnClickListener, Action1<Intent>, Handler.Callback {
    /* 存储activity的集合*/
    private static volatile LongSparseArray<WeakReference<WActivity>> activities = new LongSparseArray<>();
    /**
     * 退出参数
     */
    long mLastClick;
    static final long BACK_DOUBLE_CLICK_INTERVAL = 2500;

    private static Observable<Intent> observable;

    private long id;
    private Binding binding;
    public Subscription subscription, actionSubscription;
    private WeakHandler handler = new WeakHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activities.put(getId(), new WeakReference<WActivity>(this));
        if (subIntent())
            actionSubscription = rxRegister(this);
        getParams(getIntent());
        if (layoutId() != 0) {
            binding = DataBindingUtil.setContentView(this, layoutId());
        }
    }

    protected boolean subIntent() {
        return false;
    }

    public static Subscription rxRegister(Action1<Intent> action1) {
        if (observable == null) {
            observable = RxBus.getDefault().toObservable(Intent.class);
        }
        return observable.subscribe(action1, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getParams(intent);
        setIntent(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        resetSubscription();
        activities.remove(id);
        handler.removeCallbacksAndMessages(null);
        if (actionSubscription!=null&&!actionSubscription.isUnsubscribed())
            actionSubscription.unsubscribe();
    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (activities.size() == 1 && currentTime - mLastClick >= BACK_DOUBLE_CLICK_INTERVAL) {
            ToastUtils.showMessage("再按一次退出程序");
            mLastClick = currentTime;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void call(Intent intent) {

    }

    public void back(View view) {
        onBackPressed();
    }

    public void next(View view) {
    }

    public void onClick(View view) {
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isShouldHideInput() && ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    protected boolean isShouldHideInput() {
        return true;
    }

    protected void getParams(Intent intent) {

    }

    @Override
    public boolean handleMessage(Message msg) {

        return false;
    }

    protected Handler getHandler() {
        return handler;
    }

    protected Binding getBinding() {
        return binding;
    }

    protected final long getId() {
        if (id == 0) {
            id = createId();
        }
        return id;
    }

    private synchronized long createId() {
        long id = System.currentTimeMillis();
        while (activities.get(id) != null) {
            id++;
        }
        return id;
    }

    protected void resetSubscription() {
        if (subscription != null && subscription.isUnsubscribed()) subscription.unsubscribe();
        subscription = null;
    }

    protected final void showError(String error) {
        if (TextUtils.isEmpty(error)) error = "发生未知错误";
        ToastUtils.showMessage(error);
    }

    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    protected void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    protected abstract int layoutId();

    public static WActivity findTopActivity() {
        if (activities.size() == 0) {
            return null;
        }
        WeakReference<WActivity> weakReference = activities.valueAt(activities.size() - 1);
        if (weakReference != null && weakReference.get() != null) {
            return weakReference.get();
        }
        return null;
    }

    protected static int getActivitySize() {
        return activities.size();
    }

}
