package com.aar.app.wsp.features.gameover

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.app.NavUtils
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import com.aar.app.wsp.R
import com.aar.app.wsp.WordSearchApp
import com.aar.app.wsp.commons.DurationFormatter.fromInteger
import com.aar.app.wsp.commons.orZero
import com.aar.app.wsp.features.FullscreenActivity
import com.aar.app.wsp.features.gameplay.GamePlayActivity
import com.aar.app.wsp.features.mainmenu.MainMenuActivity
import com.aar.app.wsp.model.Difficulty
import com.aar.app.wsp.model.GameData
import com.aar.app.wsp.model.GameMode
import com.aar.app.wsp.preference.LevelPreference
import kotlinx.android.synthetic.main.activity_game_over.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class GameOverActivity : FullscreenActivity() {
    @JvmField
    @Inject
    var mViewModelFactory: ViewModelProvider.Factory? = null

    private lateinit var viewModel: GameOverViewModel
    lateinit var level: LevelPreference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)
        (application as WordSearchApp).appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, mViewModelFactory).get(GameOverViewModel::class.java)
        viewModel.onGameDataLoaded.observe(this) { gameData ->
            gameData?.let {
                showGameStat(it)
            }
        }
        viewModel.onGameDataReset.observe(this) { gameDataId ->
            val intent = Intent(this@GameOverActivity, GamePlayActivity::class.java)
            intent.putExtra(GamePlayActivity.EXTRA_GAME_DATA_ID, gameDataId)
            startActivity(intent)
            finish()
        }

        intent.extras?.getInt(EXTRA_GAME_ROUND_ID)?.let { gameId ->
            lifecycleScope.launch { viewModel.loadData(gameId) }
        }

        main_menu_btn.setOnClickListener {
            onBackPressed()
        }
        level= LevelPreference(this@GameOverActivity)
        if (level.getLevel("level")==5){
            btnReplay.visibility=View.GONE
            level.setLevel("level",1)
        }
        btnReplay.setOnClickListener {
            level.setLevel("level",level.getLevel("level")!!.plus(1))
            val intent = Intent(this@GameOverActivity, MainMenuActivity::class.java)
            intent.putExtra(MainMenuActivity.bool,true)
            intent.putExtra(MainMenuActivity.EXTRA_GAME_OVER_MODE_MAIN,extraGameMode)
            intent.putExtra(MainMenuActivity.EXTRA_COL_COUNT_MAIN,extraColumnCount)
            intent.putExtra(MainMenuActivity.EXTRA_ROW_COUNT_MAIN,extraRowCount)
            intent.putExtra(MainMenuActivity.EXTRA_GAME_THEME_ID_MAIN,extraGameThemeId)
            intent.putExtra(MainMenuActivity.EXTRA_GAME_DIFFICULTY_MAIN,extraDifficulty)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        goToMainMenu()
    }

    private fun goToMainMenu() {
        if (preferences.deleteAfterFinish()) {
            lifecycleScope.launch { viewModel.deleteGameRound() }
        }
        level.setLevel("level",1)
        NavUtils.navigateUpTo(this, Intent())
        finish()
    }

    private fun showGameStat(gd: GameData) {
        if (gd.isGameOver) {
            textCongrat.setText(R.string.lbl_game_over)
            game_stat_text.visibility = View.GONE
            btnReplay.visibility=View.GONE
        } else {
            val strGridSize = gd.grid!!.rowCount.toString() + " x " + gd.grid!!.colCount
            var str = getString(R.string.finish_text)
            str = str.replace(":gridSize".toRegex(), strGridSize)
            str = str.replace(":uwCount".toRegex(), gd.usedWords.size.toString())
            str = str.replace(":duration".toRegex(), fromInteger(gd.duration))
            textCongrat.setText(getString(R.string.congratulations))
            game_stat_text.visibility = View.VISIBLE
            game_stat_text.text = str
        }
    }
    private val extraGameMode: GameMode by lazy {
        (intent.extras?.get(GameOverActivity.EXTRA_GAME_OVER_MODE) as? GameMode) ?: GameMode.Normal
    }
    private val extraDifficulty: Difficulty by lazy {
        (intent.extras?.get(GameOverActivity.EXTRA_GAME_DIFFICULTY_OVER) as? Difficulty) ?: Difficulty.Easy
    }

    private val extraGameThemeId: Int by lazy {
        intent.extras?.getInt(GameOverActivity.EXTRA_GAME_THEME_ID_OVER).orZero()
    }


    private val extraRowCount: Int by lazy {
        intent.extras?.getInt(GameOverActivity.EXTRA_ROW_COUNT_OVER).orZero()
    }

    private val extraColumnCount: Int by lazy {
        intent.extras?.getInt(GameOverActivity.EXTRA_COL_COUNT_OVER).orZero()
    }


    companion object {
        const val EXTRA_GAME_ROUND_ID = "com.paperplanes.wsp.presentation.ui.activity.GameOverActivity"
        const val EXTRA_GAME_OVER_MODE="game_mode_over"
        const val EXTRA_GAME_DIFFICULTY_OVER = "game_max_duration_over"
        const val EXTRA_GAME_DATA_ID_OVER = "game_data_id_over"
        const val EXTRA_GAME_THEME_ID_OVER= "game_theme_id_over"
        const val EXTRA_ROW_COUNT_OVER= "row_count_over"
        const val EXTRA_COL_COUNT_OVER= "col_count_over"
    }
}