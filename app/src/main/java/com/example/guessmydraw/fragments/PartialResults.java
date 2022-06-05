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
import android.widget.Toast;

import com.example.guessmydraw.MainActivity;
import com.example.guessmydraw.R;
import com.example.guessmydraw.connection.NetworkEventCallback;
import com.example.guessmydraw.connection.Sender;
import com.example.guessmydraw.connection.messages.DrawMessage;
import com.example.guessmydraw.connection.messages.EndMatchRequestMessage;
import com.example.guessmydraw.databinding.FragmentPartialResultsBinding;
import com.example.guessmydraw.utilities.DisconnectionDialog;
import com.example.guessmydraw.utilities.GameViewModel;

import java.net.InetAddress;

public class PartialResults extends Fragment implements NetworkEventCallback {

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final static String TAG = "PARTIAL_RESULTS";
    private FragmentPartialResultsBinding binding;

    private GameViewModel gameViewModel;
    private Button endMatchButton;

    public PartialResults() {}

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);

        //register for callback to the activity receiver
        ((MainActivity)requireActivity()).registerForReceiver(this);

        // Inflate the layout for this fragment
        binding = FragmentPartialResultsBinding.inflate(inflater, container, false);
        TextView scorePlayerOneTextView = binding.scorePlayerOne;
        TextView scorePlayerTwoTextView = binding.scorePlayerTwo;
        TextView p1Name = binding.p1Name;
        TextView p2Name = binding.p2Name;

        p1Name.setText(gameViewModel.getPlayersName());
        p2Name.setText(gameViewModel.getOpponentsName());

        Log.d(TAG, "score 1: " + gameViewModel.getScorePlayerOne());
        Log.d(TAG, "score 2: " + gameViewModel.getScorePlayerTwo());
        scorePlayerOneTextView.setText(String.valueOf(gameViewModel.getScorePlayerOne()));
        scorePlayerTwoTextView.setText(String.valueOf(gameViewModel.getScorePlayerTwo()));

        this.endMatchButton = binding.endMatchButton;
        int n = gameViewModel.getEndGameRequests();
        if (n != 0){
            if(gameViewModel.isEndRequestFlag()){
                endMatchButton.setEnabled(false);
            }
            String text = requireContext().getString(R.string.end_match) + " (" + n + "/2)";
            mainHandler.post(() -> {
                this.endMatchButton.setText(text);
                if(n == 2){
                    NavHostFragment.findNavController(this).navigate(R.id.end_match);
                }
            });
        }
        this.endMatchButton.setOnClickListener(view -> {
            sendEndingMessage();
            requestEndMatch();
            gameViewModel.setEndRequestFlag(true);
            this.endMatchButton.setEnabled(false);
        });

        Button returnToLobbyButton = binding.returnToLobbyButton;
        returnToLobbyButton.setOnClickListener(view -> {

            gameViewModel.setNextRound();
            NavHostFragment.findNavController(this).navigate(R.id.return_to_lobby);
        });
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Hide status bar
        View windowDecorView = requireActivity().getWindow().getDecorView();
        windowDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void sendEndingMessage() {
        Log.d(TAG, "sending end message to opponent.");
        EndMatchRequestMessage messageToSend = new EndMatchRequestMessage();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Sender.NET_MSG_ID, messageToSend);
        ((MainActivity)requireActivity()).sendMessage(bundle);
    }

    private void requestEndMatch(){
        int n = gameViewModel.askToEndGame();
        String text = requireContext().getString(R.string.end_match) + " (" + n + "/2)";
        mainHandler.post(() -> {
            this.endMatchButton.setText(text);
            if(n == 2){
                Toast.makeText(requireActivity(), R.string.end_match_request_reached, Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).navigate(R.id.end_match);
            }
        });
    }

    @Override
    public void onEndingMessageReceived() {
        requestEndMatch();
    }

    @Override
    public void onAckMessageReceived() {/*EMPTY*/}

    @Override
    public void onStartDrawMessageReceived() {/*EMPTY*/}

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