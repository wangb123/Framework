package org.wbing.base.view.scrollablelayout;

import android.view.MotionEvent;

/**
 * Created by mier on 17/2/13.
 */
public class ScrollTopListener {
    // 记录首次按下位置
    private float mFirstPosition = 0;
    // 是否正在放大
    private Boolean mScaling = false;


    public boolean onTouch(ScrollableLayout v, MotionEvent event) {
        switch (event.getAction()) {
            //手指抬起时触发
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // 手指离开后恢复图片
                mScaling = false;
                onTopReleas();
                break;
            //手指移动时触发
            case MotionEvent.ACTION_MOVE:
                if (!mScaling) {
                    if (v.getCurrentY() == 0) {
                        mFirstPosition = event.getY();// 滚动到顶部时记录位置，否则正常返回
                    } else {
                        break;
                    }
                }
                int distance = (int) ((event.getY() - mFirstPosition) * 0.8f); // 滚动距离乘以一个系数
                if (distance < 0) { // 如果当前位置比记录位置要小，正常返回
                    break;
                }

                // 处理放大的关键代码
                mScaling = true;
                onTopMove(distance);
                return true; // 返回true表示已经消费该事件
        }
        return false;
    }


    /**
     * 处理放大的关键代码
     * @param distance
     */
    public void onTopMove(int distance){

    }

    /**
     * 手指离开后恢复
     */
    public void onTopReleas(){

    }
}
