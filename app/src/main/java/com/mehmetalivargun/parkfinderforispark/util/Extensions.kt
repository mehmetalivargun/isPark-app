package com.mehmetalivargun.parkfinderforispark.util

fun String.resolveToFee():List<String>{
    val seperated = this.split(';')
    val result = arrayListOf<String>()
    seperated.forEach {
        result.addAll(it.split(':'))
    }

    return result

}