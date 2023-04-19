package com.android.play.rotomphone.data.generator

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.android.play.rotomphone.data.PokemonListResult
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.FileReader

class GeneratorDataSource {

    private val pokemonIdList = mutableListOf<Int>()
    private val varietyPokemonIdList = mutableListOf<Int>()

    private var order = 0
    private var count = 2
    private var varietiesChainId = 0
    private var language = "ko"

    private var pokemonList = mutableListOf<Pokemon>()
    var varietiesChainMap = mutableMapOf<Int, MutableList<Int>>()

    private var path = ""

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder().baseUrl("https://pokeapi.co")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }

    private val service: PokemonService by lazy {
        retrofit.create(PokemonService::class.java)
    }

    fun createRawPokemonJson(path: String) {
        this.path = path
        getPokemonList()
    }

    fun getIdFromUrl(url: String?): Int? {
        url?.let { it ->
            "[0-9]+/$".toRegex().find(it)?.let { result ->
                return result.value.replace("/", "").toInt()
            }
        }
        return null
    }

    private fun getPokemonList() {

        Log.e("Pokemon", "path=$path")

//        var data = read(path)
//        pokemonList = Json { ignoreUnknownKeys = true }.decodeFromString<MutableList<Pokemon>>(data)
//        pokemonList.forEach { pokemon ->
//            if (pokemon.varietiesChainId != 0 && pokemon.varietiesChain.isNotEmpty()) {
//                varietiesChainId = pokemon.varietiesChainId
//                varietiesChainMap[pokemon.varietiesChainId] = pokemon.varietiesChain
//            }
//        }

        service.getList().enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        var data = Json { ignoreUnknownKeys = true }.decodeFromString<PokemonListResult>(body)
                        data.results.forEach { result ->
                            getIdFromUrl(result["url"])?.let { pokemonId ->
                                pokemonIdList.add(pokemonId)
                            }
                        }

                        pokemonList.forEach { pokemonIdList.remove(it.id) }
                        order = if (pokemonList.isNotEmpty()) pokemonList.last().order else 0

                        if (pokemonIdList.isNotEmpty())
                            get(pokemonIdList[0])

                    }
                } else
                    throw NotImplementedError(message = "GetPokemonList.onResponse not successful operation is not implemented.")
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                throw NotImplementedError(message = "GetPokemonList.onFailure operation is not implemented.")
            }
        })
    }

    fun mapToTypes(json: JSONObject): MutableList<String> {
        val typeList = mutableListOf<String>()
        json.optJSONArray("types")?.let { it ->
            for (i in 0 until it.length()) {
                typeList.add(it.optJSONObject(i).optJSONObject("type").optString("name"))
            }
        }
        return typeList
    }

    fun mapTopStats(json: JSONObject): MutableMap<String, Int> {
        val stats = mutableMapOf<String, Int>()
        json.optJSONArray("stats")?.let {
            for (i in 0 until it.length()) {
                val name = it.optJSONObject(i).optJSONObject("stat").optString("name")
                val value = it.optJSONObject(i).optInt("base_stat")
                stats[name] = value
            }
        }
        return stats
    }

    private fun mapToAbilities(json: JSONObject): MutableMap<String, Boolean> {
        val abilities = mutableMapOf<String, Boolean>()
        json.optJSONArray("abilities")?.let {
            for (i in 0 until it.length()) {
                it.optJSONObject(i).optJSONObject("ability")?.let { ability ->
                    val name = ability.optString("name")
                    val hidden = ability.optBoolean("is_hidden")
                    abilities[name] = hidden
                }
            }
        }
        return abilities
    }

    private fun mapToSprites(json: JSONObject): MutableMap<String, String?> {
        val sprites = mutableMapOf<String, String?>()
        json.optJSONObject("sprites")?.optJSONObject("other")?.let {
            it.optJSONObject("home")?.let { home ->
                if (!home.isNull("front_default")) sprites["home"] = home.optString("front_default")
            }
            it.optJSONObject("official-artwork")?.let { artwork ->
                if (!artwork.isNull("front_default")) sprites["artwork"] = artwork.optString("front_default")
            }
        }
        return sprites
    }

    private fun mapToPokemon(data: JSONObject) = Pokemon(
        id = data.optInt("id"),
        order = ++order,
        name = data.optString("name"),
        height = data.optInt("height"),
        weight = data.optInt("weight"),
        isDefault = data.optBoolean("is_default"),
        types = mapToTypes(data),
        stats = mapTopStats(data),
        abilities = mapToAbilities(data),
        sprites = mapToSprites(data),
    )

    fun get(pokemonId: Int) {

        retrofit.create(PokemonService::class.java).get(pokemonId).enqueue(object :
            Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) response.body()?.let { body ->

                    val data = JSONObject(body)
                    val pokemon = mapToPokemon(data)

                    Log.e("Pokemon", "count=$count, size[${pokemonIdList.size}], id=${pokemon.id}, order=${pokemon.order}")

                    var speciesId = data.optJSONObject("species")?.let {
                        getIdFromUrl(it.optString("url"))
                    }

                    if (pokemonId == speciesId) {
                        getSpecies(pokemon)
                    } else if (varietyPokemonIdList.contains(pokemonId)) {
                        data.optJSONArray("forms")?.let {
                            for (i in 0 until it.length()) {
                                if (data.optString("name") == it.optJSONObject(i).optString("name")) {
                                    getIdFromUrl(it.optJSONObject(i).optString("url"))?.let { formId ->
                                        getForm(pokemon, formId)
                                    }
                                }
                            }
                        }
                    } else {
                        nextPokemon(pokemon)
                    }
                    return
                }
                throw NotImplementedError(message = "Get.onFailure not successful operation is not implemented.")
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                throw NotImplementedError(message = "Get.onFailure operation is not implemented.")
            }
        })
    }

    fun nextPokemon(pokemon: Pokemon) {

        varietyPokemonIdList.remove(pokemon.id)
        pokemonIdList.remove(pokemon.id)
        pokemonList.add(pokemon)

        count--
        if (count <= 0 && varietyPokemonIdList.isEmpty()) {
            terminate()
        } else if (varietyPokemonIdList.isNotEmpty()) {
            Handler(Looper.getMainLooper()).postDelayed({
                get(varietyPokemonIdList[0])
            }, 2000)
        } else if (pokemonIdList.isNotEmpty()) {
            Handler(Looper.getMainLooper()).postDelayed({
                get(pokemonIdList[0])
            }, 2000)
        } else {
            terminate()
        }
    }

    fun getSpecies(pokemon: Pokemon) {
        service.getSpecies(pokemon.id).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) response.body()?.let { body ->
                    val json = JSONObject(body)
                    pokemon.genus = mapToGenus(json)
                    pokemon.koName = mapToKoName(json)
                    pokemon.flavors = mapToFlavors(json)
                    pokemon.color = json.optJSONObject("color").optString("name")
                    pokemon.generation = json.optJSONObject("generation").optString("name")
                    pokemon.evolutionChainId = getIdFromUrl(json.optJSONObject("evolution_chain").optString("url")) ?: 0


                    // 진화 또는 변신 폼
                    json.optJSONArray("varieties")?.let { it ->
                        if (it.length() > 1) {
                            pokemon.varietiesChainId = ++varietiesChainId
                            for (i in 0 until it.length()) {
                                getIdFromUrl(it.getJSONObject(i).optJSONObject("pokemon").optString("url"))?.let { id ->
                                    varietyPokemonIdList.add(id)
                                    pokemon.varietiesChain.add(id)
                                }
                            }
                            varietiesChainMap[varietiesChainId] = pokemon.varietiesChain
                        }
                    }

                    if (pokemon.varietiesChainId == 0) {
                        varietiesChainMap.forEach { (key, chain) ->
                            if (chain.contains(pokemon.id))
                                pokemon.varietiesChainId = key
                        }
                    }

                    nextPokemon(pokemon)
                    return
                }
                throw NotImplementedError(message = "GetSpecies.onResponse not successful operation is not implemented.")
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                throw NotImplementedError(message = "GetSpecies.onFailure operation is not implemented.")
            }
        })
    }

    private fun mapToFlavors(json: JSONObject): MutableMap<String, String> {
        var flavors = mutableMapOf<String, String>()
        json.optJSONArray("flavor_text_entries")?.let { entries ->
            for (i in 0 until entries.length()) {
                if (language == entries.optJSONObject(i).optJSONObject("language").optString("name")) {
                    entries.optJSONObject(i)?.let { flavor ->
                        val version = flavor.optJSONObject("version")?.optString("name") ?: ""
                        flavors[version] = flavor.optString("flavor_text").replace("\n", " ")
                    }
                }
            }
        }
        return flavors
    }

    private fun mapToKoName(json: JSONObject): String {
        json.optJSONArray("names")?.let { names ->
            for (i in 0 until names.length()) {
                if (language == names.optJSONObject(i).optJSONObject("language").optString("name")) {
                    return names.optJSONObject(i).optString("name")
                }
            }
        }
        return ""
    }

    private fun mapToGenus(json: JSONObject): String {
        json.optJSONArray("genera")?.let { genera ->
            for (i in 0 until genera.length()) {
                if (language == genera.optJSONObject(i).optJSONObject("language").optString("name")) {
                    return genera.optJSONObject(i).optString("genus")
                }
            }
        }
        return ""
    }

    fun getForm(pokemon: Pokemon, formId: Int) {
        service.getForm(formId).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) response.body()?.let { body ->
                    var json = JSONObject(body)
                    json.optJSONArray("form_names")?.let { forms ->
                        for (i in 0 until forms.length()) {
                            if (language == forms.optJSONObject(i).optJSONObject("language")?.optString("name")) {
                                pokemon.koName = forms.optJSONObject(i)?.optString("name") ?: ""
                            }
                        }
                    }

                    if (pokemon.varietiesChainId == 0) {
                        varietiesChainMap.forEach { (key, chain) ->
                            if (chain.contains(pokemon.id))
                                pokemon.varietiesChainId = key
                        }
                    }

                    nextPokemon(pokemon)
                    return
                }
                throw NotImplementedError(message = "GetForm.onFailure not successful operation is not implemented.")
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                throw NotImplementedError(message = "GetForm.onFailure operation is not implemented.")
            }
        })
    }

    private fun terminate() {
        Log.e("Pokemon", "terminated!!")
        write(Json { encodeDefaults = true }.encodeToString(pokemonList))
    }

    private fun write(data: String) {
        FileOutputStream(path).use { fos ->
            fos.write(JSONArray(data).toString(2).replace("\\/", "/").toByteArray())
        }
    }

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