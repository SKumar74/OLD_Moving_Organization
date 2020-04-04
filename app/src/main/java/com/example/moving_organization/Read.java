package com.example.moving_organization;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.*;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;

public class Read extends AppCompatActivity {

    TextView readtag;
    Button rtomain;
    Tag readfromtag;
    PendingIntent pIntent;

    //04/03/2020
    // Testing Intent Filter

    // Problem stemming from enableforeground code
    // Continue to look into it tomorrow

    // Testing with another read method
    String tagInfo;

    Context context;

    public static final String AFTER_WRITE = "Tag is empty, kicked back to main menu";
    public static final String read_error = "Tag could not be read, try again!";

    private NfcAdapter nfcadapter = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);


        context = this;

        readtag = (TextView) findViewById(R.id.readingTag);

        rtomain = (Button) findViewById(R.id.readtomain);



        System.out.println("ACTION: INTENT "+ getIntent().getAction());

        // I/System.out: ACTION: INTENT null gets printed out (might be getintent().getaction() )

        System.out.println("INTENT PRINT OUT ONLY: " + getIntent().toString());

        // I/System.out: INTENT PRINT OUT ONLY: Intent { cmp=com.example.moving_organization/.Read }
        // The if below doesn't match upabove intent printout with .equals below

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction()))
        {
            readFromIntent(getIntent());
        }

        /*
        pIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);

        */

        // Return to main button Intent
        rtomain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(Read.this, MainActivity.class));
            }

        });

    }


    //////////  Functions

    // Also testing with new method 02/24
    @Override
    protected void onNewIntent(Intent intent)
    {

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {

            Tag detectedtag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            Ndef ndef = Ndef.get(detectedtag);

            tagInfo = ndef.getType() + "\nmaxsize:" + ndef.getMaxSize() + "bytes\n\n";

            readFromIntent(intent);

            readtag.setText(tagInfo);

        }


    }



    // Trying out new readFromIntent and parseTextRecord functions
    // Old ones are commented out below


    public void readFromIntent(Intent intent) {
        System.out.println("INTENT RECEIVED SUCCESSFULLY!");

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] tagdata = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            NdefMessage parseddata[] = null;

            int tagdatasize = 0;

            if (tagdata != null) {
                parseddata = new NdefMessage[tagdata.length];

                for (int i = 0; i < tagdata.length; i++) {
                    parseddata[i] = (NdefMessage) tagdata[i];
                    tagdatasize += parseddata[i].toByteArray().length;
                }
            }

            try {
                if (parseddata != null) {
                    NdefRecord tagrecord = parseddata[0].getRecords()[0];
                    String textrecord = parseTextRecord(tagrecord, context);

                    System.out.println("TAG PARSED");

                    tagInfo += textrecord + "\n\ntext\n" + tagdatasize + " bytes";
                }
            } catch (Exception e) {

            }
        }

    }


    public static String parseTextRecord(NdefRecord record, Context c) {

        System.out.println("ABLE TO GET INTO PARSERECORD FUNCTION!");


        // Well known is an indicator that the rtd is authentic for the tag
        // rtd is the security protocol that indicates the authenticity of tag
        if (record.getTnf() != NdefRecord.TNF_WELL_KNOWN) {
            return null;
        }

        // Detects if record in tag is text record
        // Can remove or interchange for other types of records
        if (!Arrays.equals(record.getType(), NdefRecord.RTD_TEXT)) {
            return null;
        }

        try {
            byte[] tagpayload = record.getPayload();

            String textencoding = ((tagpayload[0] & 0x80) == 0) ? "UTF-8" : "UTF-16";

            int languagecodelength = tagpayload[0] & 0x3f;

            String languagecode = new String(tagpayload, 1, languagecodelength, "US-ASCII");

            String trecord = new String(tagpayload, languagecodelength + 1,
                    tagpayload.length - languagecodelength - 1, textencoding);

            System.out.println("MADE IT TO END OF PARSERECORD FUNCTION!");

            return trecord;
        }
        catch (Exception e) {
            Toast.makeText(c, read_error, Toast.LENGTH_LONG).show();
            e.printStackTrace();
            throw new IllegalArgumentException(); // Indicates that illegal argument has been
                                                  // into method
        }

    }






        @Override
        public void onResume()
        {
            super.onResume();
            PendingIntent pIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            nfcadapter.enableForegroundDispatch(this,pIntent,null,null);
        }




 /*
    public void readFromIntent(Intent intent)
    {
        System.out.println("Code gets up to here when reading!\n");

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

            System.out.println("Tag was read, parsed, and ready for buildTagViews function!\n");
            buildTagViews(msgs);
        }
    }


    private void buildTagViews(NdefMessage[] msgs)
    {
        System.out.println("Got to beginning of buildTagViews function! If here, then readfromIntent function works!\n");
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


  */





}
