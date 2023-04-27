package com.android.play.rotomphone.data.entity

import kotlinx.serialization.Serializable

@Serializable
data class Pokemon(
    val id: Int,
    val name: String,
    val koname: String,
)