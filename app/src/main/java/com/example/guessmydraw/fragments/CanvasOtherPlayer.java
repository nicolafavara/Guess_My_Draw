 package com.example.guessmydraw.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
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
import com.example.guessmydraw.connection.messages.StartDrawMessage;
import com.example.guessmydraw.connection.messages.TimerExpiredMessage;
import com.example.guessmydraw.connection.messages.WinMessage;
import com.example.guessmydraw.databinding.FragmentCanvasOtherPlayerBinding;
import com.example.guessmydraw.fragments.Views.OtherPlayerCanvasView;
import com.example.guessmydraw.utilities.DisconnectionDialog;
import com.example.guessmydraw.utilities.GameViewModel;
import com.example.guessmydraw.utilities.TimerViewModel;

 public class CanvasOtherPlayer extends Fragment implements OtherPlayerCanvasView.canvasViewCallback, View.OnClickListener{

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
     private final static String TAG = "CanvasOtherPlayer";
    private FragmentCanvasOtherPlayerBinding binding;

    private TextView timerTextView;
    private String rightAnswer;
    private Bundle bundle;

    // viewModel used to save all the information needed for the match
    private GameViewModel gameViewModel;
    // viewModel used to store the information of the timer that
    // is in charge of counting down during a round
    private TimerViewModel timerViewModel;

    public CanvasOtherPlayer() {}

     @Override
     public void onAttach(@NonNull Context context) {
         super.onAttach(context);
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);
        rightAnswer = gameViewModel.getChoosenWord();

        // Inflate the layout for this fragment
        binding = FragmentCanvasOtherPlayerBinding.inflate(inflater, container, false);
        timerTextView = binding.timerTextView;

        timerViewModel = new ViewModelProvider(requireActivity()).get(TimerViewModel.class);
        timerViewModel.getTimerLiveData().observe(getViewLifecycleOwner(), timeLeft -> {

            int timeLeftInSec = (int) (timeLeft / 1000);
            timerTextView.setText(String.valueOf(timeLeftInSec));

            if(timeLeftInSec == 0){
                sendTimerExpiredMessage();

                mainHandler.post(() -> {
                    Toast.makeText(getContext(), R.string.Timer_expired, Toast.LENGTH_SHORT).show();
                });

                NavHostFragment.findNavController(this).navigate(R.id.end_round);
            }
        });

        binding.sendButton.setOnClickListener(this);

        return binding.getRoot();
    }

     @Override
     public void onResume() {
         super.onResume();
         // Hide status bar
         View windowDecorView = requireActivity().getWindow().getDecorView();
         windowDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

         if(!gameViewModel.isStartDrawFlag()){
             Log.d(TAG, "sending StartDrawMessage...");
             gameViewModel.setStartDrawFlag(true);
             sendStartDrawMessage();
         }
     }

    private void sendWinMessage(float remainingSeconds) {
        WinMessage messageToSend = new WinMessage();
        messageToSend.setRemainingSeconds(remainingSeconds);
        bundle.clear();
        bundle.putParcelable(Sender.NET_MSG_ID, messageToSend);
        Log.d(TAG, "Sending win message");
        ((MainActivity)requireActivity()).sendMessage(bundle);
    }

     private void sendStartDrawMessage() {
         StartDrawMessage messageToSend = new StartDrawMessage();
         bundle.clear();
         bundle.putParcelable(Sender.NET_MSG_ID, messageToSend);
         ((MainActivity)requireActivity()).sendMessage(bundle);
     }

    private void sendTimerExpiredMessage(){
        TimerExpiredMessage messageToSend = new TimerExpiredMessage();
        bundle.clear();
        bundle.putParcelable(Sender.NET_MSG_ID, messageToSend);
        ((MainActivity)requireActivity()).sendMessage(bundle);
    }

     @Override
     public void firstDrawMessageReceived() {
         timerViewModel.requestTimer();
     }

     @Override
     public void onClick(View v) {

        if(v.getId() == R.id.send_button){
            handleSendButton();
        }
     }

     private void handleSendButton() {

         String answer = binding.answerEditText.getText().toString();
         if(!answer.equals("")){

             if(this.rightAnswer.equalsIgnoreCase(answer)){

                 float remainingSeconds = Integer.parseInt(timerTextView.getText().toString());
                 timerViewModel.cancelTimer();
                 gameViewModel.updateScorePlayerOne(remainingSeconds);
                 gameViewModel.setWordGuessedFlag(true);
                 sendWinMessage(remainingSeconds);
                 mainHandler.post(()->{
                     Toast.makeText(getContext(), R.string.you_guessed, Toast.LENGTH_SHORT).show();
                 });
                 NavHostFragment.findNavController(this).navigate(R.id.end_round);
             }
             else{
                 mainHandler.post(()->{
                     Toast.makeText(getContext(), R.string.retry, Toast.LENGTH_SHORT).show();
                 });
             }
         }
     }
 }