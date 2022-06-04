 package com.example.guessmydraw.fragments;

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
import android.widget.Toast;

import com.example.guessmydraw.MainActivity;
import com.example.guessmydraw.R;
import com.example.guessmydraw.connection.messages.DrawMessage;
import com.example.guessmydraw.connection.NetworkEventCallback;
import com.example.guessmydraw.connection.Receiver;
import com.example.guessmydraw.connection.Sender;
import com.example.guessmydraw.databinding.FragmentCanvasCurrentPlayerBinding;
import com.example.guessmydraw.fragments.Views.CurrentPlayerCanvasView;
import com.example.guessmydraw.utilities.DisconnectionDialog;
import com.example.guessmydraw.utilities.GameViewModel;

import java.net.InetAddress;

 public class CanvasCurrentPlayer extends Fragment implements NetworkEventCallback, View.OnClickListener {

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private FragmentCanvasCurrentPlayerBinding binding;
    private MainActivity activity;

    private DrawMessage messageToSend;
    private Bundle bundle;

    private GameViewModel gameViewModel;
    private CurrentPlayerCanvasView canvasView;

    public CanvasCurrentPlayer() {
        bundle = new Bundle();
        messageToSend = new DrawMessage();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //register for callback to the activity receiver
        activity.registerForReceiver(this);

        // Inflate the layout for this fragment
        binding = FragmentCanvasCurrentPlayerBinding.inflate(inflater, container, false);
        binding.buttonColorBlack.setOnClickListener(this);
        binding.buttonColorWhite.setOnClickListener(this);
        binding.buttonColorRed.setOnClickListener(this);
        binding.buttonColorOrange.setOnClickListener(this);
        binding.buttonColorYellow.setOnClickListener(this);
        binding.buttonColorGreen.setOnClickListener(this);
        binding.buttonColorBlue.setOnClickListener(this);
        binding.buttonColorLightBlue.setOnClickListener(this);
        binding.buttonColorBrown.setOnClickListener(this);
        binding.buttonColorGray.setOnClickListener(this);
        binding.buttonColorPurple.setOnClickListener(this);
        binding.buttonColorViolet.setOnClickListener(this);

        gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);
        String wordToDraw = gameViewModel.getChoosenWord();
        binding.wordToDraw.setText(wordToDraw);
        canvasView = binding.currentPlayerCanvasView;

        // TODO SE IL GIOCATORE CORRENTE E' ANCORA NEL PARTIAL RESULT FRAGMENT...
        if(!gameViewModel.isStartDrawFlag()){
            canvasView.setVisibility(View.INVISIBLE);
        }

        return binding.getRoot();
    }

     @Override
     public void onResume() {
         super.onResume();
         activity = (MainActivity) requireActivity();
     }

     public void sendMessageOverNetwork(float currentX, float currentY, float x2, float y2, int motionEventAction, int paintColor) {

        messageToSend.setCurrentX(currentX);
        messageToSend.setCurrentY(currentY);
        messageToSend.setX2(x2);
        messageToSend.setY2(y2);
        messageToSend.setMotionEventAction(motionEventAction);
        messageToSend.setPaintColor(paintColor);

        bundle.clear();
        bundle.putParcelable(Sender.NET_MSG_ID, messageToSend);

        activity.sendMessage(bundle);
    }

     @Override
     public void onWinMessageReceived() {

         gameViewModel.updateScorePlayerTwo();

         mainHandler.post(()->{
             Toast.makeText(getContext(), "Il tuo avversario ha indovinato!", Toast.LENGTH_LONG).show();
             NavHostFragment.findNavController(this).navigate(R.id.end_round);
         });
     }

     @Override
     public void onTimerExpiredMessage() {
         mainHandler.post(()->{
             Toast.makeText(getContext(), "Il tuo avversario ha perso!", Toast.LENGTH_LONG).show();
             NavHostFragment.findNavController(this).navigate(R.id.end_round);
         });
     }

     @Override
     public void onStartDrawMessageReceived() {
         gameViewModel.setStartDrawFlag(true);
         mainHandler.post(()->{
             canvasView.setVisibility(View.VISIBLE);
         });
     }

     @Override
     public void onClick(View v) {

         int color = -1;

         if (v.getId() == R.id.button_color_black){
             color = getResources().getColor(R.color.black);
         }
         else if (v.getId() == R.id.button_color_white){
             color = getResources().getColor(R.color.white);
         }
         else if (v.getId() == R.id.button_color_red){
             color = getResources().getColor(R.color.red);
         }
         else if (v.getId() == R.id.button_color_orange){
             color = getResources().getColor(R.color.orange);
         }
         else if (v.getId() == R.id.button_color_yellow){
             color = getResources().getColor(R.color.yellow);
         }
         else if (v.getId() == R.id.button_color_green){
             color = getResources().getColor(R.color.green);
         }
         else if (v.getId() == R.id.button_color_blue){
             color = getResources().getColor(R.color.blue);
         }
         else if (v.getId() == R.id.button_color_light_blue){
             color = getResources().getColor(R.color.light_blue);
         }
         else if (v.getId() == R.id.button_color_brown){
             color = getResources().getColor(R.color.brown);
         }
         else if (v.getId() == R.id.button_color_gray){
             color = getResources().getColor(R.color.gray);
         }
         else if (v.getId() == R.id.button_color_purple){
             color = getResources().getColor(R.color.purple);
         }
         else if (v.getId() == R.id.button_color_violet){
             color = getResources().getColor(R.color.violet);
         }

         Log.d("DEBUG", "COLOR CHANGED.");
         canvasView.changePaintColor(color);
     }

     @Override
     public void onEndingMessageReceived() {/*EMPTY*/}

     @Override
     public void onAckMessageReceived() {/*EMPTY*/}

     @Override
     public void onAnswerMessageReceived(String answer) {/*EMPTY*/}

     @Override
     public void onHandshakeMessageReceived(InetAddress address, String opponentsName) {/*EMPTY*/}

     @Override
     public void onDrawMessageReceived(DrawMessage msg) {/*EMPTY*/}
 }