package com.example.guessmydraw.utilities;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.guessmydraw.connection.NetworkEventCallback;
import com.example.guessmydraw.connection.Receiver;
import com.example.guessmydraw.connection.Sender;
import com.example.guessmydraw.connection.SenderInLoop;

public class ActivityViewModel extends AndroidViewModel {

    private final static String TAG = "ACTIVITY_VM";
    private final Receiver receiver;
    private Sender mainSender;
    private SenderInLoop mainSenderInLoop;

    public ActivityViewModel(@NonNull Application application) {
        super(application);
        receiver = new Receiver();
        receiver.start();
    }

    public void initSenders(String address){
        Log.d(TAG, "SENDERS INITIALIZED.");
//        mainSender = new Sender(address);
//        mainSender.start();
//        mainSenderInLoop = new SenderInLoop(address);
//        mainSenderInLoop.start();
    }

    public void sendMessage(Bundle bundle){
        Log.d(TAG, "sendMessage().");
        if(mainSender != null){
            mainSender.sendMessage(bundle);
        }
        else{
            Log.e(TAG, "MainSender is null.");
        }
    }

    public void sendMessageInLoop(Bundle bundle){
        Log.d(TAG, "sendMessageInLoop().");
        if(mainSenderInLoop != null){
            mainSenderInLoop.sendMessage(bundle);
        }
        else{
            Log.e(TAG, "MainSenderInLoop is null.");
        }
    }

    public void stopSenderInLoop(){
        Log.d(TAG, "stopSenderInLoop().");
        if(mainSenderInLoop != null){
            mainSenderInLoop.stopLoop();
        }
        else{
            Log.e(TAG, "MainSenderInLoop is null.");
        }
    }

    public void registerForReceiver(@NonNull NetworkEventCallback callback){
        Log.d(TAG, "Setting network event callback...");
        if(receiver != null) {
            receiver.setNetworkEventCallback(callback);
        }
        else{
            Log.e(TAG, "Receiver is null.");
        }
    }
}
