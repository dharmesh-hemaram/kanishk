package com.dhruv.techapps.models;

import com.google.firebase.database.IgnoreExtraProperties;

// [START comment_class]
@IgnoreExtraProperties
public class Bid {

    public String uid;
    public String author;
    public Double amount;

    public Bid() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }

    public Bid(String uid, String author, Double amount) {
        this.uid = uid;
        this.author = author;
        this.amount = amount;
    }

}
// [END comment_class]
