package com.xuwanjin.inchoate.ui.article;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.database.dao.InchoateDBHelper;
import com.xuwanjin.inchoate.model.Article;
import com.xuwanjin.inchoate.model.Paragraph;
import com.xuwanjin.inchoate.model.Vocabulary;

import java.text.BreakIterator;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class ArticleContentAdapter extends RecyclerView.Adapter<ArticleContentAdapter.ViewHolder> {
    public static final String TAG = "ArticleContentAdapter";
    private Context mContext;
    private List<Paragraph> mParagraphList;
    private Article mArticle;
    private View mHeaderView;
    private View mFooterView;
    private View mView;
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_FOOTER = 1;
    public static final int TYPE_NORMAL = 2;

    public ArticleContentAdapter(Context context, List<Paragraph> paragraphList, View view) {
        this.mContext = context;
        this.mParagraphList = paragraphList;
        this.mView = view;
    }

    public void setHeaderView(View headerView) {
        this.mHeaderView = headerView;
    }

    public void setArticle(Article article) {
        this.mArticle = article;
    }

    public void setFooterView(View footerView) {
        this.mFooterView = footerView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_FOOTER:
                view = mFooterView;
                break;
            case TYPE_HEADER:
                view = mHeaderView;
                break;
            default:
                view = LayoutInflater.from(mContext).inflate(R.layout.article_content_item, parent, false);
        }
        return new ViewHolder(view);
    }

    public void updateData(List<Paragraph> paragraphList) {
        mParagraphList = paragraphList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position >= 1 && position < getItemCount() - 1) {
            Paragraph paragraph = mParagraphList.get(position - 1);
            holder.paragraphTextView.setText(paragraph.paragraph, TextView.BufferType.SPANNABLE);
            holder.setCurrentParagraph(paragraph);

        }
    }

    @Override
    public int getItemCount() {
        if (mHeaderView != null && mFooterView != null) {
            return mParagraphList.size() + 2;
        }
        if ((mHeaderView == null && mFooterView != null) ||
                (mHeaderView != null && mFooterView == null)) {
            return mParagraphList.size() + 1;
        }
        if ((mHeaderView == null && mFooterView == null)) {
            return mParagraphList.size();
        }
        return mParagraphList == null ? 0 : mParagraphList.size();
    }

    public List<Paragraph> getParagraphList() {
        return mParagraphList;
    }

    public int getItemViewType(int position) {
        if (mHeaderView == null && mFooterView == null) {
            return TYPE_NORMAL;
        }
        // position 为零, 同时 mHeaderView 不为空, 那么第一个应该是 TYPE_HEADER
        if (position == 0) {
            if (mHeaderView != null) {
                return TYPE_HEADER;
            }
        }
        // 最后一个
        if (position == getItemCount() - 1) {
            if (mFooterView != null) {
                //最后一个,应该加载Footer
                return TYPE_FOOTER;
            } else {
                return TYPE_NORMAL;
            }
        }
        return TYPE_NORMAL;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView paragraphTextView;
        public Paragraph mParagraph;
        private ActionMode.Callback mCallback = new ActionMode.Callback() {
            private Menu mMenu;

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                menu.clear();
                mMenu = menu;
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
                    splitAndCollectVocabulary();
                }

                if (itemId == R.id.text_menu_corral) {
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

        private void splitAndCollectVocabulary() {
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
                        collectTheVocabulary(mParagraph, selectedText.toString(), wordStartTemp, wordEndTemp);
                    }
                };
                Executors.newSingleThreadExecutor().submit(saveVocabulary);
                Snackbar.make(paragraphTextView, selectedText, Snackbar.LENGTH_SHORT).show();
                Log.d(TAG, "onActionItemClicked: selectedText = " + selectedText);
            }
        }

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            if (itemView == mHeaderView
                    || itemView == mFooterView) {
                return;
            }
            paragraphTextView = itemView.findViewById(R.id.paragraph);
            paragraphTextView.setTextIsSelectable(true);
            Typeface typeface = ResourcesCompat.getFont(mContext, R.font.milote_textita);
            paragraphTextView.setFocusableInTouchMode(true);
            paragraphTextView.setTypeface(typeface);
            paragraphTextView.setCustomSelectionActionModeCallback(mCallback);
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

                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                }
            };
            return clickableSpan;
        }

        private void collectTheVocabulary(
                Paragraph paragraph, String vocabularyString, int wordStart, int wordEnd) {
            if (paragraph == null
                    || paragraph.paragraph.length() == 0
                    || "null".equalsIgnoreCase(vocabularyString)) {
                return;
            }
            InchoateDBHelper dbHelper = InchoateDBHelper.getInstance(mContext);
            Vocabulary vocabulary = new Vocabulary();
            String paragraphText = paragraph.paragraph.toString();
            vocabulary.belongedParagraph = paragraphText;
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
    }
}
