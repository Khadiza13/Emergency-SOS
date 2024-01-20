package com.example.emergency_sos;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PHONE_STATE_PERMISSION = 1;
    private static final int REQUEST_CALL_PHONE_PERMISSION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Request both phone state and call phone permissions before using telephony features
        requestPhoneStatePermission();
        requestCallPhonePermission();

        // Register a PhoneStateListener to detect volume button presses
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String phoneNumber) {
                super.onCallStateChanged(state, phoneNumber);
                if (state == TelephonyManager.CALL_STATE_IDLE) {
                    // Phone call has ended, enable volume button press detection
                    enableButtonPressDetection();
                } else {
                    // Phone call is active, disable volume button press detection
                    disableButtonPressDetection();
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void requestPhoneStatePermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_PHONE_STATE},
                    REQUEST_PHONE_STATE_PERMISSION);
        }
    }

    private void requestCallPhonePermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CALL_PHONE},
                    REQUEST_CALL_PHONE_PERMISSION);
        }
    }

    private void enableButtonPressDetection() {
        // Enable volume button press detection
        setVolumeControlStream(android.media.AudioManager.STREAM_VOICE_CALL);
    }

    private void disableButtonPressDetection() {
        // Disable volume button press detection
        setVolumeControlStream(android.media.AudioManager.USE_DEFAULT_STREAM_TYPE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            // Volume button pressed, initiate a call
            makeCall();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void makeCall() {
        // Replace "your_phone_number" with the actual phone number you want to call
        String phoneNumber = "tel:your phone number";
        Intent dial = new Intent(Intent.ACTION_CALL, android.net.Uri.parse(phoneNumber));
        startActivity(dial);
    }

    // Handle permission request results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PHONE_STATE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can now make a call (if needed)
            } else {
                // Permission denied, handle accordingly (e.g., show a message)
            }
        } else if (requestCode == REQUEST_CALL_PHONE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can now make a call
                makeCall();
            } else {
                // Permission denied, handle accordingly (e.g., show a message)
            }
        }
    }
}

