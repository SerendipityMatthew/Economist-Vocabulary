package com.xuwanjin.inchoate.ui.article;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.customtext.OnWordClickListener;
import com.xuwanjin.inchoate.customtext.SelectableTextView;
import com.xuwanjin.inchoate.model.Paragraph;

import java.util.List;

public class ArticleContentAdapter extends RecyclerView.Adapter<ArticleContentAdapter.ViewHolder> {
    private Context mContext;
    private List<Paragraph> mParagraphList;
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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_HEADER:
                view = mHeaderView;
                break;
            default:
                view = LayoutInflater.from(mContext).inflate(R.layout.article_content_item, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position >= 1) {
            holder.paragraph.setText(mParagraphList.get(position - 1).paragraph);
        }
    }

    @Override
    public int getItemCount() {
        if (mHeaderView != null) {
            return mParagraphList.size() + 1;
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
        public SelectableTextView paragraph;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            if (itemView == mHeaderView) {
                return;
            }
            final String[] selectedVocabulary = new String[1];
            paragraph = itemView.findViewById(R.id.paragraph);
            paragraph.setTextIsSelectable(true);
            paragraph.setFocusable(true);
            paragraph.setSelectTextBackColorRes(R.color.colorAccent);
            paragraph.setSelectTextFrontColorRes(R.color.colorPrimary);
            Typeface typeface = ResourcesCompat.getFont(mContext, R.font.milote_textita);
            paragraph.setFocusableInTouchMode(true);
            paragraph.setTypeface(typeface);
            paragraph.setOnWordClickListener(new OnWordClickListener() {
                @Override
                protected void onNoDoubleClick(String word) {
                    selectedVocabulary[0] = word;
                    Toast.makeText(mContext, selectedVocabulary[0].toString(), Toast.LENGTH_LONG).show();
                }
            });
            paragraph.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
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
                        Snackbar
                                .make(mView, selectedVocabulary[0], Snackbar.LENGTH_LONG)
                                .setTextColor(Color.RED)
                                .show();
                        mode.finish();
                        mMenu.close();
                    }
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    mode.getMenu().close();
                }
            });
        }
    }
}
