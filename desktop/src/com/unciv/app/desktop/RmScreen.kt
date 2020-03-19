package com.unciv.app.desktop

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.unciv.app.desktop.textRpg.AsyncAction
import com.unciv.app.desktop.textRpg.RmBaseStateAsync
import com.unciv.app.desktop.textRpg.RmGameInfo
import com.unciv.ui.utils.RmBaseScreen
import com.unciv.ui.utils.onClick
import com.unciv.ui.utils.setFontColor
import com.unciv.ui.utils.setFontSize

class RmScreen: RmBaseScreen(){
    val choiceTable = Table()
    val labelTable = Table()
    val labelScroll = ScrollPane(labelTable)

    init {
        stage.addActor(labelScroll)
        labelTable.pad(10f)
        labelTable.defaults().pad(10f)
        stage.addActor(choiceTable)

        labelScroll.width = stage.width / 2
        labelScroll.height = stage.height

        RmGame.displayText = {
                val label = Label(it, skin).apply { setWrap(true) }
//                        .setFontSize(25, "English")
                        .setFontColor(Color.WHITE)
                labelTable.add(label).width(stage.width / 3).row()
                labelScroll.layout()
                labelScroll.scrollPercentY = 1f
                labelScroll.updateVisualScroll()

        }
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