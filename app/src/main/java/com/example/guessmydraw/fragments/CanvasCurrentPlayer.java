 package com.example.guessmydraw.fragments;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.guessmydraw.connection.messages.DrawMessage;
import com.example.guessmydraw.connection.NetworkEventCallback;
import com.example.guessmydraw.connection.Receiver;
import com.example.guessmydraw.connection.Sender;
import com.example.guessmydraw.databinding.FragmentCanvasCurrentPlayerBinding;
import com.example.guessmydraw.utilities.DisconnectionDialog;

import java.net.InetAddress;

 public class CanvasCurrentPlayer extends Fragment implements NetworkEventCallback {

     private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private FragmentCanvasCurrentPlayerBinding binding;
    private String otherPlayerAddress;
    private String correctAnswer;

    private DrawMessage messageToSend;
    private Sender sender;
    private Receiver receiver;  //receiver for the answer sent from other player
    private Bundle bundle;

    public CanvasCurrentPlayer() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                new DisconnectionDialog().show(getChildFragmentManager(), DisconnectionDialog.TAG);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        // The callback can be enabled or disabled here or in handleOnBackPressed()

        this.bundle = new Bundle();
        this.messageToSend = new DrawMessage();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCanvasCurrentPlayerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.otherPlayerAddress = CanvasCurrentPlayerArgs.fromBundle(getArguments()).getOtherPlayerAddress();
        this.correctAnswer = CanvasCurrentPlayerArgs.fromBundle(getArguments()).getWord();
        Log.d("DEBUG", "CanvasCurrentPlayer: otherPlayerAddress è = " + otherPlayerAddress);
        this.sender = new Sender(otherPlayerAddress);
        this.sender.start();
        
        this.receiver = new Receiver(this);
        this.receiver.start();
    }

    public void sendMessageOverNetwork(float currentX, float currentY, float x2, float y2, int motionEventAction) {

//        Log.d("DEBUG", "coordinates: currentX = " + currentX + ", currentY = " + currentY
//                + ", x2 = " + x2 + ", y2 = " + y2 + ", motionEventAction = " + motionEventAction);

        messageToSend.setCurrentX(currentX);
        messageToSend.setCurrentY(currentY);
        messageToSend.setX2(x2);
        messageToSend.setY2(y2);
        messageToSend.setMotionEventAction(motionEventAction);

        bundle.clear();
        bundle.putParcelable(Sender.NET_MSG_ID, messageToSend);

        this.sender.sendMessage(bundle);
    }

     @Override
     public void onWinMessageReceived() {

         mainHandler.post(()->{
             Toast.makeText(getContext(), "Il tuo avversario ha indovinato!", Toast.LENGTH_SHORT).show();
         });
     }

     @Override
     public void onAnswerMessageReceived(String answer) {/*EMPTY*/}

     @Override
     public void onHandshakeMessageReceived(InetAddress address) {/*EMPTY*/}
     
     @Override
     public void onDrawMessageReceived(DrawMessage msg) {/*EMPTY*/}

 }