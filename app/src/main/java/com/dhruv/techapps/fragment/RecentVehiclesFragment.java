package com.dhruv.techapps.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class RecentVehiclesFragment extends VehicleListFragment {

    public RecentVehiclesFragment() {
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // [START recent_posts_query]
        // Last 100 posts, these are automatically the 100 most recent
        // due to sorting by push() keys
        return databaseReference.child(type).limitToFirst(100);
        // [END recent_posts_query]
    }
}
