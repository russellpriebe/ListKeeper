package com.penda.listkeeper.viewmodel

import android.arch.lifecycle.*
import android.text.TextUtils
import com.penda.listkeeper.repository.ElementRepository
import com.penda.listkeeper.Utilities
import com.penda.listkeeper.datamodel.ListElement
import com.penda.listkeeper.datamodel.MList
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.doAsync

class ElementViewModel: ViewModel() {
    var elementsList: MutableLiveData<List<ListElement>> = MutableLiveData()
    private lateinit var mRepository: ElementRepository
    var editElement: MutableLiveData<ListElement> = MutableLiveData()

    fun setRepository(elementRepository: ElementRepository, tag: String?){
        mRepository = elementRepository
        getElements(tag)

    }

    private fun getElements(tag: String?){
        tag?.let {
            launch() {
                val result = fetchList(tag)
                elementsList.postValue(result.await())
            }
        }
    }


    private fun fetchList(tag: String): Deferred<List<ListElement>> {
        return async(CommonPool) {
            mRepository.getElements(tag)
        }
    }

    fun handleElementAddition(listValue: String, tag: String?) {
        if (TextUtils.isEmpty(listValue)) {
            return
        }
        tag?.let {
            launch() {
                val result = addElementAndRefresh(listValue, tag)
                elementsList.postValue(result.await())
            }
        }
    }

    private fun addElementAndRefresh(listValue: String, tag: String?): Deferred<List<ListElement>>{
        return async(CommonPool) {

            val state = "active"
            val quantity = 1
            mRepository.insertListElement(ListElement(tag!!, listValue, state, quantity))
            mRepository.getElements(tag)
        }
    }

    fun handleElementUpdate(element: ListElement, oldElement: ListElement) {
        launch(){
            val result = updateAndRefresh(element, oldElement)
            elementsList.postValue(result.await())
        }
    }

    private fun updateAndRefresh(element: ListElement, oldElement: ListElement): Deferred<List<ListElement>>{
        return async(CommonPool) {
            mRepository.simpleUpdate(oldElement.elementValue, element.listTag, element.elementValue)
            mRepository.getElements(element.listTag)
        }
    }

    fun handleTitleUpdate(newName: String, tag: String){
        doAsync{
            mRepository.updateTitle(newName, tag)
        }
    }

    fun handleComplete(element: ListElement, oldElement: ListElement) {
        launch(){
            val result = complete(element, oldElement)
            elementsList.postValue(result.await())
        }

    }

    private fun complete(element: ListElement, oldElement: ListElement): Deferred<List<ListElement>>{
        return async(CommonPool){
            mRepository.completeUpdate(element, oldElement)
            mRepository.getElements(element.listTag)
        }
    }

    fun handleRestore(element: ListElement, oldElement: ListElement) {
        launch(){
            val result = restore(element, oldElement)
            elementsList.postValue(result.await())
        }
    }

    private fun restore(element: ListElement, oldElement: ListElement): Deferred<List<ListElement>>{
        return async(CommonPool){
            mRepository.restore(element, oldElement)
            mRepository.getElements(element.listTag)
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
        launch(){
            val result = delete(element)
            elementsList.postValue(result.await())
        }
    }

    private fun delete(element: ListElement): Deferred<List<ListElement>>{
        return async(CommonPool){
            mRepository.deleteElementSync(element)
            mRepository.getElements(element.listTag)
        }
    }

    fun restoreList(tag: String){
        launch(){
            val result = restore(tag)
            elementsList.postValue(result.await())
        }
    }

    private fun restore(tag: String): Deferred<List<ListElement>>{
        return async(CommonPool){
            mRepository.restoreList(tag)
            mRepository.getElements(tag)

        }
    }

    fun reorderList(tag: String, list: ArrayList<ListElement>){
        launch(){
            val result = reorder(tag, list)
            elementsList.postValue(result.await())
        }
    }

    private fun reorder(tag: String, list: ArrayList<ListElement>): Deferred<List<ListElement>>{
        return async(CommonPool){
            mRepository.reorderList(tag, list)
            mRepository.getElements(tag)
        }
    }

    fun getElementForEdit(name: String, tag: String){
        launch() {
            val result = fetchElement(name, tag)
            editElement.postValue(result.await())
        }
    }

    private fun fetchElement(name: String, tag: String): Deferred<ListElement> {
        return async(CommonPool) {
            mRepository.getElementForEdit(name, tag)
        }
    }

}