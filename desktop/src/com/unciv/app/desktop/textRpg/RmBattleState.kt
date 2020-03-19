package com.unciv.app.desktop.textRpg

import com.unciv.app.desktop.displayText


class RmBattleState(val player: Combatant, val enemy: Combatant): State() {
    fun attack(attacker:Combatant,defender:Combatant, attack:Ability): Int {
        val attackDamage = attack.calculateDamage(attacker) - defender.getArmor()
        defender.health -= attackDamage
        attacker.energy -= attack.getRequiredEnergy(attacker)
        attack.experience += 10
        if(attack.parameters.contains("LoseRequired")){
            val itemsLost = arrayListOf<Item>()
            for(requiredItem in attack.getRequiredItems()){
                val matchingItems = attacker.items.filter { it.isEquipped && it.parameters.contains(requiredItem) }
                if(matchingItems.size==1) itemsLost += matchingItems.first()
                else{
                    // need to choose which one
                    TODO()
                }
            }
            for(item in itemsLost){ // throw a spear, it's now in your enemy, and maybe he can use it
                attacker.items -= item
                defender.items += item
            }
        }
        defender.status += attack.parameters.findParams("CauseStatus")
        return attackDamage
    }

    override fun nextState(rmGameInfo: RmGameInfo): State {
        displayText("Your health: ${player.health}")
        displayText("Your energy: ${player.energy}")

        val playerActions = player.getUsableBattleAbilities()
        val chosenActionIndex = chooseAction(playerActions.map { it.name + " (${it.getRequiredEnergy(player)} energy)" })
        val chosenAction = playerActions[chosenActionIndex]
        if (chosenAction.parameters.contains("Escape")) {
            player.energy -= chosenAction.getRequiredEnergy(player)
            displayText("You managed to escape!")
            return RmBaseState()
        }

        val playerAttackDamage = attack(player, enemy, chosenAction)
        print("You use ${chosenAction.name} for $playerAttackDamage damage! ")
        if (enemy.health > 0) displayText("${enemy.name} has ${enemy.health} health!")
        else displayText("${enemy.name} has been defeated!")
        if (youWin()) return PostBattleState(player,enemy)

        val enemyUsableAbilities = enemy.getUsableBattleAbilities()
        if (enemy.health < 25 && enemyUsableAbilities.any { it.parameters.contains("Escape") }) {
            val escapeAbility = enemyUsableAbilities.first { it.parameters.contains("Escape") }
            displayText("Enemy ${enemy.name} is escaping with ${escapeAbility.name}!")
            val rangedActions = player.getUsableBattleAbilities().filter { it.parameters.contains("Ranged") }
            if(rangedActions.isNotEmpty()){
                chooseAndActivateAction(rangedActions.map { Action(it.name) {
                    attack(player, enemy, it)
                    print("You use ${chosenAction.name} for $playerAttackDamage damage! ")
                    if (enemy.health > 0) displayText("${enemy.name} has ${enemy.health} health!")
                    else displayText("${enemy.name} has been defeated!")
                }},true)
                if (youWin()) return PostBattleState(player,enemy)
            }
            if(enemy.canUse(escapeAbility)) {
                print("${enemy.name} has escaped!")
                return RmBaseState()
            }
            else displayText("Enemy ${enemy.name} can no longer ${escapeAbility.name}!")
        }
        else {
            val enemyAttack = enemyUsableAbilities.filterNot { it.parameters.contains("Escape") }.random()
            val enemyAttackDamage = attack(enemy, player, enemyAttack)
            displayText("${enemy.name} used ${enemyAttack.name} for $enemyAttackDamage damage!")
            if (enemyWins()) {
                displayText("You died!")
                player.health = player.maxHealth
                return DefeatState()
            }
        }
        return this
    }

    private fun youWin() = enemy.health <= 0
    private fun enemyWins() = player.health <= 0
}

class PostBattleState(val player:Combatant,val enemy: Combatant):State() {
    override fun nextState(rmGameInfo: RmGameInfo): State {
        displayText("You defeated the ${enemy.name}!")

        if (enemy.items.isNotEmpty()) {
            displayText("From the body you take: ")
            for (item in enemy.items)
                displayText(" * " + item.name)
        }

        if (enemy.corpseLoot.isNotEmpty()) {
            displayText("From the corpse you gather: ")
            for (item in enemy.corpseLoot)
                displayText(" * " + item.name)
        }
        for (item in enemy.items + enemy.corpseLoot) {
            item.isEquipped = false
            player.addItem(item)
        }

        return RmBaseState()
    }

}