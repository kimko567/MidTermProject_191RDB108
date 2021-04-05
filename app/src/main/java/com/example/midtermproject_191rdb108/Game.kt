package com.example.midtermproject_191rdb108

import android.app.AlertDialog
import android.app.Dialog
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

object Game {

    // An array that contains all game winning sequences.
    private var wins = arrayOf(arrayOf(1, 2, 3), arrayOf(1, 4, 7), arrayOf(1, 5, 9),
            arrayOf(2, 5, 8), arrayOf(3, 6, 9), arrayOf(3, 5, 7), arrayOf(4, 5, 6), arrayOf(7, 8, 9))

    // Value that ensures that every turn changes between O and X.
    private var O_X: Boolean = true

    // Value that counts exectued turns.
    private var turn: Int = 0

    // Value that determines if the game is pvp ot pvc.
    var pvp: Boolean = false

    // Value that determines if in case of pvc, the player starts the game.
    var start: Boolean = true

    var player1Name: String = "Player1"
    var player2Name: String = "Player2"

    /**
     * Returns -2 if input() failed and move was not registered.
     * Returns position which was changed if input() succeeds.
     */
    fun playerMove(position: Int): Int {

        // Checks if input() failed.
        if(!input(position))
            return -2
        return position
    }


    /**
     * Executes a move with the best possible value.
     * Returns the position that that was chosen.
     */
    fun computerMove(): Int{

        // Assigns a position that either wins the game or blocks the enemy from winning.
        val mustMakeMove = getMustMakeMove()

        // Checks if there is a move that has to be made in order to win or not lose.
        if (mustMakeMove != -2) {
            val tmp = mustMakeMove
            input(tmp)
            return tmp
        }

        // Inputs the best possible move if there is no move that has to be taken in order to win or not lose.
        else {
            val tmp = getBestMove()
            input(tmp)
            return tmp
        }
    }


    /**
     * Expects a parameter of type Int. This function tries to change given parameter(position) that is present in wins to 0 (O) if O_X or -1 (X) if !O_X.
     * Returns true if succeeds to input given position on board.
     * Returns false if fails to input given position on board.
     */
    fun input(position: Int): Boolean {

        // Checks if position is already taken by a previous move.
        if (position == -1 || position == 0 || !wins.flatten().contains(position))
            return false

        // Loops through all possible values in wins and changes the given position values accordinly to O_X.
        for (dim1 in 0..7)
            for (dim2 in 0..2)
                if (wins[dim1][dim2] == position)
                    if (O_X)
                        wins[dim1][dim2] = 0
                    else
                        wins[dim1][dim2] = -1

        // Ensures that the next move is different from previous.
        O_X = O_X == false
        turn++

        return true
    }

    /**
     * Expects array of type Int and a value of type Boolean.
     * Checks if given array needs tobe chacked for a winning or losing sequence based on given Boolean value.
     * Returns false if given array does not need to be checked.
     * Returns true if given array needs to be checked.
     */
    fun checkDull(arr: Array<Int>, lookForSelf: Boolean): Boolean {
        if ((O_X == lookForSelf && arr.contains(-1)) || (!O_X == lookForSelf && arr.contains(0)))
            return false
        return true
    }

    /**
     * Expects an array of type Int.
     * Returns a value of type Int that is unique in the array.
     */
    fun findUnique(arr: Array<Int>): Int {
        if (arr[0] == arr[1])
            return arr[2]
        if (arr[0] == arr[2])
            return arr[1]
        if (arr[1] == arr[2])
            return arr[0]
        return -2
    }


    /**
     * Returns a value of type Int as position that must be chosen.
     * Prioritizes winning, if does not find a next move winning sequence, looks for potential next opponent's move losing sequence.
     */
    fun getMustMakeMove(): Int {

        // Checks if there is a position that wins the game.
        for (item in wins) {
            if (oneCountOf(if (O_X) 0 else -1, item) == 2)
                if (findUnique(item) != -1 && findUnique(item) != 0)
                    return findUnique(item)
        }

        // Checks if there is a posiotion that might lose the game in the upcoming opponent's turn.
        for (item in wins) {
            if (oneCountOf(if (O_X) -1 else 0, item) == 2)
                if (findUnique(item) != -1 && findUnique(item) != 0)
                    return findUnique(item)
        }

        // Returns -2 if there is no must make move.
        return -2
    }

    /**
     * Expects an array of arrays of type Int.
     * Counts if there are at least 2 potential next move winning sequences.
     * Returns a value of Boolean - true if the given array is considered dangerous / potentially game losing, false if the given array is not considered dangerous.
     */
    fun checkIfDangerousPosition(array: Array<Array<Int>>): Boolean{
        var count = 0
        for (item in array) {
            if (oneCountOf(if (O_X) -1 else 0, item) == 2)
                count++
        }
        if (count >= 2)
            return true
        return false
    }


    /**
     * Returns a copy array of wins array.
     */
    fun copyWins(): Array<Array<Int>>{
        var copy = arrayOf(arrayOf(1, 2, 3), arrayOf(1, 4, 7), arrayOf(1, 5, 9),
                arrayOf(2, 5, 8), arrayOf(3, 6, 9), arrayOf(3, 5, 7), arrayOf(4, 5, 6), arrayOf(7, 8, 9))
        for (dim1 in 0..7){
            for (dim2 in 0..2){
                copy[dim1][dim2] = wins[dim1][dim2]
            }
        }
        return copy
    }

    /**
     * Returns value of type Int as position on game board (in wins array) which is calculated to be a potentially game losing if not chosen in current turn.
     */
    fun getDangerousPosition(): Int{

        // Loops through all possible positions on board.
        for (position in 1..9) {

            // Checks if position has not been already taken.
            if (wins.flatten().contains(position)){

                // Create a copy of current wins array.
                var copyOfWins = copyWins()

                // Loops through all sequences in copOfWins array.
                for (dim1 in 0..7)
                    for (dim2 in 0..2)

                        // Inputs the position with opponents value (O) or (X).
                        if (copyOfWins[dim1][dim2] == position)
                            if(O_X)
                                copyOfWins[dim1][dim2] = -1
                            else
                                copyOfWins[dim1][dim2] = 0

                // Checks if copyOfWins is a dangerous, potentially game losing.
                if (checkIfDangerousPosition(copyOfWins))
                    return position
            }
        }

        return -2
    }

    /**
     * Expects value of type Int to look for and a value of type Boolean that determines if checks for 0 (O) when true or -1 (X) when false.
     * Returns the count of possible winning moves for given position.
     */
    fun fullCountOf(position: Int, lookForSelf: Boolean): Int {
        var count = 0
        for (winningSequence in wins)
            if (checkDull(winningSequence, lookForSelf))
                count += oneCountOf(position, winningSequence)
        return count
    }

    /**
     * Expects value of type Int to look for and and array of type Int.
     * Returns the count of the given Int value in the given array.
     */
    fun oneCountOf(valueToLookFor: Int, array: Array<Int>): Int {
        var count = 0
        for (item in array)
            if (valueToLookFor == item)
                count++
        return count
    }

    /**
     * Returns the best possible move based on the count of possible wins in respective position.
     */
    fun getBestMove(): Int {

        // Count of possible wins.
        var maxWinPatternCount = -2

        // Count of blocked winning patterns. This comes in use when there are multiple positions with equal maxPos.
        var maxBlockPatternCount = -2

        // Best position.
        var bestPosition = -2

        // Checks if there is a position that would ensure a win for the oponent in the upcoming turn. If there is, then returns the position.
        if (getDangerousPosition() != -2){
            bestPosition = getDangerousPosition()
            return bestPosition
        }

        // Loops through possible positions to get the best one.
        for (position in 1..9) {

            // Checks if positions is not already taken.
            if(wins.flatten().contains(position)) {

                // Gets count of potential winning sequences of current position in loop.
                val currentPositionWinsCount = fullCountOf(position, true)

                // Gets count of potential blocked winning sequences of current position in loop.
                val currentPosiotionBlockCount = fullCountOf(position, false)

                // Checks if current position in loop has more potential winning sequences than the currently known best position.
                if (maxWinPatternCount < currentPositionWinsCount) {
                    maxWinPatternCount = currentPositionWinsCount
                    maxBlockPatternCount = currentPosiotionBlockCount
                    bestPosition = position
                }

                // Checks if a position in loop that has equal potential winning sequences to the currently known
                // has more potential blocked winning sequences than the currently known best position.
                if (maxWinPatternCount == currentPositionWinsCount) {
                    if (maxBlockPatternCount < currentPosiotionBlockCount) {
                        maxWinPatternCount = currentPositionWinsCount
                        maxBlockPatternCount = currentPosiotionBlockCount
                        bestPosition = position
                    }

                    // If a move has equal values, this checks a random value if it should change the best position or not.
                    else if(DoOrDont()){
                        maxWinPatternCount = currentPositionWinsCount
                        maxBlockPatternCount = currentPosiotionBlockCount
                        bestPosition = position
                    }
                }
            }

        }

        return bestPosition
    }


    /**
     * Returns true if the game contains a winning sequence.
     * Returns false if the game does not contain a winning sequence.
     */
    fun checkWin(): Boolean {

        // Loops through all possible winning moves.
        for(item in wins)

            // Checks if a winning move has three equal 0 (O) or -1 (X).
            if (oneCountOf(0, item) == 3 || oneCountOf(-1, item) == 3)
                return true

        return false
    }

    /**
     * turn getter.
     * Returns turn.
     */
    fun getTurn(): Int{
        return turn
    }

    /**
     * O_X getter.
     * Returns O_X.
     */
    fun getO_X(): Boolean{
        return O_X
    }

    /**
     * Returns an informing message about the outcome of the game no matter the game mode.
     */
    fun endGameMessage(): String{

        // Checks if one of the players won the game.
        if (checkWin()) {

            // If game is set to player vs player.
            if (pvp)

            // If player one does last move.
                if (!O_X)
                    return "$player1Name wins!!!"

                // If player 2 does last move.
                else
                    return "$player2Name wins!!!"

            // If game is set to player vs computer
            else

            // If player does the first move.
                if (start)

                // If player does the last move.
                    if (!O_X)
                        return "You win!!!"

                    // If computer does the last move.
                    else
                        return "You lose!!!"

                // If computer does teh first move.
                else

                // If player does the last move.
                    if (O_X)
                        return "You win!!!"

                    // If computer does the last move.
                    else
                        return "You lose!!!"
        }

        // In case the game was not won by any players.
        else
            return "Draw!!!"

    }

    /**
     * Returns a greeting message string depending on game's parameters.
     */
    fun playerGreeting(): String{

        // If game is set to player vs player.
        if(pvp)
            return "Hello there $player1Name and $player2Name!"

        // If game is set to player vs compuetr.
        else

            // If player moves first.
            if(start)
                return "Hello there $player1Name!"

            // If player moves second.
            else
                return "Hello there $player2Name!"
    }

    /**
     * Changes Player1Name value to the given value.
     */
    fun changePlayer1Name(newName: String?){
        if(!newName.isNullOrBlank())
            player1Name = newName
    }

    /**
     * Changes Player2Name value to the given value.
     */
    fun changePlayer2Name(newName: String?){
        if(!newName.isNullOrBlank())
            player2Name = newName
    }

    /**
     * In case there are multiple moves with the same value, this function ensures randomness.
     * Returns true or false which in other functions determines if current move should be changed to another equal move.
     */
    fun DoOrDont(): Boolean{
        return if((0..1).random() == 1) true else false
    }

    /**
     * Resets the game, so that it can be played again after draw or win, or lose without resetting names.
     */
    fun reset(){
        wins = arrayOf(arrayOf(1, 2, 3), arrayOf(1, 4, 7), arrayOf(1, 5, 9),
                arrayOf(2, 5, 8), arrayOf(3, 6, 9), arrayOf(3, 5, 7), arrayOf(4, 5, 6), arrayOf(7, 8, 9))
        O_X = true
        turn = 0
    }

    fun fullReset(){
        changePlayer1Name("Player1")
        changePlayer2Name("Player2")
        wins = arrayOf(arrayOf(1, 2, 3), arrayOf(1, 4, 7), arrayOf(1, 5, 9),
                arrayOf(2, 5, 8), arrayOf(3, 6, 9), arrayOf(3, 5, 7), arrayOf(4, 5, 6), arrayOf(7, 8, 9))
        O_X = true
        turn = 0
    }
}
