package com.example.guessmydraw.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.guessmydraw.MainActivity;
import com.example.guessmydraw.R;
import com.example.guessmydraw.databinding.FragmentFirstScreenBinding;
import com.example.guessmydraw.utilities.GameViewModel;

import java.util.Objects;

public class FirstScreen extends Fragment {

    private FragmentFirstScreenBinding binding;
    private EditText playersNameEditText;

    public FirstScreen() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFirstScreenBinding.inflate(inflater, container, false);

        playersNameEditText = binding.playerName;

        binding.discoverButton.setOnClickListener(view -> {
            String name = playersNameEditText.getText().toString();
            if(!name.equals("")){
                GameViewModel gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);
                gameViewModel.setPlayersName(name);

                ((FirstScreenListener) requireActivity()).startDiscovery();
            }
            else{
                Toast.makeText(requireActivity(), R.string.insert_name_string, Toast.LENGTH_LONG).show();
            }
        });

        binding.showWordButton.setOnClickListener(view -> {

            String fragmentLabel = ((MainActivity) requireActivity()).getForegroundFragmentLabel();
            if (fragmentLabel.equals(requireContext().getString(R.string.first_screen_label))){
                NavHostFragment.findNavController(this).navigate(R.id.show_word_list);
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

    public interface FirstScreenListener {
        void startDiscovery();
    }
}