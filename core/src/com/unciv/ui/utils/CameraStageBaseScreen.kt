package com.unciv.ui.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.unciv.UncivGame
import kotlin.concurrent.thread

open class RmBaseScreen:Screen {
    var stage: Stage

    init {
        stage = Stage(ExtendViewport(900f, 600f), batch)
        Gdx.input.inputProcessor = stage
    }

    override fun hide() {}
    override fun show() {}

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.act()
        stage.draw()
    }

    override fun pause() {}
    override fun resume() {}
    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }
    override fun dispose() {}

    companion object {
        var skin = Skin(Gdx.files.internal("skin/flat-earth-ui.json"))
        internal var batch: Batch = SpriteBatch()
    }
}

open class CameraStageBaseScreen : Screen {

    var game: UncivGame = UncivGame.Current
    var stage: Stage


    init {
        stage = Stage(ExtendViewport(900f,600f), batch)
    }

    override fun show() {}

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.act()
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun pause() {}

    override fun resume() {}

    override fun hide() {}

    override fun dispose() {}

    companion object {
        var skin = Skin(Gdx.files.internal("skin/flat-earth-ui.json"))

        fun resetFonts(){
//            skin.get(TextButton.TextButtonStyle::class.java).font = Fonts().getFont(45).apply { data.setScale(20/45f) }
//            skin.get(CheckBox.CheckBoxStyle::class.java).font= Fonts().getFont(45).apply { data.setScale(20/45f) }
//            skin.get(Label.LabelStyle::class.java).apply {
//                font = Fonts().getFont(45).apply { data.setScale(18/45f) }
//                fontColor= Color.WHITE
//            }
//            skin.get(TextField.TextFieldStyle::class.java).font = Fonts().getFont(45).apply { data.setScale(18/45f) }
//            skin.get(SelectBox.SelectBoxStyle::class.java).font = Fonts().getFont(45).apply { data.setScale(20/45f) }
//            skin.get(SelectBox.SelectBoxStyle::class.java).listStyle.font = Fonts().getFont(45).apply { data.setScale(20/45f) }
//            skin.get(CheckBox.CheckBoxStyle::class.java).fontColor= Color.WHITE
        }
        internal var batch: Batch = SpriteBatch()
    }

    /** It returns the assigned [InputListener] */
    fun onBackButtonClicked(action:()->Unit): InputListener {
        val listener = object : InputListener(){
            override fun keyDown(event: InputEvent?, keycode: Int): Boolean {
                if(keycode == Input.Keys.BACK){
                    action()
                    return true
                }
                return false
            }
        }
        stage.addListener( listener )
        return listener
    }

}


fun Button.disable(){
    touchable= Touchable.disabled
    color= Color.GRAY
}
fun Button.enable() {
    color = Color.WHITE
    touchable = Touchable.enabled
}


fun colorFromRGB(r: Int, g: Int, b: Int): Color {
    return Color(r/255f, g/255f, b/255f, 1f)
}

fun Actor.centerX(parent:Actor){ x = parent.width/2 - width/2 }
fun Actor.centerY(parent:Actor){ y = parent.height/2- height/2}
fun Actor.center(parent:Actor){ centerX(parent); centerY(parent)}

fun Actor.centerX(parent:Stage){ x = parent.width/2 - width/2 }
fun Actor.centerY(parent:Stage){ y = parent.height/2- height/2}
fun Actor.center(parent:Stage){ centerX(parent); centerY(parent)}


fun Actor.onClick(function: () -> Unit): Actor {
    this.addListener(object : ClickListener() {
        override fun clicked(event: InputEvent?, x: Float, y: Float) {
            function()
        }
    })
    return this
}

fun Actor.surroundWithCircle(size:Float,resizeActor:Boolean=true): IconCircleGroup {
    return IconCircleGroup(size,this,resizeActor)
}

fun Actor.addBorder(size:Float,color:Color,expandCell:Boolean=false):Table{
    val table = Table()
    table.pad(size)
    table.background = ImageGetter.getBackground(color)
    val cell = table.add(this)
    if (expandCell) cell.expand()
    cell.fill()
    table.pack()
    return table
}

fun Table.addSeparator(): Cell<Image> {
    row()
    val image = ImageGetter.getWhiteDot()
    val cell = add(image).colspan(columns).height(2f).fill()
    row()
    return cell
}

fun Table.addSeparatorVertical(): Cell<Image> {
    val image = ImageGetter.getWhiteDot()
    val cell = add(image).width(2f).fillY()
    return cell
}

/**
 * Solves concurrent modification problems - everyone who had a reference to the previous arrayList can keep using it because it hasn't changed
 */
fun <T> ArrayList<T>.withItem(item:T): ArrayList<T> {
    val newArrayList = ArrayList(this)
    newArrayList.add(item)
    return newArrayList
}

/**
 * Solves concurrent modification problems - everyone who had a reference to the previous arrayList can keep using it because it hasn't changed
 */
fun <T> HashSet<T>.withItem(item:T): HashSet<T> {
    val newHashSet = HashSet(this)
    newHashSet.add(item)
    return newHashSet
}

/**
 * Solves concurrent modification problems - everyone who had a reference to the previous arrayList can keep using it because it hasn't changed
 */
fun <T> ArrayList<T>.withoutItem(item:T): ArrayList<T> {
    val newArrayList = ArrayList(this)
    newArrayList.remove(item)
    return newArrayList
}


/**
 * Solves concurrent modification problems - everyone who had a reference to the previous arrayList can keep using it because it hasn't changed
 */
fun <T> HashSet<T>.withoutItem(item:T): HashSet<T> {
    val newHashSet = HashSet(this)
    newHashSet.remove(item)
    return newHashSet
}

/** also translates */
fun String.toLabel() = Label(this,CameraStageBaseScreen.skin)

// We don't want to use setFontSize and setFontColor because they set the font,
//  which means we need to rebuild the font cache which means more memory allocation.
fun String.toLabel(fontColor:Color= Color.WHITE, fontSize:Int=18): Label {
    var labelStyle = CameraStageBaseScreen.skin.get(Label.LabelStyle::class.java)
    if(fontColor!= Color.WHITE || fontSize!=18) { // if we want the default we don't need to create another style
        labelStyle = Label.LabelStyle(labelStyle) // clone this to another
        labelStyle.fontColor = fontColor
//        if (fontSize != 18) labelStyle.font = Fonts().getFont(45)
    }
    return Label(this,labelStyle)//.apply { setFontScale(fontSize/45f) }
}


fun Label.setFontColor(color:Color): Label {style=Label.LabelStyle(style).apply { fontColor=color }; return this}

fun Label.setFontSize(size:Int,language:String?=null): Label {
    style = Label.LabelStyle(style)
//    style.font = if(language==null) Fonts().getFont(45) else Fonts().getFont(45,language)
    style = style // because we need it to call the SetStyle function. Yuk, I know.
    return this.apply { setFontScale(size/45f) } // for chaining
}