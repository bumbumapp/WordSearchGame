package com.aar.app.wsp.preference

import android.content.Context

class LevelPreference(context: Context) {
    private var SHARD_PREFENCE="Shared"

    val prefence=context.getSharedPreferences(SHARD_PREFENCE,Context.MODE_PRIVATE)

    fun getLevel(key:String): Int? {
        return prefence.getInt(key,1)
    }
    fun setLevel(key:String,st:Int){
        val editor=prefence.edit()
        editor.putInt(key, st)
        editor.apply()
    }

}