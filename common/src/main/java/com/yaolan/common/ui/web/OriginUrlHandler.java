package com.yaolan.common.ui.web;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by zhenguo on 9/21/16.
 */

public class OriginUrlHandler extends UrlHandler {

    private String mainImagsrc, shareTitle, linkUrl;

    public OriginUrlHandler(Context context, String imagsrc, String linkUrl, String title) {
        super(context);
        this.mainImagsrc = imagsrc;
        this.linkUrl = linkUrl;
        this.shareTitle = title;
    }


    @Override
    public boolean handlerUrl(@NonNull String url) {
        return false;
    }
}
