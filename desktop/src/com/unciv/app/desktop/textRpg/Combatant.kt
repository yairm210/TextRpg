package com.unciv.app.desktop.textRpg

import com.unciv.app.desktop.displayText
import kotlin.math.min


enum class Hunger{
    Bloated,
    Full,
    Sated,
    Hungry,
    Starving,
}

open class Combatant(val name: String){
    var maxHealth = 100
    val abilities= ArrayList<Ability>()
    val items = ArrayList<Item>()
    val corpseLoot = ArrayList<Item>()
    val status = ArrayList<String>()
    var health = maxHealth
    var hunger = Hunger.Sated
    var energy = 100

    fun increaseHunger(){
        if(hunger== Hunger.Starving){
            displayText("You're dying of hunger!")
            health -= 40
            return
        }
        hunger = Hunger.values()[hunger.ordinal+1]
    }

    fun decreaseHunger(){
        if(hunger== Hunger.Bloated){
            displayText("You can't eat anymore!")
            return
        }
        hunger = Hunger.values()[hunger.ordinal-1]
        displayText("You are $hunger")
    }

    fun getUsableBattleAbilities() = abilities.filter { it.isBattleAbility() && canUse(it) }

    fun canUse(ability: Ability): Boolean {
        if (energy < ability.getRequiredEnergy(this)) return false
        if (!ability.hasRequiredItems(items)) return false
        if (ability.parameters.findParams("CannotUseWhen").any { status.contains(it) }) return false
        return true
    }

    fun healBy(amount:Int){
        health = min(health + amount, maxHealth)
    }

    fun addItem(item: Item){
        items.add(item)
        val equipSlot = item.equipSlot()
        if(canEquip(item) && items.none { it.isEquipped && it.equipSlot() == equipSlot }) { // auto-equip stuff we can
            displayText("You equip the ${item.name}")
            item.isEquipped = true
        }
    }

    fun canEquip(it: Item): Boolean {
        val equipSlot = it.equipSlot()
        if(equipSlot==null) return false
        return status.findParams("EquipSlot").contains(equipSlot)
    }

    fun getArmor(): Int {
        return (items + corpseLoot).map { it.parameters.findParams("Armor").map { it.toInt() }.sum() }.sum()
    }

}