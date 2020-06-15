package com.chengte99.bingo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_LOGIN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
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
                FirebaseAuth.getInstance().signOut();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth auth) {
        if (auth.getCurrentUser() != null) {
            Log.d(TAG, "onAuthStateChanged: " + auth.getCurrentUser().getEmail() + "/ " + auth.getCurrentUser().getUid());
        }else {
            startActivityForResult(
                    AuthUI.getInstance().createSignInIntentBuilder()
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.EmailBuilder().build(),
                                    new AuthUI.IdpConfig.GoogleBuilder().build()
                            ))
                            .setIsSmartLockEnabled(false)
                            .build()
                    , RC_LOGIN);
        }
    }
}
