package com.example.moving_organization;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.health.UidHealthStats;
import android.util.Log;
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



            loginuser.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {

                                                  loginauth(uemail.getText().toString().replace(".","-"),upassword.getText().toString());

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




        private void loginauth(final String username, final String password)
        {

            //login = FirebaseAuth.getInstance();

            dbref = FirebaseDatabase.getInstance().getReference();



            final UserAccount ua = new UserAccount(username,password);


            dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    System.out.println("EMAIL RETREIVED:\n");
                    System.out.println(ua.getUsername());
                    System.out.println("PASSWORD RETREIVED\n");
                    System.out.println(ua.getPassword());


                    if (ua.getUsername().equals(username) && ua.getPassword().equals(password))
                    {

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


}
