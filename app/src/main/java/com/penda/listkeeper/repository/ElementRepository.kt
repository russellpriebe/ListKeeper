package com.penda.listkeeper.repository

import com.penda.listkeeper.ListRoomDatabase
import com.penda.listkeeper.datamodel.*
import org.jetbrains.anko.doAsync

class ElementRepository(db: ListRoomDatabase, tag: String?) {

    private var mListElementDao: ListElementDao = db.listElementDao()
    lateinit var listElements: List<ListElement>
    private var mListDao: MListDao = db.mListDao()

    fun getElements(tag: String): List<ListElement> {
        return mListElementDao.getElements(tag)
    }

    fun runTask(func: () -> Unit) {
        doAsync {
            func()
        }
    }

    fun restoreList(tag: String) {
        val elements = mListElementDao.getNormalElements(tag)
        val len = elements?.size
        len?.let {
            for (element in elements) {
                element?.let {
                    mListElementDao.delete(element)
                    mListElementDao.insert(ListElement(element.listTag, element.elementValue, "active", 1))
                }
            }
        }
    }

    fun insertListElement(listElement: ListElement) {
        mListElementDao.insert(listElement)
    }


    fun insertList(list: MList) {
        runTask { mListDao.insert(list) }
    }

    fun deleteElementSync(element: ListElement) {
        mListElementDao.delete(element)
    }

    fun reorderList(tag: String, nList: ArrayList<ListElement>) {
        val mList = mListElementDao.getNormalElements(tag)
        val len = mList.size
        val nt = nList
          for (i in 0 until len) {
            mListElementDao.delete(mList[i])
        }
        for (i in 0 until len) {
            mListElementDao.insert(nList[i])
        }
    }

    fun simpleUpdate(name: String, tag: String, newName: String) {
        mListElementDao.update(name, tag, newName)
    }

    fun completeUpdate(element: ListElement, oldElement: ListElement) {
        mListElementDao.delete(oldElement)
        mListElementDao.insert(element)
    }

    fun restore(element: ListElement, oldElement: ListElement) {
        mListElementDao.update(oldElement.elementValue, oldElement.listTag, "active")
    }

    fun getElementForEdit(name: String, tag: String): ListElement {
        return mListElementDao.getElement(name, tag)
    }

    fun updateTitle(newName: String, tag: String){
        mListElementDao.updateTitle(newName, tag)
    }


}
