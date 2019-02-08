package com.penda.listkeeper.datamodel;

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