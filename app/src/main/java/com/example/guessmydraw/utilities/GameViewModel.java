package com.example.guessmydraw.utilities;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class GameViewModel extends AndroidViewModel {

    private boolean groupOwnerFlag;
    private boolean isMyTurnToDraw;
    private String playersName;
    private String opponentsName;
    private String opponentAddress;
    private String choosenWord;
    private int scorePlayerOne;
    private int scorePlayerTwo;
    private int roundNumber;
    private int nPlayerThatWantToEndGame;

    public GameViewModel(@NonNull Application application) {
        super(application);
    }

    public void init(){
        this.groupOwnerFlag = false;
        this.isMyTurnToDraw = false;
        this.opponentAddress = null;
        this.scorePlayerOne = 0;
        this.scorePlayerTwo = 0;
        this.roundNumber = 0;
        this.choosenWord = null;
        this.nPlayerThatWantToEndGame = 0;
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

    public int getScorePlayerOne() {
        return scorePlayerOne;
    }

    public int getScorePlayerTwo() {
        return scorePlayerTwo;
    }

    public void updateScorePlayerOne() {
        scorePlayerOne++;
    }

    public void updateScorePlayerTwo() {
        scorePlayerTwo++;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void setNextRound(){
        Log.d("DEBUG", "NEXT ROUND !!!");
        this.choosenWord = null;
        this.roundNumber++;
    }

    public String getChoosenWord() {
        return choosenWord;
    }

    public void setChoosenWord(String choosenWord) {
        this.choosenWord = choosenWord;
    }

    public void setOpponentAddress(String address){
        this.opponentAddress = address;
    }

    public String getOpponentAddress() {
        return this.opponentAddress;
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

    public int getnPlayerThatWantToEndGame() {
        return nPlayerThatWantToEndGame;
    }

    public int askToEndGame() {
        this.nPlayerThatWantToEndGame++;
        return nPlayerThatWantToEndGame;
    }

    @Override
    public String toString() {
        return "MatchViewModel{" +
                "opponentAddress='" + opponentAddress + '\'' +
                ", groupOwnerFlag=" + groupOwnerFlag +
                ", scorePlayerOne=" + scorePlayerOne +
                ", scorePlayerTwo=" + scorePlayerTwo +
                ", roundNumber=" + roundNumber +
                ", choosenWord='" + choosenWord + '\'' +
                '}';
    }
}