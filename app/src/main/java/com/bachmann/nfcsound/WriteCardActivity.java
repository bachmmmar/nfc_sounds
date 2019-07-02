package com.bachmann.nfcsound;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bachmann.nfcsound.AppContainer;
import com.bachmann.nfcsound.AppStatus;
import com.bachmann.nfcsound.R;
import com.bachmann.nfcsound.infra.DataEntry;
import com.bachmann.nfcsound.infra.DataEntryIterator;
import com.bachmann.nfcsound.infra.DataManager;

import java.io.IOException;
import java.io.InputStream;

public class WriteCardActivity extends AppCompatActivity {

    private static final String TAG_NFC = "NFC TAG";
    private static final String TAG_ACTIVITY = "WriteCardActivity";

    private AppStatus status;
    private NfcAdapter nfcAdapter;
    private DataManager manager;
    private DataEntryIterator iterator;

    private ImageView image;
    private TextView text;

    private DataEntry current_entry;

    PendingIntent pendingIntent;
    Tag myTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_card);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        status = ((AppContainer) getApplicationContext()).getStatus();
        manager = ((AppContainer) getApplicationContext()).getDataManager();
        iterator = new DataEntryIterator(manager);
        image = findViewById(R.id.currentItemImage);
        text = findViewById(R.id.currentItemText);

        //setupNFC();

        getNextProgrammableEntry();
        showImage();
    }

    private void getNextProgrammableEntry() {
        for (int i = 0; i < iterator.size(); i++) {
            current_entry = iterator.getNext();
            if (!current_entry.getLineDrawingPath().isEmpty()){
                return;
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG_ACTIVITY, "Activity Resume");
        //setupForegroundDispatch(this, nfcAdapter);
    }

    @Override
    protected void onPause() {
        //stopForegroundDispatch(this, nfcAdapter);

        Log.i(TAG_ACTIVITY, "Activity Pause");
        super.onPause();
    }

    public void showImage() {
        text.setText(current_entry.getNextName());
        String path = "";
        try {
            path = current_entry.getLineDrawingPath();
            image.setImageBitmap(getBitmapFromAssets(path));
        } catch (Exception e) {
            Toast.makeText(this, e.toString(),Toast.LENGTH_SHORT).show();
            Log.e(TAG_ACTIVITY, "Failed to show "+ current_entry.getName()
                    + " (" + path + "): " + e.toString());
        }
    }

    public void onClickNextItem(View v) {
        Log.i(TAG_ACTIVITY, "Next Item");
        getNextProgrammableEntry();
        showImage();
    }

    private Bitmap getBitmapFromAssets(String fileName) throws IOException {
        AssetManager assetManager = getAssets();

        InputStream istr = assetManager.open(fileName);
        Bitmap bitmap = BitmapFactory.decodeStream(istr);

        return bitmap;
    }


}