package com.example.guessmydraw.connection;

import android.util.Log;

import com.example.guessmydraw.connection.messages.AnswerMessage;
import com.example.guessmydraw.connection.messages.DrawMessage;
import com.example.guessmydraw.connection.messages.HandshakeMessage;
import com.example.guessmydraw.connection.messages.WinMessage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class Receiver extends Thread{

    public static final int RECEIVER_PORT = 5555;
    private static final int BUFF_SIZE = 4096;

    private final NetworkEventCallback callback;

    public Receiver(final NetworkEventCallback callback){
        this.callback = callback;
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

                Log.d("DEBUG-Receiver", "packet received.");

                byte[] bf = receivedPacket.getData();
                byte type = bf[0];

                if (type == DrawMessage.NET_ID) {
                    DrawMessage receivedMessage = ParcelableUtil.unmarshall(bf, DrawMessage.CREATOR);
                    callback.onDrawMessageReceived(receivedMessage);
                }
                else if (type == HandshakeMessage.NET_ID) {
                    callback.onHandshakeMessageReceived(receivedPacket.getAddress());
                }
                else if (type == AnswerMessage.NET_ID) {
                    String answer = ParcelableUtil.unmarshall(bf, AnswerMessage.CREATOR).getAnswer();
                    callback.onAnswerMessageReceived(answer);
                }
                else if (type == WinMessage.NET_ID) {
                    callback.onWinMessageReceived();
                }
                else {
                    throw new RuntimeException("Boh, I don't know the NET ID!");
                }

                //Log.d("DEBUG", receivedMessage.toString());

            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
}
