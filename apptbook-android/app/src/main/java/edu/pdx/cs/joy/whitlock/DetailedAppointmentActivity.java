package edu.pdx.cs.joy.whitlock;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.w3c.dom.Text;

public class DetailedAppointmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detailed_appointment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView descriptionField = findViewById(R.id.textView11);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView description = findViewById(R.id.textView12);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView beginField = findViewById(R.id.textView13);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView begin = findViewById(R.id.textView14);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView endField = findViewById(R.id.textView15);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView end = findViewById(R.id.textView16);

        descriptionField.setText("Description: ");
        description.setText(extras.getString("description"));
        beginField.setText("Begin Time: ");
        begin.setText(extras.getString("begin"));
        endField.setText("End Time: ");
        end.setText(extras.getString("end"));
    }

    public void back(View view) {
        finish();
    }
}