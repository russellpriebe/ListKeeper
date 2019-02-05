package com.penda.mylistkeeper.datamodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.penda.mylistkeeper.datamodel.ListElement;

import java.util.List;

/**
 * Created by newcomputer on 2/13/18.
 */

@Dao
public interface ListElementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ListElement listElement);

    @Delete
    void delete(ListElement listElement);

    @Query("SELECT * from ListElement WHERE listTag LIKE :tag")
    List<ListElement> getElements(String tag);

    @Query("SELECT * from ListElement WHERE listTag LIKE :tag")
    List<ListElement> getNormalElements(String tag);

    @Query("UPDATE ListElement SET elementValue = :newname WHERE elementValue LIKE :name AND listTag LIKE :tag")
    void update(String name, String tag, String newname);

    @Query("UPDATE ListElement SET elementState = :newname WHERE elementValue LIKE :name AND listTag LIKE :tag")
    void complete(String name, String tag, String newname);

    @Query("UPDATE ListELEMENT SET elementValue = :newname WHERE elementValue LIKE :name AND listTag LIKE :tag")
    void swap(String newname, String name, String tag);

    @Query("SELECT * from ListElement WHERE elementValue LIKE :name AND listTag LIKE :tag")
    ListElement getElement(String name, String tag);

    @Query("UPDATE MList SET listName = :newname WHERE listTag = :tag")
    void updateTitle(String newname, String tag);
}