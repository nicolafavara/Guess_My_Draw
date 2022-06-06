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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * class representing the thread used to stay on listening for incoming messages
 */
public class Receiver extends Thread {

    private final static String TAG = "RECEIVER";
    public static final int RECEIVER_PORT = 5555;
    private static final int BUFF_SIZE = 4096;


    private NetworkEventCallback callback;

    public Receiver(){}

    public void setNetworkEventCallback(@NonNull NetworkEventCallback callback){
        Log.d(TAG, "setNetworkEventCallback");
        this.callback = callback;
    }

    @Override
    public void run() {

        try (DatagramSocket client = new DatagramSocket(null)){
            client.setReuseAddress(true);
            client.bind(new InetSocketAddress(RECEIVER_PORT));

            while (!Thread.interrupted()){

                byte[] buffer = new byte[BUFF_SIZE];
                DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);

                Log.e(TAG, "waiting for message...");
                client.receive(receivedPacket);
                Log.e(TAG, "Message received.");

                byte[] bf = receivedPacket.getData();
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
                    callback.onWinMessageReceived();
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
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
