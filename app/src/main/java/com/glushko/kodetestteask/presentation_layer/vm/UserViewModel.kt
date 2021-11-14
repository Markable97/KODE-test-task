package com.glushko.kodetestteask.presentation_layer.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.glushko.kodetestteask.business_logic_layer.domain.User
import com.glushko.kodetestteask.business_logic_layer.interactor.UseCase
import com.glushko.kodetestteask.data_layer.datasource.response.ResponseUsers
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class UserViewModel(application: Application): AndroidViewModel(application) {

    companion object{
        const val TYPE_FIND_USER = 1
        const val TYPE_FIND_TAB = 2
        const val TYPE_SORT_ALPHABET = 1
        const val TYPE_SORT_BIRTHDAY = 2
    }

    private val useCaseRepository by lazy {UseCase()}
    private var myCompositeDisposable: CompositeDisposable? = null
    private val _liveDataUsersSearch: MutableLiveData<ResponseUsers> = MutableLiveData()
    val liveDataUserSearch = _liveDataUsersSearch
    private val _liveDataUsers: MutableLiveData<ResponseUsers> = MutableLiveData()
    val liveDataUsers = _liveDataUsers
    private val _liveDataSorted = MutableLiveData<Int>()
    val liveDataSorted: LiveData<Int> = _liveDataSorted
    init {
        myCompositeDisposable = CompositeDisposable()
    }

    fun getUsers(typeSort: Int = TYPE_SORT_ALPHABET){
        myCompositeDisposable?.addAll(
            useCaseRepository.getUsers()
                .flatMap { response ->
                    useCaseRepository.sortUser(response.items.toMutableList(), typeSort, TYPE_FIND_TAB)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handlerResponse, this::handleError)
        )
    }

    fun searchUser(text: String, typeFind: Int, department: String){

        myCompositeDisposable?.addAll(
            useCaseRepository.searchUser(text, _liveDataUsers.value?.items?.toMutableList()?: mutableListOf()
                , typeFind, department,_liveDataSorted.value?: TYPE_SORT_ALPHABET)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handlerSearch) {}
        )
    }

    private fun handlerSearch(users: List<User>){
        _liveDataUsersSearch.postValue(ResponseUsers(success = false, items = users))
    }

    private fun handlerResponse(response: List<User>){
        val responseDop = ResponseUsers(true, response)
        _liveDataUsers.postValue(responseDop)
    }
    private fun handleError(err: Throwable) {
       _liveDataUsers.postValue(ResponseUsers(success = false))
    }

    fun setSort(sort: Int) {
        _liveDataSorted.value = sort
    }

}