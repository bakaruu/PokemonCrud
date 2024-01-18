package com.pokecrud

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Pokemon(
    var id: String? = null,
    var name: String? = null,
    var number: Int? = null,
    var type: String? = null,
    var valueRating: Float? = null,
    var logo: String? = null,
    var date: String? = null


):Parcelable