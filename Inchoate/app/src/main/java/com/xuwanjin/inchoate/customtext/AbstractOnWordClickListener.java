package com.xuwanjin.inchoate.customtext;

import java.util.Calendar;

public abstract class AbstractOnWordClickListener {
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private long mLastClickTime = 0;

    public void onClick(String word) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - mLastClickTime > MIN_CLICK_DELAY_TIME) {
            mLastClickTime = currentTime;
            onNoDoubleClick(word);
        }
    }

    /**
     * 禁止第二次对某个单词的点击
     * @param word 选中的单词
     */
    protected abstract void onNoDoubleClick(String word);
}
