package com.unciv.ui.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align

object ImageGetter {
    private const val whiteDotLocation = "OtherIcons/whiteDot"

    // When we used to load images directly from different files, without using a texture atlas,
    // The draw() phase of the main screen would take a really long time because the BatchRenderer would
    // always have to switch between like 170 different textures.
    // So, we now use TexturePacker in the DesktopLauncher class to pack all the different images into single images,
    // and the atlas is what tells us what was packed where.
    var atlas = TextureAtlas("game.atlas")

    // We then shove all the drawables into a hashmap, because the atlas specifically tells us
    //   that the search on it is inefficient
    val textureRegionDrawables = HashMap<String,TextureRegionDrawable>()

    init{
        setTextureRegionDrawables()
    }


    fun setTextureRegionDrawables(){
        textureRegionDrawables.clear()
        // These are the drawables from the base game
        for(region in atlas.regions){
            val drawable =TextureRegionDrawable(region)
            textureRegionDrawables[region.name] = drawable
        }

    }

    fun refreshAtlas() {
        atlas.dispose() // To avoid OutOfMemory exceptions
        atlas = TextureAtlas("game.atlas")
        setTextureRegionDrawables()
    }

    fun getWhiteDot() =  getImage(whiteDotLocation)
    fun getDot(dotColor: Color) = getWhiteDot().apply { color = dotColor}

    fun getExternalImage(fileName:String): Image {
        return Image(TextureRegion(Texture("ExtraImages/$fileName")))
    }

    fun getImage(fileName: String): Image {
        return Image(getDrawable(fileName))
    }

    private fun getDrawable(fileName: String): TextureRegionDrawable {
        if(textureRegionDrawables.containsKey(fileName)) return textureRegionDrawables[fileName]!!
        else return textureRegionDrawables[whiteDotLocation]!!
    }

    fun getRoundedEdgeTableBackground(tintColor: Color?=null): Drawable? {
        val drawable = getDrawable("OtherIcons/civTableBackground")
        drawable.minHeight=0f
        drawable.minWidth=0f
        if(tintColor==null) return drawable
        return drawable.tint(tintColor)
    }


    fun imageExists(fileName:String) = textureRegionDrawables.containsKey(fileName)
    fun techIconExists(techName:String) = imageExists("TechIcons/$techName")

    fun getStatIcon(statName: String): Image {
        return getImage("StatIcons/$statName")
                .apply { setSize(20f,20f)}
    }

    fun getUnitIcon(unitName:String,color:Color= Color.BLACK):Image{
        return getImage("UnitIcons/$unitName").apply { this.color=color }
    }

    fun nationIconExists(nation:String) = imageExists("NationIcons/$nation")
    fun getNationIcon(nation:String) = getImage("NationIcons/$nation")

    val foodCircleColor =  colorFromRGB(129, 199, 132)
    val productionCircleColor = Color.BROWN.cpy().lerp(Color.WHITE,0.5f)!!
    val goldCircleColor = Color.GOLD.cpy().lerp(Color.WHITE,0.5f)!!

    fun getPromotionIcon(promotionName:String): Actor {
        var level = 0

        when {
            promotionName.endsWith(" I") -> level=1
            promotionName.endsWith(" II") -> level=2
            promotionName.endsWith(" III") -> level=3
        }

        val basePromotionName = if(level==0) promotionName
        else promotionName.substring(0, promotionName.length-level-1)

        if(imageExists("UnitPromotionIcons/$basePromotionName")) {
            val icon = getImage("UnitPromotionIcons/$basePromotionName")
            icon.color = colorFromRGB(255,226,0)
            val circle = icon.surroundWithCircle(30f)
            circle.circle.color = colorFromRGB(0,12,49)
            if(level!=0){
                val starTable = Table().apply { defaults().pad(2f) }
                for(i in 1..level) starTable.add(getImage("OtherIcons/Star")).size(8f)
                starTable.centerX(circle)
                starTable.y=5f
                circle.addActor(starTable)
            }
            return circle
        }
        return getImage("UnitPromotionIcons/" + promotionName.replace(' ', '_') + "_(Civ5)")
    }

    fun getBlue() = Color(0x004085bf)

    fun getCircle() = getImage("OtherIcons/Circle")
    fun getTriangle() = getImage("OtherIcons/Triangle")

    fun getBackground(color:Color): Drawable {
        val drawable = getDrawable("OtherIcons/TableBackground")
        drawable.minHeight=0f
        drawable.minWidth=0f
        return drawable.tint(color)
    }


    fun getProgressBarVertical(width:Float,height:Float,percentComplete:Float,progressColor:Color,backgroundColor:Color): Table {
        val advancementGroup = Table()
        val completionHeight = height * percentComplete
        advancementGroup.add(getImage(whiteDotLocation).apply { color = backgroundColor })
                .size(width, height - completionHeight).row()
        advancementGroup.add(getImage(whiteDotLocation).apply { color = progressColor }).size(width, completionHeight)
        advancementGroup.pack()
        return advancementGroup
    }

    fun getHealthBar(currentHealth: Float, maxHealth: Float, healthBarSize: Float): Table {
        val healthPercent = currentHealth / maxHealth
        val healthBar = Table()

        val healthPartOfBar = getWhiteDot()
        healthPartOfBar.color = when {
            healthPercent > 2 / 3f -> Color.GREEN
            healthPercent > 1 / 3f -> Color.ORANGE
            else -> Color.RED
        }
        healthBar.add(healthPartOfBar).size(healthBarSize * healthPercent, 5f)

        val emptyPartOfBar = getDot(Color.BLACK)
        healthBar.add(emptyPartOfBar).size(healthBarSize * (1 - healthPercent), 5f)

        healthBar.pad(1f)
        healthBar.pack()
        healthBar.background = getBackground(Color.BLACK)
        return healthBar
    }

    fun getLine(startX:Float,startY:Float,endX:Float,endY:Float, width:Float): Image {
        /** The simplest way to draw a line between 2 points seems to be:
         * A. Get a pixel dot, set its width to the required length (hypotenuse)
         * B. Set its rotational center, and set its rotation
         * C. Center it on the point where you want its center to be
         */

        // A
        val line = getWhiteDot()
        val deltaX = (startX-endX).toDouble()
        val deltaY = (startY-endY).toDouble()
        line.width = Math.sqrt(deltaX*deltaX+deltaY*deltaY).toFloat()
        line.height = width // the width of the line, is the height of the

        // B
        line.setOrigin(Align.center)
        val radiansToDegrees = 180 / Math.PI
        line.rotation = (Math.atan2(deltaY, deltaX) * radiansToDegrees).toFloat()

        // C
        line.x = (startX+endX)/2 - line.width/2
        line.y = (startY+endY)/2 - line.height/2

        return line
    }

}
