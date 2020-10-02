package com.dhruv.techapps.models;

import com.google.firebase.database.IgnoreExtraProperties;

// [START blog_user_class]
@IgnoreExtraProperties
public class User {

    public String username;
    public String phoneNum;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String phoneNum) {
        this.username = username;
        this.phoneNum = phoneNum;
    }

}
// [END blog_user_class]
