package com.xuwanjin.inchoate.customtext;

import java.util.Calendar;

public abstract class OnWordClickListener {
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private long mLastClickTime = 0;
    public void onClick(String word){
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - mLastClickTime > MIN_CLICK_DELAY_TIME){
            mLastClickTime = currentTime;
            onNoDoubleClick(word);
        }
    }
    protected abstract void onNoDoubleClick(String word);
}
