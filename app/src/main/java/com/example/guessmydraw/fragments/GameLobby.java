package com.example.guessmydraw.fragments;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
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
import com.example.guessmydraw.connection.messages.AckMessage;
import com.example.guessmydraw.connection.messages.AnswerMessage;
import com.example.guessmydraw.connection.messages.DrawMessage;
import com.example.guessmydraw.connection.NetworkEventCallback;
import com.example.guessmydraw.connection.Sender;
import com.example.guessmydraw.databinding.FragmentGameLobbyBinding;
import com.example.guessmydraw.utilities.DisconnectionDialog;
import com.example.guessmydraw.utilities.GameViewModel;

import java.net.InetAddress;

public class GameLobby extends Fragment implements NetworkEventCallback, View.OnClickListener{

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final String TAG = "GAME_LOBBY";
    private FragmentGameLobbyBinding binding;

    // viewModel used to save all the information needed for the match
    private GameViewModel gameViewModel;

    private Button playButton;
    private Button chooseWordButton;

    private MutableLiveData<String> chosenWord;
    private TextView roleTextView;
    private TextView wordTextView;

    public GameLobby() {}

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);

        Log.d(TAG, "Registering for receiver...");
        //register for callback to the activity receiver
        ((MainActivity)requireActivity()).registerForReceiver(this);

        // Inflate the layout for this fragment
        binding = FragmentGameLobbyBinding.inflate(inflater, container, false);

        roleTextView = binding.roleText;

        wordTextView = binding.word;
        wordTextView.setText("");

        chooseWordButton = binding.chooseWordButton;
        chooseWordButton.setEnabled(true);
        chooseWordButton.setVisibility(View.INVISIBLE);
        chooseWordButton.setOnClickListener(this);

        playButton = binding.playButton;
        playButton.setEnabled(false);
        playButton.setOnClickListener(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(requireActivity(), R.id.my_nav_host_fragment);
        chosenWord = navController.getCurrentBackStackEntry().getSavedStateHandle().getLiveData("chosenWord");
        chosenWord.observe(getViewLifecycleOwner(), word -> {

            Log.d(TAG, "chosenWord changed: " + word + "(" + chosenWord.getValue() + ")");
            sendAnswer(word);

            gameViewModel.setChoosenWord(word);
            mainHandler.post(()->{
                wordTextView.setText(word);
                chooseWordButton.setEnabled(false);
            });
        });

        //DEBUG
        //Log.d(TAG, gameViewModel.toString());

        determCurrent();
        if (gameViewModel.isAckMessageFlag()){
            playButton.setEnabled(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Hide status bar
        View windowDecorView = requireActivity().getWindow().getDecorView();
        windowDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    /**
     * Method that changes the interface based on the player's role
     */
    private void determCurrent(){

        int currRound = gameViewModel.getRoundNumber();
        Log.d(TAG, "Current round is " + currRound);

        if(gameViewModel.getGroupOwnerFlag()){
            if(currRound % 2 == 0){
                setDrawerUI();
            }
            else{
                setGuesserUI();
            }
        }
        else{
            if(currRound % 2 == 0){
                setGuesserUI();
            }
            else{
                setDrawerUI();
            }
        }
    }

    private void setDrawerUI(){
        Log.d(TAG, "Time to Draw!");
        roleTextView.setText(R.string.draw_role);
        chooseWordButton.setVisibility(View.VISIBLE);
        gameViewModel.setMyTurnToDraw(true);
    }

    private void setGuesserUI(){
        Log.d(TAG, "Time to Guess!");
        roleTextView.setText(R.string.guess);
        gameViewModel.setMyTurnToDraw(false);
    }

    /**
     * used by current player to send answer to the other player
     */
    private void sendAnswer(@NonNull String answer) {
        Log.d(TAG, "sending answer(" + answer + ") to other player.");
        AnswerMessage messageToSend = new AnswerMessage();
        messageToSend.setAnswer(answer);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Sender.NET_MSG_ID, messageToSend);
        ((MainActivity)requireActivity()).sendMessageInLoop(bundle);
    }

    /**
     * Sends an ack message to let the opponent know that the word to be guessed has been received
     */
    private void sendAck(){
        AckMessage messageToSend = new AckMessage();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Sender.NET_MSG_ID, messageToSend);
        ((MainActivity)requireActivity()).sendMessage(bundle);
    }

    @Override
    public void onAnswerMessageReceived(String answer) {
        sendAck();
        gameViewModel.setAckMessageFlag(true);

        Log.d(TAG, "answer received (" + answer + ").");
        gameViewModel.setChoosenWord(answer);
        mainHandler.post(()->{
            // we enable button only once the word to be drawn has been received
            playButton.setEnabled(true);
        });
    }

    @Override
    public void onAckMessageReceived() {
        gameViewModel.setAckMessageFlag(true);

        // by receiving the ack we are assured that the opponent got the word to guess,
        // so we are ready to play. We can then enable the button to start the game
        // and stop senderInLoop which is still sending the word in a loop.
        ((MainActivity)requireActivity()).stopSenderInLoop();
        mainHandler.post(()-> {
            playButton.setEnabled(true);
        });
    }

    @Override
    public void onStartDrawMessageReceived() {
        gameViewModel.setStartDrawFlag(true);
    }

    @Override
    public void onEndingMessageReceived() {
        // this message can be received here if the other player is still in the partial result fragment
        // and click on the End match button
        int n = gameViewModel.askToEndGame();
        if (n == 2){
            mainHandler.post(() -> {
                Toast.makeText(requireActivity(), R.string.end_match_request_reached, Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).navigate(R.id.end_match);
            });
        }
    }

    @Override
    public void onHandshakeMessageReceived(InetAddress address, String opponentsName) { /*EMPTY*/}

    @Override
    public void onWinMessageReceived(float remainingSeconds) {/*EMPTY*/}

    @Override
    public void onTimerExpiredMessage() {/*EMPTY*/}

    @Override
    public void onDrawMessageReceived(DrawMessage msg) {/*EMPTY*/}

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.choose_word_button){
            NavHostFragment.findNavController(this).navigate(R.id.start_word_list);
        }
        else if(v.getId() == R.id.play_button){
            handlePlayButton();
        }
    }

    private void handlePlayButton() {
        Log.d(TAG, "isMyTurnToDraw " + gameViewModel.getMyTurnToDraw() + ". ");
        if (gameViewModel.getMyTurnToDraw()) {
            if (chosenWord != null) {
                NavHostFragment.findNavController(this).navigate(R.id.start_current_player_games);
            }
        } else {
            NavHostFragment.findNavController(this).navigate(R.id.start_other_player_games);
        }
    }
}