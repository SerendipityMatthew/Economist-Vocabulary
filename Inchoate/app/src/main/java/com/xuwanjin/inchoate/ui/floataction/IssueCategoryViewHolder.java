package com.xuwanjin.inchoate.ui.floataction;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.ui.BaseViewHolder;

public class IssueCategoryViewHolder extends BaseViewHolder {
    public TextView categoryMenu;
    public IssueCategoryViewHolder(@NonNull View itemView) {
        super(itemView, false);
    }

    @Override
    protected void initView() {
        categoryMenu = itemView.findViewById(R.id.issue_category_menu);
    }
}
