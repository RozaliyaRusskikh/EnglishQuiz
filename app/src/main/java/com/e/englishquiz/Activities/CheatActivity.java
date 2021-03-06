package com.e.englishquiz.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.e.englishquiz.R;

public class CheatActivity extends AppCompatActivity {

    private static final String EXTRA_ANSWER_IS_TRUE = "com.e.englishquiz.answer_is_true"; //adding an extra constant
    private static final String EXTRA_ANSWER_SHOWN = "com.e.englishquiz.answer_shown"; // constant for the extra's key
    private static final String ANSWER = "cheated_answer";
    private static final String CHEAT = "cheat";
    private static final String INVISIBLE = "invisible";

    private Button mShowAnswerButton;
    private TextView mAnswerTextView;
    private boolean mAnswerIsTrue;
    private boolean mHasCheat;
    private boolean mIsInvisible;


    public static Intent newIntent(Context packageContext, boolean isAnswerTrue) { //creating method to encapsulating of implementation details (creating an Intent with the extras)
        Intent intent = new Intent(packageContext, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, isAnswerTrue);
        return intent;
    }

    public static boolean wasAnswerShown(Intent result) { // decoding the extra
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);//retrieving the value from the extra

        mAnswerTextView = (TextView) findViewById(R.id.answer_text_view);

        mShowAnswerButton = (Button) findViewById(R.id.show_answer_button);
        mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //enable cheating: show answer
                String answer = String.valueOf(mAnswerIsTrue);
                mAnswerTextView.setText(answer);
                setAnswerShownResult(true);
                mHasCheat = true;

                // checking the device's build version first
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // circular animation will appear when the app is running on a device with API level 21 or higher
                    // adding activity animation code
                    int cx = mShowAnswerButton.getWidth() / 2;
                    int cy = mShowAnswerButton.getHeight() / 2;
                    float radius = mShowAnswerButton.getWidth();
                    Animator anim = ViewAnimationUtils.
                            createCircularReveal(mShowAnswerButton, cx, cy, radius, 0); // creating animator
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mShowAnswerButton.setVisibility(View.INVISIBLE); // hiding the ShowAnswerButton: radius moves from the width of the button to 0
                        }
                    });
                    anim.start();
                } else {
                    mShowAnswerButton.setVisibility(View.INVISIBLE);
                }
                mIsInvisible = true;
            }
        });
    }

    private void setAnswerShownResult(boolean isAnswerShown) { // sending back an intent
        Intent data = new Intent(); // creating an intent instance
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown); // putting an extra on intent
        setResult(RESULT_OK, data); // setting a result (sending data back to the parent)
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);
        saveInstanceState.putBoolean(ANSWER, mAnswerIsTrue); // saving the current state of the app
        saveInstanceState.putBoolean(CHEAT, mHasCheat);
        saveInstanceState.putBoolean(INVISIBLE, mIsInvisible);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) { // Now rotating CheatActivity does not clear out cheating result
        super.onRestoreInstanceState(savedInstanceState);
        mAnswerIsTrue = savedInstanceState.getBoolean(ANSWER);
        mIsInvisible = savedInstanceState.getBoolean(INVISIBLE);
        mHasCheat = savedInstanceState.getBoolean(CHEAT);
        if (mHasCheat) {
            setAnswerShownResult(true);
            if (mAnswerIsTrue) {
                mAnswerTextView.setText(R.string.true_button);
            } else {
                mAnswerTextView.setText(R.string.false_button);
            }
        }

        if (mIsInvisible) {
            mShowAnswerButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
