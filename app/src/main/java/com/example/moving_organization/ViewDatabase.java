package com.example.moving_organization;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class ViewDatabase extends AppCompatActivity {

        Button backtomain;
        Button viewdb;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_view_database);



            backtomain = (Button) findViewById(R.id.dbtomain);
            viewdb = (Button) findViewById(R.id.checkdatabase);


            backtomain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ViewDatabase.this, MainActivity.class));
                }
            });


            }




    }

