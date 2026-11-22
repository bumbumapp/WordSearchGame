package com.aar.app.wsp.data.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.aar.app.wsp.features.gamethemeselector.GameThemeItem
import com.aar.app.wsp.model.GameTheme
import io.reactivex.Flowable

@Dao
interface GameThemeDataSource {
    @Query("SELECT * FROM game_themes")
    suspend fun getThemeList(): List<GameTheme>

    /** Themes that have at least one word fitting a grid whose longer side is [maxChar], with that word count. */
    @Query("SELECT * FROM (SELECT *, (SELECT COUNT(*) FROM words WHERE game_theme_id=game_themes.id AND LENGTH(string) <= :maxChar) AS words_count FROM game_themes) WHERE words_count > 0")
    fun getThemeItemList(maxChar: Int): LiveData<List<GameThemeItem>>

    @Insert
    fun insertAll(gameThemes: List<GameTheme>)

    @Query("DELETE FROM game_themes")
    fun deleteAll()
}