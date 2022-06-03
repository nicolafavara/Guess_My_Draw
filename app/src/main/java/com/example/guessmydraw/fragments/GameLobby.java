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
import com.example.guessmydraw.connection.Receiver;
import com.example.guessmydraw.connection.SenderInLoop;
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

    private Button playButton;
    private Button chooseWordButton;

    private TextView roleTextView;
    private TextView wordTextView;
    private MutableLiveData<String> chosenWord;

    private boolean groupOwnerFlag = false;
    private String opponentAddress;

    private Sender sender;
    private SenderInLoop senderInLoop;
    private GameViewModel gameViewModel;

    public GameLobby() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivity activity = (MainActivity) requireActivity();

        //register for callback to the activity receiver
        activity.registerForReceiver(this);

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

        // Inflate the layout for this fragment
        binding = FragmentGameLobbyBinding.inflate(inflater, container, false);

        this.roleTextView = binding.roleText;

        this.wordTextView = binding.word;
        this.wordTextView.setText("");

        this.chooseWordButton = binding.chooseWordButton;
        this.chooseWordButton.setEnabled(true);
        this.chooseWordButton.setVisibility(View.INVISIBLE);
        this.chooseWordButton.setOnClickListener(view -> {

            // TODO: FARE IN QUESTO MODO ANCHE IN ALTRI CASI
            NavDestination dest = NavHostFragment.findNavController(this).getCurrentDestination();
            if (dest == null) return;

            String fragmentLabel = Objects.requireNonNull(dest.getLabel()).toString();
            if (fragmentLabel.equals(requireContext().getString(R.string.game_lobby_label))){
                NavHostFragment.findNavController(this).navigate(R.id.start_word_list);
            }
        });

        this.playButton = binding.playButton;
        this.playButton.setEnabled(false);
        this.playButton.setOnClickListener(view -> {

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
        // Hide status bar
        View windowDecorView = requireActivity().getWindow().getDecorView();
        windowDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);
        groupOwnerFlag = gameViewModel.getGroupOwnerFlag();
        opponentAddress = gameViewModel.getOpponentAddress();
        //TODO CAPIRE PERCHE' A VOLTE E' NULL
        assert opponentAddress != null;
        Log.d(LOG_STRING_GAME_LOBBY, "STARTING SENDER WITH ADDRESS: " + opponentAddress);

        if(gameViewModel.getMyTurnToDraw()){
            this.senderInLoop = new SenderInLoop(opponentAddress);
            this.senderInLoop.start();
        }

        this.sender = new Sender(opponentAddress);
        this.sender.start();

        this.chosenWord = Navigation.findNavController(requireActivity(), R.id.my_nav_host_fragment)
                                .getCurrentBackStackEntry().getSavedStateHandle().getLiveData("chosenWord");
        this.chosenWord.observe(getViewLifecycleOwner(), word -> {

            Log.d(LOG_STRING_GAME_LOBBY, "chosenWord changed: " + word + "(" + chosenWord.getValue() + ")");
            sendAnswer(word);

            gameViewModel.setChoosenWord(word);
            mainHandler.post(()->{
                this.roleTextView.setText(R.string.draw);
                this.wordTextView.setText(word);
                this.chooseWordButton.setVisibility(View.VISIBLE);
                this.chooseWordButton.setEnabled(false);
                //this.playButton.setEnabled(true);
            });
        });

        if(chosenWord.getValue() == null){
            determCurrent();
        }
    }

    private void determCurrent(){

        int currRound = gameViewModel.getRoundNumber();
        Log.d(LOG_STRING_GAME_LOBBY, "Current round is " + currRound);

        if(currRound == 0){
            Log.d(LOG_STRING_GAME_LOBBY, "first round.");
            if(groupOwnerFlag){

                Log.d(LOG_STRING_GAME_LOBBY, "Time to Draw!");
                gameViewModel.setMyTurnToDraw(true);
                this.roleTextView.setText(R.string.draw);
                this.chooseWordButton.setVisibility(View.VISIBLE);
            }
            else{
                Log.d(LOG_STRING_GAME_LOBBY, "Time to Guess!");
                gameViewModel.setMyTurnToDraw(false);
                this.roleTextView.setText(R.string.guess);
            }
        }
        else{
            Log.d(LOG_STRING_GAME_LOBBY, "Round " + currRound + ". ");
            boolean wasMyTurnToDraw = gameViewModel.getMyTurnToDraw();
            Log.d(LOG_STRING_GAME_LOBBY, "wasMyTurnToDraw " +  gameViewModel.getMyTurnToDraw() + ". ");
            if(wasMyTurnToDraw){

                Log.d(LOG_STRING_GAME_LOBBY, "Time to Guess!");
                gameViewModel.setMyTurnToDraw(false);
                this.roleTextView.setText(R.string.guess);
            }
            else{
                Log.d(LOG_STRING_GAME_LOBBY, "Time to Draw!");
                gameViewModel.setMyTurnToDraw(true);
                this.roleTextView.setText(R.string.draw);
                this.chooseWordButton.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * used by current player to send answer to the other player
     */
    private void sendAnswer(String answer) {

        if(chosenWord != null){

            Log.d(LOG_STRING_GAME_LOBBY, "sending answer(" + answer + ") to other player.");
            AnswerMessage messageToSend = new AnswerMessage();
            messageToSend.setAnswer(answer);
            Bundle bundle = new Bundle();
            bundle.putParcelable(Sender.NET_MSG_ID, messageToSend);
            //sender.sendMessage(bundle);

            senderInLoop.sendMessage(bundle);
        }
        else
            Log.d(LOG_STRING_GAME_LOBBY, "chosen word not sent because null. ");
    }

    private void sendAck(){
        AckMessage messageToSend = new AckMessage();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Sender.NET_MSG_ID, messageToSend);
        this.sender.sendMessage(bundle);
    }

    @Override
    public void onAnswerMessageReceived(String answer) {

        sendAck();

        Log.d(LOG_STRING_GAME_LOBBY, "answer received (" + answer + ").");
        gameViewModel.setChoosenWord(answer);
        mainHandler.post(()->{
            // enable button to choose word only after the current player has obtained
            // the IP of the other player to be able to send him the correct answer
            this.playButton.setEnabled(true);
        });
    }

    @Override
    public void onAckMessageReceived() {

        senderInLoop.interrupt();
        mainHandler.post(()-> {
            this.playButton.setEnabled(true);
        });
    }

    @Override
    public void onHandshakeMessageReceived(InetAddress address, String opponentsName) { /*EMPTY*/}

    @Override
    public void onWinMessageReceived() {/*EMPTY*/}

    @Override
    public void onTimerExpiredMessage() {/*EMPTY*/}

    @Override
    public void onEndingMessageReceived() {/*EMPTY*/}

    @Override
    public void onDrawMessageReceived(DrawMessage msg) {/*EMPTY*/}

}