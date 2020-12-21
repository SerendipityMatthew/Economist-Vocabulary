package com.xuwanjin.inchoate.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class BaseViewModel:AndroidViewModel {
    constructor(application: Application) : super(application)

    override fun onCleared() {
        super.onCleared()
    }
}