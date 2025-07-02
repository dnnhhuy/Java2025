package edu.pdx.cs.joy.whitlock;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int GET_SUM = 42;
    private ArrayAdapter<Integer> sums;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        sums = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());

        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(this.sums);
    }

    public void launchCalculator(View view) {
        Intent intent = new Intent(this, CalculatorActivity.class);
        startActivityForResult(intent, GET_SUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_SUM) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    int sum = data.getIntExtra(CalculatorActivity.SUM_VALUE, 0);
                    Toast.makeText(this, "Sum was: " + sum, Toast.LENGTH_SHORT).show();
                    this.sums.add(sum);
                }
            }
        }
    }
}