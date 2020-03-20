package com.unciv.app.desktop.textRpg

import com.unciv.app.desktop.displayText

class RmGameInfo{
    val unit: Combatant
    var day = 1
    fun passDay(){
        day++
        displayText("-----------")
        displayText("Day $day")
        displayText("You are ${unit.hunger}")
        if (unit.health < unit.maxHealth) {
            displayText("You are injured. Health: " + unit.health)
        }
        if(unit.energy<100) displayText("You are tired. Energy: " + unit.energy)
    }

    init {
        unit = Combatant("Goblin")
        unit.abilities += MonsterGenerator.run.copy()
        unit.abilities += Ability("Punch", arrayListOf("Strength", "Body"), listOf("Damage=2", "Energy=1"))
        unit.abilities += Ability("Spear Thrust", arrayListOf("Strength", "Spear", "Accuracy"),
                listOf("Damage=10", "Energy=10", "Requires=Spear"))
        unit.abilities += Ability("Spear Throw", arrayListOf("Strength", "Spear", "Accuracy", "Ranged"),
                        listOf("Damage=15", "Energy=10", "Requires=Spear", "Ranged", "LoseRequired", "CauseStatus=Burdened"))
                .apply { experience=100 }
        unit.abilities += Ability("Stab", arrayListOf("Strength", "Dagger"), listOf("Damage=8", "Energy=5", "Requires=Dagger"))
                .apply { experience=10 }
        unit.items += Item("Pathetic wooden spear", "Spear", "Equip=Hands").apply { isEquipped=true }
        unit.status += sequenceOf("EquipSlot=Hands","EquipSlot=Chest")
        displayText("You wake up hungry again.")
        displayText("You are a Goblin, one of many in this cave.")
        displayText("But something feels different, now - you're confident that life is changing for the better.")
    }
}