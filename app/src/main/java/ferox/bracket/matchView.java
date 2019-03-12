package ferox.bracket;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;


public class matchView extends ConstraintLayout {

    Context mContext;
    String mMatchNumber;
    String mP1Seed;
    String mP2Seed;
    String mP1Name = "";
    String mP2Name = "";
    TextView tMatchNumber;
    TextView tP1Seed;
    TextView tP2Seed;
    TextView tP1Name;
    TextView tP2Name;



    public matchView(Context context) {
        super(context);

        init(context, null);
    }

    public matchView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public matchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    public matchView(Context context, @Nullable AttributeSet attrs, String matchNumber, String P1Seed, String P2Seed, String P1Name, String P2Name) {
        super(context, attrs);
        mMatchNumber = matchNumber;
        mP1Seed = P1Seed;
        mP2Seed = P2Seed;
        mP1Name = P1Name;
        mP2Name = P2Name;

        init2(context);
    }


    private void init(Context context, AttributeSet attrs) {
        mContext = context;

        final TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.matchView);

        mMatchNumber = ta.getString(R.styleable.matchView_matchNumber);
        mP1Seed = ta.getString(R.styleable.matchView_player1Seed);
        mP2Seed = ta.getString(R.styleable.matchView_player2Seed);
        mP1Name = ta.getString(R.styleable.matchView_player1Name);
        mP2Name = ta.getString(R.styleable.matchView_player2Name);

        ta.recycle();

        getMatchView(mMatchNumber, mP1Seed, mP2Seed, mP1Name, mP2Name);
    }

    private void init2(Context context) {
        TextView tMatchNumber = new TextView(getContext());
        TextView tP1Seed = new TextView(getContext());
        TextView tP2Seed = new TextView(getContext());
        TextView tP1Name = new TextView(getContext());
        TextView tP2Name = new TextView(getContext());


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = getResources().getDimensionPixelSize(R.dimen.match_width);
        int height = getResources().getDimensionPixelSize(R.dimen.match_height);
        int desiredWSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        int desiredHSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        setMeasuredDimension(desiredWSpec, desiredHSpec);
        super.onMeasure(desiredWSpec, desiredHSpec);
    }

    private void getMatchView(String matchNumber, String P1Seed, String P2Seed, String P1Name, String P2Name){

        System.out.println(matchNumber);
        System.out.println(P1Seed);
        System.out.println(P2Seed);
        System.out.println(P1Name);
        System.out.println(P2Name);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ConstraintLayout constraintLayout = (ConstraintLayout) inflater.inflate(R.layout.match, this, true);

        TextView textView1 = constraintLayout.findViewById(R.id.matchNumber);
        textView1.setText(matchNumber);
        TextView textView2 = constraintLayout.findViewById(R.id.seed1);
        textView2.setText(P1Seed);
        TextView textView3 = constraintLayout.findViewById(R.id.seed2);
        textView3.setText(P2Seed);
        TextView textView4 = constraintLayout.findViewById(R.id.participant1);
        textView4.setText(P1Name);
        TextView textView5 = constraintLayout.findViewById(R.id.participant2);
        textView5.setText(P2Name);

        addView(constraintLayout);
        requestLayout();
    }

    public void setMatchNumberText(String s) {
        tMatchNumber.setText(s);
    }

    public void setP1SeedText(String s) {
        tP1Seed.setText(s);
    }

    public void setP2SeedText(String s) {
        tP2Seed.setText(s);
    }

    public void setP1NameText(String s) {
        tP1Name.setText(s);
    }

    public void setP2NameText(String s) {
        tP2Name.setText(s);
    }
}