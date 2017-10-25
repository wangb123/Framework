package com.yaolan.common.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yaolan.common.R;

import org.wbing.base.utils.Utils;

/**
 * Created by 10213 on 2017/10/24.
 */

public class TopToolBar extends FrameLayout {
    ImageButton back;
    TextView title;

    public TopToolBar(@NonNull Context context) {
        super(context);
        init();
    }

    public TopToolBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TopToolBar(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_top_bar, this);
        back = findViewById(R.id.back);
        title = findViewById(R.id.title);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setPadding(getPaddingLeft(), getPaddingTop() + Utils.getStatusBarHeight(getContext()), getPaddingRight(), getPaddingBottom());
        }
    }

    public void setBackIcon(@DrawableRes int resId) {
        if (back != null) {
            back.setImageResource(resId);
        }
    }

    public void setTitleText(CharSequence title) {
        if (this.title != null) {
            this.title.setText(title);
        }
    }

    public View getBackView() {
        return back;
    }

    public View getTitleView() {
        return title;
    }

}
