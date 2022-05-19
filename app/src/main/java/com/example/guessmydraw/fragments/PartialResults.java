package com.example.guessmydraw.fragments;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.guessmydraw.R;
import com.example.guessmydraw.connection.NetworkEventCallback;
import com.example.guessmydraw.connection.Receiver;
import com.example.guessmydraw.connection.Sender;
import com.example.guessmydraw.connection.messages.AnswerMessage;
import com.example.guessmydraw.connection.messages.DrawMessage;
import com.example.guessmydraw.connection.messages.EndingMessage;
import com.example.guessmydraw.databinding.FragmentPartialResultsBinding;
import com.example.guessmydraw.utilities.DisconnectionDialog;
import com.example.guessmydraw.utilities.GameViewModel;

import java.net.InetAddress;
import java.util.Objects;

public class PartialResults extends Fragment implements NetworkEventCallback {

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private FragmentPartialResultsBinding binding;
    private String LOG_STRING_PARTIAL_RESULTS = "PARTIAL_RESULTS";
    private GameViewModel gameViewModel;

    private Button endMatchButton;

    private Sender sender;
    private Receiver receiver;

    public PartialResults() {
        // Required empty public constructor
        this.receiver = new Receiver(this);
        this.receiver.start();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPartialResultsBinding.inflate(inflater, container, false);
        TextView scorePlayerOneTextView = binding.scorePlayerOne;
        TextView scorePlayerTwoTextView = binding.scorePlayerTwo;
        TextView p1Name = binding.p1Name;
        TextView p2Name = binding.p2Name;

        gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);

        String opponentAddress = gameViewModel.getOpponentAddress();
        this.sender = new Sender(opponentAddress);
        this.sender.start();

        p1Name.setText(gameViewModel.getPlayersName());
        p2Name.setText(gameViewModel.getOpponentsName());

        Log.d(LOG_STRING_PARTIAL_RESULTS, "score 1: " + gameViewModel.getScorePlayerOne());
        Log.d(LOG_STRING_PARTIAL_RESULTS, "score 2: " + gameViewModel.getScorePlayerTwo());
        scorePlayerOneTextView.setText(String.valueOf(gameViewModel.getScorePlayerOne()));
        scorePlayerTwoTextView.setText(String.valueOf(gameViewModel.getScorePlayerTwo()));

        this.endMatchButton = binding.endMatchButton;
        this.endMatchButton.setOnClickListener(view -> {

            sendEndingMessage();
            endMatch();
            this.endMatchButton.setEnabled(false);
        });

        Button returnToLobbyButton = binding.returnToLobbyButton;
        returnToLobbyButton.setOnClickListener(view -> {

            gameViewModel.setNextRound();
            NavHostFragment.findNavController(this).navigate(R.id.return_to_lobby);
        });
        return binding.getRoot();
    }

    private void sendEndingMessage() {
        Log.d(LOG_STRING_PARTIAL_RESULTS, "sending end message to opponent.");
        EndingMessage messageToSend = new EndingMessage();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Sender.NET_MSG_ID, messageToSend);
        this.sender.sendMessage(bundle);
    }

    @Override
    public void onEndingMessageReceived() {
        endMatch();
    }

    private void endMatch(){
        int n = gameViewModel.askToEndGame();
        String text = requireContext().getString(R.string.end_match) + " (" + n + "/2)";
        mainHandler.post(() -> {
            this.endMatchButton.setText(text);
            if(n == 2){
                NavHostFragment.findNavController(this).navigate(R.id.end_match);
            }
        });
    }

    @Override
    public void onHandshakeMessageReceived(InetAddress address, String opponentsName) {/*EMPTY*/}

    @Override
    public void onDrawMessageReceived(DrawMessage msg) {/*EMPTY*/}

    @Override
    public void onAnswerMessageReceived(String answer) {/*EMPTY*/}

    @Override
    public void onWinMessageReceived() {/*EMPTY*/}

    @Override
    public void onTimerExpiredMessage() {/*EMPTY*/}
}