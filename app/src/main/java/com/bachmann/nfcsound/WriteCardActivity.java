package com.bachmann.nfcsound;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bachmann.nfcsound.infra.DataEntry;
import com.bachmann.nfcsound.infra.DataEntryIterator;
import com.bachmann.nfcsound.infra.DataManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class WriteCardActivity extends AppCompatActivity {

    private static final String TAG_NFC = "NFC TAG";
    private static final String TAG_ACTIVITY = "WriteCardActivity";

    private NfcAdapter nfcAdapter;
    private DataManager manager;
    private DataEntryIterator iterator;

    private ImageView image;
    private TextView text;

    private DataEntry current_entry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_card);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        manager = ((AppContainer) getApplicationContext()).getDataManager();
        iterator = new DataEntryIterator(manager);
        image = findViewById(R.id.currentItemImage);
        text = findViewById(R.id.currentItemText);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

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
        setupForegroundDispatch(this, nfcAdapter);
    }

    @Override
    protected void onPause() {
        stopForegroundDispatch(this, nfcAdapter);

        Log.i(TAG_ACTIVITY, "Activity Pause");
        super.onPause();
    }


    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
        processIntent(getIntent());
    }

    /**
     * Start Writing tag if intent appeared
     */
    void processIntent(Intent intent) {
        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if(supportedTechs(detectedTag.getTechList())) {
            // check if tag is writable (to the extent that we can
            if(writableTag(detectedTag)) {
                //writeTag here
                WriteResponse wr = writeTag(getTagAsNdef(), detectedTag);
                String message = (wr.getStatus() == 1? "Success: " : "Failed: ") + wr.getMessage();
                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(),getString(R.string.Hint_tagNotWritable), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(),getString(R.string.Hint_tagNotSupported), Toast.LENGTH_LONG).show();
        }

    }

    private NdefMessage getTagAsNdef() {
        String nameToWrite = current_entry.getName();
        byte[] bytesToWrite = nameToWrite.getBytes(Charset.forName("UTF8"));

        NdefRecord [] record = new NdefRecord[] {NdefRecord.createMime(getString(R.string.nfc_mime_type), bytesToWrite)};

        return new NdefMessage(record);

    }


    private class WriteResponse {
        int status;
        String message;
        WriteResponse(int Status, String Message) {
            this.status = Status;
            this.message = Message;
        }
        public int getStatus() {
            return status;
        }
        public String getMessage() {
            return message;
        }
    }

    public WriteResponse writeTag(NdefMessage message, Tag tag) {
        int size = message.toByteArray().length;
        String mess = "";
        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                if (!ndef.isWritable()) {
                    return new WriteResponse(0, getString(R.string.Hint_tagReadOnly));
                }
                if (ndef.getMaxSize() < size) {
                    return new WriteResponse(0,getString(R.string.Hint_tagToLessCapacity));
                }
                ndef.writeNdefMessage(message);
                mess = getString(R.string.Hint_tagWriteSuccessful) + " (" + current_entry.getName() +")";
                return new WriteResponse(1,mess);
            } else {
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        mess = getString(R.string.Hint_tagWriteSuccessfulWithFormat) + " (" + current_entry.getName() +")";
                        return new WriteResponse(1,mess);
                    } catch (IOException e) {
                        return new WriteResponse(0, getString(R.string.Hint_tagFormatFailed));
                    }
                } else {
                    return new WriteResponse(0, getString(R.string.Hint_tagNotSupportNDEF));
                }
            }
        } catch (Exception e) {
            return new WriteResponse(0, getString(R.string.Hint_tagWriteFailed));
        }
    }

    public static boolean supportedTechs(String[] techs) {
        boolean ultralight=false;
        boolean nfcA=false;
        boolean ndef=false;
        for(String tech:techs) {
            if(tech.equals("android.nfc.tech.MifareUltralight")) {
                ultralight=true;
            }else if(tech.equals("android.nfc.tech.NfcA")) {
                nfcA=true;
            } else if(tech.equals("android.nfc.tech.Ndef") || tech.equals("android.nfc.tech.NdefFormatable")) {
                ndef=true;
            }
        }
        if(ultralight && nfcA && ndef) {
            return true;
        } else {
            return false;
        }
    }

    private boolean writableTag(Tag tag) {
        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                if (!ndef.isWritable()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.Hint_tagReadOnly),Toast.LENGTH_LONG).show();
                    ndef.close();
                    return false;
                }
                ndef.close();
                return true;
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), getString(R.string.Hint_tagReadFailed),Toast.LENGTH_LONG).show();
        }
        return false;
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


    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        //ended up setting mFilters and mTechLists to null --> all cards get handled inside this activity
        adapter.enableForegroundDispatch(activity, pendingIntent, null, null);
    }

    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }
}