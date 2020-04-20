package com.example.moving_organization

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {




        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

             var curruser = FirebaseAuth.getInstance()

            // Create transition to read, write, and view database


            Readbutton.setOnClickListener{
                val readintent = Intent(this@MainActivity, Read::class.java)
                startActivity(readintent)
            }

            Writebutton.setOnClickListener{
                val writeintent = Intent(this@MainActivity, Write::class.java)
                startActivity(writeintent)
            }

            VDB.setOnClickListener{
                val dbintent = Intent(this@MainActivity, ViewDatabase::class.java)
                startActivity(dbintent)
            }

            Signout.setOnClickListener{
                        curruser.signOut() // Converts FirebaseAuth to non-null type
                val loginintent = Intent(this@MainActivity, SignIn::class.java)
                startActivity(loginintent)
            }


        }

}
