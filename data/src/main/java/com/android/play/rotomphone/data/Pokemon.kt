package com.android.play.rotomphone.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Pokemon(
    @SerialName("id") val id: Int = 1,
    @SerialName("order") var order: Int = 1,
    @SerialName("name") var name: String = "",
    @SerialName("height") var height: Int = 0,
    @SerialName("weight") var weight: Int = 0,
    @SerialName("is_default") var isDefault: Boolean = false,
    @SerialName("types") var types: MutableList<String> = mutableListOf(),
    @SerialName("stats") var stats: MutableMap<String, Int> = mutableMapOf(),
    @SerialName("abilities") var abilities: MutableMap<String, Boolean> = mutableMapOf(),
    @SerialName("sprites") var sprites: MutableMap<String, String?> = mutableMapOf(),
    @SerialName("koname") var koName: String = "",
    @SerialName("color") var color: String = "",
    @SerialName("generation") var generation: String = "",
    @SerialName("genus") var genus: String = "",
    @SerialName("flavors") var flavors: MutableMap<String, String> = mutableMapOf(),
    @SerialName("evolution_chain_id") var evolutionChainId: Int = 0,
    @SerialName("varieties_chain_id") var varietiesChainId: Int = 0,
    @SerialName("varieties_chain") var varietiesChain: MutableList<Int> = mutableListOf(),
)