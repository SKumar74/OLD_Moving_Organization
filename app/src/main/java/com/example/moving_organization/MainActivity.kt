package com.example.moving_organization

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create transition to read, write, and view database

        // FIX INTENTS

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


    }

}
