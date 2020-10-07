package com.dhruv.techapps.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class MyBidsFragment extends VehicleListFragment {

    public MyBidsFragment() {
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {


        // [START my_top_posts_query]
        // My top posts by number of stars
        String myUserId = getUid();
        return databaseReference.child("cars").child(myUserId)
                .orderByChild("starCount");
        // [END my_top_posts_query]
    }
}
