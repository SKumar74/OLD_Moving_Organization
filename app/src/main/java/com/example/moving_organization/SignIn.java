package com.example.moving_organization;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.health.UidHealthStats;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moving_organization.objects.MoverTag;
import com.example.moving_organization.objects.UserAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.widget.Toast.LENGTH_LONG;


public class SignIn extends AppCompatActivity {


    private FirebaseAuth login;
    private DatabaseReference dbref;


    // To test
    FirebaseUser signincheck;

    Button loginuser;
    Button createuser;

    TextView uemail;
    TextView upassword;
    TextView loginerror;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        uemail = (TextView) findViewById(R.id.enteremail);
        upassword = (TextView) findViewById(R.id.enterpassword);
        loginuser = (Button) findViewById(R.id.loginbutton);
        createuser = (Button) findViewById(R.id.createaccountbutton);
        loginerror = (TextView) findViewById(R.id.loginerrortext);

        login = FirebaseAuth.getInstance();






        //Update: Database and create account work
        // Check database login and try to login to main menu
        // ONCE DATABASE DONE, RE_ENABLE AUTH ON FIREBASE AND TEST TO SEE IF LOGIN BUTTON TAKES USER TO MAIN MENU

        // Testing email creation, if success, then jumps to main menu
        loginuser.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              //createacc(uemail.getText().toString(),upassword.getText().toString());

                                              //NOTE 03/06/2020 3:55 AM
                                              // Test out loginauth function
                                              // If works, then change hardcode to some other method
                                              // Test out task with create user button
                                              // Should only display correct or wrong text, not both
                                              // If both functions work, work on read intent problem

                                              loginauth(uemail.getText().toString(),upassword.getText().toString());

                                          }

        }
        );


        // Testing for database (Note: when testing this, disable email auth from firebase)
        createuser.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              //createaccounttodb(uemail.getText().toString(),upassword.getText().toString());
                                              createacc(uemail.getText().toString(),upassword.getText().toString());

                                          }
        }
        );



    }



    public void createacc(final String email, final String password)
    {
        dbref = FirebaseDatabase.getInstance().getReference();

        final UserAccount useracc = new UserAccount(email,password);


        System.out.println("EMAIL RETREIVED:\n");
        System.out.println(useracc.getUsername());
        System.out.println("PASSWORD RETREIVED\n");
        System.out.println(useracc.getPassword());


        login.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignIn.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign-in is complete, send to main menu
                        if (task.isSuccessful()) {
                            // Should store email for User and password for Password field
                            // Update for 03/26: Look into storing documents into collection, previous attempts
                            // Update for 03/27: Was thinking too complex, just needed to make series of child nodes
                            // and check database to see if the structure works with user account creation
                            // so the two lines below are structured fine
                            dbref.child("Users").push().setValue(useracc.getUsername().replace(".","-"));
                            dbref.child("Users").child(useracc.getUsername().replace(".","-")).child("Password").push().setValue(useracc.getPassword());



                            loginerror.setText("Account created successfully!");

                        }

                        else{
                            loginerror.setText("Failed to fill-out fields.");
                        }
                    }
                });




    }




    /*
    private void createaccounttodb(String username, String password)
    {



        dbref = FirebaseDatabase.getInstance().getReference();


        UserAccount ua = new UserAccount(username,password);

        dbref.child("UserAccount2").setValue(ua);

        //loginerror.setText("Account created successfully!");

    }

     */



    private void loginauth(final String username, final String password)
    {

        //UPDATE: WORKS
        // Need to add name field to screen and change ref and into add to db function
        dbref = FirebaseDatabase.getInstance().getReference("Users");






        final UserAccount ua = new UserAccount(username,password);

        // Might replace with login.signinwithemailandPassword

        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            //UserAccount ua = dataSnapshot.getValue(UserAccount.class);

                String curruser_email = "";



                /*if (signincheck != null)
                {
                    accUID = signincheck.getUid();
                }
                */


                System.out.println("EMAIL RETREIVED:\n");
                System.out.println(ua.getUsername());
                System.out.println("PASSWORD RETREIVED\n");
                System.out.println(ua.getPassword());


                if (ua.getUsername().equals(username) && ua.getPassword().equals(password))
                {
                    signincheck = FirebaseAuth.getInstance().getCurrentUser();
                    curruser_email = signincheck.getEmail();
                    System.out.println("ACCOUNT JUST LOGGED IN:\n");
                    System.out.println(curruser_email);
                    tomainmenu();
                }
                else
                {

                    loginerror.setText("Invalid Email or Password entered.");
                    System.out.println("FAILED TO RETRIEVE DATA");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {



            }
        });



    }


    private void tomainmenu()
    {
        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    /*

    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseUser currUser = login.getCurrentUser();

        //updateUI(currUser);
    }


    private void createlogin(String email, String password)
    {
        /*
        if (!validatelogin())
        {
            loginerror.setText("Sign In Failed, try again");
            return;
        }

         */
    }


    /*
    login.createUserWithEmailandPassword(email, password)
            .addonCompleteListener(this, new OnCompleteListener<AuthResult>())



}
*/