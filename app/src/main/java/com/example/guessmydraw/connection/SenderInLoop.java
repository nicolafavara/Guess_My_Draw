package com.example.guessmydraw.connection;

import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.net.DatagramPacket;

public class SenderInLoop extends Sender {

    public static String TAG = "SenderInLoop";

    public SenderInLoop(@NonNull String destName) {
        super(destName);
    }

    @Override
    protected final void sendPacket(Parcelable msg) {

        try {

            Log.d(TAG, "Sending message to " + dstAddress.getHostAddress() + ".");
            byte[] bytesToSend = ParcelableUtil.marshall(msg);

            DatagramPacket packetToSend = new DatagramPacket(   //preparing datagram
                    bytesToSend,
                    bytesToSend.length,
                    dstAddress,
                    Receiver.RECEIVER_PORT
            );

            while (!this.isInterrupted()){

                senderSocket.send(packetToSend);
                Log.d(TAG, "Datagram sent!");

                Thread.sleep(500);
            }

        }
        catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        catch (InterruptedException e) {
            Log.d(TAG, "thread interrupted.");
        }
    }

}