package com.example.finalproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

public class MainActivity extends Activity {
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        gameView = new GameView(this);
        setContentView(gameView);

        // SHOW LOGIN DIALOG
        final EditText input = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("Defender Login")
                .setMessage("Enter your name to begin:")
                .setView(input)
                .setCancelable(false)
                .setPositiveButton("Enter Kingdom", (dialog, which) -> {
                    String name = input.getText().toString();
                    if (!name.isEmpty()) {
                        gameView.setPlayerName(name);
                    }
                })
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gameView != null) gameView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (gameView != null) gameView.pause();
    }
}