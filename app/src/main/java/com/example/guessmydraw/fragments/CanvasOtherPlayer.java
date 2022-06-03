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

import com.example.guessmydraw.MainActivity;
import com.example.guessmydraw.R;
import com.example.guessmydraw.connection.Sender;
import com.example.guessmydraw.connection.messages.TimerExpiredMessage;
import com.example.guessmydraw.connection.messages.WinMessage;
import com.example.guessmydraw.databinding.FragmentCanvasOtherPlayerBinding;
import com.example.guessmydraw.fragments.Views.OtherPlayerCanvasView;
import com.example.guessmydraw.utilities.DisconnectionDialog;
import com.example.guessmydraw.utilities.GameViewModel;
import com.example.guessmydraw.utilities.TimerModelView;

 public class CanvasOtherPlayer extends Fragment implements OtherPlayerCanvasView.canvasViewCallback{

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private FragmentCanvasOtherPlayerBinding binding;
    private MainActivity activity;

    private TextView timerTextView;
    private String rightAnswer;
    private Bundle bundle;

    private GameViewModel gameViewModel;
    private TimerModelView timerModelView;

    public CanvasOtherPlayer() {
        this.bundle = new Bundle();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (MainActivity) requireActivity();

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                new DisconnectionDialog().show(getChildFragmentManager(), DisconnectionDialog.TAG);
            }
        };
        activity.getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentCanvasOtherPlayerBinding.inflate(inflater, container, false);

        this.timerTextView = binding.timerTextView;

        gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);
        timerModelView = new ViewModelProvider(requireActivity()).get(TimerModelView.class);
        timerModelView.getTimerLiveData().observe(getViewLifecycleOwner(), timeLeft -> {

            int timeLeftInSec = (int) (timeLeft / 1000);
            timerTextView.setText(String.valueOf(timeLeftInSec));

            if(timeLeftInSec == 0){
                sendTimerExpiredMessage();

                mainHandler.post(() -> {
                    Toast.makeText(getContext(), "FINE", Toast.LENGTH_SHORT).show();
                });

                NavHostFragment.findNavController(this).navigate(R.id.end_round);
            }
        });

        binding.sendButton.setOnClickListener(view -> {
            String answer = binding.answerEditText.getText().toString();
            if(!answer.equals("")){

                if(this.rightAnswer.equalsIgnoreCase(answer)){

                    timerModelView.cancelTimer();
                    gameViewModel.updateScorePlayerOne();
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
        this.rightAnswer = gameViewModel.getChoosenWord();
    }

     @Override
     public void onResume() {
         super.onResume();
         activity = (MainActivity) requireActivity();
         // Hide status bar
         View windowDecorView = activity.getWindow().getDecorView();
         windowDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
     }

     @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void sendWinMessage() {
        WinMessage messageToSend = new WinMessage();
        bundle.clear();
        bundle.putParcelable(Sender.NET_MSG_ID, messageToSend);
        activity.sendMessage(bundle);
    }

    private void sendTimerExpiredMessage(){
        TimerExpiredMessage messageToSend = new TimerExpiredMessage();
        bundle.clear();
        bundle.putParcelable(Sender.NET_MSG_ID, messageToSend);
        activity.sendMessage(bundle);
    }

     @Override
     public void firstDrawMessageReceived() {
         timerModelView.requestTimer();
     }

 }