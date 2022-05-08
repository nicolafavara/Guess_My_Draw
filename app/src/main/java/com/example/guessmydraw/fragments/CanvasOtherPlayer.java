 package com.example.guessmydraw.fragments;

import android.content.Context;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.guessmydraw.MainActivity;
import com.example.guessmydraw.connection.messages.AnswerMessage;
import com.example.guessmydraw.connection.Sender;
import com.example.guessmydraw.connection.messages.WinMessage;
import com.example.guessmydraw.databinding.FragmentCanvasOtherPlayerBinding;
import com.example.guessmydraw.utilities.DisconnectionDialog;

public class CanvasOtherPlayer extends Fragment {

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private FragmentCanvasOtherPlayerBinding binding;

    private String currentPlayerAddress;
    private String rightAnswer;
    private Sender sender;
    private Bundle bundle;

    public CanvasOtherPlayer() {
        // Required empty public constructor
        this.bundle = new Bundle();
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
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCanvasOtherPlayerBinding.inflate(inflater, container, false);

        binding.sendButton.setOnClickListener(view -> {

            String answer = binding.answerEditText.getText().toString();
            if(!answer.equals("")){

                if(this.rightAnswer.equals(answer)){
                    sendWinMessage();
                    mainHandler.post(()->{
                        Toast.makeText(getContext(), "Hai indovinato!!!", Toast.LENGTH_SHORT).show();
                    });
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

        this.currentPlayerAddress = CanvasOtherPlayerArgs.fromBundle(getArguments()).getCurrentPlayerAddress();
        this.rightAnswer = CanvasOtherPlayerArgs.fromBundle(getArguments()).getRightAnswer();
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


}