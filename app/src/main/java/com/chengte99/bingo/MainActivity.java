package com.chengte99.bingo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_LOGIN = 100;
    private FirebaseAuth auth;
    private TextView nickText;
    private ImageView avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findviews();
        auth = FirebaseAuth.getInstance();
    }

    private void findviews() {
        nickText = findViewById(R.id.nickname);
        avatar = findViewById(R.id.avatar);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();
        auth.addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
        auth.removeAuthStateListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
//        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu_signout:
                auth.signOut();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth auth) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            startActivityForResult(
                    AuthUI.getInstance().createSignInIntentBuilder()
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.EmailBuilder().build(),
                                    new AuthUI.IdpConfig.GoogleBuilder().build()
                            ))
                            .setIsSmartLockEnabled(false)
                            .build()
                    , RC_LOGIN);
        } else {
            Log.d(TAG, "onAuthStateChanged: " + user.getEmail() +
                    "/ " + user.getUid());
            final String displayName = user.getDisplayName();
            if (displayName != null) {
                FirebaseDatabase.getInstance()
                        .getReference("users")
                        .child(user.getUid())
                        .child("displayName")
                        .setValue(displayName)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d(TAG, "onComplete: ");
                            }
                        });
            }

            FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(user.getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Member member = dataSnapshot.getValue(Member.class);
                            if (member != null) {
                                if (member.nickname != null) {
                                    nickText.setText(member.nickname);
                                } else {
                                    showInputNicknameDialog(member.displayName);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

//            FirebaseDatabase.getInstance()
//                    .getReference("users")
//                    .child(user.getUid())
//                    .child("nickname")
//                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            if (dataSnapshot.getValue() != null) {
//                                String nickname = dataSnapshot.getValue().toString();
//                                //do something
//                                Log.d(TAG, "onDataChange: " + nickname);
//                            } else {
//                                showInputNicknameDialog(displayName);
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });
        }
    }

    private void showInputNicknameDialog(String displayName) {
        final EditText edNickname = new EditText(this);
        edNickname.setText(displayName);
        new AlertDialog.Builder(this)
                .setTitle("Your nickname")
                .setMessage("Please input your nickname")
                .setView(edNickname)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance()
                                .getReference("users")
                                .child(auth.getUid())
                                .child("nickname")
                                .setValue(edNickname.getText().toString());
                    }
                })
                .show();
    }
}
