package com.example.currencyconversion.api

import com.google.gson.JsonObject

data class FixerObject(
        val success: Boolean,
        val timestamp: Long,
        val base: String,
        val date: String,
        val rates: JsonObject
)