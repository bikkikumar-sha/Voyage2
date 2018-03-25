package com.fallout.android.voyage2;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


    public class scannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{
        private  ZXingScannerView mScannerView;
        private static int camRequestCode = 100;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_scanner);

            //check camera permissions --request permission
            if (ContextCompat.checkSelfPermission(scannerActivity.this, android.Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(scannerActivity.this, new String[] {android.Manifest.permission.CAMERA}, camRequestCode);
            }




            mScannerView = new ZXingScannerView(this);
        }

        public void onClick(View v){
            setContentView(mScannerView);
            mScannerView.setResultHandler(this);
            mScannerView.startCamera();
        }

        @Override
        protected void onPause() {
            super.onPause();
            mScannerView.stopCamera();
        }

        @Override
        public void handleResult(Result result){
//        Toast.makeText(getApplicationContext(), result.getText(), Toast.LENGTH_SHORT).show();
            Log.v("handler result", result.getText());
            Intent intent = getIntent();
            intent.putExtra("scan_result", String.valueOf(result.getText()));
            setResult(RESULT_OK, intent);
            finish();


            //mScannerView.resumeCameraPreview(this);
        }


    }