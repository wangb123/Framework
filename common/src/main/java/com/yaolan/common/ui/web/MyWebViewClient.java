package com.yaolan.common.ui.web;

import android.graphics.Bitmap;
import android.webkit.CookieManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URLDecoder;

/**
 * MyWebViewClient
 * <p>
 * Created by zhenguo on 9/20/16.
 */
public class MyWebViewClient extends WebViewClient {

    private String mainImagsrc,linkUrl,shareTitle;

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        try {
            CookieManager cookieManager = CookieManager.getInstance();
            String cookieStr =  URLDecoder.decode(cookieManager.getCookie(url), "UTF-8");
            if (cookieStr != null && cookieStr.indexOf("img") > 0) {
                String cookieStr1 = cookieStr.substring(cookieStr.indexOf("img"));
                mainImagsrc = cookieStr1.substring(cookieStr1.indexOf("=") + 1, cookieStr1.length());
            }
            linkUrl=url;
            shareTitle=view.getTitle();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        // 自定义404页面可以在这里设置
        super.onReceivedError(view, request, error);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        // 如果需要对某些网页进行处理可以在这里处理
        return super.shouldInterceptRequest(view, request);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        FirstUrlHandler firstUrlHandler = new FirstUrlHandler(view.getContext());
        OriginUrlHandler originUrlHandler = new OriginUrlHandler(view.getContext(),mainImagsrc,linkUrl,shareTitle);
        firstUrlHandler.setNextUrlHandler(originUrlHandler);
        boolean isHandle = firstUrlHandler.handlerUrl(url);
        if (isHandle) {
            return true;
        } else {
            view.loadUrl(url);
            return false;
        }
    }
}
