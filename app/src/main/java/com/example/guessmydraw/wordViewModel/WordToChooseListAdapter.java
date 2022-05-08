package com.example.guessmydraw.wordViewModel;

import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.guessmydraw.MainActivity;
import com.example.guessmydraw.R;
import com.example.guessmydraw.fragments.ChooseWord;
import com.example.guessmydraw.fragments.ChooseWordDirections;
import com.example.guessmydraw.fragments.GameLobbyDirections;


public class WordToChooseListAdapter extends ListAdapter<Word, WordViewHolder> {

    private Fragment fragment;

    public WordToChooseListAdapter(@NonNull DiffUtil.ItemCallback<Word> diffCallback, @NonNull ChooseWord fragment) {
        super(diffCallback);
        this.fragment = fragment;
    }

    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return WordViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(WordViewHolder holder, int position) {
        Word current = getItem(position);
        holder.bind(current.getWord());

        holder.itemView.setOnClickListener((itemView) -> {

//            NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.game_lobby, true).build();
//            Navigation.findNavController(MainActivity.this, R.id.my_nav_host_fragment)
//                    .navigate(R.id.disconnection, null, navOptions);

//            GameLobbyDirections.StartOtherPlayerGames action = GameLobbyDirections.startOtherPlayerGames();
//            action.setGroupOwnerAddress(this.groupOwnerAddress);
//            Log.d("DEBUG", "changing to other player canvas fragment");
//            navController.navigate(action);

            NavHostFragment.findNavController(fragment).getPreviousBackStackEntry().getSavedStateHandle().set("chosenWord", current.getWord());
//            ChooseWordDirections.ReturnWordToLobby action = ChooseWordDirections.returnWordToLobby();
//            action.setWord(current.getWord());
//            NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.choose_word, true).build();
            NavHostFragment.findNavController(fragment).popBackStack();
        });
    }

    public static class WordDiff extends DiffUtil.ItemCallback<Word> {

        @Override
        public boolean areItemsTheSame(@NonNull Word oldItem, @NonNull Word newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Word oldItem, @NonNull Word newItem) {
            return oldItem.getWord().equals(newItem.getWord());
        }
    }
}
