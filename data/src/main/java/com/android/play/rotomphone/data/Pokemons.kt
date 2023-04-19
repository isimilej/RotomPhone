package com.android.play.rotomphone.data

import android.content.Context
import android.util.Log
import com.android.play.rotomphone.data.generator.GeneratorDataSource
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class PokemonListResult(
    @SerialName("results") val results: List<MutableMap<String, String>>
)

class Pokemons {
    fun createRawJson(context: Context, path: String) {
        var path = "${context.filesDir}/pokemon.json"
        Log.e("Pokemon", "path=$path")

        GeneratorDataSource().createRawPokemonJson(path)
    }
}