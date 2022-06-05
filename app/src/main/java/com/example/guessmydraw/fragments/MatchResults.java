package com.example.guessmydraw.fragments;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.guessmydraw.R;
import com.example.guessmydraw.databinding.FragmentMatchResultsBinding;
import com.example.guessmydraw.utilities.DisconnectionDialog;
import com.example.guessmydraw.utilities.GameViewModel;

public class MatchResults extends Fragment {

    private FragmentMatchResultsBinding binding;

    public MatchResults() {}

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

        //if we are in this fragment then the game is over, so we perform the disconnect
        ((DeviceList.DeviceActionListener) requireActivity()).disconnect();

        GameViewModel gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);

        // Inflate the layout for this fragment
        binding = FragmentMatchResultsBinding.inflate(inflater, container, false);

        TextView textView = binding.resultText;
        int score1 = gameViewModel.getScorePlayerOne();
        int score2 = gameViewModel.getScorePlayerTwo();

        if (score1 > score2){
            textView.setText(R.string.you_win);
        }
        else if(score1 == score2){
            textView.setText(R.string.draw_text);
        }
        else{
            textView.setText(R.string.defeat_text);
        }

        Button button = binding.returnToFirstScreenButton;
        button.setOnClickListener(view -> {
            NavHostFragment.findNavController(this).navigate(R.id.return_to_first_screen);
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
}