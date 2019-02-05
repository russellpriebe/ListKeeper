package com.penda.listkeeper;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.penda.listkeeper.datamodel.ListElement;
import com.penda.listkeeper.datamodel.ListElementDao;
import com.penda.listkeeper.datamodel.MList;
import com.penda.listkeeper.datamodel.MListDao;

/**
 * Created by newcomputer on 2/13/18.
 */

@Database(entities = {MList.class, ListElement.class}, version = 1, exportSchema = false)
public abstract class ListRoomDatabase extends RoomDatabase {

    public abstract MListDao mListDao();
    public abstract ListElementDao listElementDao();

    private static ListRoomDatabase INSTANCE;


    static ListRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ListRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ListRoomDatabase.class, "word_database")
                            .build();

                }
            }
        }
        return INSTANCE;
    }

}

