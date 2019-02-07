package com.penda.listkeeper.datamodel;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Created by newcomputer on 2/13/18.
 */

@Entity(primaryKeys = {"listTag", "listName"})
public class MList {
    @NonNull
    public String listTag;
    @NonNull
    public String listName;
    public String listType;
    public String listDate;


    public MList(@NonNull String listTag, @NonNull String listType, String listName, String listDate){
        this.listTag = listTag;
        this.listType = listType;
        this.listName = listName;
        this.listDate = listDate;
    }

}
