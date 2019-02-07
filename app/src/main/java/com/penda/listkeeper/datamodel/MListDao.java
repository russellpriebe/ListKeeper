package com.penda.listkeeper.datamodel;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Created by newcomputer on 2/13/18.
 */

@Dao
public interface MListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MList list);

    @Delete
    void delete(MList list);

    @Update
    void update(MList list);

    @Query("SELECT * from MList")
    LiveData<List<MList>> getLists();

    @Query("SELECT * from MList")
    List<MList> getNormalLists();
}
