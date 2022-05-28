package com.example.guessmydraw.connection;

import android.util.Log;

import com.example.guessmydraw.connection.messages.AckMessage;
import com.example.guessmydraw.connection.messages.AnswerMessage;
import com.example.guessmydraw.connection.messages.DrawMessage;
import com.example.guessmydraw.connection.messages.EndingMessage;
import com.example.guessmydraw.connection.messages.HandshakeMessage;
import com.example.guessmydraw.connection.messages.TimerExpiredMessage;
import com.example.guessmydraw.connection.messages.WinMessage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class Receiver extends Thread {

    public static final int RECEIVER_PORT = 5555;
    private static final int BUFF_SIZE = 4096;

    private final NetworkEventCallback callback;
    private final AckReceivedCallback ackCallback;

    public Receiver(final NetworkEventCallback callback, final AckReceivedCallback ackCallback){
        this.callback = callback;
        this.ackCallback = ackCallback;
    }

    @Override
    public void run() {

        try (DatagramSocket client = new DatagramSocket(null)){
            client.setReuseAddress(true);
            client.bind(new InetSocketAddress(RECEIVER_PORT));

        //try (DatagramSocket client = new DatagramSocket(RECEIVER_PORT) ){

            while (!Thread.interrupted()){

                byte[] buffer = new byte[BUFF_SIZE];
                DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);

                // rimane in attesa della risposta del server fino allo scadere del timeout
                client.receive(receivedPacket);

                byte[] bf = receivedPacket.getData();
                byte type = bf[0];

                if (type == DrawMessage.NET_ID) {
                    Log.d("DEBUG-Receiver", "packet DrawMessage received.");
                    DrawMessage receivedMessage = ParcelableUtil.unmarshall(bf, DrawMessage.CREATOR);
                    callback.onDrawMessageReceived(receivedMessage);
                }
                else if (type == HandshakeMessage.NET_ID) {
                    Log.d("DEBUG-Receiver", "packet HandshakeMessage received.");
                    String opponentsName = ParcelableUtil.unmarshall(bf, HandshakeMessage.CREATOR).getPlayersName();
                    callback.onHandshakeMessageReceived(receivedPacket.getAddress(), opponentsName);
                }
                else if (type == AnswerMessage.NET_ID) {
                    Log.d("DEBUG-Receiver", "packet AnswerMessage received.");
                    String answer = ParcelableUtil.unmarshall(bf, AnswerMessage.CREATOR).getAnswer();
                    callback.onAnswerMessageReceived(answer);
                }
                else if (type == WinMessage.NET_ID) {
                    Log.d("DEBUG-Receiver", "packet WinMessage received.");
                    callback.onWinMessageReceived();
                }
                else if (type == TimerExpiredMessage.NET_ID) {
                    Log.d("DEBUG-Receiver", "packet TimerExpiredMessage received.");
                    callback.onTimerExpiredMessage();
                }
                else if (type == EndingMessage.NET_ID) {
                    Log.d("DEBUG-Receiver", "packet EndingMessage received.");
                    callback.onEndingMessageReceived();
                }
                else if (type == AckMessage.NET_ID) {
                    Log.d("DEBUG-Receiver", "packet AckMessage received.");
                    ackCallback.onAckMessageReceived();
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
