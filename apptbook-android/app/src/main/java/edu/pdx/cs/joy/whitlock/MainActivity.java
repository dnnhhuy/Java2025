package edu.pdx.cs.joy.whitlock;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final int GET_SUM = 42;
    private HashMap<String, AppointmentBook> apptStorage;

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
        this.apptStorage = new HashMap<>();
        readAppointmentFromFile();
    }



    public void createAppointment(View view) {
        EditText editOwner = findViewById(R.id.editOwner);
        EditText editDescription = findViewById(R.id.editDescription);
        EditText editBegin = findViewById(R.id.editBegin);
        EditText editEnd = findViewById(R.id.editEnd);

        String owner = editOwner.getText().toString();
        String description = editDescription.getText().toString();
        String begin = editBegin.getText().toString();
        String end = editEnd.getText().toString();
        if (owner.isBlank()) {
            Toast.makeText(this, "Owner's name cannot be empty. Please input owner's name", Toast.LENGTH_SHORT).show();
        } else {
            AppointmentBook apptBook = this.apptStorage.get(owner);
            if (apptBook == null) {
                apptBook = new AppointmentBook(owner);
                this.apptStorage.put(owner, apptBook);
            }

            try {
                apptBook.addAppointment(new Appointment(description, begin, end));
                Toast.makeText(this, "Add appointment successfully", Toast.LENGTH_SHORT).show();
                editOwner.getText().clear();
                editDescription.getText().clear();
                editBegin.getText().clear();
                editEnd.getText().clear();
            } catch (Appointment.IllegalTimeInputException | Appointment.IllegalDateTimeFormat e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        writeAppointmentToFile();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void searchAppointmentBook(View view) {
        EditText editOwner = findViewById(R.id.editOwner);
        EditText editBegin = findViewById(R.id.editBegin);
        EditText editEnd = findViewById(R.id.editEnd);

        String owner = editOwner.getText().toString();
        String begin = editBegin.getText().toString();
        String end = editEnd.getText().toString();
        if (owner.isBlank()) {
            Toast.makeText(this, "Owner's name cannot be empty. Please input owner's name", Toast.LENGTH_SHORT).show();
        } else {
            AppointmentBook appointmentBook = this.apptStorage.get(owner);
            if (appointmentBook == null) {
                Toast.makeText(this, String.format("Cannot find %s's appointment's book", owner), Toast.LENGTH_SHORT).show();
            } else {
                ArrayList<Appointment> apptList = (ArrayList<Appointment>) appointmentBook.getAppointments();
                Intent intent = new Intent(this, AppointmentActivity.class);
                intent.putExtra("owner", owner);
                intent.putExtra("begin", begin);
                intent.putExtra("end", end);
                intent.putExtra("appointmentList", apptList);
                startActivity(intent);
            }
        }
    }

        private void readAppointmentFromFile() {
        File dataDir = this.getDataDir();
        File dataFile = new File(dataDir, "appointmentBooks.txt");
        try (BufferedReader br = new BufferedReader(new FileReader(dataFile))) {
            while (br.ready()) {
                String owner = br.readLine();
                AppointmentBook apptBook = new AppointmentBook(owner);
                String line = br.readLine();
                while (!line.equals("********") && line != null) {
                    String description = line;
                    String start = br.readLine();
                    String end = br.readLine();
                    line = br.readLine();
                    apptBook.addAppointment(new Appointment(description, start, end));
                }
                this.apptStorage.put(owner, apptBook);
            }
        } catch (IOException | Appointment.IllegalDateTimeFormat | Appointment.IllegalTimeInputException e) {
            Toast.makeText(this, "While reading sums file: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        }

    private void writeAppointmentToFile() {
        File dataDir = this.getDataDir();
        File dataFile = new File(dataDir, "appointmentBooks.txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(dataFile))) {
            for (String owner: this.apptStorage.keySet()) {
                AppointmentBook apptBook = this.apptStorage.get(owner);
                pw.println(owner);
                for (Appointment appt : (ArrayList<Appointment>) apptBook.getAppointments()) {
                    pw.println(appt.getDescription());
                    pw.println(appt.getBeginTimeString());
                    pw.println(appt.getEndTimeString());
                }
                pw.println("********");
            }
        } catch (IOException e) {
            Toast.makeText(this, "While writing sums file: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
}