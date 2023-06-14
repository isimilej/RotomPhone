package com.android.play.rotomphone.data

import android.content.Context
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.FileReader
import kotlinx.serialization.decodeFromString


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

    fun getList(path: String): MutableList<Pokemon> = Json { ignoreUnknownKeys = true }.decodeFromString<MutableList<Pokemon>>(read(path))

    private fun read(path: String): String {
        try {
            BufferedReader(FileReader(path)).use { reader ->
                var line: String?
                val b = StringBuilder()
                while (reader.readLine().also { line = it } != null) {
                    b.append(line)
                }
                return b.toString()
            }
        } catch (e: Exception) {
            return ""
        }
    }

}