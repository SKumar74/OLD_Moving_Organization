package com.example.moving_organization;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.moving_organization.objects.MoverTag;
import com.example.moving_organization.objects.UserAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignIn extends AppCompatActivity {


    private FirebaseAuth login;
    private DatabaseReference dbref;

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



    public void createacc(String email, String password)
    {
        login.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignIn.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign-in is complete, send to main menu

                        // main menu intent works
                        // tomainmenu();
                        loginerror.setText("Account created successfully!");

                    }
                })
                .addOnFailureListener(SignIn.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                            //loginerror.setText("Failed to login, please retry!");
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