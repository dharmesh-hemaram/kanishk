package com.dhruv.techapps.models;

import com.google.firebase.database.IgnoreExtraProperties;

// [START comment_class]
@IgnoreExtraProperties
public class Bidding {

    public String uid;
    public String author;
    public Double amount;

    public Bidding() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }

    public Bidding(String uid, String author, Double text) {
        this.uid = uid;
        this.author = author;
        this.amount = amount;
    }

}
// [END comment_class]
