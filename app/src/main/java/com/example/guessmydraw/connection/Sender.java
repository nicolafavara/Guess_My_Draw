package com.example.guessmydraw.connection;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Sender extends Thread {

    public static String TAG = "Sender";
    public static String NET_MSG_ID = "NET_MSG_ID";

    public Handler mHandler;

    protected DatagramSocket senderSocket;
    protected InetAddress dstAddress;

    private final String destName;
    private final Lock handlerLock = new ReentrantLock();
    private final Queue<Bundle> enqueuedMessages = new LinkedList<>();

    public Sender(@NonNull String destName){
        this.destName = destName;
    }

    @Override
    public final void run() {

        Log.d(TAG, "Starting...");

        try {
            senderSocket = new DatagramSocket();
            dstAddress = InetAddress.getByName(destName);
        }
        catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        Looper.prepare();

        mHandler = new Handler(Looper.myLooper()){
            public void handleMessage(Message msg) {
                Log.d(TAG, "Incoming new message to send...");
                Parcelable msgToSend = (Parcelable) msg.getData().get(NET_MSG_ID);
                sendPacket(msgToSend);
            }
        };

        handlerLock.lock();
            while (!enqueuedMessages.isEmpty()) {
                Message msg = mHandler.obtainMessage();
                msg.setData(enqueuedMessages.poll());
                mHandler.sendMessage(msg);
            }
        handlerLock.unlock();

        Looper.loop();
    }

    public void sendMessage(Bundle msgData) {
        handlerLock.lock();
            if (mHandler != null) {
                Message msg = mHandler.obtainMessage();
                msg.setData(msgData);
                mHandler.sendMessage(msg);
            }
            else {
                enqueuedMessages.add(msgData);
            }
        handlerLock.unlock();
    }

    protected void sendPacket(Parcelable msg){

        if (msg == null){
            Log.e("DEBUG", "Attempting to send empty message.");
            return;
        }

        try {

            Log.d(TAG, "sending message to " + dstAddress.getHostAddress() + ".");
            byte[] bytesToSend = ParcelableUtil.marshall(msg);

            DatagramPacket packetToSend = new DatagramPacket(   //preparing datagram
                    bytesToSend,
                    bytesToSend.length,
                    dstAddress,
                    Receiver.RECEIVER_PORT
            );
            senderSocket.send(packetToSend);
            Log.d(TAG, "Datagram sent!");

        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
