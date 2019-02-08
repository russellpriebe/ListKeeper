package com.penda.listkeeper.datamodel;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Created by newcomputer on 2/13/18.
 */
@Entity(primaryKeys = {"listTag", "elementValue"})
public class ListElement {
    @NonNull
    public String listTag;
    @NonNull
    public String elementValue;
    public String elementState;
    public int quantity;
    public ListElement(@NonNull String listTag, @NonNull String elementValue, String elementState, int quantity){
        this.listTag = listTag;
        this.elementValue = elementValue;
        this.elementState = elementState;
        this.quantity = quantity;
    }

}
