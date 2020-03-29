package com.xuwanjin.inchoate.ui.today;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.xuwanjin.inchoate.model.Article;

import java.util.List;

public class TodayViewModel extends ViewModel {
    public LiveData<List<Article>> articleLiveData = new MutableLiveData<>();;

    public TodayViewModel() {

    }

    public LiveData<List<Article>> getTodayNewsArticle(){
        return null;
    }

}
