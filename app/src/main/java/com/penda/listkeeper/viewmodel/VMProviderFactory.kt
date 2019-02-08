package com.penda.listkeeper.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class VMProviderFactory {
    companion object {
        val viewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {

                return modelClass.newInstance()
            }
        }
    }

}