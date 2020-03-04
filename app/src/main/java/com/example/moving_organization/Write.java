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

import com.example.moving_organization.objects.MoverTag;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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

            //Read rinstance = new Read();
            // writing (textview), and tagdb (button) for findview

            Button Writetotag;

            TextView tagcontent;
            TextView tagnot;
            TextView taglocation;
            TextView taginstructions;

            Spinner condition;

            // Variable for database test button
            Button testdatabase;

            DatabaseReference dbref;
            //FirebaseDatabase firebasedb;

            private String dbtagformat;





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
                taglocation = (TextView) findViewById(R.id.enterlocation);
                taginstructions = (TextView) findViewById(R.id.enterinstructions);

                testdatabase = (Button) findViewById(R.id.testdb);







                //dbref = firebasedb.getReference("Testing for success");
                //dbtagformat = dbref.push().getKey();


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




                //


                // Tag Data sample entry without UI change






                // Test firebase button
                testdatabase.setOnClickListener(new View.OnClickListener()
                {

                    @Override
                    public void onClick(View w) {
                        // 1st Attempt (Child method)
                        //dbref.child("Boxes").setValue(mTag);

                        addbox(taginstructions.getText().toString(),tagcontent.getText().toString(),taglocation.getText().toString());

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
                                                              // Added two more fields, needs testing
                                                              write(tagcontent.getText().toString(), taglocation.getText().toString(),taginstructions.getText().toString(),myTag);
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


    private void addbox(String Instruction, String Content, String Location){
            /*final MoverTag mTag = new MoverTag(taginstructions.getText().toString(),tagcontent.getText().toString(),taglocation.getText().toString());

            dbref = FirebaseDatabase.getInstance().getReference();

            dbref.child("Boxes").setValue(mTag);
            */

            /*
            String tcontent = tagcontent.getText().toString();
            String tlocation = taglocation.getText().toString();
            String tinstructions = taginstructions.getText().toString();

            */

            // Make this global (add to constructor)


            byte[] tid = myTag.getId();

            //String tagidnum = tid.toString();

            String taguid = BytesToHex(tid);

            String locationid = Location + taguid;

            dbref = FirebaseDatabase.getInstance().getReference();


            MoverTag mTag = new MoverTag(Instruction,Content,Location);

            dbref.child(locationid).setValue(mTag);



    }

    // Either call in write with button above
    // Or try onclick call in xml
    /*
    private void insertboxinfo()
    {
        addbox(tagcontent.getText().toString());
    }

     */




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
    // Add two more inputs for instructions and location
    private void write(String in1,String in2, String in3, Tag tag) throws IOException, FormatException
    {
        // Creating multiple array copies didn't exactly work, might try if this method doesn't work
        // First try inserting multiple create records in records
        // If not, then add other two inputs into new NdefMessage




        //NdefRecord[] records = { createRecord(in1,in2,in3) };

        NdefMessage message = new NdefMessage(new NdefRecord[]{createRecord(in1,in2,in3)});
        // Get instance of Ndef for tag

        Ndef format = Ndef.get(tag); // ndef = nfc message format
        // Establish connection for device to communicate with tag
        format.connect();
        // Write message to tag

        format.writeNdefMessage(message);

        // Once done writing, make sure to close the connection
        format.close();
    }


    private NdefRecord createRecord(String in1, String in2, String in3) throws UnsupportedEncodingException
    {
        String lang = "en";
        int inputsL = in1.getBytes().length + in2.getBytes().length + in3.getBytes().length;

        byte[] inputBytes = new byte[inputsL];





        // inputsL is a byte array which now has enough space
        // allocated for all three text fields
        // Take all inputs and use system.arraycopy to copy
        // everything one  by one (example open in one of the tabs)
        // Now don't need payload and length variables underneath
        // Try out and email professor 

        //inputBytes = in1.getBytes();

        //Add to inputBytes - in1, in2, in3
                //input.getBytes();
        // try creating additional byte for input
        byte[] in1Bytes = in1.getBytes();
        byte[] in2Bytes = in2.getBytes();
        byte[] in3Bytes = in3.getBytes();
        //byte[] input2Bytes = input2.getBytes();
        //byte[] input3Bytes = input3.getBytes();String input

        //byte[] langBytes = lang.getBytes("US-ASCII");
        //int langlength = langBytes.length;


        //int inputLength = inputBytes.length;
        //int input2Length = input2Bytes.length;
        //int input3Length = input3Bytes.length;
        // Actual message
        //byte[] payload = new byte[1 + langlength + inputLength];

        //payload[0] = (byte) langlength; // langlength is destination array that holds tag data

        // Test if copy into end of length of subsequent record works
        // May be spacing issue

        // Test out arraycopies! 2/18 2:05AM
        // Almost works!!!!
        //String inputstr = new String();

        System.arraycopy(in1Bytes, 0,inputBytes,0,in1Bytes.length);
        String inputstr = new String(inputBytes);
        System.out.println("Contents1: "+ inputstr);

        System.arraycopy(in2Bytes, 0, inputBytes, in1Bytes.length,in2Bytes.length);
        inputstr = new String(inputBytes);
        System.out.println("Contents2: "+ inputstr);

        System.arraycopy(in3Bytes,0, inputBytes, in1Bytes.length + in2Bytes.length,in3Bytes.length);
        inputstr = new String(inputBytes);
        System.out.println("Contents3: "+ inputstr);

        //System.arraycopy(langBytes, 0, payload, 1, langlength);

        //System.arraycopy(inputBytes, 0, payload, langlength - 1, inputLength);

        // Try langlength for destPos for 2nd,3rd, and 4th arraycopys

        // 02/14 1:56 AM Update: Issue us out of bounds exception with arraycopy, try swap with inputbytes2
        // Swap kind of works, big spacing issue
        // Also previous tag didn't overwrite properly, but new one did





        // Record is payload of data and metadata about message, including the record
        // records place in message,type, and ID
        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], inputBytes);

        return recordNFC;

    }




    /*
    private NdefRecord createRecord(String input) throws UnsupportedEncodingException
    {
        String lang = "en";
        byte[] inputBytes = input.getBytes();
        // try creating additional byte for input
        //byte[] input2Bytes = input2.getBytes();
        //byte[] input3Bytes = input3.getBytes();String input

        byte[] langBytes = lang.getBytes("US-ASCII");
        int langlength = langBytes.length;
        int inputLength = inputBytes.length;
        //int input2Length = input2Bytes.length;
        //int input3Length = input3Bytes.length;
        // Actual message
        byte[] payload = new byte[1 + langlength + inputLength];

        payload[0] = (byte) langlength; // langlength is destination array that holds tag data

        // Test if copy into end of length of subsequent record works
        // May be spacing issue

        System.arraycopy(langBytes, 0, payload, 1, langlength);

        System.arraycopy(inputBytes, 0, payload, langlength - 1, inputLength);

        // Try langlength for destPos for 2nd,3rd, and 4th arraycopys

        // 02/14 1:56 AM Update: Issue us out of bounds exception with arraycopy, try swap with inputbytes2
        // Swap kind of works, big spacing issue
        // Also previous tag didn't overwrite properly, but new one did





        // Record is payload of data and metadata about message, including the record
        // records place in message,type, and ID
        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);

        return recordNFC;

    }


     */


    private static String BytesToHex(byte[] toHex)
    {
            StringBuilder sb = new StringBuilder();
            for (byte ba: toHex)
            {
                sb.append(String.format("%02x", ba));
            }

            return sb.toString();
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
