package edu.pdx.cs.joy.whitlock;

import edu.pdx.cs.joy.AbstractAppointment;

public class Appointment extends AbstractAppointment {
    private final int flightNumber;
    public Appointment(int flightNumber) {
        this.flightNumber = flightNumber;
    }

    @Override
    public String getBeginTimeString() {
        return "BEGINTIME";
    }

    @Override
    public String getEndTimeString() {
        return "ENDTIME";
    }

    @Override
    public String getDescription() {
        return "DESCRIPTION";
    }
}
