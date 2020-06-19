package com.xuwanjin.inchoate.ui.vocabulary;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.database.dao.InchoateDBHelper;
import com.xuwanjin.inchoate.model.Vocabulary;
import com.xuwanjin.inchoate.ui.BaseFragment;
import com.xuwanjin.inchoate.ui.BaseItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Matthew Xu
 */
public class VocabularyYardFragment extends BaseFragment<VocabularyYardAdapter, BaseItemDecoration> {
    private static List<Vocabulary> mVocabularyList;

    @Override
    protected void initView(View view) {
        mRecyclerView = view.findViewById(R.id.vocabulary_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

    }

    @Override
    protected void loadData() {
        List<Vocabulary> vocabularyList = fetchDataFromDBOrNetwork();
        mBaseAdapter = new VocabularyYardAdapter(getContext(), mVocabularyList);
        mRecyclerView.setAdapter(mBaseAdapter);
        mVocabularyList = vocabularyList.stream().filter(new Predicate<Vocabulary>() {
            @Override
            public boolean test(Vocabulary vocabulary) {
                String voca = vocabulary.vocabularyContent ;
                return voca != null && !voca.trim().equals("");
            }
        }).collect(Collectors.toList());
        mBaseAdapter.updateData(mVocabularyList);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_vocabulary_yard;
    }

    @Override
    protected List<Vocabulary> fetchDataFromDBOrNetwork() {
        return getVocabularyListFromDB();
    }

    private List<Vocabulary> getVocabularyListFromDB() {
        InchoateDBHelper helper = InchoateDBHelper.getInstance(getContext());
        List<Vocabulary> vocabularyList = helper.getVocabularyAll();
        return vocabularyList;
    }

    @Override
    protected <T> T initFakeData() {
        return null;
    }
}
