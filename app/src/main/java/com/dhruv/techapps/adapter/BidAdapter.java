package com.dhruv.techapps.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.dhruv.techapps.R;
import com.dhruv.techapps.common.Common;
import com.dhruv.techapps.models.Bid;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class BidAdapter extends RecyclerView.Adapter<BidAdapter.BidViewHolder> {

    private static final String TAG = "BidAdapter";
    public List<Bid> mBids = new ArrayList<>();
    private final Context mContext;
    private final DatabaseReference mDatabaseReference;
    private final ChildEventListener mChildEventListener;

    public BidAdapter(final Context context, DatabaseReference ref) {
        mContext = context;
        mDatabaseReference = ref;

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Bid bid = dataSnapshot.getValue(Bid.class);
                mBids.add(bid);
                notifyItemInserted(mBids.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                Toast.makeText(mContext, "Failed to load comments.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        ref.addChildEventListener(childEventListener);
        // [END child_event_listener_recycler]

        // Store reference to listener so it can be removed on app stop
        mChildEventListener = childEventListener;
    }

    @NonNull
    @Override
    public BidViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_bid, parent, false);
        return new BidViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BidViewHolder holder, int position) {
        Bid bid = mBids.get(position);
        String author = bid.author;
        char first = author.charAt(0); // just like array, string index is zero based
        char last = author.charAt(author.length() - 1); // last char is at index length - 1
        author = first + String.valueOf(last);
        holder.authorView.setText(author);
        holder.amountView.setText(Common.formatCurrency(bid.amount));
    }

    @Override
    public int getItemCount() {
        return mBids.size();
    }

    public void cleanupListener() {
        if (mChildEventListener != null) {
            mDatabaseReference.removeEventListener(mChildEventListener);
        }
    }

    static class BidViewHolder extends RecyclerView.ViewHolder {

        public TextView authorView;
        public TextView amountView;

        public BidViewHolder(View itemView) {
            super(itemView);

            authorView = itemView.findViewById(R.id.bidAuthor);
            amountView = itemView.findViewById(R.id.bidAmount);
        }
    }

}