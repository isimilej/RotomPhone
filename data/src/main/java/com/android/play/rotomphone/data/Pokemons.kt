package com.android.play.rotomphone.data

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

class Pokemons {
    fun getList() {
        val retrofit = Retrofit.Builder().baseUrl("https://pokeapi.co")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
        retrofit.create(PokemonService::class.java).getList().enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    Log.d("Success", response.body()!!)
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                //TODO("Not yet implemented")
            }

        })
    }
}


interface PokemonService {
    @GET("api/v2/pokemon?limit=2000")
    fun getList(): Call<String>
}