package org.wbing.base.view.empty;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.wbing.base.R;


/**
 * Created by ermu on 16/12/27.
 */
public class EmptyView extends LinearLayout implements IEmptyLayout{

    /**
     * 空View
     */
    protected View mEmptyView;
    protected TextView mEmptyTextView;
    protected TextView mEmptySubHeadView;
    protected ImageView mEmptyImageView;
    protected TextView mEmptyBtn;

    private int mEmptyIcon = R.drawable.icon_empty_net_error;
    private int mErrorIcon = R.drawable.icon_empty_net_error;
    /**
     * 空view的文案
     */
    private String mEmptyText = getResources().getString(R.string.empty_otherall);

    /**
     * 错误view的文案
     */
    private String mErrorText = getResources().getString(R.string.error_default);


    /**
     * 空view的subhead文案
     */
    private String mEmptySubText = getResources().getString(R.string.empty_sub_head_defaut);

    /**
     * 错误view的subhead文案
     */
    private String mErrorSubText = getResources().getString(R.string.empty_sub_head_defaut);

    /**
     * 空view的点击事件
     */
    private OnClickListener mEmptyOnClickListener;
    /**
     * 错误view的点击事件
     */
    private OnClickListener mErrorOnClickListener;


    public EmptyView(Context context) {
        super(context);
        init();
    }

    public EmptyView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        mEmptyView = inflate(getContext(), R.layout.empty_view, this);
        mEmptyTextView = (TextView) mEmptyView.findViewById(R.id.text);
        mEmptySubHeadView = (TextView) mEmptyView.findViewById(R.id.sub_head);
        mEmptyImageView = (ImageView) mEmptyView.findViewById(R.id.icon);
        mEmptyBtn = (TextView) mEmptyView.findViewById(R.id.btn);
    }

    /**
     * 设置空view的点击事件
     *
     * @param l
     * @return
     */
    public void setEmptyOnclickListener(OnClickListener l) {
        this.mEmptyOnClickListener = l;
    }

    /**
     * 设置错误view的点击事件
     *
     * @param l
     * @return
     */
    public void setErrorOnclickListener(OnClickListener l) {
        this.mErrorOnClickListener = l;
    }

    @Override
    public void showErrorView(int errorCode, String errorMessage) {
        setErrorText(errorMessage);
        showErrorView();
    }

    /**
     * 显示错误view
     */
    public void showErrorView() {
        setVisibility(View.VISIBLE);

        if (mErrorText != null) {
            mEmptyTextView.setText(mErrorText);
        }
        mEmptyTextView.setClickable(false);
        setOnClickListener(mErrorOnClickListener);

        if (mErrorIcon > 0) {
            mEmptyImageView.setImageResource(mErrorIcon);
        }

        if (mErrorSubText != null) {
            mEmptySubHeadView.setText(mErrorSubText);
        }
    }

    /**
     * 隐藏view
     */
    public void hideView() {
        if (getVisibility() != View.VISIBLE) {
            return;
        }
        setVisibility(View.GONE);
    }

    /**
     * 显示空View
     */

    public void showEmptyView() {
        setVisibility(View.VISIBLE);

        if (mEmptyText != null) {
            mEmptyTextView.setText(mEmptyText);
        }
        setClickable(false);
        mEmptyTextView.setOnClickListener(mEmptyOnClickListener);

        if (mEmptyIcon > 0) {
            mEmptyImageView.setImageResource(mEmptyIcon);
        }

        if (mEmptySubText != null) {
            mEmptySubHeadView.setText(mEmptySubText);
        }

    }

    /**
     * 设置空View的icon
     *
     * @return
     */
    public void setEmptyIcon(int iconID) {
        try {
            mEmptyIcon = iconID;
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置错误View的icon
     *
     * @return
     */
    public void setErrorIcon(int iconID) {
        try {
            mErrorIcon = iconID;
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * 设置空view的文案
     *
     * @param text
     * @return
     */
    public void setEmptyText(String text) {
        if (!TextUtils.isEmpty(text)) {
            mEmptyText = text;
        }
    }


    /**
     * 设置空view的文案
     *
     * @param textID
     * @return
     */
    public void setEmptyText(int textID) {

        try {
            mEmptyText = getResources().getString(textID);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置错误view的文案
     *
     * @param text
     * @return
     */
    public void setErrorText(String text) {
        if (!TextUtils.isEmpty(text)) {
            mErrorText = text;
        }
    }


    /**
     * 设置错误view的文案
     *
     * @param textID
     * @return
     */
    public void setErrorText(int textID) {
        try {
            mErrorText = getResources().getString(textID);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * 设置空view的subhead文案
     *
     * @param text
     * @return
     */
    public void setEmptySubText(String text) {
        if (!TextUtils.isEmpty(text)) {
            mEmptySubText = text;
        }
    }


    /**
     * 设置空view的subhead文案
     *
     * @param textID
     * @return
     */
    public void setEmptySubText(int textID) {

        try {
            mEmptySubText = getResources().getString(textID);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * 设置错误view的subhead文案
     *
     * @param text
     * @return
     */
    public void setErrorSubText(String text) {
        if (!TextUtils.isEmpty(text)) {
            mErrorSubText = text;
        }
    }


    /**
     * 设置错误view的subhead文案
     *
     * @param textID
     * @return
     */
    public void setErrorSubText(int textID) {
        try {
            mErrorSubText = getResources().getString(textID);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * 显示默认的空View
     *
     * @return
     */
    public void showEmptyDefault() {
        setEmptyText(getResources().getString(R.string.empty_otherall));
        setEmptyIcon(R.drawable.icon_empty_net_error);
        setEmptySubText(getResources().getString(R.string.empty_sub_head_defaut));
        showEmptyView();
    }

    /**
     * 显示默认的错误View
     *
     * @return
     */
    public void showErrorDefault() {
        setErrorText(getResources().getString(R.string.error_default));
        setErrorIcon(R.drawable.icon_empty_net_error);
        setErrorSubText(getResources().getString(R.string.error_sub_head_defaut));
        showEmptyView();
    }

    public View getEmptyView() {
        return mEmptyView;
    }

    public ImageView getEmptyIcon() {
        return mEmptyImageView;
    }

    public TextView getEmptyText() {
        return mEmptyTextView;
    }

    public TextView getEmptyBtn() {
        return mEmptyBtn;
    }

    public TextView getSubHeadView() {
        return mEmptySubHeadView;
    }

}