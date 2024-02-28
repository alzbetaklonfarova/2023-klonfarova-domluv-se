package com.example.rocnikovaprace;
import com.google.firebase.database.*;

public class ItemCounter {



        private DatabaseReference countRef;

        public ItemCounter(String countReference) {
            this.countRef = FirebaseDatabase.getInstance().getReference(countReference);
        }

        public DatabaseReference getCountReference() {
            return countRef;
        }
    }


