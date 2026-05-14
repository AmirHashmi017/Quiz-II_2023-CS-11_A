package com.example.a2023_cs_11_a_mad_quiz_2

import com.google.gson.annotations.SerializedName

data class Country(
    @SerializedName("name") val name: CountryName,
    @SerializedName("cca2") val cca2: String
)

data class CountryName(
    @SerializedName("common") val common: String
)
