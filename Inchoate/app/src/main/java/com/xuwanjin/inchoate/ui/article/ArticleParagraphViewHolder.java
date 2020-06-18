package com.xuwanjin.inchoate.ui.article;

import android.content.Context;
import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;
import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.Utils;
import com.xuwanjin.inchoate.database.dao.InchoateDBHelper;
import com.xuwanjin.inchoate.model.Article;
import com.xuwanjin.inchoate.model.Paragraph;
import com.xuwanjin.inchoate.model.Vocabulary;
import com.xuwanjin.inchoate.ui.BaseViewHolder;

import java.text.BreakIterator;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static com.xuwanjin.inchoate.InchoateApp.NAVIGATION_CONTROLLER;

public class ArticleParagraphViewHolder extends BaseViewHolder {
    public static final String TAG = "ArticleParagraphViewHolder";
    public TextView paragraphTextView;
    public Paragraph mParagraph;
    private Article mArticle;

    public ArticleParagraphViewHolder(@NonNull final View itemView, Context context, Article article, boolean isHeaderOrFooter) {
        super(itemView, isHeaderOrFooter);
        mContext = context;
        mArticle = article;
    }

    private void splitAndCollectVocabulary(boolean isCollectParagraph) {
        int wordStart = 0;
        int wordEnd = paragraphTextView.getText().length();
        if (paragraphTextView.isFocused()) {
            final int selStart = paragraphTextView.getSelectionStart();
            final int selEnd = paragraphTextView.getSelectionEnd();

            wordStart = Math.max(0, Math.min(selStart, selEnd));
            wordEnd = Math.max(0, Math.max(selStart, selEnd));
        }
        int wordStartTemp = wordStart;
        int wordEndTemp = wordEnd;
        // Perform your definition lookup with the selected text
        final CharSequence selectedText = paragraphTextView.getText().subSequence(wordStart, wordEnd);
        if (selectedText.toString().contains(" ")) {
            Snackbar.make(paragraphTextView, "please select a word", Snackbar.LENGTH_SHORT).show();
        } else {
            Runnable saveVocabulary = new Runnable() {
                @Override
                public void run() {
                    collectTheVocabulary(mParagraph, selectedText.toString(), wordStartTemp, wordEndTemp, isCollectParagraph);
                }
            };
            Executors.newSingleThreadExecutor().submit(saveVocabulary);
            Snackbar.make(paragraphTextView, selectedText, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(Color.RED)
                    .show();
            Log.d(TAG, "splitAndCollectVocabulary: selectedText = " + selectedText);
        }
    }

    public void setCurrentParagraph(Paragraph paragraph) {
        this.mParagraph = paragraph;
    }

    private ClickableSpan getClickableSpan(final String word) {
        ClickableSpan clickableSpan = new ClickableSpan() {
            final String mWord = word;
            @Override
            public void onClick(View widget) {
                Toast.makeText(widget.getContext(), mWord, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
            }
        };
        return clickableSpan;
    }

    private void collectTheVocabulary(
            Paragraph paragraph, String vocabularyString, int wordStart, int wordEnd, boolean isCollectParagraph) {
        if (paragraph == null
                || paragraph.paragraph.length() == 0
                || "".equals(vocabularyString.trim())
                || "null".equalsIgnoreCase(vocabularyString)) {
            return;
        }
        InchoateDBHelper dbHelper = InchoateDBHelper.getInstance(mContext);
        Vocabulary vocabulary = new Vocabulary();
        String paragraphText = paragraph.paragraph.toString();
        if (isCollectParagraph){
            vocabulary.belongedParagraph = paragraphText;
        }
        String belongedSentence = getSentence(paragraphText, vocabularyString, wordStart, wordEnd);
        vocabulary.belongedSentence = belongedSentence;
        vocabulary.belongedArticleTitle = paragraph.articleName;
        vocabulary.belongedIssueDate = paragraph.issueDate;
        vocabulary.vocabularyContent = vocabularyString;
        vocabulary.belongedSectionName = mArticle.section;
        vocabulary.belongedArticleUrl = mArticle.articleUrl;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        String dateString = simpleDateFormat.format(date);
        vocabulary.collectedDate = dateString.substring(0, 11);
        vocabulary.collectedTime = dateString.substring(11, dateString.length() - 1);

        Log.d(TAG, "collectTheVocabulary: vocabularyString = " + vocabularyString);
        dbHelper.insertVocabulary(vocabulary);
    }

    public String getSentence(String text, String vocabulary, int start, int end) {
        Locale locale = Locale.US;
        BreakIterator breakIterator = BreakIterator.getSentenceInstance(locale);
        String belongedSentence = "";
        breakIterator.setText(text);
        int boundaryIndex = breakIterator.first();
        int lastBoundaryIndex = boundaryIndex;
        while (boundaryIndex != BreakIterator.DONE) {
            if (boundaryIndex > 0) {
                lastBoundaryIndex = boundaryIndex;
            }
            boundaryIndex = breakIterator.next();
            if (boundaryIndex < 0) {
                break;
            }
            if (lastBoundaryIndex < start && boundaryIndex > start) {
                belongedSentence = text.substring(lastBoundaryIndex, boundaryIndex).replace("\n", "").trim();
            }
        }
        return belongedSentence;
    }

    @Override
    protected void initView() {
        paragraphTextView = itemView.findViewById(R.id.paragraph);
        paragraphTextView.setTextIsSelectable(true);
        paragraphTextView.setFocusableInTouchMode(true);
        Log.d(TAG, "initView: ");
        ActionMode.Callback mCallback = new ActionMode.Callback() {
            private Menu mMenu;

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                menu.clear();
                mMenu = menu;
                Log.d(TAG, "onCreateActionMode: ");
                MenuInflater inflater = mode.getMenuInflater();
                if (inflater != null) {
                    inflater.inflate(R.menu.text_select_menu, menu);
                }
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.text_menu_collect) {
                    splitAndCollectVocabulary(false);
                }

                if (itemId == R.id.text_menu_corral) {
                    splitAndCollectVocabulary(true);
                }
                if (itemId == R.id.text_menu_vocabulary_yard) {
                    Utils.navigationController(NAVIGATION_CONTROLLER, R.id.navigation_vocabulary_yard);
                }
                // Finish and close the ActionMode
                mode.finish();
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mode.getMenu().close();
            }
        };
        paragraphTextView.setCustomSelectionActionModeCallback(mCallback);
    }
}
