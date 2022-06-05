package com.example.guessmydraw.fragments;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDestination;
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
import java.util.Objects;

public class GameLobby extends Fragment implements NetworkEventCallback {

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final String LOG_STRING_GAME_LOBBY = "GAME_LOBBY";
    private FragmentGameLobbyBinding binding;
    private MainActivity activity;

    private Button playButton;
    private Button chooseWordButton;

    private TextView roleTextView;
    private TextView wordTextView;
    private MutableLiveData<String> chosenWord;

    private GameViewModel gameViewModel;

    public GameLobby() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("DEBUG", "GAMEEEEEEE LOBBYYYYYYYYYYYY");

        activity = (MainActivity) requireActivity();

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                new DisconnectionDialog().show(getChildFragmentManager(), DisconnectionDialog.TAG);
            }
        };
        activity.getOnBackPressedDispatcher().addCallback(this, callback);
        // The callback can be enabled or disabled here or in handleOnBackPressed()
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        //register for callback to the activity receiver
        activity.registerForReceiver(this);

        // Inflate the layout for this fragment
        binding = FragmentGameLobbyBinding.inflate(inflater, container, false);

        roleTextView = binding.roleText;

        wordTextView = binding.word;
        wordTextView.setText("");

        chooseWordButton = binding.chooseWordButton;
        chooseWordButton.setEnabled(true);
        chooseWordButton.setVisibility(View.INVISIBLE);
        chooseWordButton.setOnClickListener(view -> {

            String fragmentLabel = activity.getForegroundFragmentLabel();
            if (fragmentLabel.equals(requireContext().getString(R.string.game_lobby_label))){
                NavHostFragment.findNavController(this).navigate(R.id.start_word_list);
            }
        });

        playButton = binding.playButton;
        playButton.setEnabled(false);
        playButton.setOnClickListener(view -> {

            Log.d(LOG_STRING_GAME_LOBBY, "isMyTurnToDraw " +  gameViewModel.getMyTurnToDraw() + ". ");
            if (gameViewModel.getMyTurnToDraw()){
                if (chosenWord != null){
                    NavHostFragment.findNavController(this).navigate(R.id.start_current_player_games);
                }
                else{
                    Toast.makeText(requireActivity(), "Prima di iniziare, scegli una parola da disegnare", Toast.LENGTH_LONG).show();
                }
            }
            else{
                NavHostFragment.findNavController(this).navigate(R.id.start_other_player_games);
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        //TODO NECESSATIO?
        activity = (MainActivity) requireActivity();
        // Hide status bar
        View windowDecorView = activity.getWindow().getDecorView();
        windowDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gameViewModel = new ViewModelProvider(activity).get(GameViewModel.class);

        chosenWord = Navigation.findNavController(activity, R.id.my_nav_host_fragment)
                                .getCurrentBackStackEntry().getSavedStateHandle().getLiveData("chosenWord");
        chosenWord.observe(getViewLifecycleOwner(), word -> {

            Log.d(LOG_STRING_GAME_LOBBY, "chosenWord changed: " + word + "(" + chosenWord.getValue() + ")");
            sendAnswer(word);

            gameViewModel.setChoosenWord(word);
            mainHandler.post(()->{
                wordTextView.setText(word);
                chooseWordButton.setEnabled(false);
            });
        });

        Log.d(LOG_STRING_GAME_LOBBY, gameViewModel.toString());
        determCurrent();
        if (gameViewModel.isAckMessageFlag()){
            playButton.setEnabled(true);
        }
    }

    private void determCurrent(){

        int currRound = gameViewModel.getRoundNumber();
        Log.d(LOG_STRING_GAME_LOBBY, "Current round is " + currRound);

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
        Log.d(LOG_STRING_GAME_LOBBY, "Time to Draw!");
        roleTextView.setText(R.string.draw);
        chooseWordButton.setVisibility(View.VISIBLE);
        gameViewModel.setMyTurnToDraw(true);
    }

    private void setGuesserUI(){
        Log.d(LOG_STRING_GAME_LOBBY, "Time to Guess!");
        roleTextView.setText(R.string.guess);
        gameViewModel.setMyTurnToDraw(false);
    }

    /**
     * used by current player to send answer to the other player
     */
    private void sendAnswer(@NonNull String answer) {

        Log.d(LOG_STRING_GAME_LOBBY, "sending answer(" + answer + ") to other player.");
        AnswerMessage messageToSend = new AnswerMessage();
        messageToSend.setAnswer(answer);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Sender.NET_MSG_ID, messageToSend);
        activity.sendMessageInLoop(bundle);
    }

    private void sendAck(){
        AckMessage messageToSend = new AckMessage();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Sender.NET_MSG_ID, messageToSend);
        activity.sendMessage(bundle);
    }

    @Override
    public void onAnswerMessageReceived(String answer) {

        sendAck();
        gameViewModel.setAckMessageFlag(true);

        Log.d(LOG_STRING_GAME_LOBBY, "answer received (" + answer + ").");
        gameViewModel.setChoosenWord(answer);
        mainHandler.post(()->{
            // enable button to choose word only after the current player has obtained
            // the IP of the other player to be able to send him the correct answer
            playButton.setEnabled(true);
        });
    }

    @Override
    public void onAckMessageReceived() {

        //TODO gameViewModel può essere null se il metodo viene chiamato non appena si accede a questo fragment
        if(gameViewModel != null){

            gameViewModel.setAckMessageFlag(true);
            activity.stopSenderInLoop();
            mainHandler.post(()-> {
                playButton.setEnabled(true);
            });
        }
        else{
            Log.e(LOG_STRING_GAME_LOBBY, "GameViewModel is NULL");
        }
    }

    @Override
    public void onStartDrawMessageReceived() {
        gameViewModel.setStartDrawFlag(true);
    }

    @Override
    public void onEndingMessageReceived() {
        //TODO CHECK
        //this message can be received here if the other player is still in the partial result fragment
        //and click on the End match button
        int n = gameViewModel.askToEndGame();
        if (n == 2){
            mainHandler.post(() -> {
                Toast.makeText(activity, "Numero di richieste per terminare la partita raggiunte.", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).navigate(R.id.end_match);
            });
        }
    }

    @Override
    public void onHandshakeMessageReceived(InetAddress address, String opponentsName) { /*EMPTY*/}

    @Override
    public void onWinMessageReceived() {/*EMPTY*/}

    @Override
    public void onTimerExpiredMessage() {/*EMPTY*/}

    @Override
    public void onDrawMessageReceived(DrawMessage msg) {/*EMPTY*/}

}