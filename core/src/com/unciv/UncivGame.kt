package com.unciv

import com.badlogic.gdx.Application
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import com.unciv.ui.utils.*

class UncivGame(
        val version: String
) : Game() {

    /**
     * This exists so that when debugging we can see the entire map.
     * Remember to turn this to false before commit and upload!
     */
    var viewEntireMapForDebug = false
    /** For when you need to test something in an advanced game and don't have time to faff around */
    val superchargedForDebug = false

    /** Simulate until this turn on the first "Next turn" button press.
     *  Does not update World View changes until finished.
     *  Set to 0 to disable.
     */
    val simulateUntilTurnForDebug: Int = 0

    var rewriteTranslationFiles = false


    var music: Music? = null
    val musicLocation = "music/thatched-villagers.mp3"
    var isInitialized = false



    override fun create() {
        Gdx.input.setCatchKey(Input.Keys.BACK, true)
        if (Gdx.app.type != Application.ApplicationType.Desktop) {
            viewEntireMapForDebug = false
            rewriteTranslationFiles = false
        }
        Current = this


        // If this takes too long players, especially with older phones, get ANR problems.
        // Whatever needs graphics needs to be done on the main thread,
        // So it's basically a long set of deferred actions.
        screen = LoadingScreen()
    }


    fun setScreen(screen: CameraStageBaseScreen) {
        Gdx.input.inputProcessor = screen.stage
        super.setScreen(screen)
    }



    // Maybe this will solve the resume error on chrome OS, issue 322? Worth a shot
    override fun resize(width: Int, height: Int) {
        resume()
    }

    companion object {
        lateinit var Current: UncivGame
        fun isCurrentInitialized() = this::Current.isInitialized
    }
}

class LoadingScreen:CameraStageBaseScreen() {
    init {
        val happinessImage = ImageGetter.getImage("StatIcons/Happiness")
        happinessImage.center(stage)
        happinessImage.setOrigin(Align.center)
        happinessImage.addAction(Actions.sequence(
                Actions.delay(1f),
                Actions.rotateBy(360f, 0.5f)))
        stage.addActor(happinessImage)
    }
}