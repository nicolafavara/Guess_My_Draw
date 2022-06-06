package com.example.guessmydraw.connection;

import android.os.Bundle;
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
import java.util.LinkedList;
import java.util.Queue;

/**
 * class representing the thread used to stay on listening for incoming messages
 */
public class Receiver extends Thread {

    private final static String TAG = "RECEIVER";
    public static final int RECEIVER_PORT = 5555;
    private static final int BUFF_SIZE = 4096;

    private final static Queue<DatagramPacket> enqueuedMessages = new LinkedList<>();

    public Receiver(){}

    public DatagramPacket getPacket(){
        DatagramPacket packet;
        synchronized (enqueuedMessages){
            packet = enqueuedMessages.poll();
        }
        return packet;
    }

    @Override
    public void run() {

        try (DatagramSocket client = new DatagramSocket(null)){
            client.setReuseAddress(true);
            client.bind(new InetSocketAddress(RECEIVER_PORT));

            while (!Thread.interrupted()){

                byte[] buffer = new byte[BUFF_SIZE];
                DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);

                Log.d(TAG, "waiting for message...");
                client.receive(receivedPacket);
                Log.d(TAG, "Message received.");

                synchronized (enqueuedMessages){
                    enqueuedMessages.add(receivedPacket);
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
