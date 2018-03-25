package com.fallout.android.voyage2;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.hololo.tutorial.library.Step;
import com.hololo.tutorial.library.TutorialActivity;

public class TutoActivity extends TutorialActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setPrevText("BACK"); // Previous button text
        setNextText("NEXT"); // Next button text
        setFinishText("START"); // Finish button text
        setCancelText("CANCEL"); // Cancel button text


        addFragment(new Step.Builder().setTitle("Solve Mysteries like a detective!")
                .setContent("")
                .setBackgroundColor(Color.parseColor("#20B2AA")) // int background color
                .setDrawable(R.drawable.det_tuto) // int top drawable
                .setSummary("Solving the mystery will reveal the location of next qr code.")
                .build());

        addFragment(new Step.Builder().setTitle("Scan the hidden QR codes!")
                .setContent("")
                .setBackgroundColor(Color.parseColor("#20B2AA")) // int background color
                .setDrawable(R.drawable.scan_qr_tuto) // int top drawable
                .setSummary("Scanning the correct qr-code will unlock the next part of the story.")
                .build());

        addFragment(new Step.Builder().setTitle("Let the Adventure Begin!")
                .setContent("")
                .setBackgroundColor(Color.parseColor("#20B2AA")) // int background color
                .setDrawable(R.drawable.tmap_tuto) // int top drawable
                .setSummary("One who completes the story in shortest time, will get the rewards.")
                .build());

    }
    @Override
    public void finishTutorial() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();    }
}
