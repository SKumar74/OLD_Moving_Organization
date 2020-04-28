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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Write extends AppCompatActivity{
 //SC:  Errors are usually added in aseparate error file 
                public static final String ERROR_DETECTED = "No Tag detected";
                public static final String WRITE_SUCCESS = "Tag written to successfully!";
                public static final String WRITE_ERROR = "Error writing to tag, get closer to the tag";
                NfcAdapter nfcAdapter;
                PendingIntent pendingIntent;
                IntentFilter writeTagFilters[];
                boolean writeMode;
                Tag myTag;
                Context context; // Interface for global information on application environment


                Button Writetotag;

                TextView tagcontent;
                TextView tagnot;
                TextView taglocation;
                TextView taginstructions;

                Spinner condition;

                // Variable for database test button
                Button testdatabase;

                DatabaseReference dbref;


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
                    taglocation = (TextView) findViewById(R.id.enterlocation);
                    taginstructions = (TextView) findViewById(R.id.enterinstructions);

                    testdatabase = (Button) findViewById(R.id.testdb);






                    // Test firebase button
                    testdatabase.setOnClickListener(new View.OnClickListener()
                    {

                        @Override
                        public void onClick(View w) {

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
                                                                  write(tagcontent.getText().toString(), taglocation.getText().toString(),taginstructions.getText().toString(),myTag);
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


                    nfcAdapter = NfcAdapter.getDefaultAdapter(this);
                    if (nfcAdapter == null)
                    {
                        Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
                        finish();
                    }


                    pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
                    IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
                    tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
                    writeTagFilters = new IntentFilter[] { tagDetected};



                }


        private void addbox(String Instruction, String Content, String Location){



                String check_user = "NoJbT7CdHvgyCFpL3O4IPEpslrA3";


                byte[] tid = myTag.getId();


                String taguid = BytesToHex(tid);

                String locationid = Location + taguid;

                dbref = FirebaseDatabase.getInstance().getReference();


                MoverTag mTag = new MoverTag(Instruction,Content,Location);


            dbref.child("Users").child(check_user).child("Boxes").child(locationid).push().setValue(mTag);




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






        private void write(String in1,String in2, String in3, Tag tag) throws IOException, FormatException
        {





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



            byte[] in1Bytes = in1.getBytes();
            byte[] in2Bytes = in2.getBytes();
            byte[] in3Bytes = in3.getBytes();



            System.arraycopy(in1Bytes, 0,inputBytes,0,in1Bytes.length);
            String inputstr = new String(inputBytes);
            System.out.println("Contents1: "+ inputstr);

            System.arraycopy(in2Bytes, 0, inputBytes, in1Bytes.length,in2Bytes.length);
            inputstr = new String(inputBytes);
            System.out.println("Contents2: "+ inputstr);

            System.arraycopy(in3Bytes,0, inputBytes, in1Bytes.length + in2Bytes.length,in3Bytes.length);
            inputstr = new String(inputBytes);
            System.out.println("Contents3: "+ inputstr);




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

            setIntent(intent);

            if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()))
            {
                myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            }

        }




  }
