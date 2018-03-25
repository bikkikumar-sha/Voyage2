package com.fallout.android.voyage2;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private Button scanButton;
    private int REQUEST_CODE = 0;
    private TextView storyTextview;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        scanButton = view.findViewById(R.id.scan_button);
        storyTextview = view.findViewById(R.id.story_textview);

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {      //not singed in
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }                                               //already singed in
        else {
            firebaseUser = firebaseAuth.getCurrentUser();
        }


        //todo update story textview and also screen on win

        //update the storytextview if activity restart -- also update if already won
        updateStory("update");


        //set on click listerner onm the buttons
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Objects.equals(scanButton.getText(), "send email")) {
                    //the story has been completed  -- send email instead
                    sendEmail();


                } else {
                    Intent intent = new Intent(getActivity(), scannerActivity.class);
                    startActivityForResult(intent, REQUEST_CODE);
                }
            }
        });
    }

    //get the scan results --do stuffs with it
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                String res = extras.getString("scan_result");
//                Toast.makeText(getActivity(), "" + res, Toast.LENGTH_SHORT).show();
                Log.d("games", "" + res);
                updateStory(res);       //do stuffs based on the result scanned

            }
        }
    }

    void updateStory(String res) {
        final String PREFS_NAME = "MyPref";
        final SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_NAME, getActivity().MODE_PRIVATE);
        final SharedPreferences.Editor editor = getActivity().getSharedPreferences(PREFS_NAME, getActivity().MODE_PRIVATE).edit();

        int checkpoint = prefs.getInt("my_checkpoint", 0);

        //Only Activated on first scan -- sets the story the user wants to play -- set the begintime...
        if (checkpoint == 0) {
            if (Objects.equals(res, "secretstory1")) {
                long beginTime = System.currentTimeMillis();
                editor.putInt("my_story", 1);
                editor.putLong("begin_time", beginTime);
                editor.commit();

            } else if (Objects.equals(res, "secretstory2")) {
                long beginTime = System.currentTimeMillis();
                editor.putInt("my_story", 2);
                editor.putLong("begin_time", beginTime);
                editor.commit();
            } else {
                Log.v("games", "story can't be selected");
                return;
            }
        }
        int myStory = prefs.getInt("my_story", -1);
        String[] story_qr = null;
        String[] story_frag = null;
        if (myStory == 1) {
            story_qr = getResources().getStringArray(R.array.story1_qr);
            story_frag = getResources().getStringArray(R.array.story1);
        } else if (myStory == 2) {
            story_qr = getResources().getStringArray(R.array.story2_qr);
            story_frag = getResources().getStringArray(R.array.story2);
        } else {
            Log.v("games", "story cant be selected");
            return;
        }
        //-----------the story number is set--------------


        //check if the function is called from on create
        //if called from oncreate -- it will have "update" as the passed parameter
        if (Objects.equals(res, "update")) {
            Log.d("games cp", "" + checkpoint);
            storyTextview.setText("");
            for (int i = 0; i < checkpoint; i++) {            //[populate the story text view]
                storyTextview.append(story_frag[i]);
            }
            if (checkpoint == 11) {
                scanButton.setText("send email");
            }
            return;
        }
        //----------------call from oncreate ends--------------------------


        //now check if the qr code received is the correct one, and advance the story
        //qr scan is correct --increment the checkpoint --populate the story textview
        if (Objects.equals(story_qr[checkpoint], res)) {
            editor.putInt("my_checkpoint", checkpoint + 1);
            editor.commit();
            storyTextview.setText("");
            for (int i = 0; i <= checkpoint; i++) {            //[populate the story text view]
                storyTextview.append(story_frag[i]);
            }
            if (checkpoint == 10) {             //game is won
                Log.d("games", "You win");
                Toast.makeText(getActivity(), "Congratulations! you won the game!", Toast.LENGTH_SHORT).show();

                //-- hide scan button
                scanButton.setText("send email");

                //set the end time...
                long endTime = System.currentTimeMillis();
                editor.putLong("end_time", endTime);
                editor.commit();
            }
            Toast.makeText(getActivity(), "Success! Story Updated.", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(getActivity(), "Invalid Qr code", Toast.LENGTH_SHORT).show();
        }

    }


    private void sendEmail() {
        final String PREFS_NAME = "MyPref";
        final SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_NAME, getActivity().MODE_PRIVATE);
        //get the time played(seconds)
        Long b = prefs.getLong("begin_time", 0);
        Long e = prefs.getLong("end_time", 0);
        final Long tt = (e-b)/1000;

        //dialog asking if send an winning email or not.
        new AlertDialog.Builder(getActivity()).setTitle("Send E-Mail")
                .setMessage("Do you Want To send Email now?")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //sending email on winning
                        int storyNumber = prefs.getInt("my_story", 0);
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
                                Uri.fromParts("mailto", "bikkikumarsha@gmail.com", null));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "I Completed Voyage 2");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "Completion ID: TH32NXSD347AVI54SN" + storyNumber + "HUQ" + String.valueOf(tt) + "EX3"
                                + "\n Name: " + firebaseUser.getDisplayName()
                                + "\n Email: " + firebaseUser.getEmail());
                                startActivity(emailIntent);
                    }
                }).create().show();
    }


}