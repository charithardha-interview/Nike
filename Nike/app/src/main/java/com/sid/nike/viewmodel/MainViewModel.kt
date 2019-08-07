package com.sid.nike.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.sid.nike.db.Repository
import com.sid.nike.db.dto.Definition

class MainViewModel : AndroidViewModel {
    var context:Context
    var repository: Repository
    var definitionsLiveData: MutableLiveData<List<Definition>>
    var definitionsLocalLiveData: MutableLiveData<List<Definition>>

    companion object {
        private var APP_PREFERENCES = "com.sid.nike.APP_PREFERENCES"
        private var KEY_TERMS = "KEY_TERMS"
    }

    constructor(application: Application) : super(application) {
        this.context = application.applicationContext
        this.repository = Repository.createInstance(application)
        definitionsLiveData = repository.definitionsLiveData
        definitionsLocalLiveData = repository.definitionsLocalLiveData
    }

    fun getSharedPreferences():SharedPreferences =
        context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)

    fun search(term: String) {
        repository.downloadTerm(term)
    }

    fun load(term: String) {
        repository.loadTerm(term)
    }

    fun delete(term: String) {
        repository.deleteTerm(term)
    }

    fun getSearchHistory():List<String> = getSharedPreferences().getStringSet(KEY_TERMS, mutableSetOf()).toList()

    fun addToSearchHistory(term: String) {
        var sharedPreferences = getSharedPreferences()
        var termsSet = sharedPreferences.getStringSet(KEY_TERMS, mutableSetOf())
        if(!termsSet.contains(term)) termsSet.add(term)
        sharedPreferences.edit().putStringSet(KEY_TERMS,termsSet).commit()
    }
}