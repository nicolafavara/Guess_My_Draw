package com.example.guessmydraw.fragments;

import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
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
import com.example.guessmydraw.connection.messages.AnswerMessage;
import com.example.guessmydraw.connection.messages.DrawMessage;
import com.example.guessmydraw.connection.messages.HandshakeMessage;
import com.example.guessmydraw.connection.NetworkEventCallback;
import com.example.guessmydraw.connection.Sender;
import com.example.guessmydraw.databinding.FragmentGameLobbyBinding;
import com.example.guessmydraw.utilities.DisconnectionDialog;

import java.net.InetAddress;

public class GameLobby extends Fragment implements WifiP2pManager.ConnectionInfoListener, NetworkEventCallback {

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private FragmentGameLobbyBinding binding;
    private String groupOwnerAddress = null;
    private Button playButton;
    private Button chooseWordButton;

    private TextView roleTextView;
    private TextView wordTextView;
    private MutableLiveData<String> chosenWord;
    private String answer = null;

    private boolean isMyTurnToDraw = false;
    private boolean isGroupOwner = false;
    private String peerAddress;

    private Sender sender;
    private Receiver receiver;

    public GameLobby() {
        this.receiver = new Receiver(this);
        this.receiver.start();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("DEBUG", "inside onCreate() ! ! !");

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
        // Inflate the layout for this fragment
        binding = FragmentGameLobbyBinding.inflate(inflater, container, false);

        this.roleTextView = binding.roleText;

        this.wordTextView = binding.word;
        this.wordTextView.setText("");

        this.chooseWordButton = binding.chooseWordButton;
        this.chooseWordButton.setVisibility(View.GONE);
        this.chooseWordButton.setEnabled(false);
        this.chooseWordButton.setOnClickListener(view -> {
            NavHostFragment.findNavController(this).navigate(R.id.choose_word);
        });

        this.playButton = binding.playButton;
        this.playButton.setEnabled(false);
        this.playButton.setOnClickListener(view -> {

            if (isMyTurnToDraw){
                if (chosenWord != null){
                    GameLobbyDirections.StartCurrentPlayerGames action = GameLobbyDirections.startCurrentPlayerGames(this.peerAddress, chosenWord.getValue());
                    Log.d("DEBUG", "changing to current player canvas fragment");
                    NavHostFragment.findNavController(this).navigate(action);
                }
                else{
                    Toast.makeText(requireActivity(), "Prima di iniziare, scegli una parola da disegnare", Toast.LENGTH_LONG).show();
                }
            }
            else{
                GameLobbyDirections.StartOtherPlayerGames action = GameLobbyDirections.startOtherPlayerGames(this.groupOwnerAddress, this.answer);
                Log.d("DEBUG", "changing to other player canvas fragment");
                NavHostFragment.findNavController(this).navigate(action);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((GameCallback) requireActivity()).askForConnectionInfo(this);

        this.chosenWord = Navigation.findNavController(requireActivity(), R.id.my_nav_host_fragment)
                                .getCurrentBackStackEntry().getSavedStateHandle().getLiveData("chosenWord");
        this.chosenWord.observe(getViewLifecycleOwner(), s -> {
            Log.d("DEBUG", "chosenWord changed: " + s + "(" + chosenWord.getValue() + ")");
            // Do something with the result.
            sendAnswer(chosenWord.getValue());
            mainHandler.post(()->{
                // enable button to choose word only after the current player has obtained
                // the IP of the other player to be able to send him the correct answer
                this.playButton.setEnabled(true);
            });
        });
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {

        if (info.groupOwnerAddress != null) {

            Log.d("DEBUG", "indirizzo Owner del gruppo: " + info.groupOwnerAddress.getHostAddress());
            // String from WifiP2pInfo struct
            this.groupOwnerAddress = info.groupOwnerAddress.getHostAddress();

            Log.d("DEBUG", "info.groupFormed: " + info.groupFormed + "info.isGroupOwner: " + info.isGroupOwner);
            if (info.groupFormed && info.isGroupOwner) { //we are the group owner

                isGroupOwner = true;
                isMyTurnToDraw = true;
                this.roleTextView.setText(R.string.draw);
                this.chooseWordButton.setVisibility(View.VISIBLE);
            }
            else if (info.groupFormed) { //we are a peer

                this.sender = new Sender(groupOwnerAddress);
                this.sender.start();
                //sends a packet to the group owner to let him know the IP address of the peer
                sendAddress();
                this.roleTextView.setText(R.string.guess);

            }
        }
    }

    /**
     * used by Peer to send address to the GroupOwner
     */
    private void sendAddress() {

        Log.d("DEBUG", "sending address to other player.");
        HandshakeMessage messageToSend = new HandshakeMessage();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Sender.NET_MSG_ID, messageToSend);
        this.sender.sendMessage(bundle);
    }

    /**
     * used by current player to send answer to the other player
     */
    private void sendAnswer(String answer) {

        if(chosenWord != null){

            Log.d("DEBUG", "sending answer(" + answer + ") to other player.");
            AnswerMessage messageToSend = new AnswerMessage();
            messageToSend.setAnswer(answer);
            Bundle bundle = new Bundle();
            bundle.putParcelable(Sender.NET_MSG_ID, messageToSend);
            this.sender.sendMessage(bundle);
        }
        else
            Log.d("DEBUG", "chosen word not sent because null. ");
    }

    @Override
    public void onHandshakeMessageReceived(InetAddress address) {

        Log.d("DEBUG", "received peer address: " + address.getHostAddress());
        peerAddress = address.getHostAddress();

        this.sender = new Sender(peerAddress);
        this.sender.start();

        mainHandler.post(()->{
            // enable button to choose word only after the current player has obtained
            // the IP of the other player to be able to send him the correct answer
            chooseWordButton.setEnabled(true);
        });
    }

    @Override
    public void onAnswerMessageReceived(String answer) {

        Log.d("DEBUG", "answer received (" + answer + ").");
        this.answer = answer;

        mainHandler.post(()->{
            // enable button to choose word only after the current player has obtained
            // the IP of the other player to be able to send him the correct answer
            this.playButton.setEnabled(true);
        });
    }

    @Override
    public void onWinMessageReceived() {/*EMPTY*/}

    @Override
    public void onDrawMessageReceived(DrawMessage msg) {/*EMPTY*/}

    public interface GameCallback {
        void askForConnectionInfo(WifiP2pManager.ConnectionInfoListener listener);
    }
}