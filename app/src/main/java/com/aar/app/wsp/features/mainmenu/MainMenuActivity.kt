package com.aar.app.wsp.features.mainmenu

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import com.aar.app.wsp.R
import com.aar.app.wsp.commons.orZero
import com.aar.app.wsp.features.FullscreenActivity
import com.aar.app.wsp.features.gamehistory.GameHistoryActivity
import com.aar.app.wsp.features.gameover.GameOverActivity
import com.aar.app.wsp.features.gameplay.GamePlayActivity
import com.aar.app.wsp.features.gamethemeselector.ThemeSelectorActivity
import com.aar.app.wsp.features.settings.SettingsActivity
import com.aar.app.wsp.model.Difficulty
import com.aar.app.wsp.model.GameMode
import com.aar.app.wsp.model.GameTheme
import com.aar.app.wsp.preference.LevelPreference
import com.github.abdularis.horizontalspinner.HorizontalSelector.OnSelectedItemChanged
import kotlinx.android.synthetic.main.activity_main_menu.*

class MainMenuActivity : FullscreenActivity() {

    private val gameRoundDimValues: IntArray by lazy {
        resources.getIntArray(R.array.game_round_dimension_values)
    }
    lateinit var level:LevelPreference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        imageEnjoy.startAnimation(AnimationUtils.loadAnimation(this, R.anim.tag_enjoy))
        selectorGameMode.onSelectedItemChangedListener = object : OnSelectedItemChanged {
            override fun onSelectedItemChanged(newItem: String?) {
                if (newItem == getString(R.string.mode_count_down) || newItem == getString(R.string.mode_marathon)) {
                    selectorDifficulty.visibility = View.VISIBLE
                } else {
                    selectorDifficulty.visibility = View.GONE
                }
            }
        }
        btnSettings.setOnClickListener {
            goToSetting()
        }
        btnNewGame.setOnClickListener {
            newGame()
        }
        btnHistory.setOnClickListener {
            goToHistory()
        }
        level=LevelPreference(this@MainMenuActivity)
        if (shouldbeagain()){
            val intent = Intent(this@MainMenuActivity, GamePlayActivity::class.java)
            intent.putExtra(GamePlayActivity.EXTRA_GAME_DIFFICULTY, extraDifficulty)
            intent.putExtra(GamePlayActivity.EXTRA_GAME_MODE, extraGameMode)
            intent.putExtra(GamePlayActivity.EXTRA_GAME_THEME_ID, extraGameThemeId)
            intent.putExtra(GamePlayActivity.EXTRA_ROW_COUNT, extraColumnCount)
            intent.putExtra(GamePlayActivity.EXTRA_COL_COUNT, extraRowCount)
            startActivity(intent)
        }


    }
    private val extraGameMode: GameMode by lazy {
        (intent.extras?.get(MainMenuActivity.EXTRA_GAME_OVER_MODE_MAIN) as? GameMode) ?: GameMode.Normal
    }
    private fun shouldbeagain(): Boolean {
        return intent.extras?.containsKey(MainMenuActivity.bool) ?: false
    }
    private val extraDifficulty: Difficulty by lazy {
        (intent.extras?.get(MainMenuActivity.EXTRA_GAME_DIFFICULTY_MAIN) as? Difficulty) ?: Difficulty.Easy
    }

    private val extraGameThemeId: Int by lazy {
        intent.extras?.getInt(MainMenuActivity.EXTRA_GAME_THEME_ID_MAIN).orZero()
    }


    private val extraRowCount: Int by lazy {
        intent.extras?.getInt(MainMenuActivity.EXTRA_ROW_COUNT_MAIN).orZero()
    }

    private val extraColumnCount: Int by lazy {
        intent.extras?.getInt(MainMenuActivity.EXTRA_COL_COUNT_MAIN).orZero()
    }

    private fun goToSetting() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun newGame() {
        val dim = gridSizeDimension
        val intent = Intent(this, ThemeSelectorActivity::class.java)
        intent.putExtra(ThemeSelectorActivity.EXTRA_ROW_COUNT, dim)
        intent.putExtra(ThemeSelectorActivity.EXTRA_COL_COUNT, dim)
        startActivityForResult(intent, 100)
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
    }

    private fun goToHistory() {
        val i = Intent(this, GameHistoryActivity::class.java)
        startActivity(i)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            startNewGame(
                data!!.getIntExtra(ThemeSelectorActivity.EXTRA_THEME_ID, GameTheme.NONE.id)
            )
        }
    }

    private fun startNewGame(gameThemeId: Int) {
        val dim = gridSizeDimension
        level.setLevel("level",1)
        val intent = Intent(this@MainMenuActivity, GamePlayActivity::class.java)
        intent.putExtra(GamePlayActivity.EXTRA_GAME_DIFFICULTY, difficultyFromSpinner)
        intent.putExtra(GamePlayActivity.EXTRA_GAME_MODE, gameModeFromSpinner)
        intent.putExtra(GamePlayActivity.EXTRA_GAME_THEME_ID, gameThemeId)
        intent.putExtra(GamePlayActivity.EXTRA_ROW_COUNT, dim)
        intent.putExtra(GamePlayActivity.EXTRA_COL_COUNT, dim)
        startActivity(intent)
    }

    private val gameModeFromSpinner: GameMode
        get() {
            if (selectorGameMode.currentValue != null) {
                return when (selectorGameMode.currentValue) {
                    getString(R.string.mode_hidden) -> GameMode.Hidden
                    getString(R.string.mode_count_down) -> GameMode.CountDown
                    getString(R.string.mode_marathon) -> GameMode.Marathon
                    else -> GameMode.Normal
                }
            }
            return GameMode.Normal
        }

    private val difficultyFromSpinner: Difficulty
        get() {
            if (selectorDifficulty.currentValue != null) {
                return when (selectorDifficulty.currentValue) {
                    getString(R.string.diff_easy) -> Difficulty.Easy
                    getString(R.string.diff_medium) -> Difficulty.Medium
                    else -> Difficulty.Hard
                }
            }
            return Difficulty.Easy
        }

    private val gridSizeDimension: Int
        get() = gameRoundDimValues[selectorGridSize.currentIndex]

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
    companion object {
        const val bool = "again"
        const val EXTRA_GAME_OVER_MODE_MAIN="game_mode_over_main"
        const val EXTRA_GAME_THEME_ID_MAIN= "game_theme_id_main"
        const val EXTRA_ROW_COUNT_MAIN = "row_count_main"
        const val EXTRA_COL_COUNT_MAIN= "col_count_main"
        const val EXTRA_GAME_DIFFICULTY_MAIN = "game_max_duration_main"

    }
}