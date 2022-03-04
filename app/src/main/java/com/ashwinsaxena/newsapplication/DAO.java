package com.ashwinsaxena.newsapplication;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface DAO {

    String SELECT_ALL = "SELECT * FROM NewsTable";
    String DELETE_ALL = "DELETE FROM NewsTable";

    @Insert
    void insertNews(ArrayList<DataModel> dataModel);

    @Query(DELETE_ALL)
    void deleteAll();

    @Query(SELECT_ALL)
    List<DataModel> getNews();

}
