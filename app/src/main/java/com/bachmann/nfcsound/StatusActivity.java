package com.bachmann.nfcsound;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;


public class StatusActivity extends AppCompatActivity {

    private static final String TAG_NFC = "NFC TAG";
    private static final String TAG_ACTIVITY = "StatusActivity";

    private NfcAdapter nfcAdapter;
    private NFCSoundManager manager;

    private ImageView image;

    PendingIntent pendingIntent;
    Tag myTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        manager = ((NFCSoundManager)getApplicationContext());

        image = findViewById(R.id.ivImage);

        setupNFC();

        if (!manager.isInitialized()) {
            manager.initializeApplication();
            pinApp();
        }

        showImage();
    }


    @Override
    protected void onResume() {
    super.onResume();
        Log.i(TAG_ACTIVITY, "Activity Resume");
        setupForegroundDispatch(this, nfcAdapter);
    }

    @Override
    protected void onPause() {
        stopForegroundDispatch(this, nfcAdapter);

        Log.i(TAG_ACTIVITY, "Activity Pause");
        super.onPause();
    }

    public void onClickRecent(View v) {
        Log.i("Button", "Play Clicked");
    }

    private void setupNFC() {

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
        }
        else if(!nfcAdapter.isEnabled()){
            Toast.makeText(this,
                    "NFC NOT Enabled!",
                    Toast.LENGTH_LONG).show();

            startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
        }

        readFromIntent(getIntent());

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
    }


    private void pinApp() {
        startLockTask();
    }


    public void tagDetected(String text) {
        try {
            manager.soundTagDetected(text);
        } catch (Exception e) {
            Toast.makeText(this, e.toString(),Toast.LENGTH_SHORT).show();
        }

        showImage();
    }

    public void showImage() {
        try {
            String path = manager.getImagePathToShow();
            image.setImageBitmap(getBitmapFromAssets(path));
        } catch (Exception e) {
            Toast.makeText(this, e.toString(),Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap getBitmapFromAssets(String fileName) throws IOException {
        AssetManager assetManager = getAssets();

        InputStream istr = assetManager.open(fileName);
        Bitmap bitmap = BitmapFactory.decodeStream(istr);

        return bitmap;
    }


    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        readFromIntent(intent);
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        }
    }



    private void readFromIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs = null;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            }
            buildTagViews(msgs);
        }
    }
    private void buildTagViews(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0) return;

        String text = "";
        byte[] payload = msgs[0].getRecords()[0].getPayload();
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16"; // Get the Text Encoding
        int languageCodeLength = payload[0] & 0063; // Get the Language Code, e.g. "en"
        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
        languageCodeLength=0;
        try {
            // Get the Text
            //text = new String(payload, languageCodeLength+1 , payload.length - languageCodeLength - 1, textEncoding);
            text = new String(payload, languageCodeLength, payload.length - languageCodeLength, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG_NFC,"UnsupportedEncoding: "+ e.toString());
        } catch (StringIndexOutOfBoundsException e) {
            Log.e(TAG_NFC,"Error while putting payload into string: "+ e.toString() + ", " + payload);
            Log.e(TAG_NFC,"LanguageCodeLength: " + languageCodeLength);
            Log.e(TAG_NFC,"TextEncoding: " + textEncoding);
            Log.e(TAG_NFC,"PayloadLength: " + payload.length);
        }

        Toast.makeText(StatusActivity.this, "NFC Content: " + text,Toast.LENGTH_LONG).show();
        Log.e(TAG_NFC, "NFC Content: " + text );

        tagDetected(text);
    }


    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            Resources res = activity.getResources();
            String mime_type = res.getString(R.string.nfc_mime_type);
            filters[0].addDataType(mime_type);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }


}
