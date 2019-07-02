package com.bachmann.nfcsound;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

public class IntroductionActivity extends AppCompatActivity {

    private static final String TAG_ACTIVITY = "IntroductionActivity";

    private AppStatus status;
    private NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        status = ((AppContainer) getApplicationContext()).getStatus();
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        isNfcSupportedOnDevice();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG_ACTIVITY, "Activity Resume");
        updateViewButtons();
    }


    private boolean isNfcSupportedOnDevice() {
        if (nfcAdapter == null) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle(getString(R.string.Allert_NFCNotSupported_title));
            alertDialog.setMessage(getString(R.string.Allert_NFCNotSupported_text));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.Allert_NFCNotSupported_okButton),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            alertDialog.show();
            return false;
        }
        return true;
    }

    public void onClickEnableDisableNFC(View v) {
        Log.i(TAG_ACTIVITY, "Switch NFC Clicked");


        Toast.makeText(this, getString(R.string.NFC_instruction),
                    Toast.LENGTH_LONG).show();

        startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
    }


    public void onClickEnableDisablePinning(View v) {
        if (!status.isAppLocked()) {
            Log.i(TAG_ACTIVITY, "Switch Pinning Clicked -> lock");
            startLockTask();
        } else {
            Log.i(TAG_ACTIVITY, "Switch Pinning Clicked -> unlock");
            stopLockTask();
        }

    }

    public void onClickStartExplorerGame(View v) {
        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, getString(R.string.NFC_disabled),
                    Toast.LENGTH_LONG).show();
            return;
        }

        startActivity(new Intent(this, ExplorerGameActivity.class));
    }

    public void onClickWriteCard(View v) {
        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, getString(R.string.NFC_disabled),
                    Toast.LENGTH_LONG).show();
            return;
        }

        startActivity(new Intent(this, WriteCardActivity.class));
    }



    private void updateViewButtons() {
        Switch sw = findViewById(R.id.enableNFC);
        sw.setChecked(nfcAdapter.isEnabled());

        Switch pin = findViewById(R.id.enablePinning);
        pin.setChecked(status.isAppLocked());
    }
}
