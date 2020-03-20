package com.unciv.app.desktop

import com.badlogic.gdx.Files
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.tools.texturepacker.TexturePacker
import com.unciv.UncivGame
import com.unciv.app.desktop.textRpg.Action
import com.unciv.app.desktop.textRpg.AsyncAction
import com.unciv.app.desktop.textRpg.RmBaseStateAsync
import com.unciv.app.desktop.textRpg.RmGameInfo
import com.unciv.ui.utils.RmBaseScreen
import com.unciv.ui.utils.setFontSize
import java.io.File
import java.lang.Thread.sleep
import kotlin.concurrent.thread
import kotlin.system.exitProcess

fun displayText(string:String){
    RmGame.displayText(string)
}


class RmGame(): Game() {

    fun chooseAction(actions: List<String>): Int {
        for ((i, action) in actions.withIndex()) {
            val placement = i+1
            com.unciv.app.desktop.displayText("$placement - $action")
        }
        while (true) {
            val read = readLine()
            if (read == null) continue
            try {
                val chosenIndex = read.toInt() -1
                if (chosenIndex !in actions.indices) throw Exception()
                return chosenIndex
            } catch (ex: Exception) {
                com.unciv.app.desktop.displayText("Not a valid choice!")
            }
        }
    }

    fun chooseAndActivateAction(actions:List<AsyncAction>): List<AsyncAction> {
        val chosenActionIndex = chooseAction(actions.map { it.name })
        val chosenAction = actions[chosenActionIndex]
        return chosenAction.action()
    }

    fun runTextBasedGame(){
        val gameInfo = RmGameInfo()
        var choices = RmBaseStateAsync(gameInfo).choices()
        while(choices.any()) choices = chooseAndActivateAction(choices)
    }

    override fun create() {
        if(true){
            runTextBasedGame()
            exitProcess(0)
        }
        else screen = RmScreen()
    }

    companion object{
        var displayText:(String)->Unit = { println(it) }
    }

    override fun resize(width: Int, height: Int) {
        screen.resize(width,height)
        super.resize(width, height)
    }
}

internal object DesktopLauncher {

    @JvmStatic
    fun main(arg: Array<String>) {

        if(true) {
            LwjglApplication(RmGame())
            return
        }

        packImages()

        val config = LwjglApplicationConfiguration()
        // Don't activate GL 3.0 because it causes problems for MacOS computers
        config.addIcon("ExtraImages/Icon.png", Files.FileType.Internal)
        config.title = "Unciv"
        config.useHDPI = true

        val versionFromJar = DesktopLauncher.javaClass.`package`.specificationVersion

        val game = UncivGame(if (versionFromJar != null) versionFromJar else "Desktop")

        LwjglApplication(game, config)
    }

    private fun packImages() {
        val startTime = System.currentTimeMillis()

        val settings = TexturePacker.Settings()
        // Apparently some chipsets, like NVIDIA Tegra 3 graphics chipset (used in Asus TF700T tablet),
        // don't support non-power-of-two texture sizes - kudos @yuroller!
        // https://github.com/yairm210/UnCiv/issues/1340
        settings.maxWidth = 2048
        settings.maxHeight = 2048
        settings.combineSubdirectories = true
        settings.pot = true
        settings.fast = true

        // This is so they don't look all pixelated
        settings.filterMag = Texture.TextureFilter.MipMapLinearLinear
        settings.filterMin = Texture.TextureFilter.MipMapLinearLinear

        if (File("../Images").exists()) // So we don't run this from within a fat JAR
            TexturePacker.process(settings, "../Images", ".", "game")

        // pack for mods as well
        val modDirectory = File("mods")
        if(modDirectory.exists()) {
            for (mod in modDirectory.listFiles()!!){
                TexturePacker.process(settings, mod.path + "/Images", mod.path, "game")
            }
        }

        val texturePackingTime = System.currentTimeMillis() - startTime
        println("Packing textures - "+texturePackingTime+"ms")
    }
}
