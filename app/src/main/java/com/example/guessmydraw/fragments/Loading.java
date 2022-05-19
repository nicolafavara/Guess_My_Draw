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
import android.widget.TextView;

import com.example.guessmydraw.R;
import com.example.guessmydraw.connection.NetworkEventCallback;
import com.example.guessmydraw.connection.Receiver;
import com.example.guessmydraw.connection.Sender;
import com.example.guessmydraw.connection.messages.DrawMessage;
import com.example.guessmydraw.connection.messages.HandshakeMessage;
import com.example.guessmydraw.databinding.FragmentLoadingBinding;
import com.example.guessmydraw.utilities.DisconnectionDialog;
import com.example.guessmydraw.utilities.GameViewModel;

import java.net.InetAddress;

public class Loading extends Fragment implements WifiP2pManager.ConnectionInfoListener, NetworkEventCallback {

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private FragmentLoadingBinding binding;
    private String groupOwnerAddress;
    private boolean groupOwnerFlag = false;

    private Sender sender;
    private Receiver receiver;

    private TextView addressTextView;
    private GameViewModel gameViewModel;

    public Loading() {
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
        binding = FragmentLoadingBinding.inflate(inflater, container, false);
        this.addressTextView = binding.address;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);
        gameViewModel.init();
        ((Loading.GameCallback) requireActivity()).askForConnectionInfo(this);
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {

        if (info.groupOwnerAddress != null) {

            Log.d("DEBUG", "indirizzo Owner del gruppo: " + info.groupOwnerAddress.getHostAddress());
            // String from WifiP2pInfo struct
            this.groupOwnerAddress = info.groupOwnerAddress.getHostAddress();

            Log.d("DEBUG", "info.groupFormed: " + info.groupFormed + "info.isGroupOwner: " + info.isGroupOwner);
            if (info.groupFormed && info.isGroupOwner) { //we are the group owner

                this.groupOwnerFlag = true;
                Log.d("DEBUG", "I'm the group owner.");
                this.gameViewModel.setGroupOwnerFlag(true);
            }
            else if (info.groupFormed) { //we are a peer

                this.gameViewModel.setOpponentAddress(groupOwnerAddress);
                this.sender = new Sender(groupOwnerAddress);
                this.sender.start();
                //sends a packet to the group owner to let him know the IP address of the peer
                sendHandshakeMessage();
            }
            else{
                Log.d("DEBUG", "nooooooooooooooooooooooooooooooooooooooooooooooooooooooooo");
            }
        }
    }

    /**
     * used by Peer to send address to the GroupOwner
     */
    private void sendHandshakeMessage() {

        Log.d("DEBUG", "sending address to other player.");
        HandshakeMessage messageToSend = new HandshakeMessage();
        Bundle bundle = new Bundle();
        String name = gameViewModel.getPlayersName();
        messageToSend.setPlayersName(name);
        bundle.putParcelable(Sender.NET_MSG_ID, messageToSend);
        this.sender.sendMessage(bundle);
    }

    @Override
    public void onHandshakeMessageReceived(InetAddress address, String opponentsName) {

        Log.d("DEBUG", "onHandshakeMessageReceived: " + address.getHostAddress());
        if (groupOwnerFlag){   //if we are the peer we already know the opponent's IP (the groupOwner's)
            this.gameViewModel.setOpponentAddress(address.getHostAddress());
            this.sender = new Sender(address.getHostAddress());
            this.sender.start();
            sendHandshakeMessage();
        }
        this.gameViewModel.setOpponentsName(opponentsName);
        mainHandler.post(() -> {
                Log.d("DEBUG", "STARTING LOBBY");
                NavHostFragment.findNavController(this).navigate(R.id.start_lobby);
            }
        );
    }

    @Override
    public void onDrawMessageReceived(DrawMessage msg) {/*EMPTY*/}

    @Override
    public void onAnswerMessageReceived(String answer) {/*EMPTY*/}

    @Override
    public void onWinMessageReceived() {/*EMPTY*/}

    @Override
    public void onTimerExpiredMessage() {/*EMPTY*/}

    @Override
    public void onEndingMessageReceived() {/*EMPTY*/}

    public interface GameCallback {
        void askForConnectionInfo(WifiP2pManager.ConnectionInfoListener listener);
    }
}