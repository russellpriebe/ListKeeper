package com.penda.mylistkeeper.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

class VMProviderFactory {
    companion object {
        val viewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {

                return modelClass.newInstance()
            }
        }
    }

}