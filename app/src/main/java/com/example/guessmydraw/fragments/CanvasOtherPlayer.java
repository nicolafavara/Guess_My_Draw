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
import android.widget.TextView;
import android.widget.Toast;

import com.example.guessmydraw.R;
import com.example.guessmydraw.connection.Sender;
import com.example.guessmydraw.connection.messages.TimerExpiredMessage;
import com.example.guessmydraw.connection.messages.WinMessage;
import com.example.guessmydraw.databinding.FragmentCanvasOtherPlayerBinding;
import com.example.guessmydraw.fragments.Views.OtherPlayerCanvasView;
import com.example.guessmydraw.utilities.DisconnectionDialog;
import com.example.guessmydraw.utilities.GameTimer;
import com.example.guessmydraw.utilities.GameViewModel;

 public class CanvasOtherPlayer extends Fragment implements GameTimer.TimerInterface, OtherPlayerCanvasView.canvasViewCallback{

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private FragmentCanvasOtherPlayerBinding binding;

    private GameTimer timer;
    private TextView timerTextView;

    private String currentPlayerAddress;
    private String rightAnswer;
    private Sender sender;
    private Bundle bundle;

    private GameViewModel gameViewModel;

    public CanvasOtherPlayer() {
        this.bundle = new Bundle();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                new DisconnectionDialog().show(getChildFragmentManager(), DisconnectionDialog.TAG);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentCanvasOtherPlayerBinding.inflate(inflater, container, false);

        this.timerTextView = binding.timerTextView;
        this.timer = new GameTimer(this.timerTextView, this);

        gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);

        binding.sendButton.setOnClickListener(view -> {

            String answer = binding.answerEditText.getText().toString();
            if(!answer.equals("")){

                if(this.rightAnswer.equalsIgnoreCase(answer)){
                    gameViewModel.updateScorePlayerOne();

                    timer.cancelTimer();
                    sendWinMessage();
                    mainHandler.post(()->{
                        Toast.makeText(getContext(), "Hai indovinato!!!", Toast.LENGTH_SHORT).show();
                    });
                    NavHostFragment.findNavController(this).navigate(R.id.end_round);
                }
                else{
                    mainHandler.post(()->{
                        Toast.makeText(getContext(), "Riprova", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.currentPlayerAddress = gameViewModel.getOpponentAddress();
        this.rightAnswer = gameViewModel.getChoosenWord();
        Log.d("DEBUG", "CanvasOtherPlayer: l'IP del giocatore corrente è: " + currentPlayerAddress);
        this.sender = new Sender(currentPlayerAddress);
        this.sender.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void sendWinMessage() {
        WinMessage messageToSend = new WinMessage();
        bundle.clear();
        bundle.putParcelable(Sender.NET_MSG_ID, messageToSend);
        this.sender.sendMessage(bundle);
    }

    private void sendTimerExpiredMessage(){
        TimerExpiredMessage messageToSend = new TimerExpiredMessage();
        bundle.clear();
        bundle.putParcelable(Sender.NET_MSG_ID, messageToSend);
        this.sender.sendMessage(bundle);
    }

     @Override
     public void onTimerExpired() {

         sendTimerExpiredMessage();

         mainHandler.post(() -> {
             Toast.makeText(getContext(), "FINE", Toast.LENGTH_SHORT).show();
         });

         NavHostFragment.findNavController(this).navigate(R.id.end_round);
     }

     @Override
     public void firstDrawMessageReceived() {
        Log.d("DEBUG", "starting timer.");
        this.timer.start();
     }
 }