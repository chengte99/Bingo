package com.chengte99.bingo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BingoActivity extends AppCompatActivity {
    public static final int STATUS_INIT = 0;
    public static final int STATUS_CREATED = 1;
    public static final int STATUS_JOINED = 2;
    public static final int STATUS_CREATOR_TURN = 3;
    public static final int STATUS_JOINER_TURN = 4;
    public static final int STATUS_CREATOR_BINGO = 5;
    public static final int STATUS_JOINER_BINGO = 6;

    private static final String TAG = BingoActivity.class.getSimpleName();
    private String roomid;
    private boolean is_creator;
    private RecyclerView recyclerView;
    private List<NumberButton> buttons;
    private FirebaseRecyclerAdapter<Boolean, BingoViewHolder> adapter;
    private Map<Integer, Integer> numberMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bingo);

        roomid = getIntent().getStringExtra("ROOMID");
        is_creator = getIntent().getBooleanExtra("IS_CREATOR", false);
        Log.d(TAG, "onCreate: " + roomid);
        if (is_creator) {
            for (int i = 0; i < 25; i++) {
                FirebaseDatabase.getInstance().getReference("rooms")
                        .child(roomid)
                        .child("numbers")
                        .child(String.valueOf(i + 1))
                        .setValue(false);
            }

            FirebaseDatabase.getInstance().getReference("rooms")
                    .child(roomid)
                    .child("status")
                    .setValue(STATUS_CREATED);
        } else {
            FirebaseDatabase.getInstance().getReference("rooms")
                    .child(roomid)
                    .child("status")
                    .setValue(STATUS_JOINED);
        }

        findViews();

        numberMap = new HashMap<>();
        buttons = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            NumberButton button = new NumberButton(this);
            button.setNumber(i + 1);
            // pos, is_picked

            buttons.add(button);
        }
        Collections.shuffle(buttons);
        for (int i = 0; i < 25; i++) {
            numberMap.put(buttons.get(i).getNumber(), i);
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 5));

        Query query = FirebaseDatabase.getInstance().getReference("rooms")
                .child(roomid)
                .child("numbers")
                .orderByKey();
        FirebaseRecyclerOptions<Boolean> options = new FirebaseRecyclerOptions.Builder<Boolean>()
                .setQuery(query, Boolean.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Boolean, BingoViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull BingoViewHolder holder, int position, @NonNull Boolean model) {
                holder.numberButton.setText(String.valueOf(buttons.get(position).getNumber()));
//                holder.numberButton.setEnabled(!model);
            }

            @Override
            public void onChildChanged(@NonNull ChangeEventType type, @NonNull DataSnapshot snapshot, int newIndex, int oldIndex) {
                super.onChildChanged(type, snapshot, newIndex, oldIndex);
                Log.d(TAG, "onChildChanged: " + type + "/" + snapshot.getKey() + "/" + snapshot.getValue());
                if (type == ChangeEventType.CHANGED) {
                    int number = Integer.parseInt(snapshot.getKey());
                    boolean is_picked = (boolean) snapshot.getValue();
                    int pos = numberMap.get(number);
                    BingoViewHolder holder = (BingoViewHolder) recyclerView.findViewHolderForAdapterPosition(pos);
                    holder.numberButton.setEnabled(!is_picked);
                }
            }

            @NonNull
            @Override
            public BingoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = getLayoutInflater().inflate(R.layout.single_button, parent, false);
                return new BingoViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private void findViews() {
        TextView info = findViewById(R.id.info);
        recyclerView = findViewById(R.id.recycler);
    }

    public class BingoViewHolder extends RecyclerView.ViewHolder {
        NumberButton numberButton;
        public BingoViewHolder(@NonNull View itemView) {
            super(itemView);
            numberButton = itemView.findViewById(R.id.number_button);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
