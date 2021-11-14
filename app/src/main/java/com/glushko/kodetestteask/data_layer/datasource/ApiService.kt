package com.glushko.kodetestteask.data_layer.datasource

import com.glushko.kodetestteask.data_layer.datasource.response.ResponseUsers
import io.reactivex.Observable
import retrofit2.http.GET

interface ApiService {
    companion object{
        //Methods
        const val GET_USERS = "users"
    }


    @GET(GET_USERS)
    fun getUsers(): Observable<ResponseUsers>

}