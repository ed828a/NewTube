package com.example.edward.newtube.util


/**
 * Created by Edward on 6/22/2018.
 */

fun String.extractDate(): String {
    val stringArray = this.split('T')

    return stringArray[0]
}