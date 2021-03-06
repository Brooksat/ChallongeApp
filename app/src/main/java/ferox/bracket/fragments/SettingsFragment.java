package ferox.bracket.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import ferox.bracket.Activity.BracketActivity;
import ferox.bracket.Activity.NewTournamentActivity;
import ferox.bracket.CustomClass.ChallongeRequests;
import ferox.bracket.Interface.VolleyCallback;
import ferox.bracket.R;
import ferox.bracket.Tournament.Tournament;

public class SettingsFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    Intent intent;

    final static String NAN = "NAN";

    final static String SINGLE_ELIMINATION = "Single Elimination";
    final static String DOUBLE_ELIMINATION = "Double Elimination";
    final static String ROUND_ROBIN = "Round Robin";
    final static String SWISS = "Swiss";

    final static String MATCH_WIN = "Match Wins";
    final static String GAME_SET_WINS = "Game/Set Wins";
    final static String POINTS_SCORED = "Points Scored";
    final static String POINTS_DIFF = "Points Difference";
    final static String CUSTOM = "Custom";

    /**
     * Winner's bracket winner must lose twice to be eliminated
     */
    final static String GRANDS_DEFAULT = "";
    final static String GRANDS_SINGLE_MATCH = "single match";
    final static String GRANDS_SKIP_ = "skip";


    private int setYear;
    private int setMonth;
    private int setDay;
    private int setHour;
    private int setMinute;
    private String tournamentStartDate;

    private Tournament tournament;
    private Tournament updatedTournament;

    private ArrayList<String> formatErrors;

    private Calendar calendar;
    private DatePickerDialog.OnDateSetListener mOnDateSetListener;
    private TimePickerDialog.OnTimeSetListener mOnTimeSetListener;
    private Spinner formatMenu;
    private ArrayAdapter<CharSequence> formatMenuAdapter;
    private Spinner rankedByMenu;
    private ArrayAdapter<CharSequence> rankedByMenuAdapter;

    private LinearLayout errorsLayout;
    private EditText name;
    private EditText url;
    private EditText subDomain;
    private EditText description;
    private CheckBox holdThirdPlace;
    private RadioGroup grandFinalsModifier;
    private EditText ptsPerMatchWin;
    private EditText ptsPerMatchTie;
    private EditText ptsPerGameWin;
    private EditText ptsPerGameTie;
    private EditText ptsPerBye;
    private CheckBox startAt;
    private TextView dateDay;
    private TextView dateTime;
    private TextView dateTimezone;
    private EditText checkInDuration;
    private EditText maxParticipants;
    private CheckBox showRounds;
    private CheckBox isTournamentPrivate;
    private CheckBox notifyMatchOpen;
    private CheckBox notifyTournamentOver;
    private CheckBox traditionalSeeding;
    private CheckBox allowAttachments;
    private Button applySettings;

    String tournamentURL;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d("OnCreateSettings", "ran");
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        intent = Objects.requireNonNull(getActivity()).getIntent();
        tournament = Objects.requireNonNull(intent.getExtras()).getParcelable("tournament");

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("OnViewCreatedSettings", "ran");

        errorsLayout = view.findViewById(R.id.tournament_errors_linear_layout);
        name = view.findViewById(R.id.tournament_name);
        url = view.findViewById(R.id.url);
        subDomain = view.findViewById(R.id.subdomain);
        description = view.findViewById(R.id.description);
        holdThirdPlace = view.findViewById(R.id.third_place_match_checkbox);
        grandFinalsModifier = view.findViewById(R.id.grand_finals_radio_group);
        ptsPerMatchWin = view.findViewById(R.id.points_per_match_win);
        ptsPerMatchTie = view.findViewById(R.id.points_per_match_tie);
        ptsPerGameWin = view.findViewById(R.id.points_per_gameset_win);
        ptsPerGameTie = view.findViewById(R.id.points_per_gameset_tie);
        ptsPerBye = view.findViewById(R.id.points_per_bye);

        startAt = view.findViewById(R.id.start_at_header_checkbox);
        checkInDuration = view.findViewById(R.id.check_in_duration);
        maxParticipants = view.findViewById(R.id.max_number_participants);
        showRounds = view.findViewById(R.id.show_rounds_checkbox);
        isTournamentPrivate = view.findViewById(R.id.tournament_private_checkbox);
        notifyMatchOpen = view.findViewById(R.id.notify_match_open_checkbox);
        notifyTournamentOver = view.findViewById(R.id.notify_tournament_over_checkbox);
        traditionalSeeding = view.findViewById(R.id.traditional_seeding_checkbox);
        allowAttachments = view.findViewById(R.id.allow_attachments_checkbox);
        applySettings = view.findViewById(R.id.apply_settings);

        dateDay = view.findViewById(R.id.date_day);
        dateTime = view.findViewById(R.id.date_time);
        calendar = Calendar.getInstance();
        dateTimezone = view.findViewById(R.id.settings_timezone);

        formatMenu = view.findViewById(R.id.format_menu);
        rankedByMenu = view.findViewById(R.id.ranked_by_menu);


        formatErrors = new ArrayList<>();
        updatedTournament = new Tournament();

        dateTimezone.setText(calendar.getTimeZone().getDisplayName(calendar.getTimeZone().inDaylightTime(new Date()), TimeZone.SHORT));

        setYear = calendar.get(Calendar.YEAR);
        setMonth = calendar.get(Calendar.MONTH);
        setDay = calendar.get(Calendar.DAY_OF_MONTH);
        setHour = calendar.get(Calendar.HOUR_OF_DAY);
        setMinute = calendar.get(Calendar.MINUTE);


        if (getContext() instanceof NewTournamentActivity) {
            applySettings.setText("Create");
        }


        dateDay.setOnClickListener(v -> {

            if (startAt.isChecked()) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, mOnDateSetListener, setYear, setMonth, setDay);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        dateTime.setOnClickListener(v -> {


            if (startAt.isChecked()) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, mOnTimeSetListener, setHour, setMinute, true);
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.show();
            }
        });

        mOnDateSetListener = (view1, year, month, dayOfMonth) -> {

            setYear = year;
            setMonth = month;
            setDay = dayOfMonth;

            setDay(year, month, dayOfMonth);
            Log.d("startAt", String.valueOf(getDate()));
        };

        mOnTimeSetListener = (view1, hourOfDay, minute) -> {

            setHour = hourOfDay;
            setMinute = minute;

            setTime(hourOfDay, minute);
            Log.d("startAt", String.valueOf(getDate()));

        };


        formatMenu.setOnItemSelectedListener(this);
        formatMenuAdapter = ArrayAdapter.createFromResource(getContext(), R.array.format_array, R.layout.menu_spinner_item);
        formatMenuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        formatMenu.setAdapter(formatMenuAdapter);


        rankedByMenu.setOnItemSelectedListener(this);
        rankedByMenuAdapter = ArrayAdapter.createFromResource(getContext(), R.array.ranked_by_array, R.layout.menu_spinner_item);
        rankedByMenuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rankedByMenu.setAdapter(rankedByMenuAdapter);

        //Cannot not scroll from area in description box if focus
        description.setOnTouchListener((v, event) -> {
            if (v.getId() == R.id.description) {
                if (v.hasFocus()) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                }
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
            }
            return false;
        });


        applySettings.setOnClickListener(v -> {
            formatErrors.clear();
            setTournamentInfo();
            errorsLayout.removeAllViews();
            if (formatErrors.size() == 0) {
                if (getContext() instanceof NewTournamentActivity) {
                    sendTournamentCreateRequest();
                }
                //
                else if (getContext() instanceof BracketActivity) {
                    sendTournamentUpdateRequest();
                }
            } else {
                for (int i = 0; i < formatErrors.size(); i++) {
                    TextView error = (TextView) getLayoutInflater().inflate(R.layout.menu_spinner_item, null);
                    error.setText(String.valueOf(formatErrors.get(i)));
                    error.setSelected(true);
                    error.setTextColor(Color.RED);
                    errorsLayout.addView(error);
                }
            }
        });

        if (tournament.isSearched()) {
            applySettings.setClickable(false);
        }
        sendSettingsRequest();

    }

    private void sendTournamentUpdateRequest() {
        ChallongeRequests.sendRequest(new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                Toast.makeText(getContext(), "Tournament Updated", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErrorResponse(ArrayList errorList) {
                if (errorList != null) {
                    for (int i = 0; i < errorList.size(); i++) {
                        TextView error = (TextView) getLayoutInflater().inflate(R.layout.menu_spinner_item, null);
                        error.setText(String.valueOf(errorList.get(i)));
                        error.setSelected(true);
                        error.setTextColor(Color.RED);
                        errorsLayout.addView(error);
                    }
                }
                //TODO inconvenient if changing multiple settings and an error only pertains to one
                //sendSettingsRequest();
            }
        }, ChallongeRequests.tournamentUpdate(updatedTournament));
    }

    private void sendTournamentCreateRequest() {
        ChallongeRequests.sendRequest(new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                Toast.makeText(getContext(), "Tournament Created", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErrorResponse(ArrayList errorList) {
                if (errorList != null) {
                    for (int i = 0; i < errorList.size(); i++) {
                        TextView error = (TextView) getLayoutInflater().inflate(R.layout.menu_spinner_item, null);
                        error.setText(String.valueOf(errorList.get(i)));
                        error.postDelayed(() -> error.setSelected(true), 1500);
                        error.setTextColor(Color.RED);
                        errorsLayout.addView(error);
                    }
                }
            }
        }, ChallongeRequests.tounamentCreate(updatedTournament));
    }


    private void sendSettingsRequest() {
        if (tournament.getId() != null) {
            //ChallongeRequests.sendRequest(response -> getTournamentInfo(response), ChallongeRequests.tournamentShow(tournamentURL));
            ChallongeRequests.sendRequest(new VolleyCallback() {
                @Override
                public void onSuccess(String response) {

                    tournament = new Gson().fromJson(new JsonParser().parse(response).getAsJsonObject().get("tournament"), Tournament.class);
                    tournament.undoJsonShenanigans();
                    getTournamentInfo();
                    Objects.requireNonNull(getView()).jumpDrawablesToCurrentState();

                }

                @Override
                public void onErrorResponse(ArrayList errorList) {
                    getTournamentInfo();
                }
            }, ChallongeRequests.tournamentShow(tournament.getId()));
        }
    }

    /**
     * Formats a string to the form XXX.X (a.k.a.) a number less than 1000 to at most 1 decimal point<br>
     * 1000.01 returns 100.0<br>
     * 999.9 returns 999.9<br>
     * 00001.10000 returns 1.1<br>
     * 12. returns 12.<br>
     * .234 returns .2<br>
     * "" and "." returns "0"<br>
     *
     * @param parameter string to be formatted
     * @return if parsable float then String of form XXX.X else returns NAN
     */
    private String formatPointSetting(String parameter) {

        if (parameter.equals("") || parameter.equals(".")) {
            return "0";
        }
        String newParam = parameter.replaceFirst("^0+(?!$)", "");
        try {
            float floatCheck = Float.parseFloat(newParam);
        } catch (NumberFormatException e) {
            return NAN;
        } catch (NullPointerException e) {
            return NAN;
        }


        int decimalIndex = newParam.indexOf(".");
        if (decimalIndex > -1) {
            String integerPart = "";
            if (decimalIndex > 0) {
                integerPart = newParam.substring(0, decimalIndex);
            }
            String decimalPart = "";
            Log.d("newParam", newParam + "-" + decimalIndex);
            Log.d("decimalPart", decimalPart + "|");
            if (decimalIndex < newParam.length() - 1) {
                decimalPart = newParam.substring(decimalIndex + 1);
            }
            Log.d("decimalPart", decimalPart + "|");

            if (!StringUtils.isEmpty(integerPart)) {
                if (integerPart.length() > 3) {
                    integerPart = integerPart.substring(0, 3);
                }
            }

            if (!StringUtils.isEmpty(decimalPart)) {
                if (decimalPart.length() > 1) {
                    decimalPart = decimalPart.substring(0, 1);
                }
            }
            newParam = integerPart + "." + decimalPart;
        } else {
            if (newParam.length() > 3) {
                newParam = newParam.substring(0, 3);
            }
        }

        return newParam;
    }

    private void setCustomPointsSetting() {

        String ppmw = formatPointSetting(ptsPerMatchWin.getText().toString());
        String ppmt = formatPointSetting(ptsPerMatchTie.getText().toString());
        String ppgw = formatPointSetting(ptsPerGameWin.getText().toString());
        String ppgt = formatPointSetting(ptsPerGameTie.getText().toString());
        String ppb = formatPointSetting(ptsPerBye.getText().toString());

        Log.d("pointssetting1", ppmw);
        Log.d("pointssetting2", ppmt);
        Log.d("pointssetting3", ppgw);
        Log.d("pointssetting4", ppgt);
        Log.d("pointssetting5", ppb);
        if (formatMenu.getSelectedItem().toString().equals(SWISS)) {

            if (!ppmw.equals(NAN)) {
                updatedTournament.setSwissPtsForMatchWin(Float.valueOf(ppmw));
            } else {
                formatErrors.add("Points per match win must be a number");
            }
            if (!ppmt.equals(NAN)) {
                updatedTournament.setSwissPtsForMatchTie(Float.valueOf(ppmt));
            } else {
                formatErrors.add("Points per match tie must be a number");
            }
            if (!ppgw.equals(NAN)) {
                updatedTournament.setSwissPtsForGameWin(Float.valueOf(ppgw));
            } else {
                formatErrors.add("Points per game win must be a number");
            }
            if (!ppgt.equals(NAN)) {
                updatedTournament.setSwissPtsForGameTie(Float.valueOf(ppgt));
            } else {
                formatErrors.add("Points per game tie must be a number");
            }
            if (!ppb.equals(NAN)) {
                updatedTournament.setSwissPtsForBye(Float.valueOf(ppb));
            } else {
                formatErrors.add("Points per bye must be a number");
            }
        } else if (formatMenu.getSelectedItem().toString().equals(ROUND_ROBIN)) {
            if (!ppmw.equals(NAN)) {
                updatedTournament.setRrPtsForMatchWin(Float.valueOf(ppmw));
            } else {
                formatErrors.add("Points per match win must be a number");
            }
            if (!ppmt.equals(NAN)) {
                updatedTournament.setRrPtsForMatchTie(Float.valueOf(ppmt));
            } else {
                formatErrors.add("Points per match tie must be a number");
            }
            if (!ppgw.equals(NAN)) {
                updatedTournament.setRrPtsForGameWin(Float.valueOf(ppgw));
            } else {
                formatErrors.add("Points per game win must be a number");
            }
            if (!ppgt.equals(NAN)) {
                updatedTournament.setRrPtsForGameTie(Float.valueOf(ppgt));
            } else {
                formatErrors.add("Points per game tie must be a number");
            }
        }

    }


    private void setGFModifier() {
        int selectIndex = grandFinalsModifier.getCheckedRadioButtonId();
        switch (selectIndex) {
            case R.id.grand_finals_default:
                updatedTournament.setGrandFinalsModifier(GRANDS_DEFAULT);
                break;
            case R.id.grand_finals_single_match:
                updatedTournament.setGrandFinalsModifier(GRANDS_SINGLE_MATCH);
                break;
            case R.id.grand_finals_skip:
                updatedTournament.setGrandFinalsModifier(GRANDS_SKIP_);
                break;
            default:
                updatedTournament.setGrandFinalsModifier(GRANDS_DEFAULT);
        }

    }


    private void setTournamentInfo() {
        updatedTournament.setId(tournament.getId());
        updatedTournament.setName(name.getText().toString());
        updatedTournament.setUrl(url.getText().toString());
        updatedTournament.setSubdomain(subDomain.getText().toString());
        updatedTournament.setDescription(description.getText().toString());
        Log.d("tourneyType", formatMenu.getSelectedItem().toString().toLowerCase());
        updatedTournament.setType(formatMenu.getSelectedItem().toString().toLowerCase());
        updatedTournament.setHoldThirdPlaceMatch(holdThirdPlace.isChecked());


        setGFModifier();
        setRankedBy();
        setCustomPointsSetting();


        try {
            updatedTournament.setCheckInDuration(Integer.valueOf(checkInDuration.getText().toString()));
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Check in duration must be number", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        try {
            updatedTournament.setSignUpCap(Integer.parseInt(maxParticipants.getText().toString()));
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Max participants must be number", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


        updatedTournament.setStartAt("");
        if (startAt.isChecked()) {
            updatedTournament.setStartAt(getDate());
        }
        Log.d("startAt", String.valueOf(getDate()));

        updatedTournament.setShowRounds(showRounds.isChecked());
        updatedTournament.setPrivate(isTournamentPrivate.isChecked());
        updatedTournament.setNotifyUsersMatchesOpens(notifyMatchOpen.isChecked());
        updatedTournament.setNotifyUsersTourneyOver(notifyTournamentOver.isChecked());
        updatedTournament.setSequentialPairings(!traditionalSeeding.isChecked());
        updatedTournament.setAcceptAttachments(allowAttachments.isChecked());

        updatedTournament.setState(tournament.getState());

        Log.d("applySettings", updatedTournament.toString());


    }

    private void getTournamentInfo() {

        Log.d("getTournamentInfo", "ran");
        name.setText(tournament.getName());
        url.setText(tournament.getUrl());
        subDomain.setText(tournament.getSubdomain());
        //handles formatting
        description.setText(Html.fromHtml(tournament.getDescription()));

        formatMenu.setSelection(formatMenuAdapter.getPosition(WordUtils.capitalize(tournament.getType())), true);


        holdThirdPlace.setChecked(tournament.isHoldThirdPlaceMatch());
        getGrandsModifier();
        getRankedBy();


        if (tournament.getType().equals(SWISS.toLowerCase())) {
            ptsPerMatchWin.setText(String.valueOf(tournament.getSwissPtsForMatchWin()));
            ptsPerMatchTie.setText(String.valueOf(tournament.getSwissPtsForMatchTie()));
            ptsPerGameWin.setText(String.valueOf(tournament.getSwissPtsForGameWin()));
            ptsPerGameTie.setText(String.valueOf(tournament.getSwissPtsForGameTie()));
            ptsPerBye.setText(String.valueOf(tournament.getSwissPtsForBye()));
        } else {
            ptsPerMatchWin.setText(String.valueOf(tournament.getRrPtsForMatchWin()));
            ptsPerMatchTie.setText(String.valueOf(tournament.getRrPtsForMatchTie()));
            ptsPerGameWin.setText(String.valueOf(tournament.getRrPtsForGameWin()));
            ptsPerGameTie.setText(String.valueOf(tournament.getRrPtsForGameTie()));
        }

        checkInDuration.setText(String.valueOf(tournament.getCheckInDuration()));

        if (!StringUtils.isEmpty(tournament.getStartAt())) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US);
                Calendar cal = Calendar.getInstance();
                cal.setTime(simpleDateFormat.parse(tournament.getStartAt()));
                setDay(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                setTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
                startAt.setChecked(true);
                Log.d("theDate", tournament.getStartAt() + " -> " + cal.getTime());

            } catch (ParseException e) {
                e.printStackTrace();

            }
        } else {
            startAt.setChecked(false);
        }

        maxParticipants.setText(String.valueOf(tournament.getSignUpCap()));
        showRounds.setChecked(tournament.isShowRounds());
        isTournamentPrivate.setChecked(tournament.isPrivate());
        notifyMatchOpen.setChecked(tournament.isNotifyUsersMatchesOpens());
        notifyTournamentOver.setChecked(tournament.isNotifyUsersTourneyOver());
        traditionalSeeding.setChecked(!tournament.isSequentialPairings());
        allowAttachments.setChecked(tournament.isAcceptAttachments());


    }

    private void setRankedBy() {
        String rankedBy = rankedByMenu.getSelectedItem().toString();
        switch (rankedBy) {
            case MATCH_WIN:
                updatedTournament.setRankedBy(Tournament.MATCH_WINS);
                break;
            case GAME_SET_WINS:
                updatedTournament.setRankedBy(Tournament.GAME_WINS);
                break;
            case POINTS_SCORED:
                updatedTournament.setRankedBy(Tournament.POINTS_SCORED);
                break;
            case POINTS_DIFF:
                updatedTournament.setRankedBy(Tournament.POINTS_DIFFERENCE);
                break;
            case CUSTOM:
                updatedTournament.setRankedBy(Tournament.CUSTOM);
                break;

        }
    }

    private String getDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US);
        Calendar cal = Calendar.getInstance();
        cal.set(setYear, setMonth, setDay, setHour, setMinute);
        //cal.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        return simpleDateFormat.format(cal.getTime());
    }

    private void setDay(int year, int month, int day) {
        String date = year + "/" + padLeadingZeros(month + 1, 2) + "/" + padLeadingZeros(day, 2);
        dateDay.setText(date);
    }

    private void setTime(int hour, int minute) {
        String time1 = padLeadingZeros(hour, 2) + ":" + padLeadingZeros(minute, 2);
        dateTime.setText(time1);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        String item = (String) parent.getItemAtPosition(pos);


        if (parent == getView().findViewById(R.id.format_menu)) {
            switch (item) {
                case SINGLE_ELIMINATION: {
                    getView().findViewById(R.id.single_elim_layout).setVisibility(View.VISIBLE);
                    getView().findViewById(R.id.double_elim_layout).setVisibility(View.GONE);
                    getView().findViewById(R.id.round_robin_layout).setVisibility(View.GONE);
                    break;
                }
                case DOUBLE_ELIMINATION: {
                    getView().findViewById(R.id.single_elim_layout).setVisibility(View.GONE);
                    getView().findViewById(R.id.double_elim_layout).setVisibility(View.VISIBLE);
                    getView().findViewById(R.id.round_robin_layout).setVisibility(View.GONE);
                    break;
                }
                case ROUND_ROBIN: {
                    getView().findViewById(R.id.single_elim_layout).setVisibility(View.GONE);
                    getView().findViewById(R.id.double_elim_layout).setVisibility(View.GONE);
                    getView().findViewById(R.id.round_robin_layout).setVisibility(View.VISIBLE);
                    getView().findViewById(R.id.swiss_points_per_bye_layout).setVisibility(View.GONE);
                    getView().findViewById(R.id.ranked_by).setVisibility(View.VISIBLE);
                    getView().findViewById(R.id.ranked_by_menu).setVisibility(View.VISIBLE);

                    rankedByMenu.getOnItemSelectedListener().onItemSelected(
                            rankedByMenu,
                            rankedByMenu.getSelectedView(),
                            rankedByMenuAdapter.getPosition(rankedByMenu.getSelectedItem().toString()),
                            rankedByMenu.getSelectedItemId());

                    //handle situation when switching between RR and Swiss format
                    ptsPerMatchWin.setText(String.valueOf(tournament.getRrPtsForMatchWin()));
                    ptsPerMatchTie.setText(String.valueOf(tournament.getRrPtsForMatchTie()));
                    ptsPerGameWin.setText(String.valueOf(tournament.getRrPtsForGameWin()));
                    ptsPerGameTie.setText(String.valueOf(tournament.getRrPtsForGameTie()));


                    break;
                }
                case SWISS: {
                    getView().findViewById(R.id.single_elim_layout).setVisibility(View.GONE);
                    getView().findViewById(R.id.double_elim_layout).setVisibility(View.GONE);
                    getView().findViewById(R.id.round_robin_layout).setVisibility(View.VISIBLE);
                    getView().findViewById(R.id.swiss_points_per_bye_layout).setVisibility(View.VISIBLE);
                    getView().findViewById(R.id.rr_swiss_custom_parameters).setVisibility(View.VISIBLE);
                    getView().findViewById(R.id.ranked_by).setVisibility(View.GONE);
                    getView().findViewById(R.id.ranked_by_menu).setVisibility(View.GONE);
                    //handle situation when switching between RR and Swiss format
                    ptsPerMatchWin.setText(String.valueOf(tournament.getSwissPtsForMatchWin()));
                    ptsPerMatchTie.setText(String.valueOf(tournament.getSwissPtsForMatchTie()));
                    ptsPerGameWin.setText(String.valueOf(tournament.getSwissPtsForGameWin()));
                    ptsPerGameTie.setText(String.valueOf(tournament.getSwissPtsForGameTie()));
                    ptsPerBye.setText(String.valueOf(tournament.getSwissPtsForBye()));


                    break;
                }

            }
        }
        if (parent == getView().findViewById(R.id.ranked_by_menu)) {

            switch (item) {
                case CUSTOM: {
                    getView().findViewById(R.id.rr_swiss_custom_parameters).setVisibility(View.VISIBLE);
                    break;
                }
                default: {
                    getView().findViewById(R.id.rr_swiss_custom_parameters).setVisibility(View.GONE);
                    break;
                }
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * @param number    int that is to be formatted
     * @param padAmount pad length
     * @return if padAmount > 0 then {@code String} that's been padded the specified amount
     * else returns String.valueOf(number)
     */
    private String padLeadingZeros(int number, int padAmount) {
        if (padAmount > 0) {
            return String.format(Locale.US, "%0" + 2 + "d", number);
        }

        return String.valueOf(number);
    }

    private void getGrandsModifier() {
        if (tournament.getGrandFinalsModifier() != null) {


            switch (tournament.getGrandFinalsModifier()) {
                case GRANDS_DEFAULT: {
                    grandFinalsModifier.check(R.id.grand_finals_default);
                    break;
                }
                case GRANDS_SINGLE_MATCH: {
                    grandFinalsModifier.check(R.id.grand_finals_single_match);
                    break;
                }
                case GRANDS_SKIP_: {
                    grandFinalsModifier.check(R.id.grand_finals_skip);
                    break;
                }
                default: {
                    grandFinalsModifier.check(R.id.grand_finals_default);
                    break;
                }
            }
        } else {
            grandFinalsModifier.check(R.id.grand_finals_default);
        }
    }

    private void getRankedBy() {
        switch (tournament.getRankedBy()) {
            //formatMenu.setSelection(formatMenuAdapter.getPosition(WordUtils.capitalize(tournament.getType())), true);
            case Tournament.MATCH_WINS:
                rankedByMenu.setSelection(rankedByMenuAdapter.getPosition(MATCH_WIN));
                break;
            case Tournament.GAME_WINS:
                rankedByMenu.setSelection(rankedByMenuAdapter.getPosition(GAME_SET_WINS));
                break;
            case Tournament.POINTS_SCORED:
                rankedByMenu.setSelection(rankedByMenuAdapter.getPosition(POINTS_SCORED));
                break;
            case Tournament.POINTS_DIFFERENCE:
                rankedByMenu.setSelection(rankedByMenuAdapter.getPosition(POINTS_DIFF));
                break;
            case Tournament.CUSTOM:
                rankedByMenu.setSelection(rankedByMenuAdapter.getPosition(CUSTOM));
                break;
        }
    }


    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }
}
