package com.example.guessmydraw.utilities;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

/**
 * ViewModel used to save all the data needed to allow the proper execution of a match
 */
public class GameViewModel extends AndroidViewModel {

    private boolean isMyTurnToDraw;
    private boolean groupOwnerFlag;

    // flag indicating whether an AckMessage was received
    // in the current round, used to indicate that the opponent has
    // correctly received the word they are to guess
    private boolean ackMessageFlag;

    // flag indicating whether the opponent is ready to receive
    // the drawing, then to indicate that you can start drawing
    private boolean startDrawFlag;

    // flag indicating whether the next DrawMessage is the first in the round
    private boolean firstDrawMessageFlag;

    // flag indicating whether in the current round
    // the player has requested to end the game
    private boolean endRequestFlag;

    // flag used to indicate that the word was guessed in the current round
    private boolean wordGuessedFlag;

    private float scorePlayerOne;
    private float scorePlayerTwo;
    private float lastBonus;
    private int roundNumber;

    // number of requests saved to end the game
    private int endGameRequests;

    private String playersName;
    private String opponentsName;
    private String opponentAddress;
    private String choosenWord;

    // Used to save the drawing bitmap in the current state
    private Bitmap bitmap;

    public GameViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * initializes the values of the viewModel
     */
    public void init(){
        groupOwnerFlag = false;
        isMyTurnToDraw = false;
        firstDrawMessageFlag = true;
        endRequestFlag = false;
        ackMessageFlag = false;
        startDrawFlag = false;
        wordGuessedFlag = false;
        scorePlayerOne = 0;
        scorePlayerTwo = 0;
        roundNumber = 0;
        endGameRequests = 0;
        lastBonus = 0;
        opponentsName = null;
        opponentAddress = null;
        choosenWord = null;
        bitmap = null;
    }

    /**
     * resets the viewModel values to prepare for the next round
     */
    public void setNextRound(){
        Log.d("DEBUG", "NEXT ROUND !!!");
        roundNumber++;
        firstDrawMessageFlag = true;
        endRequestFlag = false;
        ackMessageFlag = false;
        startDrawFlag = false;
        wordGuessedFlag = false;
        choosenWord = null;
        bitmap = null;
    }

    /**
     * incrise the end game requests
     * @return current end game request number
     */
    public int askToEndGame() {
        endGameRequests++;
        return endGameRequests;
    }


    //region setter and getter method

    public int getRoundNumber() {
        return roundNumber;
    }

    public int getEndGameRequests() {
        return endGameRequests;
    }

    public String getPlayersName() {
        return playersName;
    }

    public void setPlayersName(String playersName) {
        this.playersName = playersName;
    }

    public String getOpponentsName() {
        return opponentsName;
    }

    public void setOpponentsName(String opponentsName) {
        this.opponentsName = opponentsName;
    }

    public boolean isWordGuessedFlag() {
        return wordGuessedFlag;
    }

    public void setWordGuessedFlag(boolean wordGuessedFlag) {
        this.wordGuessedFlag = wordGuessedFlag;
    }

    public float getLastBonus() {
        return lastBonus;
    }

    private void setLastBonus(float lastBonus) {
        this.lastBonus = lastBonus;
    }

    public float getScorePlayerOne() {
        return scorePlayerOne;
    }

    public void updateScorePlayerOne(float remainingSeconds) {
        float bonus = remainingSeconds/50;
        setLastBonus(bonus);
        scorePlayerOne = scorePlayerOne + 1 + bonus;
    }

    public float getScorePlayerTwo() {
        return scorePlayerTwo;
    }

    public void updateScorePlayerTwo(float remainingSeconds) {
        float bonus = remainingSeconds/50;
        setLastBonus(bonus);
        scorePlayerTwo = scorePlayerTwo + 1 + bonus;
    }

    public String getChoosenWord() {
        return choosenWord;
    }

    public void setChoosenWord(String choosenWord) {
        this.choosenWord = choosenWord;
    }

    public void setOpponentAddress(@NonNull String address){
        opponentAddress = address;
    }

    public String getOpponentAddress() {
        return opponentAddress;
    }

    public void setGroupOwnerFlag(boolean b){
        groupOwnerFlag = b;
    }

    public boolean getGroupOwnerFlag(){
        return groupOwnerFlag;
    }

    public void setMyTurnToDraw(boolean myTurnToDraw) {
        isMyTurnToDraw = myTurnToDraw;
    }

    public boolean getMyTurnToDraw() {
        return isMyTurnToDraw;
    }

    public boolean getIsFirstMsg() {
        return firstDrawMessageFlag;
    }

    public void setIsFirstMsg(boolean value) {
        firstDrawMessageFlag = value;
    }

    public boolean isEndRequestFlag() {
        return endRequestFlag;
    }

    public void setEndRequestFlag(boolean endRequestFlag) {
        this.endRequestFlag = endRequestFlag;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public boolean isAckMessageFlag() { return ackMessageFlag; }

    public void setAckMessageFlag(boolean ackMessageFlag) {
        this.ackMessageFlag = ackMessageFlag;
    }

    public boolean isStartDrawFlag() {
        return startDrawFlag;
    }

    public void setStartDrawFlag(boolean startDrawFlag) {
        this.startDrawFlag = startDrawFlag;
    }

    //end region

    @Override
    public String toString() {
        return "GameViewModel{" +
                "groupOwnerFlag=" + groupOwnerFlag + "\n" +
                ", isMyTurnToDraw=" + isMyTurnToDraw + "\n" +
                ", isFirstMsg=" + firstDrawMessageFlag + "\n" +
                ", myEndRequest=" + endRequestFlag + "\n" +
                ", ackMessageFlag=" + ackMessageFlag + "\n" +
                ", startDrawFlag=" + startDrawFlag + "\n" +
                ", scorePlayerOne=" + scorePlayerOne + "\n" +
                ", scorePlayerTwo=" + scorePlayerTwo + "\n" +
                ", roundNumber=" + roundNumber + "\n" +
                ", endGameRequests=" + endGameRequests + "\n" +
                ", playersName='" + playersName + '\'' + "\n" +
                ", opponentsName='" + opponentsName + '\'' + "\n" +
                ", opponentAddress='" + opponentAddress + '\'' + "\n" +
                ", choosenWord='" + choosenWord + '\'' + "\n" +
                ", bitmap=" + bitmap + "\n" +
                '}';
    }
}