package com.example.guessmydraw.connection;

import com.example.guessmydraw.connection.messages.DrawMessage;

import java.net.InetAddress;

public interface NetworkEventCallback {

    void onHandshakeMessageReceived(InetAddress address);
    void onDrawMessageReceived(DrawMessage msg);
    void onAnswerMessageReceived(String answer);
    void onWinMessageReceived();
    //void onTimeOutMessageReceive();
}

