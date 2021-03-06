package ferox.bracket.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyLog;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;

import ferox.bracket.CustomClass.ChallongeRequests;
import ferox.bracket.Interface.VolleyCallback;
import ferox.bracket.R;
import ferox.bracket.Tournament.Tournament;



public class MainActivity extends AppCompatActivity {

    ImageButton main_search;
    EditText apikeyParam;
    Button signIn;
    CheckBox savePasswordCheckBox;
    LinearLayout errorsLayout;

    private final static String PREFS_NAME = "preferences";

    private static final String PREFS_KEY = "key";
    private String defaultKey = "";
    private String key;

    private static final String PREFS_SAVE_API_KEY = "save key";
    private boolean defaultSaveAPIKey = false;
    private boolean saveAPIKey;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        VolleyLog.DEBUG = true;
        //ChallongeRequests.setApiKey("WoaE4Sj7pEIlkTxdiDlEZVLXLbYp3TItipqBJbDJ");
        ChallongeRequests.setApplicationContext(this.getApplicationContext());


        main_search = findViewById(R.id.main_search_button);
        main_search.setOnClickListener(v -> makeSearchDialog());
        apikeyParam = findViewById(R.id.log_in_api_key);
        signIn = findViewById(R.id.log_in_button);
        signIn.setOnClickListener(v -> sendTournamentIndexRequest());
        savePasswordCheckBox = findViewById(R.id.log_in_save_password);
        errorsLayout = findViewById(R.id.log_in_errors_layout);

        loadPreferences();
        //Intent intent = new Intent(this, HomeActivity.class);
        //startActivity(intent);
    }


    private void sendTournamentIndexRequest() {
        ChallongeRequests.setApiKey(apikeyParam.getText().toString().trim());
        ChallongeRequests.sendRequest(new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                goToHomeActivity();
            }

            @Override
            public void onErrorResponse(ArrayList errorList) {
                errorsLayout.removeAllViews();
                for (int i = 0; i < errorList.size(); i++) {
                    TextView error = (TextView) getLayoutInflater().inflate(R.layout.menu_spinner_item, null);
                    error.setText(String.valueOf(errorList.get(i)));
                    error.setSelected(true);
                    error.setTextColor(Color.RED);
                    errorsLayout.addView(error);
                }
            }
        }, ChallongeRequests.tournamentsIndex());
    }

    public void goToHomeActivity() {
        savePreferences();
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void makeSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Find a Tournament");
        View searchDialog = getLayoutInflater().inflate(R.layout.search_dialog, null);
        EditText searchURL = searchDialog.findViewById(R.id.main_search_dialog_url);
        EditText searchSubdomain = searchDialog.findViewById(R.id.main_search_dialog_subdomain);
        LinearLayout errorsLayout = searchDialog.findViewById(R.id.main_search_error_layout);
        builder.setView(searchDialog);
        builder.setPositiveButton("Search", (dialog1, which) -> {
        });
        builder.setNegativeButton("Cancel", (dialog1, which) -> {
        });

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialog1 -> {
            Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(v -> {
                ChallongeRequests.sendRequest(new VolleyCallback() {
                    @Override
                    public void onSuccess(String response) {

                        Gson gson = new Gson();
                        JsonParser jsonParser = new JsonParser();
                        JsonElement jsonElement = jsonParser.parse(response);
                        Tournament searchedTournament = gson.fromJson(jsonElement.getAsJsonObject().get("tournament"), Tournament.class);
                        searchedTournament.undoJsonShenanigans();
                        searchedTournament.setSearched(true);
                        searchedTournament.setUrl(searchURL.getText().toString());
                        searchedTournament.setSubdomain(searchSubdomain.getText().toString());
                        Intent intent = new Intent(MainActivity.this, BracketActivity.class);
                        intent.putExtra("tournament", searchedTournament);
                        dialog.dismiss();

                        validateTournament(searchedTournament, intent);


                    }

                    @Override
                    public void onErrorResponse(ArrayList errorList) {
                        Log.d("SearchAttempt", "Error Response Ran");
                        errorsLayout.removeAllViews();
                        for (int i = 0; i < errorList.size(); i++) {
                            TextView error = (TextView) getLayoutInflater().inflate(R.layout.menu_spinner_item, null);
                            error.setText(String.valueOf(errorList.get(i)));
                            error.setSelected(true);
                            error.setTextColor(Color.RED);
                            errorsLayout.addView(error);
                        }
                        TextView error = (TextView) getLayoutInflater().inflate(R.layout.menu_spinner_item, null);
                        error.setText("Could not find tournament with supplied URL and subdomain");
                        error.postDelayed(() -> error.setSelected(true), 1000);
                        error.setTextColor(Color.RED);
                        errorsLayout.addView(error);
                    }
                }, ChallongeRequests.jsonAtTheEndOfTheNormalURL(searchURL.getText().toString(), searchSubdomain.getText().toString()));
            });
        });
        dialog.show();
    }

    private void validateTournament(Tournament tournament, Intent intent) {
        ChallongeRequests.sendRequest(new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                tournament.setHasAccess(true);
                startActivity(intent);
            }

            @Override
            public void onErrorResponse(ArrayList errorList) {
                tournament.setHasAccess(false);
                startActivity(intent);
            }
        }, ChallongeRequests.validateAccess(tournament));
    }

    private void savePreferences() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        if (savePasswordCheckBox.isChecked()) {
            key = apikeyParam.getText().toString();
            saveAPIKey = savePasswordCheckBox.isChecked();
            editor.putString(PREFS_KEY, key);
            editor.putBoolean(PREFS_SAVE_API_KEY, saveAPIKey);
            editor.apply();
        } else {
            editor.clear();
            editor.apply();
        }
    }

    private void loadPreferences() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        key = settings.getString(PREFS_KEY, defaultKey);
        apikeyParam.setText(key);
        saveAPIKey = settings.getBoolean(PREFS_SAVE_API_KEY, defaultSaveAPIKey);
        savePasswordCheckBox.setChecked(saveAPIKey);
    }
}
