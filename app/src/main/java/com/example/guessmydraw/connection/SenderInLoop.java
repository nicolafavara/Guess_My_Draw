package com.example.guessmydraw.connection;

import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.net.DatagramPacket;

/**
 * class that extends Sender and overrides the sendPacket method
 * in order to be able to send a message in a loop until we stop it
 */
public class SenderInLoop extends Sender {

    public final static String TAG = "SenderInLoop";
    public boolean stopLoop;

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

            while (!stopLoop){

                senderSocket.send(packetToSend);
                Log.d(TAG, "Datagram sent!");

                Thread.sleep(500);
            }
            stopLoop = false;

        }
        catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        catch (InterruptedException e) {
            Log.d(TAG, "thread interrupted.");
        }
    }

    public void stopLoop(){
        stopLoop = true;
    }

}