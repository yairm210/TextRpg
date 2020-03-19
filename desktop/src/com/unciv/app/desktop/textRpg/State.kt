package com.unciv.app.desktop.textRpg

import com.unciv.app.desktop.displayText

class Action(val name:String, val action:()->Unit)
class AsyncAction(val name:String, val action:()->List<AsyncAction>)

abstract class State {
    abstract fun nextState(rmGameInfo: RmGameInfo): State

    fun chooseAndActivateAction(actions:List<Action>, addExitAction:Boolean=false){
//        if(actions.size==1){
//            actions.first().action()
//            return
//        }
        var allActions = actions
        if(addExitAction) allActions = allActions + Action("Exit"){}
        val chosenAction = chooseAction(allActions.map { it.name })
        allActions[chosenAction].action()
    }

    fun chooseAction(actions: List<String>): Int {
        for ((i, action) in actions.withIndex()) {
            val placement = i+1
            displayText("$placement - $action")
        }
        while (true) {
            val read = readLine()
            if (read == null) continue
            try {
                val chosenIndex = read.toInt() -1
                if (chosenIndex !in actions.indices) throw Exception()
                return chosenIndex
            } catch (ex: Exception) {
                displayText("Not a valid choice!")
            }
        }
    }
}

class VictoryState(): State() {
    override fun nextState(rmGameInfo: RmGameInfo): State {
        displayText("You beat the entire game!")
        return this
    }
}
class DefeatState(): State() {
    override fun nextState(rmGameInfo: RmGameInfo): State {
        displayText("You Lose, loser!")
        return this
    }
}