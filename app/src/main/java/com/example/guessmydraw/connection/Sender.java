package com.example.guessmydraw.connection;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Sender extends Thread {

    public Handler mHandler;

    //TODO SISTEMARE:
    public static String NET_MSG_ID = "coordinates";

    private final String destName;

    private DatagramSocket senderSocket;
    private InetAddress dstAddress;

    private final Lock handlerLock = new ReentrantLock();
    private final Queue<Bundle> enqueuedMessages = new LinkedList<>();

    public Sender(String destName){
        this.destName = destName;
    }

    @Override
    public void run() {

        try {
            this.senderSocket = new DatagramSocket();
            this.dstAddress = InetAddress.getByName(this.destName);
        }
        catch (IOException e) {
            Log.e("ERRORE", e.getMessage());
        }

        Looper.prepare();

        mHandler = new Handler(Looper.myLooper()){
            public void handleMessage(Message msg) {
                Log.d("Sender", "Incoming new message to send...");
                Parcelable msgToSend = (Parcelable) msg.getData().get(NET_MSG_ID);
                sendPacket(msgToSend);
            }
        };

        handlerLock.lock();
            while (!enqueuedMessages.isEmpty()) {
                Message msg = mHandler.obtainMessage();
                msg.setData(enqueuedMessages.poll());
                this.mHandler.sendMessage(msg);
            }
        handlerLock.unlock();

        Looper.loop();
    }

    public void sendMessage(Bundle msgData) {
        handlerLock.lock();
            if (mHandler != null) {
                Message msg = mHandler.obtainMessage();
                msg.setData(msgData);
                this.mHandler.sendMessage(msg);
            }
            else {
                enqueuedMessages.add(msgData);
            }
        handlerLock.unlock();
    }

    private void sendPacket(Parcelable msg){

        try {

            Log.d("Sender", "sending message to " + dstAddress.getHostAddress() + ".");
            byte[] bytesToSend = ParcelableUtil.marshall(msg);

            DatagramPacket packetToSend = new DatagramPacket(   //preparing datagram
                    bytesToSend,
                    bytesToSend.length,
                    dstAddress,
                    Receiver.RECEIVER_PORT
            );
            senderSocket.send(packetToSend);
            Log.d("Sender", "Datagram sent!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
