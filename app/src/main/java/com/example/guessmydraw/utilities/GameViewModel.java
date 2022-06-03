package com.example.guessmydraw.utilities;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class GameViewModel extends AndroidViewModel {

    private boolean groupOwnerFlag;
    private boolean isMyTurnToDraw;
    private boolean isFirstMsg;
    private boolean myEndRequest;
    private String playersName;
    private String opponentsName;
    private String opponentAddress;
    private String choosenWord;
    private int scorePlayerOne;
    private int scorePlayerTwo;
    private int roundNumber;
    private int endGameRequests;
    private Bitmap bitmap;

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
        this.endGameRequests = 0;
        this.isFirstMsg = true;
        this.bitmap = null;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void setNextRound(){
        Log.d("DEBUG", "NEXT ROUND !!!");
        this.choosenWord = null;
        this.roundNumber++;
        this.isFirstMsg = true;
        this.bitmap = null;
        this.myEndRequest = false;
    }

    public int askToEndGame() {
        this.endGameRequests++;
        return endGameRequests;
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

    public String getChoosenWord() {
        return choosenWord;
    }

    public void setChoosenWord(String choosenWord) {
        this.choosenWord = choosenWord;
    }

    public void setOpponentAddress(@NonNull String address){
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

    public boolean getIsFirstMsg() {
        return isFirstMsg;
    }

    public void setIsFirstMsg(boolean value) {
        this.isFirstMsg = value;
    }

    public boolean isMyEndRequest() {
        return myEndRequest;
    }

    public void setMyEndRequest(boolean myEndRequest) {
        this.myEndRequest = myEndRequest;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
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