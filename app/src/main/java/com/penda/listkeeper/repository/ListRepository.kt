package com.penda.listkeeper.repository

import androidx.lifecycle.LiveData
import com.penda.listkeeper.ListRoomDatabase
import com.penda.listkeeper.datamodel.ListElement
import com.penda.listkeeper.datamodel.ListElementDao
import com.penda.listkeeper.datamodel.MList
import com.penda.listkeeper.datamodel.MListDao
import org.jetbrains.anko.doAsync

/**
 * Created by newcomputer on 2/13/18.
 */

class ListRepository(val db: ListRoomDatabase) {

    private var mListDao: MListDao = db.mListDao()
    private var mListElementDao: ListElementDao = db.listElementDao()
    internal var lists: LiveData<List<MList>>
    internal lateinit var listElements: List<ListElement>


    init {
        lists = mListDao.lists
    }

    fun deleteList(list: MList) {
        doAsync {
            val elements = mListElementDao.getNormalElements(list.listTag)
            clearList(elements)
            mListDao.delete(list)
            listElements = mListElementDao.getElements(list.listTag)
        }
    }

    fun clearList(list: List<ListElement>){
        val len = list.size -1
        for (i in 0 until len) {
            mListElementDao.delete(list[i])
        }
    }

    fun getLists(): LiveData<List<MList>> {
        return lists
    }

    fun reorderListList(nList: ArrayList<MList>){
            val mList = mListDao.getNormalLists()
            val len = mList.size
            for (i in 0 until len) {
                mListDao.delete(mList[i])
            }
            for (i in 0 until len) {
                mListDao.insert(nList[i])
            }
    }

    fun getElements(list: MList): List<ListElement>{
            return mListElementDao.getNormalElements(list.listTag)
    }

}
