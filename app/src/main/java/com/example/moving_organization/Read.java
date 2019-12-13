package com.example.moving_organization;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.*;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

public class Read extends AppCompatActivity {

    TextView readtag;
    Button rtomain;
    Tag readfromtag;
    PendingIntent pIntent;
    public static final String AFTER_WRITE = "Tag is empty, kicked back to main menu";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        readtag = (TextView) findViewById(R.id.readingTag);

        rtomain = (Button) findViewById(R.id.readtomain);


        readFromIntent(getIntent());

        pIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);



        // Return to main button Intent
        rtomain.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                startActivity(new Intent(Read.this, MainActivity.class));
            }

        });

    }


    public void readFromIntent(Intent intent)
    {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action))
        {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs = null;
            if (rawMsgs != null)
            {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++)
                {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }

            }
            buildTagViews(msgs);
        }
    }


    private void buildTagViews(NdefMessage[] msgs)
    {
        if (msgs == null || msgs.length == 0)
        {
            Toast.makeText(this, AFTER_WRITE, Toast.LENGTH_LONG).show();
            return;
        }

        String text = "";
        byte[] payload = msgs[0].getRecords()[0].getPayload();
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16"; // Get text encoding
        int langaugeCodeLength = payload[0] & 0063; // get Language Code (en)

        try {
            text = new String(payload, langaugeCodeLength + 1, payload.length - langaugeCodeLength - 1, textEncoding);
        }
        catch (UnsupportedEncodingException e)
        {
            Log.e("UnsupportedEncoding", e.toString());
        }

        readtag.setText("Content: "+ text);

    }


    @Override
    protected void onNewIntent(Intent intent)
    {
       // Write instance = new Write();
        setIntent(intent);
        readFromIntent(intent); // Problem for write button, invokes method from read.java

        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()))
        {
            readfromtag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        }



    }

}
