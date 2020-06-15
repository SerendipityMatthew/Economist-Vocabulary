package com.xuwanjin.inchoate.customtext;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelectableTextView extends AppCompatTextView {
    private CharSequence mText;
    private SpannableString mSpannableString;
    private BackgroundColorSpan mBackgroundColorSpan;
    private ForegroundColorSpan mForegroundColorSpan;
    private int mSelectTextFrontColor = Color.WHITE;
    private int mSelectTextBackColor = Color.BLACK;
    private BufferType mBufferType;
    private OnWordClickListener mOnWordClickListener;
    public SelectableTextView(Context context) {
        super(context);
    }

    public SelectableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SelectableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setSelectTextBackColorRes(int selectTextBackColorRes){
        mSelectTextBackColor = getContext().getResources().getColor(selectTextBackColorRes, getContext().getTheme());
    }

    public void setSelectTextFrontColorRes(int selectTextFrontColorRes){
        mSelectTextFrontColor = getContext().getResources().getColor(selectTextFrontColorRes, getContext().getTheme());
    }
    public void setOnWordClickListener(OnWordClickListener onWordClickListener){
        mOnWordClickListener = onWordClickListener;
    }
    @Override
    public void setCustomSelectionActionModeCallback(ActionMode.Callback actionModeCallback) {
        super.setCustomSelectionActionModeCallback(actionModeCallback);
        dismissSelected();
    }
    public void dismissSelected(){
        mSpannableString.removeSpan(mBackgroundColorSpan);
        mSpannableString.removeSpan(mForegroundColorSpan);
        setText(mSpannableString, mBufferType);
    }
    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        setTextIsSelectable(false);
        mBufferType = type;
        mText = text;
        mSpannableString = new SpannableString(mText);
        setMovementMethod(LinkMovementMethod.getInstance());
        List<WordInfo> wordInfoList = getWordInfo();
        for (int i = 0; i < wordInfoList.size(); i++){
            WordInfo info = wordInfoList.get(i);
            mSpannableString.setSpan(getClickableSpan(), info.getStart(), info.getEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        super.setText(mSpannableString, type);
    }

    private List<WordInfo> getWordInfo(){
        List<String> words = splitVocabulary();
        List<WordInfo> result = new ArrayList<>();
        int startIndex = 0;
        for (int i = 0; i < words.size(); i++){
            String word = words.get(i);
            int start = mText.toString().indexOf(word, startIndex);
            int end = start + word.length();
            startIndex = end;
            WordInfo wordInfo = new WordInfo();
            wordInfo.setStart(start);
            wordInfo.setEnd(end);
            result.add(wordInfo);
        }
        return result;
    }
    private List<String> splitVocabulary(){
        if (TextUtils.isEmpty(mText.toString())){
            return new ArrayList<>();
        }
        List<String> results = new ArrayList<>();
        Pattern pattern = Pattern.compile("[a-zA-Z-']+");
        Matcher matcher = pattern.matcher(mText);
        while (matcher.find()){
            results.add(matcher.group(0));
        }
        return results;
    }
    private ClickableSpan getClickableSpan(){
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                String word = null;
                try {
                    word = getText().subSequence(getSelectionStart(), getSelectionEnd()).toString();
                }catch (Exception e){
                    e.printStackTrace();
                }
                setSelectedSpan();
                if (mOnWordClickListener != null){
                    mOnWordClickListener.onClick(word);
                }
            }

            private void setSelectedSpan() {
                if (mBackgroundColorSpan == null
                        || mForegroundColorSpan == null){
                    mForegroundColorSpan = new ForegroundColorSpan(mSelectTextFrontColor);
                    mBackgroundColorSpan = new BackgroundColorSpan(mSelectTextBackColor);
                }else {
                    mSpannableString.removeSpan(mBackgroundColorSpan);
                    mSpannableString.removeSpan(mForegroundColorSpan);
                }
                mSpannableString.setSpan(mBackgroundColorSpan, getSelectionStart(), getSelectionEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                mSpannableString.setSpan(mForegroundColorSpan, getSelectionStart(), getSelectionEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                setText(mSpannableString, mBufferType);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLACK);
                ds.setUnderlineText(false);
            }
        };
        return clickableSpan;
    }

}
