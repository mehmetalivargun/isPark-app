package com.mehmetalivargun.parkfinderforispark.util


fun String.resolveToFee():String{
    val formatted =this.replace(';','\n')+'\n'
    return formatted
}
