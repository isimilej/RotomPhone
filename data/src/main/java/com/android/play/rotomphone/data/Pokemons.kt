package com.android.play.rotomphone.data

import android.content.Context
import android.util.Log
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.FileReader
import kotlinx.serialization.decodeFromString
import java.io.IOException


@Serializable
data class PokemonListResult(
    @SerialName("results") val results: List<MutableMap<String, String>>
)

class Pokemons {
    fun createRawJson(context: Context, path: String) {
//        var path = "${context.filesDir}/pokemon.json"
//        Log.e("Pokemon", "path=$path")
//
//        GeneratorDataSource().createRawPokemonJson(path)
    }

    fun getList(context: Context): MutableList<Pokemon> {
        return try {
            var content = context.assets.open("pokemons.json").bufferedReader().use {
                it.readText()
            }
            Json { ignoreUnknownKeys = true }.decodeFromString(content)
        } catch (e: IOException) {
            Log.e("POKEMON", e.toString())
            mutableListOf()
        }
    }

    fun get(id: Int, context: Context): Pokemon? {
        getList(context).forEach {
            if (it.id == id) return it
        }
        return null
    }

}