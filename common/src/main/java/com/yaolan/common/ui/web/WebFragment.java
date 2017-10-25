package com.yaolan.common.ui.web;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.yaolan.common.R;

import org.wbing.base.WApp;

import java.util.Map;

/**
 * Created by 王冰 on 2016/12/23.
 */

public class WebFragment extends Fragment {
    private static final String ARG_PARAM_URL = "url";

    private String mUrl;
    private WebView mWebView;

    private OnWebViewChangeListener mListener;

    public WebFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param url url 链接
     * @return A new instance of fragment WebFragment.
     */
    public static WebFragment newInstance(String url) {
        CookieManager.getInstance().removeAllCookie();
        WebFragment fragment = new WebFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CookieSyncManager.createInstance(getContext());
        if (getArguments() != null) {
            mUrl = getArguments().getString(ARG_PARAM_URL);
        }
    }

    private void initWebView() {
        mWebView.getSettings().setDefaultTextEncodingName("utf-8");
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setHorizontalScrollBarEnabled(false);// 水平不显示
        mWebView.setVerticalScrollBarEnabled(false); // 垂直不显示
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setWebChromeClient(new MyWebChromeClient(mListener));
        mWebView.getSettings().setUseWideViewPort(true);
        // 安全考虑，防止密码泄漏，尤其是root过的手机
        mWebView.getSettings().setSavePassword(false);
        String ua = mWebView.getSettings().getUserAgentString();
        String appUA = ua + "; MYAPP";
        mWebView.getSettings().setUserAgentString(appUA);
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        mWebView.getSettings().setDatabaseEnabled(true);
        String dir = getActivity().getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        //启用地理定位
        mWebView.getSettings().setGeolocationEnabled(true);
        //设置定位的数据库路径
        mWebView.getSettings().setGeolocationDatabasePath(dir);
        //最重要的方法，一定要设置，这就是出不来的主要原因
        mWebView.getSettings().setDomStorageEnabled(true);
        Map<String, String> header = WApp.getInstance().getHttpHeaders();
//        header.put("is-app", "1");
//        header.put("version", PhoneInfo.getInstance().getVersion());
        mWebView.loadUrl(mUrl, header);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_web, container, false);
        mWebView = (WebView) view.findViewById(R.id.wv_content);
        initWebView();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnWebViewChangeListener) {
            mListener = (OnWebViewChangeListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * 返回事件处理
     *
     * @return TRUE webview返回  false 不可以返回
     */
    public boolean onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        } else {
            return false;
        }
    }

    public interface OnWebViewChangeListener {
        void onWebViewTitleChanged(String title);
        void onWebViewProgressChanged(int newProgress);
    }
}