package com.penda.listkeeper.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.penda.listkeeper.repository.ListRepository
import com.penda.listkeeper.datamodel.MList
import com.penda.listkeeper.datamodel.ShareBundle
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

class ListViewModel: ViewModel() {
    lateinit var cardsList: LiveData<List<MList >>
    val shareElements: MutableLiveData<ShareBundle> = MutableLiveData()
    private lateinit var mRepository: ListRepository

    fun setRepository(listRepository: ListRepository){
        mRepository = listRepository
        cardsList = mRepository.getLists()
    }

    fun getLists() {
        cardsList =  mRepository.getLists()
    }

    fun deleteList(list: MList){
        mRepository.deleteList(list)
    }

    fun reorderList(list: ArrayList<MList>){
        launch(){
            val result = reorder(list)
            if(result.await()){
                cardsList = mRepository.getLists()
            }
        }
    }

    private fun reorder(list: ArrayList<MList>): Deferred<Boolean>{
        return async{
            mRepository.reorderListList(list)
            true
        }
    }

    fun share(list: MList){
        launch(){
            val result = getElementsForShare(list)
            shareElements.postValue(result.await())
        }
    }

    private fun getElementsForShare(list: MList): Deferred<ShareBundle>{
        return async(CommonPool){
            val tem = mRepository.getElements(list)
            ShareBundle(list, tem)
        }
    }

}