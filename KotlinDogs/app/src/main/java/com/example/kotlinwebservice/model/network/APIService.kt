package com.example.kotlinwebservice.model.network

import com.example.kotlinwebservice.model.pojo.DogsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface APIService {

    @GET
    fun getCharecterByName(@Url url: String) : Call<DogsResponse>
}