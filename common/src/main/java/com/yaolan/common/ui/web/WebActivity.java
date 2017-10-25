package com.yaolan.common.ui.web;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.yaolan.common.R;
import com.yaolan.common.databinding.ActivityWebBinding;

import org.wbing.base.ui.WActivity;
import org.wbing.base.utils.LogUtils;


/**
 * Created by 王冰 on 2016/12/30.
 */

public class WebActivity extends WActivity<ActivityWebBinding> implements WebFragment.OnWebViewChangeListener {
    public static void start(Context context, String url) {
        Intent starter = new Intent(context, WebActivity.class);
        starter.putExtra("url", url);
        LogUtils.e(url);
        context.startActivity(starter);
    }

    String url;
    WebFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (TextUtils.isEmpty(url)) return;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, fragment = WebFragment.newInstance(url))
                .commit();

    }

    @Override
    public void getParams(Intent intent) {
        if (intent.hasExtra("url"))
            url = intent.getStringExtra("url");
        else finish();
    }

    @Override
    public void onBackPressed() {
        if (fragment == null || !fragment.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        finish();
    }

    @Override
    public int layoutId() {
        return R.layout.activity_web;
    }

    @Override
    public void onWebViewTitleChanged(String title) {
        getBinding().topBar.setTitleText(title);
    }

    @Override
    public void onWebViewProgressChanged(int newProgress) {
        if (newProgress >= 100) {
            getBinding().pbWeb.setVisibility(View.GONE);
        } else {
            if (getBinding().pbWeb.getVisibility() == View.GONE) {
                getBinding().pbWeb.setVisibility(View.VISIBLE);
            }
            getBinding().pbWeb.setProgress(newProgress);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
