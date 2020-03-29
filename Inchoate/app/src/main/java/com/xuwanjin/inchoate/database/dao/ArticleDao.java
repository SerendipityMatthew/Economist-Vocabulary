package com.xuwanjin.inchoate.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.xuwanjin.inchoate.model.Article;

import java.util.List;

@Dao
public interface ArticleDao {
//    @Query("SELECT * FROM article WHERE id = :id")
    LiveData<Article> getById(String id);

//    @Query("SELECT * FROM article")
    LiveData<List<Article>> all();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void add(List<Article> articles );

//    @Query("DELETE FROM article")
    void deleteAll();

    @Delete
    void delete(Article model );
}
