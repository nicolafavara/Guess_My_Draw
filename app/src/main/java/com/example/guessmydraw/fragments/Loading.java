package com.example.guessmydraw.fragments;

import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
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

import com.example.guessmydraw.MainActivity;
import com.example.guessmydraw.R;
import com.example.guessmydraw.connection.NetworkEventCallback;
import com.example.guessmydraw.connection.Sender;
import com.example.guessmydraw.connection.messages.DrawMessage;
import com.example.guessmydraw.connection.messages.HandshakeMessage;
import com.example.guessmydraw.databinding.FragmentLoadingBinding;
import com.example.guessmydraw.utilities.DisconnectionDialog;
import com.example.guessmydraw.utilities.GameViewModel;

import java.net.InetAddress;
import java.util.Objects;

public class Loading extends Fragment implements WifiP2pManager.ConnectionInfoListener, NetworkEventCallback {

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final static String TAG = "LOADING_FRAGMENT";
    private FragmentLoadingBinding binding;

    private GameViewModel gameViewModel;

    public Loading() {}

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
        gameViewModel.init();

        //register for callback to the activity receiver
        ((MainActivity)requireActivity()).registerForReceiver(this);

        // Inflate the layout for this fragment
        binding = FragmentLoadingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //request for wifiP2p requestConnectionInfo() method
        ((MainActivity)requireActivity()).askForConnectionInfo(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Hide status bar
        View windowDecorView = requireActivity().getWindow().getDecorView();
        windowDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {

        if (info.groupOwnerAddress != null) {

            Log.d(TAG, "indirizzo Owner del gruppo: " + info.groupOwnerAddress.getHostAddress());
            // String from WifiP2pInfo struct
            String groupOwnerAddress = info.groupOwnerAddress.getHostAddress();

            Log.d(TAG, "info.groupFormed: " + info.groupFormed + "info.isGroupOwner: " + info.isGroupOwner);
            if (info.groupFormed && info.isGroupOwner) { //we are the group owner

                Log.d(TAG, "I'm the group owner.");
                gameViewModel.setGroupOwnerFlag(true);
                gameViewModel.setMyTurnToDraw(true);
            }
            else if (info.groupFormed) { //we are a peer

                assert groupOwnerAddress != null;
                gameViewModel.setOpponentAddress(groupOwnerAddress);
                gameViewModel.setMyTurnToDraw(false);
                ((MainActivity)requireActivity()).initSenders(groupOwnerAddress);
                //sends a packet to the group owner to let him know the IP address of the peer
                sendHandshakeMessage(true);
            }
            else{
                Log.d(TAG, "nooooooooooooooooooooooooooooooooooooooooooooooooooooooooo");
            }
        }
    }

    /**
     * used by Peer to send address to the GroupOwner
     */
    private void sendHandshakeMessage(boolean inLoop) {

        Log.d(TAG, "sending address to other player.");
        HandshakeMessage messageToSend = new HandshakeMessage();
        Bundle bundle = new Bundle();
        String name = gameViewModel.getPlayersName();
        messageToSend.setPlayersName(name);
        bundle.putParcelable(Sender.NET_MSG_ID, messageToSend);
        if (inLoop){
            ((MainActivity)requireActivity()).sendMessageInLoop(bundle);
        }
        else{
            ((MainActivity)requireActivity()).sendMessage(bundle);
        }
    }

    @Override
    public void onHandshakeMessageReceived(InetAddress address, String opponentsName) {

        Log.d(TAG, "onHandshakeMessageReceived: " + address.getHostAddress());
        if (gameViewModel.getGroupOwnerFlag()){
            //we are the group owner, so we use the message received to save the peer's address
            gameViewModel.setOpponentAddress(Objects.requireNonNull(address.getHostAddress()));
            ((MainActivity)requireActivity()).initSenders(address.getHostAddress());
            sendHandshakeMessage(false);
        }
        else{
            //peer has received the handshake message, it can stop sender
            ((MainActivity)requireActivity()).stopSenderInLoop();
        }

        gameViewModel.setOpponentsName(opponentsName);
        mainHandler.post(() -> {

                String fragmentLabel = ((MainActivity) requireActivity()).getForegroundFragmentLabel();
                if(fragmentLabel.equals(getResources().getString(R.string.loading_label))){
                    Log.d(TAG, "STARTING LOBBY");
                    NavHostFragment.findNavController(this).navigate(R.id.start_lobby);
                }
            }
        );
    }

    @Override
    public void onDrawMessageReceived(DrawMessage msg) {/*EMPTY*/}

    @Override
    public void onAnswerMessageReceived(String answer) {/*EMPTY*/}

    @Override
    public void onWinMessageReceived(float remainingSeconds) {/*EMPTY*/}

    @Override
    public void onTimerExpiredMessage() {/*EMPTY*/}

    @Override
    public void onEndingMessageReceived() {/*EMPTY*/}

    @Override
    public void onAckMessageReceived() {/*EMPTY*/}

    @Override
    public void onStartDrawMessageReceived() {/*EMPTY*/}

    public interface GameCallback {
        void askForConnectionInfo(WifiP2pManager.ConnectionInfoListener listener);
    }
}