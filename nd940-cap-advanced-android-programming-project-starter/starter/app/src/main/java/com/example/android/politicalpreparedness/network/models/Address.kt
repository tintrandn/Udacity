package com.example.android.politicalpreparedness.network.models

data class Address(
    var line1: String? = null,
    var line2: String? = null,
    var city: String,
    var state: String,
    var zip: String
) {
    fun toFormattedString(): String {
        var output = if (!line1.isNullOrEmpty()) line1.plus("\n") else "\n"
        if (!line2.isNullOrEmpty()) output = output.plus(line2).plus("\n")
        output = output.plus("$city, $state $zip")
        return output
    }
}