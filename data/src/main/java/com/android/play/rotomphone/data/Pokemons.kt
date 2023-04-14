package com.android.play.rotomphone.data

import android.content.Context
import android.os.Handler
import android.util.Log
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.FileReader

@Serializable
data class Pokemon(
    @SerialName("id") val id: Int = 1,
    @SerialName("order") var order: Int = 1,
    @SerialName("name") var name: String = "",
    @SerialName("height") var height: Int = 0,
    @SerialName("weight") var weight: Int = 0,
    @SerialName("is_default") var isDefault: Boolean = false,

)

@Serializable
data class PokemonListResult(
    @SerialName("results") val results: List<MutableMap<String, String>>)

class Pokemons {

    private val pokemonIdList = mutableListOf<Int>()
    private val varietyIdList = mutableListOf<Int>()

    private var order = 1 // counting....
    private var count = 10
    private var resultList = JSONArray()
    private var varietiesChainId = 0
    private var varietiesChainMap = mutableMapOf<Int, Int>()
    private var language = "ko"

    private var path = ""

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder().baseUrl("https://pokeapi.co")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }

    private val service: PokemonService by lazy {
        retrofit.create(PokemonService::class.java)
    }

    fun getIdFromUrl(url: String?): Int? {
        url?.let { it ->
            "[0-9]+/$".toRegex().find(it)?.let { result ->
                return result.value.replace("/", "").toInt()
            }
        }
        return null
    }

    fun createRawPokemonJson(context: Context) {
        getPokemonList(context)
    }

    fun getPokemonList(context: Context) {

        path = "${context.filesDir}/pokemon.json"
        Log.e("Pokemon", "path=$path")

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
                        if (pokemonIdList.isNotEmpty())
                            get(pokemonIdList[0])

                    }
                } else
                    TODO("Not yet implemented")
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    fun get(id: Int) {

        var result = JSONObject()

        retrofit.create(PokemonService::class.java).get(id).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {

                    var data = Json { ignoreUnknownKeys = true }.decodeFromString<Pokemon>(response.body()!!)
                    Log.e("Pokemon", data.toString())

                    val response = JSONObject(response.body()!!)

                    Log.e("Pokemon", "count=$count, pokemonIdList.size=${pokemonIdList.size}")

                    data.order = order++

                    val typeList = JSONArray()
                    val types = response.optJSONArray("types")
                    for (i in 0 until types.length()) {
                        typeList.put(types.optJSONObject(i).optJSONObject("type").optString("name"))
                    }
                    result.put("types", typeList) // put type JsonArray

                    val map = JSONObject()
                    val stats = response.optJSONArray("stats")
                    for (i in 0 until stats.length()) {
                        val name = stats.optJSONObject(i).optJSONObject("stat").optString("name")
                        val value = stats.optJSONObject(i).optInt("base_stat")
                        map.put(name, value)
                    }
                    result.put("stats", map) // put stats JsonArray

                    val abilityMap = JSONObject()
                    val abilities = response.optJSONArray("abilities")
                    for (i in 0 until abilities.length()) {
                        val name = abilities.optJSONObject(i).optJSONObject("ability").optString("name")
                        val hidden = abilities.optJSONObject(i).optBoolean("is_hidden")
                        abilityMap.put(name, hidden)
                    }
                    result.put("abilities", abilityMap) // put stats JsonArray

                    val spriteMap = JSONObject()
                    response.optJSONObject("sprites")?.optJSONObject("other")?.let {
                        it.optJSONObject("home")?.let { home ->
                            spriteMap.put("home", home.optString("front_default"))
                        }
                        it.optJSONObject("official-artwork")?.let { artwork ->
                            spriteMap.put("artwork", artwork.optString("front_default"))
                        }
                    }
                    result.put("sprites", spriteMap)

                    var speciesId: Int? = null
                    response.optJSONObject("species")?.let {
                        speciesId = getIdFromUrl(it.optString("url"))
                    }

                    if (id == speciesId) {
                        getSpecies(result, id.toString())
                    } else if (varietyIdList.contains(id)) {
                        response.optJSONArray("forms")?.let {
                            for (i in 0 until it.length()) {
                                if (response.optString("name") == it.optJSONObject(i).optString("name")) {
                                    var formId = getIdFromUrl(it.optJSONObject(i).optString("url"))
                                    getForm(result, id, formId.toString())
                                }
                            }
                        }
                    } else {
                        resultList.put(result)
                        Log.e("Pokemon", result.toString())
                        nextPokemon(id)
                    }
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    fun nextPokemon(currentPokemonId: Int) {
        count--
        varietyIdList.remove(currentPokemonId)
        pokemonIdList.remove(currentPokemonId)
        if (count <= 0) {
            terminate()
        } else if (varietyIdList.isNotEmpty()) {
            Handler().postDelayed({
                get(varietyIdList[0])
            }, 2000)
        } else if (pokemonIdList.isNotEmpty()) {
            Handler().postDelayed({
                get(pokemonIdList[0])
            }, 2000)
        } else {
            terminate()
        }
    }

    fun getSpecies(result: JSONObject, id: String) {
        service.getSpecies(id).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val json = JSONObject(response.body()!!)

                    val color = json.optJSONObject("color").optString("name")
                    result.put("color", color)

                    var name = ""
                    val names = json.optJSONArray("names")
                    for (i in 0 until names.length()) {
                        if (names.optJSONObject(i).optJSONObject("language").optString("name") == language) {
                            name = names.optJSONObject(i).optString("name")
                            break
                        }
                    }
                    result.put("koname", name)

                    var generation = json.optJSONObject("generation").optString("name")
                    result.put("generation", generation)

                    var genus = ""
                    val genera = json.optJSONArray("genera")
                    for (i in 0 until names.length()) {
                        if (genera.optJSONObject(i).optJSONObject("language").optString("name") == language) {
                            genus = genera.optJSONObject(i).optString("genus")
                            break
                        }
                    }
                    result.put("genus", genus)

                    val flavorMap = JSONObject()
                    val flavors = json.optJSONArray("flavor_text_entries")
                    for (i in 0 until flavors.length()) {
                        if (flavors.optJSONObject(i).optJSONObject("language").optString("name") == language) {
                            val text = flavors.optJSONObject(i).optString("flavor_text")
                            val version = flavors.optJSONObject(i).optJSONObject("version").optString("name")
                            flavorMap.put(version, text.replace("\n", " "))
                        }
                    }
                    result.put("description", flavorMap)

                    val chain = getIdFromUrl(json.optJSONObject("evolution_chain").optString("url")) // 진화-chain
                    result.put("evolution_id", chain)

                    json.optJSONArray("varieties")?.let { it ->
                        if (it.length() > 1) {
                            val varieties = JSONObject()
                            val varietiesChain = JSONArray()

                            varietiesChainId++
                            for (i in 0 until it.length()) {
                                val id = getIdFromUrl(it.getJSONObject(i).optJSONObject("pokemon").optString("url"))
                                varietyIdList.add(id!!)
                                varietiesChainMap[id!!] = varietiesChainId
                                varietiesChain.put(id)
                            }
                            result.put("varieties_id", varietiesChainId)
                            result.put("varieties", varietiesChain)
                        }
                    }

                    //is_default = true

                    Log.e("Pokemon", result.toString())

                    resultList.put(result)
                    nextPokemon(id.toInt())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    fun getForm(result: JSONObject, id: Int, formId: String) {
        val retrofit = Retrofit.Builder().baseUrl("https://pokeapi.co")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()

        //https://pokeapi.co/api/v2/pokemon-form/10133/ -- formid 가 다르구나.
        retrofit.create(PokemonService::class.java).getForm(formId).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    var json = JSONObject(response.body()!!)
                    json.optJSONArray("form_names")?.let { forms ->

                        for (i in 0 until forms.length()) {
                            if (language == forms.optJSONObject(i).optJSONObject("language")?.optString("name")) {
                                result.put("koname", forms.optJSONObject(i).optString("name"))
                            }
                        }
                    }

                    resultList.put(result)
                    Log.e("Pokemon", result.toString())
                    nextPokemon(id)
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    fun terminate() {
        Log.e("Pokemon", "terminated!!")
        Log.e("Pokemon", varietiesChainMap.toString())
        Log.e("Pokemon", resultList.toString())
        write(resultList)
    }

    // ??
    private fun write(data: JSONArray) {
        FileOutputStream(path).use { fos ->
            fos.write(data.toString(2).replace("\\/", "/").toByteArray())
        }
    }

    // ??
    private fun read(path: String): String {
        BufferedReader(FileReader(path)).use { reader ->
            var line: String?
            val b = StringBuilder()
            while (reader.readLine().also { line = it } != null) {
                b.append(line)
            }
            return b.toString()
        }
    }
}

interface PokemonService {
    @GET("api/v2/pokemon?limit=2000")
    fun getList(): Call<String>

    @GET("api/v2/pokemon/{id}")
    fun get(@Path("id") id: Int): Call<String>

    @GET("api/v2/pokemon-species/{id}")
    fun getSpecies(@Path("id") id: String): Call<String>

    @GET("api/v2/pokemon-form/{id}")
    fun getForm(@Path("id")id: String): Call<String>
}