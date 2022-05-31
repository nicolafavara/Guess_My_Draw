 package com.example.guessmydraw.fragments;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.guessmydraw.R;
import com.example.guessmydraw.connection.messages.DrawMessage;
import com.example.guessmydraw.connection.NetworkEventCallback;
import com.example.guessmydraw.connection.Receiver;
import com.example.guessmydraw.connection.Sender;
import com.example.guessmydraw.databinding.FragmentCanvasCurrentPlayerBinding;
import com.example.guessmydraw.utilities.DisconnectionDialog;
import com.example.guessmydraw.utilities.GameViewModel;

import java.net.InetAddress;

 public class CanvasCurrentPlayer extends Fragment implements NetworkEventCallback {

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private FragmentCanvasCurrentPlayerBinding binding;
    private String opponentPlayerAddress;
    private String correctAnswer;

    private DrawMessage messageToSend;
    private Sender sender;
    private Receiver receiver;  //receiver for the answer sent from other player
    private Bundle bundle;

    private GameViewModel gameViewModel;

    public CanvasCurrentPlayer() {}

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
        gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);
        String wordToDraw = gameViewModel.getChoosenWord();
        binding.wordToDraw.setText(wordToDraw);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        this.opponentPlayerAddress = gameViewModel.getOpponentAddress();
        Log.d("DEBUG", "CanvasCurrentPlayer: otherPlayerAddress è = " + opponentPlayerAddress);
        this.correctAnswer = gameViewModel.getChoosenWord();
        this.sender = new Sender(opponentPlayerAddress);
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

         gameViewModel.updateScorePlayerTwo();

         mainHandler.post(()->{
             Toast.makeText(getContext(), "Il tuo avversario ha indovinato!", Toast.LENGTH_LONG).show();
             NavHostFragment.findNavController(this).navigate(R.id.end_round);
         });
     }

     @Override
     public void onTimerExpiredMessage() {
         mainHandler.post(()->{
             Toast.makeText(getContext(), "Il tuo avversario ha perso!", Toast.LENGTH_LONG).show();
             NavHostFragment.findNavController(this).navigate(R.id.end_round);
         });
     }

     @Override
     public void onEndingMessageReceived() {/*EMPTY*/}

     @Override
     public void onAckMessageReceived() {/*EMPTY*/}

     @Override
     public void onAnswerMessageReceived(String answer) {/*EMPTY*/}

     @Override
     public void onHandshakeMessageReceived(InetAddress address, String opponentsName) {/*EMPTY*/}
     
     @Override
     public void onDrawMessageReceived(DrawMessage msg) {/*EMPTY*/}

 }