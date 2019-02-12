package com.penda.listkeeper.viewmodel

import androidx.lifecycle.*
import android.text.TextUtils
import com.penda.listkeeper.repository.ElementRepository
import com.penda.listkeeper.Utilities
import com.penda.listkeeper.datamodel.ListElement
import com.penda.listkeeper.datamodel.MList
import kotlinx.coroutines.*

import org.jetbrains.anko.doAsync

class ElementViewModel(private val mRepository: ElementRepository): ViewModel() {
    var elementsList: MutableLiveData<List<ListElement>> = MutableLiveData()
    var editElement: MutableLiveData<ListElement> = MutableLiveData()
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.IO + viewModelJob)


    companion object {
        /**
         * Factory for creating [MainViewModel]
         *
         * @param arg the repository to pass to [MainViewModel]
         */
        val FACTORY = singleArgViewModelFactory(::ElementViewModel)
    }


    fun getElements(tag: String?){
        tag?.let {
            uiScope.launch(Dispatchers.IO) {
                val result = mRepository.getElements(tag)
                elementsList.postValue(result)
            }
        }
    }

    fun handleElementAddition(listValue: String, tag: String?) {
        if (TextUtils.isEmpty(listValue)) {
            return
        }
        tag?.let {
            uiScope.launch(Dispatchers.IO) {
                val state = "active"
                val quantity = 1
                mRepository.insertListElement(ListElement(tag, listValue, state, quantity))
                val result = mRepository.getElements(tag)
                elementsList.postValue(result)
            }
        }
    }

    fun handleElementUpdate(element: ListElement, oldElement: ListElement) {
        uiScope.launch(Dispatchers.IO){
            mRepository.simpleUpdate(oldElement.elementValue, element.listTag, element.elementValue)
            val result = mRepository.getElements(element.listTag)
            elementsList.postValue(result)
        }
    }

    fun handleTitleUpdate(newName: String, tag: String){
        doAsync{
            mRepository.updateTitle(newName, tag)
        }
    }

    fun handleComplete(element: ListElement, oldElement: ListElement) {
        uiScope.launch(Dispatchers.IO){
            mRepository.completeUpdate(element, oldElement)
            val result = mRepository.getElements(element.listTag)
            elementsList.postValue(result)
        }

    }

    fun handleRestore(element: ListElement, oldElement: ListElement) {
        uiScope.launch(Dispatchers.IO){
            mRepository.restore(element, oldElement)
            val result = mRepository.getElements(element.listTag)
            elementsList.postValue(result)
        }
    }

    fun handleListAddition(listName: String, tag: String) {
        if (TextUtils.isEmpty(listName)) {
            return
        }
        var listType = "shopping"
        val listDate = Utilities.getDateString()
        mRepository.insertList(MList(tag, listType, listName, listDate))
    }

    fun deleteElement(element: ListElement) {
        uiScope.launch(Dispatchers.IO){
            mRepository.deleteElementSync(element)
            val result = mRepository.getElements(element.listTag)
            elementsList.postValue(result)
        }
    }

    fun restoreList(tag: String){
        uiScope.launch(Dispatchers.IO){
            mRepository.restoreList(tag)
            val result = mRepository.getElements(tag)
            elementsList.postValue(result)
        }
    }

    fun reorderList(tag: String, list: ArrayList<ListElement>){
        uiScope.launch(Dispatchers.IO){
            mRepository.reorderList(tag, list)
            val result = mRepository.getElements(tag)
            elementsList.postValue(result)
        }
    }

    fun getElementForEdit(name: String, tag: String){
        uiScope.launch(Dispatchers.IO) {
            val result = mRepository.getElementForEdit(name, tag)
            editElement.postValue(result)
        }
    }
}