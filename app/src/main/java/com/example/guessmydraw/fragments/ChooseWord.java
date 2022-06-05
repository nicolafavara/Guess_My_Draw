package com.example.guessmydraw.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.guessmydraw.MainActivity;
import com.example.guessmydraw.R;
import com.example.guessmydraw.connection.NetworkEventCallback;
import com.example.guessmydraw.connection.messages.DrawMessage;
import com.example.guessmydraw.databinding.FragmentChooseWordBinding;
import com.example.guessmydraw.databinding.FragmentGameLobbyBinding;
import com.example.guessmydraw.databinding.FragmentWordListBinding;
import com.example.guessmydraw.utilities.GameViewModel;
import com.example.guessmydraw.wordViewModel.WordListAdapter;
import com.example.guessmydraw.wordViewModel.WordToChooseListAdapter;
import com.example.guessmydraw.wordViewModel.WordViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.net.InetAddress;

public class ChooseWord extends Fragment implements NetworkEventCallback {

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private FragmentChooseWordBinding binding;
    private final String TAG = "CHOOSE_WORD";

    private GameViewModel gameViewModel;

    public ChooseWord() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);

        //register for callback to the activity receiver
        ((MainActivity)requireActivity()).registerForReceiver(this);

        // Inflate the layout for this fragment
        binding = FragmentChooseWordBinding.inflate(inflater, container, false);

        RecyclerView recyclerView = binding.recyclerview;
        final WordToChooseListAdapter adapter = new WordToChooseListAdapter(new WordToChooseListAdapter.WordDiff(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        WordViewModel wordViewModel = new ViewModelProvider(this).get(WordViewModel.class);
        wordViewModel.getAllWords().observe(getViewLifecycleOwner(), words -> {
            // Update the cached copy of the words in the adapter.
            adapter.submitList(words);
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
    public void onEndingMessageReceived() {
        int n = gameViewModel.askToEndGame();
        if (n == 2){
            mainHandler.post(() -> {
                Toast.makeText(requireActivity(), R.string.end_match_request_reached, Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).navigate(R.id.end_match);
            });
        }
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

    @Override
    public void onAckMessageReceived() {/*EMPTY*/}

    @Override
    public void onStartDrawMessageReceived() {/*EMPTY*/}
}