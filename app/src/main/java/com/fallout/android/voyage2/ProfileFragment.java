package com.fallout.android.voyage2;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private TextView nameTextView;
    private TextView emailTextView;
    private Button logoutButton;
    private TextView noScTextView;
    private String Uid;
    private TextView memIdTextView;
    private ProgressBar progressBar;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        Uid = mUser.getUid();
        progressBar = view.findViewById(R.id.story_progress);
        nameTextView = view.findViewById(R.id.pf_name);
        emailTextView = view.findViewById(R.id.pf_email);
        logoutButton = view.findViewById(R.id.pf_logout);
        noScTextView = view.findViewById(R.id.pf_nosc);
        memIdTextView = view.findViewById(R.id.pf_memid);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String name = mUser.getDisplayName();
        String email = mUser.getEmail();
        nameTextView.setText(name);
        emailTextView.setText(email);

        //listens for logout button click to log out user
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creating a dialog to confirm sign out....
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(getActivity());
                }
                builder.setTitle("Sign Out?")
                        .setMessage("NOTE: Your Progress will be lost!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mAuth.signOut();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                getActivity().finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        //displaying member id
        memIdTextView.setText(Uid);
        memIdTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                dialogBuilder.setTitle(Uid);
                android.support.v7.app.AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

        //set the scanned qr
        final String PREFS_NAME = "MyPref";
        final SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_NAME, getActivity().MODE_PRIVATE);
        int scans = prefs.getInt("my_checkpoint", 0);

        noScTextView.setText(String.valueOf(scans - 1));
        //set progress for progress bar
        if (scans > 1) {
            progressBar.setProgress((scans - 1) * 10);
        }

    }


}
