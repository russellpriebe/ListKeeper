package com.penda.listkeeper.datamodel;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

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
