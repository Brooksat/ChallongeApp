package ferox.bracket.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Collections;

import ferox.bracket.CustomClass.ChallongeRequests;
import ferox.bracket.Interface.VolleyCallback;
import ferox.bracket.R;
import ferox.bracket.Tournament.Tournament;


public class HomeActivity extends AppCompatActivity {

    private static final String RESET = "Reset";

    String api_key = "hyxStYdr5aFDRNHEHscBgrzKGXCgNFp4GWfErw07";
    String subDomain;
    ArrayList<Tournament> tournamentList;
    ArrayList<String> nameList;

    ListView listView;
    ArrayAdapter<String> listViewAdapter;
    ImageButton homeOptions;
    TextView homeSubdomain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        subDomain = "";
        listView = findViewById(R.id.tournament_list);
        nameList = new ArrayList<>();
        listViewAdapter = new ArrayAdapter<>(this, R.layout.menu_list_item, nameList);
        listView.setAdapter(listViewAdapter);
        homeOptions = findViewById(R.id.home_menu_options);
        homeOptions.setOnClickListener(v1 -> {

            PopupMenu popupMenu = new PopupMenu(this, homeOptions);
            popupMenu.getMenuInflater().inflate(R.menu.home_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getTitle().toString()) {
                    case "Subdomain": {
                        makeSetSubdomainDialog();
                        break;
                    }
                    case "Refresh": {
                        refresh();
                        break;
                    }
                }
                return true;
            });
            popupMenu.show();
        });
        homeSubdomain = findViewById(R.id.home_subdomain);
        homeSubdomain.setOnClickListener(v -> makeSetSubdomainDialog());

        tournamentList = new ArrayList<>();


        ChallongeRequests.sendRequest(new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                updateTournamentList(response);
            }

            @Override
            public void onErrorResponse(ArrayList errorResponse) {

            }
        }, ChallongeRequests.tournamentsIndex(subDomain));

    }

    public void goToCreateTournamentActivity(View view) {
        Intent intent = new Intent(this, NewTournamentActivity.class);
        intent.putExtra("tournament", new Tournament());
        startActivity(intent);
    }

    private void updateTournamentList(String jsonResponse) {
        Gson gson = new Gson();
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(jsonResponse);
        JsonArray tournaments = jsonElement.getAsJsonArray();

        Log.d("List size", String.valueOf(tournaments.size()));

        for (int i = 0; i < tournaments.size(); i++) {

            Tournament tournament = gson.fromJson(tournaments.get(i).getAsJsonObject().get("tournament"), Tournament.class);
            tournament.undoJsonShenanigans();
            tournament.setHasAccess(true);
            if (!tournament.getType().equals(Tournament.FREE_FOR_ALL)) {
                tournamentList.add(0, tournament);
            }

            Log.d("TOURID", String.valueOf(tournament.getId()));
        }
        if (!subDomain.isEmpty()) {
            Collections.reverse(tournamentList);
        }

        for (int i = 0; i < tournamentList.size(); i++) {
            nameList.add(tournamentList.get(i).getName());
        }


        listView.setOnItemClickListener((parent, view, position, id) -> {
            Log.i("TAG", tournamentList.get(position).toString());
            Intent intent = new Intent(view.getContext(), BracketActivity.class);
            intent.putExtra("tournament", tournamentList.get(position));

            startActivity(intent);
            Log.d("TournamentName", String.valueOf(tournamentList.get(position).getName()));

        });
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Would you like to Reset the match");
            builder.setNegativeButton("Cancel", (dialog1, which) -> {
            });
            builder.setPositiveButton("Reset", (dialog, which) -> {
            });
            AlertDialog dialog = builder.create();
            setReopenDialog(dialog, tournamentList.get(position));
            dialog.show();
            return true;
        });

        //listViewAdapter.notifyDataSetChanged();
        ((ArrayAdapter<String>) listView.getAdapter()).notifyDataSetChanged();
        listView.invalidate();
    }

    private void setReopenDialog(AlertDialog dialog, Tournament tournament) {
        View dialogueLayout = getLayoutInflater().inflate(R.layout.match_reset_dialog, null);
        TextView tournamentResetName = dialogueLayout.findViewById(R.id.match_reset_tournament_name);
        tournamentResetName.setText(tournament.getName());
        EditText reopenPrompt = dialogueLayout.findViewById(R.id.match_reset_prompt);
        LinearLayout errorsLayout = dialogueLayout.findViewById(R.id.match_reset_error_layout);
        dialog.setView(dialogueLayout);
        dialog.setOnShowListener(dialog1 -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(v -> {
                if (reopenPrompt.getText().toString().equals(RESET)) {
                    sendTournamentResetRequest(tournament.getId(), errorsLayout);
                    dialog.dismiss();
                } else {
                    errorsLayout.removeAllViews();
                    TextView error = (TextView) getLayoutInflater().inflate(R.layout.menu_spinner_item, null);
                    error.setText("Type 'Reset' with no spaces or other punctuation on either side");
                    error.setSelected(true);
                    error.setTextColor(Color.RED);
                    errorsLayout.addView(error);
                }
            });
        });
    }

    private void sendTournamentResetRequest(String id, LinearLayout errorsLayout) {
        ChallongeRequests.sendRequest(new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                Toast.makeText(HomeActivity.this, "Tournament Reset", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErrorResponse(ArrayList errorList) {
                errorsLayout.removeAllViews();
                for (int i = 0; i < errorList.size(); i++) {
                    Toast.makeText(HomeActivity.this, String.valueOf(errorList.get(i)), Toast.LENGTH_SHORT).show();
                }
            }
        }, ChallongeRequests.tournamentReset(id));

    }

    private void makeSetSubdomainDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogueLayout = getLayoutInflater().inflate(R.layout.home_set_subdomain_dialog, null);
        EditText subdomainText = dialogueLayout.findViewById(R.id.home_new_subdomain);
        builder.setView(dialogueLayout);
        builder.setPositiveButton("OK", (dialog, which) -> {

            subDomain = subdomainText.getText().toString();
            homeSubdomain.setText(String.format("(%s)", subDomain));
            refresh();

        })
                .setNegativeButton("Cancel", (dialog, which) -> {
                })
                .create().show();
    }

    public void refresh() {
        nameList.clear();
        tournamentList.clear();
        listViewAdapter.notifyDataSetChanged();
        ChallongeRequests.sendRequest(new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                updateTournamentList(response);
            }

            @Override
            public void onErrorResponse(ArrayList errorResponse) {

            }
        }, ChallongeRequests.tournamentsIndex(subDomain));
    }


}
