package com.unciv.app.desktop.textRpg

import kotlin.math.log10
import kotlin.math.max

data class Item(val name:String, val parameters: ArrayList<String>, var isEquipped: Boolean = false) {

    fun isEquippable() = equipSlot() != null
    fun equipSlot()  = parameters.findParams("Equip").firstOrNull()

    constructor(name:String, vararg params:String):this(name, arrayListOf()){
        parameters += params
    }

    fun times(amount:Int): ArrayList<Item> {
        val list = arrayListOf<Item>()
        for(i in 1..amount) list += Item(name,parameters)
        return list
    }
}

fun List<String>.findParams(param:String): List<String> {
    return this.filter { it.startsWith("$param=") }.map { it.removePrefix("$param=") }
}

data class Ability(val name:String, val experienceClasses: ArrayList<String>, val parameters:List<String>) {
    var experience = 1000

    init {
        experienceClasses.add(name)
    }

    fun calculateExpBonus(player: Combatant): Double {
        var expBonus = 1.0
        val experienceClassToExp = HashMap<String,Int>()
        for (ability in player.abilities)
            for (experienceClass in ability.experienceClasses)
                if (experienceClass in experienceClasses) {
                    if(!experienceClassToExp.containsKey(experienceClass))
                        experienceClassToExp[experienceClass]=0
                    experienceClassToExp[experienceClass] = experienceClassToExp[experienceClass]!! + ability.experience
                }
        for(experienceClassExp in experienceClassToExp.values)
            expBonus *= log10(max(experienceClassExp.toDouble()/100,2.0)) // 1000 is the 'base' amount of XP, making its bonus *1
        return expBonus
    }

    fun isAttack() = parameters.findParams("Damage").any()
    fun getRequiredEnergy(combatant: Combatant): Int {
        val energyParam = parameters.findParams("Energy").firstOrNull()
        if (energyParam == null) return 0
        var baseEnergy = energyParam.toDouble()
        val expBonus = calculateExpBonus(combatant)
        if(expBonus>1) baseEnergy /= expBonus
        return baseEnergy.toInt()
    }
    fun isBattleAbility() = isAttack() || parameters.contains("Escape")
    private fun getBaseDamage() = parameters.findParams("Damage").first().toInt()

    fun calculateDamage(player: Combatant): Int {
        return (getBaseDamage() * calculateExpBonus(player)).toInt()
    }

    fun getRequiredItems() = parameters.findParams("Requires")

    fun hasRequiredItems(items: ArrayList<Item>): Boolean {
        for(requiredItem in getRequiredItems())
            if(items.none { it.isEquipped && requiredItem in it.parameters })
                return false
        return true
    }

    fun getAbilityLevel():String{
        return when {
            experience < 100 -> "Bad"
            experience < 300 -> "Mediocre"
            experience < 1000 -> "Okay"
            experience < 3000 -> "Good"
            experience < 10000 -> "Great"
            else -> "Amazing"
        }
    }

}