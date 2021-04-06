package com.example.midtermproject_191rdb108

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.first)
    }

    /**
     * Changes content view to pvp set up layout.
     * Expect value of android.view.View.
     */
    fun pvpSetUp(view: android.view.View){
        Game.pvp = true
        setContentView(R.layout.enternamepvp)
    }

    /**
     * Changes content view to pvc set up layout.
     * Expects value of android.view.View.
     */
    fun pvcSetUp(view: android.view.View){
        Game.pvp = false
        setContentView(R.layout.enternamepvc)
    }

    /**
     * Starts the game based on previously chosen parameters.
     * Changes content view to game board layout.
     * Expects value of android.view.View.
     */
    fun startGame(view: android.view.View){
        // Resets game.
        Game.reset()

        // Sets both player names accordingly to previously taken actions
        setPlayerNames()

        // Sets content view to where the game board is.
        setContentView(R.layout.game_board)

        // Shows a greeting dialog.
        greetingDialog(view)

        // Ensures the correct player names are displayed.
        findViewById<TextView>(R.id.name1).setText(Game.player1Name)
        findViewById<TextView>(R.id.name2).setText(Game.player2Name)

        // Changes background accordingly.
        changeBackground()

        // Makes a move if the first turn is a computers turn.
        if (!Game.pvp && !Game.start)
            computerMove(view, 3000)
    }

    /**
     * Exectues a turn and chages all the values accordignly.
     * Expects android.view.View.
     */
    fun Turn(view: View){

        // Checks if game is pvp.
        if(Game.pvp) {

            // Gets the tag of the view that triggered this function.
            val position: Int = view.getTag().toString().toInt()

            // Checks if player move succeded.
            if (Game.playerMove(position) != -2){

                // Changes the view to display O or X accordingly.
                inputOorX(view)

                // Changes the background to display which player should make next move.
                changeBackground()
            }

            // Checks if game has ended.
            if(checkIfGameEnd(view))
                return
        }

        // Code to execute if game is pvc.
        else{

            // Checks which player should do or has done the first move.
            if (Game.getO_X() == Game.start) {

                // Gets the tag of the view that triggered this function.
                val pos: Int = view.getTag().toString().toInt()

                // Checks if player move succeded.
                if(Game.playerMove(pos) != -2) {

                    // Changes the view to display O or X accordingly.
                    inputOorX(view)

                    // Changes the background to display which player should make next move.
                    changeBackground()


                    // Checks if game has ended.
                    if (checkIfGameEnd(view))
                        return

                    // Executes computer turn.
                    computerMove(view, 500)

                }
            }
        }

    }

    /**
     * Set the player names accordingly.
     */
    fun setPlayerNames(){

        // Creates a variable to store text from TextInputFields.
        var viewText: Editable? = null

        // Sets names if game is set to pvp.
        if(Game.pvp){

            // Sets player 1 name.
            viewText = findViewById<TextInputLayout>(R.id.pvpnameinput1)?.editText?.text
            Game.changePlayer1Name(if (viewText.isNullOrBlank()) null else viewText.toString())

            // Sets player 2 name.
            viewText = findViewById<TextInputLayout>(R.id.pvpnameinput2)?.editText?.text
            Game.changePlayer2Name(if (viewText.isNullOrBlank()) null else viewText.toString())
        }

        // Set names if game is set to pvc.
        else{
            viewText = findViewById<TextInputLayout>(R.id.pvcenternameinput)?.editText?.text

            // Sets names accordingly if player does first move.
            if(Game.start) {
                Game.changePlayer1Name(if (viewText.isNullOrBlank()) "Player" else viewText.toString())
                Game.changePlayer2Name("Computer")
            }

            // Sets names accordingly if computer does first move.
            else {
                Game.changePlayer2Name(if (viewText.isNullOrBlank()) "Player" else viewText.toString())
                Game.changePlayer1Name("Computer")
            }
        }
    }

    /**
     * Exectues a computer move and changes the content view accordingly.
     * Expect value of android.view.View and value of type Long that determines how long is the computer move dealyed before being executed.
     */
    fun computerMove(view: View, delayTime: Long){
        var computerMoveHandler: Handler = Handler(android.os.Looper.myLooper()!!)
        var computerMoveRunnable: Runnable = Runnable {
            val computerMovePosition: Int = Game.computerMove()
            if (!Game.getO_X()) {
                findViewById<View>(R.id.board).findViewWithTag<View>(computerMovePosition.toString()).setBackgroundResource(R.drawable.o)
                findViewById<View>(R.id.board).findViewWithTag<View>(computerMovePosition.toString()).setTag(0)
            }
            else {
                findViewById<View>(R.id.board).findViewWithTag<View>(computerMovePosition.toString()).setBackgroundResource(R.drawable.x)
                findViewById<View>(R.id.board).findViewWithTag<View>(computerMovePosition.toString()).setTag(-1)
            }
            changeBackground()
            checkIfGameEnd(view)
        }
        computerMoveHandler.postDelayed(computerMoveRunnable, delayTime)
    }

    /**
     * Changes which player starts in case of pvc.
     */
    fun changeStart(view: View){
        Game.start = Game.start == false
    }

    /**
     * Checks if the game has ended.
     * Expects android.view.View on which it should call end game dialog.
     */
    fun checkIfGameEnd(view: View): Boolean {

        // Checks if one of the players won.
        if (Game.checkWin()) {
            onGameEndDialog(view)
            return true
        }

        // Checks if all of the turns have been done.
        else if (Game.getTurn() >= 9) {
            onGameEndDialog(view)
            return true
        }

        return false
    }

    /**
     * Inputs O or X values into android.view.View value that triggers this function.
     */
    fun inputOorX(view: View){

        // Checks if O should be put.
        if (!Game.getO_X()) {
            view.setBackgroundResource(R.drawable.o)
            view.setTag(0.toString())
        }

        // Cheks if X should be put.
        else{
            view.setBackgroundResource(R.drawable.x)
            view.setTag((-1).toString())
        }
    }

    /**
     * Changes background that acts as an indicator on which players move is currently expected.
     */
    fun changeBackground(){

        // Expecting O move.
        if(Game.getO_X()) {
            findViewById<TextView>(R.id.name1).setBackgroundColor(Color.GREEN)
            findViewById<TextView>(R.id.name2).setBackgroundColor(Color.WHITE)
        }

        // Expecting X move.
        else{
            findViewById<TextView>(R.id.name1).setBackgroundColor(Color.WHITE)
            findViewById<TextView>(R.id.name2).setBackgroundColor(Color.GREEN)
        }
    }

    /**
     * Creates and show Alert dialog when the game ends.
     * Expects android.view.View on top of which show the dialog.
     */
    fun onGameEndDialog(view: View) {

        // Creates builder for the dialog.
        val builder = AlertDialog.Builder(view.context)

        // Sets the given message to show on the dialog.
        builder.setMessage(Game.endGameMessage())

        // Creates a positive button that resets the game.
        builder.setPositiveButton("Retry") { _,_ ->
            setContentView(R.layout.game_board)
            startGame(view)
        }

        // Creates a neutral button that takes back to home screen.
        builder.setNeutralButton("Menu") { _,_ ->
            setContentView(R.layout.first)
            Game.fullReset()
        }

        // Enforces clicking only on the two buttons on the dialog.
        builder.setCancelable(false)

        // Shows the dialog.
        builder.show()
    }

    /**
     * Shows a greeting dialog based on games parameters.
     */
    fun greetingDialog(view: View){

        // Creates the builder for the dialog.
        var builder = AlertDialog.Builder(view.context)

        // Sets the message for the dialog.
        builder.setMessage(Game.playerGreeting())

        // Assigns builder created dialog to a variable.
        var dialog = builder.show()

        // Ensures that dialog disappears after given time.
        var dialogDismissHandler: Handler = Handler(android.os.Looper.myLooper()!!)
        var dismissRunnable: Runnable = Runnable {
            dialog.dismiss()
        }
        dialogDismissHandler.postDelayed(dismissRunnable, 2000)
    }


}