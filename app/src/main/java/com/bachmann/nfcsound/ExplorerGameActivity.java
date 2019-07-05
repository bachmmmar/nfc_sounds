package com.bachmann.nfcsound;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bachmann.nfcsound.infra.DataManager;

import java.io.IOException;
import java.io.InputStream;


public class ExplorerGameActivity extends AppCompatActivity {

    private static final String TAG_NFC = "NFC TAG";
    private static final String TAG_ACTIVITY = "ExplorerGameActivity";

    private AppStatus status;
    private NfcAdapter nfcAdapter;
    private NFCSoundManager manager;

    private ImageView displayed_image;
    private TextView displayed_name;

    PendingIntent pendingIntent;
    Tag myTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer_game);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        status = ((AppContainer) getApplicationContext()).getStatus();
        DataManager dm = ((AppContainer)getApplicationContext()).getDataManager();
        manager = new NFCSoundManager(getResources(), dm);

        displayed_image = findViewById(R.id.ivImage);
        displayed_name = findViewById(R.id.tvName);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        setupNFC();

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

    @Override
    public void onBackPressed() {
        if (status.isAppLocked()) {
            return;
        }
        super.onBackPressed();
    }

    private void setupNFC() {

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
    }


    public void tagDetected(String text) {
        try {
            manager.soundTagDetected(text);
        } catch (Exception e) {
            Toast.makeText(this, e.toString(),Toast.LENGTH_SHORT).show();
        }

        showName();
        showImage();
    }

    public void showImage() {
        try {
            String path = manager.getImagePathToShow();
            displayed_image.setImageBitmap(getBitmapFromAssets(path));
        } catch (Exception e) {
            Toast.makeText(this, e.toString(),Toast.LENGTH_SHORT).show();
        }
    }

    public void showName() {
        String n = manager.getNameToShow();
        displayed_name.setText(n);
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
        processIntent(intent);
    }

    void processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];

        String name = new String(msg.getRecords()[0].getPayload());

        //Toast.makeText(ExplorerGameActivity.this, "NFC Content: " + name,Toast.LENGTH_LONG).show();
        Log.i(TAG_NFC, "NFC Content: " + name );

        tagDetected(name);
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

    public void onClickRecent(View v) {
        Log.i(TAG_ACTIVITY, "Recent Clicked");
        showRecentPlaylistSelection();
    }

    private void showRecentPlaylistSelection(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ExplorerGameActivity.this);
        builder.setTitle(R.string.RecentlyPlayedDialogTitel)
                .setItems(manager.getRecentlyPlayed(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        String sound_name = manager.getRecentlyPlayed()[which];
                        Log.i(TAG_ACTIVITY, sound_name + " playlist has been selected!");

                        manager.soundTagDetected(sound_name);
                        showImage();
                    }
                });
        builder.create();
        builder.show();
    }

}
