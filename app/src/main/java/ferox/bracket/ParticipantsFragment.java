package ferox.bracket;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class ParticipantsFragment extends Fragment {

    private static final String TAG = "ParticipantsFragment";
    String api_key = "hyxStYdr5aFDRNHEHscBgrzKGXCgNFp4GWfErw07";

    String url;


    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;
    ArrayList<String> playerSeeds = new ArrayList<>();
    ArrayList<Participant> players = new ArrayList<>();



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        View v = inflater.inflate(R.layout.fragment_participants, container, false);
        CustomLinearLayoutManager linearLayoutManager = new CustomLinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);


        adapter = new RecyclerViewAdapter(getContext(), players, linearLayoutManager);


        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        ((DefaultItemAnimator) itemAnimator).setSupportsChangeAnimations(false);


        // idk what getActivity gets
        recyclerView = v.findViewById(R.id.participant_list);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(itemAnimator);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
        adapter.getHelper().attachToRecyclerView(recyclerView);


        url = intent.getStringExtra("tournamentURL");
        ChallongeRequests.sendGet(response -> initPlayerList(response), ChallongeRequests.participantIndex(url));
        return v;
    }




    public void initPlayerList(String jsonString) {

        JsonParser jsonParser = new JsonParser();
        JsonElement tournament = jsonParser.parse(jsonString);
        JsonArray participants = tournament.getAsJsonArray();

        for (JsonElement participant : participants) {
            Participant player = new Participant();
            JsonObject participantObject = participant.getAsJsonObject().get("participant").getAsJsonObject();

            player.setId(participantObject.get("id").getAsInt());
            player.setName(participantObject.get("name").getAsString());
            player.setSeed(participantObject.get("seed").getAsInt());
            players.add(player);
            playerSeeds.add(String.valueOf(player.getSeed()));

        }


        adapter.notifyDataSetChanged();
    }


}