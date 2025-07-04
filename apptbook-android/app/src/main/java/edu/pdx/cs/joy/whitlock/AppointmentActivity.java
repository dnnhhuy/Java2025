package edu.pdx.cs.joy.whitlock;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class AppointmentActivity extends AppCompatActivity {

    private ArrayList<Appointment> appointments;
    private String owner;
    private String beginString;
    private String endString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_appointment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        this.owner = extras.getString("owner");
        this.beginString = extras.getString("begin");
        this.endString = extras.getString("end");
        this.appointments = extras.getSerializable("appointmentList", ArrayList.class);

        DateTimeFormatter formatter =  DateTimeFormatter.ofPattern("M/d/yyyy h:mm a");
        LocalDateTime begin;
        try {
            begin = LocalDateTime.parse(this.beginString, formatter);
        } catch(DateTimeParseException ex) {
            begin = LocalDateTime.MIN;
        }

        LocalDateTime end;
        try {
            end = LocalDateTime.parse(this.endString, formatter);
        } catch (DateTimeParseException ex) {
            end = LocalDateTime.MAX;
        }

        LocalDateTime finalBegin = begin;
        LocalDateTime finalEnd = end;
        List<Appointment> list = new ArrayList<>();
        for (Appointment appointment : this.appointments) {
            if ((appointment.getBeginTime()
                    .equals(finalBegin) || appointment.getBeginTime().isAfter(finalBegin)) && (appointment.getEndTime().equals(finalEnd) || appointment.getEndTime().isBefore(finalEnd))) {
                list.add(appointment);
            }
        }
        ArrayList<Appointment> filteredAppt = (ArrayList<Appointment>) list;


        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView textView = findViewById(R.id.textView);
        textView.setText(String.format("%s has %d appointments", owner, filteredAppt.size()));
        textView.setTextSize(20);

        ArrayAdapter<Appointment> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredAppt);
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
        Intent newIntent = new Intent(this, DetailedAppointmentActivity.class);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Appointment getAppt = filteredAppt.get(position);
                newIntent.putExtra("description", getAppt.getDescription());
                newIntent.putExtra("begin", getAppt.getBeginTimeString());
                newIntent.putExtra("end", getAppt.getEndTimeString());
                startActivity(newIntent);
            }
        });
    }

    public void backToMain(View view) {
        finish();
    }

}