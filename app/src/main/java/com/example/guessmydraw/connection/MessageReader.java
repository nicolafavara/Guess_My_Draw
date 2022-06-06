package com.example.guessmydraw.connection;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.guessmydraw.connection.messages.AckMessage;
import com.example.guessmydraw.connection.messages.AnswerMessage;
import com.example.guessmydraw.connection.messages.DrawMessage;
import com.example.guessmydraw.connection.messages.EndMatchRequestMessage;
import com.example.guessmydraw.connection.messages.HandshakeMessage;
import com.example.guessmydraw.connection.messages.StartDrawMessage;
import com.example.guessmydraw.connection.messages.TimerExpiredMessage;
import com.example.guessmydraw.connection.messages.WinMessage;

import java.net.DatagramPacket;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Class representing the thread used to take the various messages
 * from the receiver queue and send them through the registered callback
 */
public class MessageReader extends Thread {

    private final static String TAG = "MessageReader";

    // receiver used to receive message from opponent
    private static final Receiver receiver = new Receiver();

    private NetworkEventCallback callback;

    public MessageReader(){
        receiver.start();
    }

    public void setNetworkEventCallback(@NonNull NetworkEventCallback callback){
        this.callback = callback;
    }

    @Override
    public void run() {

        while (!Thread.interrupted()){

            DatagramPacket receivedPacket = receiver.getPacket();

            if (receivedPacket == null) continue;

            byte[] bf = receivedPacket.getData();
            if(bf == null) continue;

            byte type = bf[0];

            if (callback == null){
                Log.e(TAG, "Callback is null.");
                continue;
            }

            if (type == DrawMessage.NET_ID) {
                Log.d(TAG, "packet DrawMessage received.");
                DrawMessage receivedMessage = ParcelableUtil.unmarshall(bf, DrawMessage.CREATOR);
                callback.onDrawMessageReceived(receivedMessage);
            }
            else if (type == HandshakeMessage.NET_ID) {
                Log.d(TAG, "packet HandshakeMessage received.");
                String opponentsName = ParcelableUtil.unmarshall(bf, HandshakeMessage.CREATOR).getPlayersName();
                callback.onHandshakeMessageReceived(receivedPacket.getAddress(), opponentsName);
            }
            else if (type == AnswerMessage.NET_ID) {
                Log.d(TAG, "packet AnswerMessage received.");
                String answer = ParcelableUtil.unmarshall(bf, AnswerMessage.CREATOR).getAnswer();
                callback.onAnswerMessageReceived(answer);
            }
            else if (type == WinMessage.NET_ID) {
                Log.d(TAG, "packet WinMessage received.");
                float remainingSeconds = ParcelableUtil.unmarshall(bf, WinMessage.CREATOR).getRemainingSeconds();
                callback.onWinMessageReceived(remainingSeconds);
            }
            else if (type == TimerExpiredMessage.NET_ID) {
                Log.d(TAG, "packet TimerExpiredMessage received.");
                callback.onTimerExpiredMessage();
            }
            else if (type == EndMatchRequestMessage.NET_ID) {
                Log.d(TAG, "packet EndMatchRequestMessage received.");
                callback.onEndingMessageReceived();
            }
            else if (type == AckMessage.NET_ID) {
                Log.d(TAG, "packet AckMessage received.");
                callback.onAckMessageReceived();
            }
            else if (type == StartDrawMessage.NET_ID) {
                Log.d(TAG, "packet StartDrawMessage received.");
                callback.onStartDrawMessageReceived();
            }
            else {
                throw new RuntimeException("Unknown NET ID!");
            }
        }
    }
}
