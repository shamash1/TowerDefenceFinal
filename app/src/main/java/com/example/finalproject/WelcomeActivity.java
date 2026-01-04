package com.example.finalproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnLogout, btnSave;
    private EditText etType,etPrice;
    private TextView tvMyName;

    private FirebaseAuth mAuth;
    FBsingleton fb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initialization();
    }

    private void initialization() {
        // initialize

        fb = FBsingleton.getInstance();
        mAuth = FirebaseAuth.getInstance();

        etType = findViewById(R.id.etType);
        etPrice = findViewById(R.id.etPrice);


        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);
        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

        tvMyName = findViewById(R.id.tvMyName);

/*        if(mAuth != null)
        {
            tvMyName.setText(mAuth.getCurrentUser().getEmail());
        }*/

    }

    @Override
    public void onClick(View v) {
        if (v == btnSave) {
            String type = etType.getText().toString();
            int price = Integer.parseInt(etPrice.getText().toString());

            fb.setDetails(type, price);
        }
        if(v == btnLogout)
        {
            FirebaseAuth.getInstance().signOut();
            finish(); // close the activity
        }

    }

    public void userDataChange(String name) {
        System.out.println(name);
        tvMyName.setText("my Name: " + name);

    }
}