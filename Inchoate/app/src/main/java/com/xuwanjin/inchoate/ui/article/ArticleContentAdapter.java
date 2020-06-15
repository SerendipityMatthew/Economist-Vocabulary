package com.xuwanjin.inchoate.ui.article;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
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
import com.xuwanjin.inchoate.customtext.OnWordClickListener;
import com.xuwanjin.inchoate.customtext.SelectableTextView;
import com.xuwanjin.inchoate.database.dao.InchoateDBHelper;
import com.xuwanjin.inchoate.model.Article;
import com.xuwanjin.inchoate.model.Paragraph;
import com.xuwanjin.inchoate.model.Vocabulary;

import java.text.BreakIterator;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
            paragraphTextView.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
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
                        int min = 0;
                        int max = paragraphTextView.getText().length();
                        if (paragraphTextView.isFocused()) {
                            final int selStart = paragraphTextView.getSelectionStart();
                            final int selEnd = paragraphTextView.getSelectionEnd();

                            min = Math.max(0, Math.min(selStart, selEnd));
                            max = Math.max(0, Math.max(selStart, selEnd));
                        }
                        // Perform your definition lookup with the selected text
                        final CharSequence selectedText = paragraphTextView.getText().subSequence(min, max);
                        if (selectedText.toString().contains(" ")) {
                            Snackbar.make(paragraphTextView, "please select a word", Snackbar.LENGTH_SHORT).show();
                            return false;
                        } else {
                            collectTheVocabulary(mParagraph, selectedText.toString());
                            Snackbar.make(paragraphTextView, selectedText, Snackbar.LENGTH_SHORT).show();
                            Log.d(TAG, "onActionItemClicked: selectedText = " + selectedText);
                        }
                    }
                    // Finish and close the ActionMode
                    mode.finish();
                    return false;
                }

                private void collectTheVocabulary(Paragraph paragraph, String vocabularyString) {
                    if (paragraph == null || paragraph.paragraph.length() == 0
                            || "null".equalsIgnoreCase(vocabularyString)) {
                        return;
                    }
                    InchoateDBHelper dbHelper = InchoateDBHelper.getInstance(mContext);
                    Vocabulary vocabulary = new Vocabulary();
                    vocabulary.belongedParagraph = paragraph.paragraph.toString();
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

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    mode.getMenu().close();
                }
            });
        }

        public void setCurrentParagraph(Paragraph paragraph) {
            this.mParagraph = paragraph;
        }

        private ClickableSpan getClickableSpan(final String word) {
            ClickableSpan clickableSpan = new ClickableSpan() {
                final String mWord;

                {
                    mWord = word;
                }

                @Override
                public void onClick(View widget) {
                    Log.d("tapped on:", mWord);
                    Toast.makeText(widget.getContext(), mWord, Toast.LENGTH_SHORT)
                            .show();
                }

                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                }
            };
            return clickableSpan;
        }
    }
}
