package com.unciv.app.desktop

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.FloatAction
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.sun.jmx.remote.internal.ArrayQueue
import com.unciv.app.desktop.textRpg.AsyncAction
import com.unciv.app.desktop.textRpg.RmBaseStateAsync
import com.unciv.app.desktop.textRpg.RmGameInfo
import com.unciv.ui.utils.RmBaseScreen
import com.unciv.ui.utils.onClick
import com.unciv.ui.utils.setFontColor
import com.unciv.ui.utils.setFontSize
import java.util.concurrent.ConcurrentLinkedQueue

class RmScreen: RmBaseScreen(){
    val choiceTable = Table()
    val labelTable = Table()
    val labelScroll = ScrollPane(labelTable)

    val q=ConcurrentLinkedQueue<String>()

    fun displayText(text:String) {
        val label = Label(text, skin).apply { setWrap(true) }
                .setFontColor(Color.WHITE)
        labelTable.add(label).width(stage.width / 3).row()
        labelTable.pack()
        labelScroll.layout()

        labelScroll.addAction(object : FloatAction(0f, 1f, 0.3f) {
            // Here it's the same, only the Y axis is inverted - when at 0 we're at the top, not bottom - so we invert it back.
            val originalScrollY = labelScroll.scrollPercentY
            override fun update(percent: Float) {
                labelScroll.scrollPercentY = 1 * percent + originalScrollY * (1 - percent)
                labelScroll.updateVisualScroll()
            }
        })
    }

    init {
        stage.addActor(labelScroll)
        labelTable.pad(10f)
        labelTable.defaults().pad(10f)
        choiceTable.defaults().pad(10f)
        stage.addActor(choiceTable)

        labelScroll.width = stage.width / 2
        labelScroll.height = stage.height

        RmGame.displayText = { q.add(it) }
        stage.addAction(Actions.forever(Actions.sequence(
                Actions.run { if(q.isNotEmpty()) displayText(q.remove()) },
                Actions.delay(0.3f))))
        val rmGameInfo = RmGameInfo()
        update(RmBaseStateAsync(rmGameInfo).choices())
    }

    fun update(choices:List<AsyncAction>){

        choiceTable.clear()
        for(choice in choices){
            val choiceButton = TextButton(choice.name, skin)
            choiceButton.onClick{ update(choice.action()) }
            choiceTable.add(choiceButton).row()
        }
        choiceTable.pack()
        choiceTable.width = stage.width/2
        choiceTable.height = stage.height
        choiceTable.x = stage.width/2
    }
}