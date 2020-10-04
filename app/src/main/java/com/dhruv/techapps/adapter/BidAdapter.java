package com.dhruv.techapps.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.dhruv.techapps.R;
import com.dhruv.techapps.models.Bid;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

public class BidAdapter extends RecyclerView.Adapter<BidAdapter.BidViewHolder> {

    private static final String TAG = "BidAdapter";
    public List<Bid> mBids = new ArrayList<>();
    private Context mContext;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    private List<String> mBidIds = new ArrayList<>();
    private DecimalFormat currencyFormat;

    public BidAdapter(final Context context, DatabaseReference ref) {
        mContext = context;
        mDatabaseReference = ref;

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');

        currencyFormat = new DecimalFormat("₹ #,###", symbols);

        // Create child event listener
        // [START child_event_listener_recycler]
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                // A new comment has been added, add it to the displayed list
                Bid bid = dataSnapshot.getValue(Bid.class);

                // [START_EXCLUDE]
                // Update RecyclerView
                mBidIds.add(dataSnapshot.getKey());
                mBids.add(bid);
                notifyItemInserted(mBids.size() - 1);
                // [END_EXCLUDE]
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                Bid newBid = dataSnapshot.getValue(Bid.class);
                String commentKey = dataSnapshot.getKey();

                // [START_EXCLUDE]
                int commentIndex = mBidIds.indexOf(commentKey);
                if (commentIndex > -1) {
                    // Replace with the new data
                    mBids.set(commentIndex, newBid);

                    // Update the RecyclerView
                    notifyItemChanged(commentIndex);
                } else {
                    Log.w(TAG, "onChildChanged:unknown_child:" + commentKey);
                }
                // [END_EXCLUDE]
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                String commentKey = dataSnapshot.getKey();

                // [START_EXCLUDE]
                int commentIndex = mBidIds.indexOf(commentKey);
                if (commentIndex > -1) {
                    // Remove data from the list
                    mBidIds.remove(commentIndex);
                    mBids.remove(commentIndex);

                    // Update the RecyclerView
                    notifyItemRemoved(commentIndex);
                } else {
                    Log.w(TAG, "onChildRemoved:unknown_child:" + commentKey);
                }
                // [END_EXCLUDE]
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                Bid movedComment = dataSnapshot.getValue(Bid.class);
                String commentKey = dataSnapshot.getKey();

                // ...
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

    @Override
    public BidViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
        holder.amountView.setText(currencyFormat.format(bid.amount));
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