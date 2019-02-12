package com.penda.listkeeper.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.penda.listkeeper.repository.ListRepository
import com.penda.listkeeper.datamodel.MList
import com.penda.listkeeper.datamodel.ShareBundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ListViewModel(private val mRepository: ListRepository): ViewModel() {
    lateinit var cardsList: LiveData<List<MList >>
    val shareElements: MutableLiveData<ShareBundle> = MutableLiveData()
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.IO + viewModelJob)


    companion object {
        /**
         * Factory for creating [MainViewModel]
         *
         * @param arg the repository to pass to [MainViewModel]
         */
        val FACTORY = singleArgViewModelFactory(::ListViewModel)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun getListForAdapter(){
            cardsList =  mRepository.getLists()
    }

    fun deleteList(list: MList){
        mRepository.deleteList(list)
    }

    fun reorderList(list: ArrayList<MList>){
        uiScope.launch(Dispatchers.IO){
            mRepository.reorderListList(list)
            cardsList = mRepository.getLists()
        }
    }

    fun share(list: MList){
        uiScope.launch(Dispatchers.IO){
            val result = mRepository.getElements(list)
            shareElements.postValue(ShareBundle(list, result))
        }
    }
}