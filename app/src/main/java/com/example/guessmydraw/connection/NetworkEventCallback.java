package com.example.guessmydraw.connection;

import com.example.guessmydraw.connection.messages.DrawMessage;

import java.net.InetAddress;

public interface NetworkEventCallback {
    void onHandshakeMessageReceived(InetAddress address, String opponentsName);
    void onDrawMessageReceived(DrawMessage msg);
    void onAnswerMessageReceived(String answer);
    void onWinMessageReceived();
    void onTimerExpiredMessage();
    void onEndingMessageReceived();
    void onAckMessageReceived();
}

