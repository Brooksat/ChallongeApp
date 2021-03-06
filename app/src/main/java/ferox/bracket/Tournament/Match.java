package ferox.bracket.Tournament;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Match {

    public final static String PENDING = "pending";
    public final static String OPEN = "open";
    public final static String COMPLETE = "complete";
    public final static String NONE = "";

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("tournament_id")
    @Expose
    private String tournamentID;
    @SerializedName("identifier")
    @Expose
    private String identifier;

    private int p1Seed;
    private int p2Seed;
    @SerializedName("player1_prereq_identifier")
    @Expose
    private int p1PrereqIdentifier;
    @SerializedName("player2_prereq_identifier")
    @Expose
    private int p2PrereqIdentifier;
    @SerializedName("player1_is_prereq_match_loser")
    @Expose
    private boolean p1IsPrereqLoser;
    @SerializedName("player2_is_prereq_match_loser")
    @Expose
    private boolean p2IsPrereqLoser;
    private Participant p1;
    private Participant p2;
    private Match p1PreviousMatch;
    private Match p2PreviousMatch;
    private boolean P1Decided;
    private boolean P2Decided;
    @SerializedName("player1_placeholder_text")
    @Expose
    private String p1PrereqText;
    @SerializedName("player2_placeholder_text")
    @Expose
    private String p2PrereqText;
    @SerializedName("winner_id")
    @Expose
    private String winnerID;
    @SerializedName("loser_id")
    @Expose
    private String loserID;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("scores_csv")
    @Expose
    private String scoresCSV;
    private boolean isWinners;
    private ArrayList<String> games;
    private String score;
    private String p1Score;
    private String p2Score;


    public Match(String id, int p1Seed, int p2Seed, Participant p1, Participant p2,
                 Match p1PreviousMatch, Match p2PreviousMatch, boolean P1Decided, boolean P2Decided,
                 String p1PrereqText, String p2PrereqText) {
        super();
        this.id = id;
        this.p1Seed = p1Seed;
        this.p2Seed = p2Seed;
        this.p1 = p1;
        this.p2 = p2;
        this.p1PreviousMatch = p1PreviousMatch;
        this.p2PreviousMatch = p2PreviousMatch;
        this.P1Decided = P1Decided;
        this.P2Decided = P2Decided;
        this.p1PrereqText = p1PrereqText;
        this.p2PrereqText = p2PrereqText;

    }

    public Match() {
        id = "";
        tournamentID = "";
        identifier = "";
        p1Seed = 0;
        p2Seed = 0;
        p1PrereqIdentifier = 0;
        p2PrereqIdentifier = 0;
        p1 = null;
        p2 = null;
        p1PreviousMatch = null;
        p2PreviousMatch = null;
        P1Decided = false;
        P2Decided = false;
        p1PrereqText = "";
        p2PrereqText = "";
        winnerID = "";
        loserID = "";

    }

    //TODO gotta format score_csv
    public String getSettings() {
        String s = "match[scores_csv]=" + p1Score + "-" + p2Score + "&";
        s += "match[winner_id]=" + winnerID;
        return s;
    }

    public String formatScore() {
        String s = "";
        return s;
    }

    /**
     * When deserializing JSON sets any JsonNull fields to their default values, therefore settings
     * string to null, this methods changes String fields to ""
     */
    public void undoJsonShenanigans() {
        if (id == null) {
            id = "";
        }
        if (identifier == null) {
            identifier = "";
        }
        if (tournamentID == null) {
            tournamentID = "";
        }
        if (p1PrereqText == null) {
            p1PrereqText = "";
        }
        if (p2PrereqText == null) {
            p2PrereqText = "";
        }
        if (winnerID == null) {
            winnerID = "";
        }
        if (loserID == null) {
            loserID = "";
        }
        if (p1Score == null) {
            p1Score = "-";
        }
        if (p2Score == null) {
            p2Score = "-";
        }
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTournamentID() {
        return tournamentID;
    }

    public void setTournamentID(String tournamentID) {
        this.tournamentID = tournamentID;
    }

    public int getP1PrereqIdentifier() {
        return p1PrereqIdentifier;
    }

    public void setP1PrereqIdentifier(int p1PrereqIdentifier) {
        this.p1PrereqIdentifier = p1PrereqIdentifier;
    }

    public int getP2PrereqIdentifier() {
        return p2PrereqIdentifier;
    }

    public void setP2PrereqIdentifier(int p2PrereqIdentifier) {
        this.p2PrereqIdentifier = p2PrereqIdentifier;
    }

    public boolean isP1IsPrereqLoser() {
        return p1IsPrereqLoser;
    }

    public void setP1IsPrereqLoser(boolean p1IsPrereqLoser) {
        this.p1IsPrereqLoser = p1IsPrereqLoser;
    }

    public boolean isP2IsPrereqLoser() {
        return p2IsPrereqLoser;
    }

    public void setP2IsPrereqLoser(boolean p2IsPrereqLoser) {
        this.p2IsPrereqLoser = p2IsPrereqLoser;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public int getP1Seed() {
        return p1Seed;
    }

    public void setP1Seed(int p1Seed) {
        this.p1Seed = p1Seed;
    }

    public int getP2Seed() {
        return p2Seed;
    }

    public void setP2Seed(int p2Seed) {
        this.p2Seed = p2Seed;
    }

    public Participant getP1() {
        return p1;
    }

    public void setP1(Participant p1) {
        this.p1 = p1;
    }

    public Participant getP2() {
        return p2;
    }

    public void setP2(Participant p2) {
        this.p2 = p2;
    }

    public Match getP1PreviousMatch() {
        return p1PreviousMatch;
    }

    public void setP1PreviousMatch(Match p1PreviousMatch) {
        this.p1PreviousMatch = p1PreviousMatch;
    }

    public Match getP2PreviousMatch() {
        return p2PreviousMatch;
    }

    public void setP2PreviousMatch(Match p2PreviousMatch) {
        this.p2PreviousMatch = p2PreviousMatch;
    }

    public boolean isP1Decided() {
        return P1Decided;
    }

    public void setP1Decided(boolean p1Decided) {
        P1Decided = p1Decided;
    }

    public boolean isP2Decided() {
        return P2Decided;
    }

    public void setP2Decided(boolean p2Decided) {
        P2Decided = p2Decided;
    }

    public String getP1PrereqText() {
        return p1PrereqText;
    }

    public void setP1PrereqText(String p1PrereqText) {
        this.p1PrereqText = p1PrereqText;
    }

    public String getP2PrereqText() {
        return p2PrereqText;
    }

    public void setP2PrereqText(String p2PrereqText) {
        this.p2PrereqText = p2PrereqText;
    }

    public String getWinnerID() {
        return winnerID;
    }

    public void setWinnerID(String winnerID) {
        this.winnerID = winnerID;
    }

    public String getLoserID() {
        return loserID;
    }

    public void setLoserID(String loserID) {
        this.loserID = loserID;
    }

    public boolean isWinners() {
        return isWinners;
    }

    public void setIsWinners(boolean winners) {
        isWinners = winners;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getScoresCSV() {
        return scoresCSV;
    }

    public void setScoresCSV(String scoresCSV) {
        this.scoresCSV = scoresCSV;
    }

    public void setWinners(boolean winners) {
        isWinners = winners;
    }

    public ArrayList<String> getGames() {
        return games;
    }

    public void setGames(ArrayList<String> games) {
        this.games = games;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getP1Score() {
        return p1Score;
    }

    public void setP1Score(String p1Score) {
        this.p1Score = p1Score;
    }

    public String getP2Score() {
        return p2Score;
    }

    public void setP2Score(String p2Score) {
        this.p2Score = p2Score;
    }
}
