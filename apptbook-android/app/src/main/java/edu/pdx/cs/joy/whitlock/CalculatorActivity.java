package edu.pdx.cs.joy.whitlock;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CalculatorActivity extends AppCompatActivity {

    static final String SUM_VALUE = "SUM";
    private int sum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calculator);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


    }

    public void backToMain(View view) {
        Intent intent = new Intent();
        intent.putExtra(SUM_VALUE, this.sum);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void computeSum(View view) {
        EditText leftOperand = findViewById(R.id.leftOperand);
        EditText rightOperand = findViewById(R.id.rightOperand);

        int leftString = 0;
        try {
            leftString = Integer.parseInt(leftOperand.getText().toString());
        } catch (NumberFormatException ex) {
            Toast.makeText(this, "Invalid left operand " + leftString, Toast.LENGTH_SHORT).show();
        }

        int rightString = 0;
        try {
            rightString = Integer.parseInt(rightOperand.getText().toString());
        } catch (NumberFormatException ex) {
            Toast.makeText(this, "Invalid right operand " + rightString, Toast.LENGTH_SHORT).show();
        }

        int sumValue = leftString + rightString;
        this.sum = sumValue;
        TextView viewById = findViewById(R.id.result);
        viewById.setText(String.valueOf(sumValue));

        Appointment appointment = new Appointment(sumValue);
        Toast.makeText(this, appointment.toString(), Toast.LENGTH_LONG).show();
    }
}