package com.example.moving_organization.objects;

import android.location.Location;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

    public class MoverTag {
    // Testing Tag Object

        private String content;
        private String location;
        private String instructions;
        //private String entryupdate; // Maybe get current date from some library
        //private String Mover;

    public MoverTag(){

    }


        public MoverTag(String Instructions, String contentsStrings,  String where)
        {
            this.location = where;
            this.content = contentsStrings;
            this.instructions = Instructions;
            //this.entryupdate = updates;
            //this.Mover = Mover;
        }

        // Getters and Setters


        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getInstructions() {
            return instructions;
        }

        public void setInstructions(String instructions) {
            this.instructions = instructions;
        }
/*
        public String getEntryupdate() {
            return entryupdate;
        }

        public void setEntryupdate(String entryupdate) {
            this.entryupdate = entryupdate;
        }

        public String getMover() {
            return Mover;
        }

        public void setMover(String mover) {
            Mover = mover;
        }
*/
        public static void main(String[] rs)
    {




    }


}

