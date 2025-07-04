package edu.pdx.cs.joy.whitlock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import edu.pdx.cs.joy.AbstractAppointment;
import edu.pdx.cs.joy.AbstractAppointmentBook;

public class AppointmentBook extends AbstractAppointmentBook {
    private final String owner;
    private ArrayList<Appointment> appointmentList;
    AppointmentBook(String owner) {
        this.owner = owner;
        this.appointmentList = new ArrayList<>();
    }
    @Override
    public String getOwnerName() {
        return this.owner;
    }

    @Override
    public Collection getAppointments() {
        return this.appointmentList;
    }
    @Override
    public void addAppointment(AbstractAppointment appt) {
        this.appointmentList.add((Appointment) appt);
    }
}
