package com.example.moving_organization;

import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class Write extends AppCompatActivity{

        public static final String ERROR_DETECTED = "No Tag detected";
        public static final String WRITE_SUCCESS = "Tag written to successfully!";
        public static final String WRITE_ERROR = "Error writing to tag, get closer to the tag";
        NfcAdapter nfcAdapter;
        PendingIntent pendingIntent;
        IntentFilter writeTagFilters[];
        boolean writeMode;
        Tag myTag;
        Context context; // Interface for global information on application environment

        Read rinstance = new Read();
        // writing (textview), and tagdb (button) for findview

        Button Writetotag;
        TextView tagcontent;
        TextView tagnot;
        Spinner condition;




        @Override
        public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        tomainbutton();


        context = this;

        tagcontent = (TextView) findViewById(R.id.writing);
        Writetotag = (Button) findViewById(R.id.TagDB);
        tagnot = (TextView) findViewById(R.id.tagnotification);
        condition = (Spinner) findViewById(R.id.contentstatus);

        ArrayList<String> statusarray = new ArrayList<>();

        statusarray.add("Packed");
        statusarray.add("Received");
        statusarray.add("Damaged");

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,statusarray);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // R.layout.simple_spinner_dropdown_item is customer spinner layout, mess around with it later for app appearance
        // For now, try padding

        // Weird issue: Can select spinner items
        // But spinner items don't show up, just space to select items MUST FIX
        condition.setAdapter(statusAdapter);
        condition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = parent.getItemAtPosition(position).toString();
                // For now use Toast (Phone notification), later use new textview
                //Toast.makeText(parent.getContext(), selection,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        Writetotag.setOnClickListener(new View.OnClickListener()
        {
             @Override
             public void onClick(View w) {
                     try {
                             if(myTag == null)
                             {
                                     Toast.makeText(context, ERROR_DETECTED, Toast.LENGTH_LONG).show();
                             }
                             else
                             {
                                     // write function for write
                                     write(tagcontent.getText().toString(), myTag);
                                     //Toast.makeText(context, WRITE_SUCCESS, Toast.LENGTH_LONG).show();
                                     tagnot.setText(WRITE_SUCCESS);

                             }
                     }
                     catch (IOException e)
                     {
                             Toast.makeText(context, WRITE_ERROR, Toast.LENGTH_LONG).show();
                             e.printStackTrace();
                     }
                     catch(FormatException e)
                     {
                             Toast.makeText(context, WRITE_ERROR, Toast.LENGTH_LONG).show();
                             e.printStackTrace();
                     }



             }
        }
         );

            // Copy and paste here if not working in on create From nfcAdapter to writeTagFilters
            // Update: 4:13am movinf nfcAdapter and rest of snipper to button event listener, need to plug in phone to test new build

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null)
        {
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
        }



           //This section is causing problems for write section, error is something to do with unwritten tag,
           //gonna try to adding button to read, and moving this code to Read.java

        //rinstance.readFromIntent(getIntent());

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[] { tagDetected};



        }


  private void tomainbutton()
  {
      Button wtomain;

      wtomain = (Button) findViewById(R.id.rtomain);

      wtomain.setOnClickListener(new View.OnClickListener()
      {
          @Override
          public void onClick(View view)
          {

              Intent returnmain = new Intent(Write.this, MainActivity.class);
              startActivity(returnmain);
          }

      });

  }





        // Write intents to buttons

    // Write to Tag
    private void write(String input, Tag tag) throws IOException, FormatException
    {
        NdefRecord[] records = { createRecord(input) };
        NdefMessage message = new NdefMessage(records);
        // Get instance of Ndef for tag
        Ndef format = Ndef.get(tag); // ndef = nfc message format
        // Establish connection for device to communicate with tag
        format.connect();
        // Write message to tag
        format.writeNdefMessage(message);

        // Once done writing, make sure to close the connection
        format.close();
    }

    private NdefRecord createRecord(String input) throws UnsupportedEncodingException
    {
        String lang = "en";
        byte[] inputBytes = input.getBytes();
        byte[] langBytes = lang.getBytes("US-ASCII");
        int langlength = langBytes.length;
        int inputLength = inputBytes.length;
        // Actual message
        byte[] payload = new byte[1 + langlength + inputLength];

        payload[0] = (byte) langlength;

        System.arraycopy(langBytes, 0, payload, 1, langlength);
        System.arraycopy(inputBytes, 0, payload, 1 + langlength, inputLength);

        // Record is payload of data and metadata about message, including the record
        // records place in message,type, and ID
        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);

        return recordNFC;

    }


    // Enabling and Disabling write

    private void WriteModeOn()
    {
        writeMode = true;
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
    }

    private void WriteModeOff()
    {
        writeMode = false;
        nfcAdapter.disableForegroundDispatch(this);
    }


    @Override
    public void onPause()
    {
        super.onPause();
        WriteModeOff();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        WriteModeOn();
    }


    @Override
    protected void onNewIntent(Intent intent)
    {
        //Read instance = new Read();
        setIntent(intent);
        //instance.readFromIntent(intent); // Problem for write button, invokes method from read.java
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()))
        {
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        }

    }




  }
