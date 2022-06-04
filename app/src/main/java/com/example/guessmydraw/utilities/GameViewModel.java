package com.example.guessmydraw.utilities;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class GameViewModel extends AndroidViewModel {

    private boolean groupOwnerFlag;
    private boolean isMyTurnToDraw;
    private boolean isFirstMsg;
    private boolean myEndRequest;
    private boolean ackMessageFlag;
    private boolean startDrawFlag;
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
        groupOwnerFlag = false;
        isMyTurnToDraw = false;
        isFirstMsg = true;
        ackMessageFlag = false;
        startDrawFlag = false;
        endGameRequests = 0;
        scorePlayerOne = 0;
        scorePlayerTwo = 0;
        roundNumber = 0;
        opponentAddress = null;
        choosenWord = null;
        bitmap = null;

    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void setNextRound(){
        Log.d("DEBUG", "NEXT ROUND !!!");
        choosenWord = null;
        roundNumber++;
        isFirstMsg = true;
        bitmap = null;
        myEndRequest = false;
        ackMessageFlag = false;
        startDrawFlag = false;
    }

    public int askToEndGame() {
        endGameRequests++;
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
        return isFirstMsg;
    }

    public void setIsFirstMsg(boolean value) {
        isFirstMsg = value;
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

    public boolean isAckMessageFlag() {
        return ackMessageFlag;
    }

    public void setAckMessageFlag(boolean ackMessageFlag) {
        this.ackMessageFlag = ackMessageFlag;
    }

    public boolean isStartDrawFlag() {
        return startDrawFlag;
    }

    public void setStartDrawFlag(boolean startDrawFlag) {
        this.startDrawFlag = startDrawFlag;
    }

    @Override
    public String toString() {
        return "GameViewModel{" +
                "groupOwnerFlag=" + groupOwnerFlag + "\n" +
                ", isMyTurnToDraw=" + isMyTurnToDraw + "\n" +
                ", isFirstMsg=" + isFirstMsg + "\n" +
                ", myEndRequest=" + myEndRequest + "\n" +
                ", ackMessageFlag=" + ackMessageFlag + "\n" +
                ", startDrawFlag=" + startDrawFlag + "\n" +
                ", playersName='" + playersName + '\'' + "\n" +
                ", opponentsName='" + opponentsName + '\'' + "\n" +
                ", opponentAddress='" + opponentAddress + '\'' + "\n" +
                ", choosenWord='" + choosenWord + '\'' + "\n" +
                ", scorePlayerOne=" + scorePlayerOne + "\n" +
                ", scorePlayerTwo=" + scorePlayerTwo + "\n" +
                ", roundNumber=" + roundNumber + "\n" +
                ", endGameRequests=" + endGameRequests + "\n" +
                ", bitmap=" + bitmap + "\n" +
                '}';
    }
}