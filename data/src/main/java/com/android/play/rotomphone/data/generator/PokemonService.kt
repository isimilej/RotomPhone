package com.android.play.rotomphone.data.generator

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface PokemonService {
    @GET("api/v2/pokemon?limit=2000")
    fun getList(): Call<String>

    @GET("api/v2/pokemon/{id}")
    fun get(@Path("id") id: Int): Call<String>

    @GET("api/v2/pokemon-species/{speciesId}")
    fun getSpecies(@Path("speciesId") speciesId: Int): Call<String>

    @GET("api/v2/pokemon-form/{formId}")
    fun getForm(@Path("formId")formId: Int): Call<String>
}