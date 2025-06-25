package edu.pdx.cs.joy.whitlock;
import edu.pdx.cs.joy.AppointmentBookDumper;

import java.io.PrintWriter;
import java.io.Writer;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class XmlDumper implements AppointmentBookDumper<AppointmentBook> {
    private final Writer writer;
    protected static final String PUBLIC_ID =
            "-//Joy of Coding at PSU//DTD Appointment Book//EN";

    XmlDumper(Writer writer) {
        this.writer = writer;
    }

    @Override
    public void dump(AppointmentBook appointmentBook) {

        // Create an empty Document
        Document doc =  null;
        try {
            DocumentBuilderFactory documentBuilderFactory =  DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setValidating(true);

            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            DOMImplementation dom = documentBuilder.getDOMImplementation();
            DocumentType docType = dom.createDocumentType("apptbook", PUBLIC_ID, "apptbook.dtd");
            doc = dom.createDocument(null, "apptbook", docType);

        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        // Construct DOM tree
        try {
            Element root = doc.getDocumentElement();

            Element owner = doc.createElement("owner");
            owner.setTextContent(appointmentBook.getOwnerName());
            root.appendChild(owner);

            for (Appointment appt : appointmentBook.getAppointments()) {
                LocalDateTime beginTime = appt.getBeginTime();
                LocalDateTime endTime = appt.getEndTime();

                Element appointment = doc.createElement("appt");
                root.appendChild(appointment);

                Element begin = doc.createElement("begin");
                appointment.appendChild(begin);


                Element date = doc.createElement("date");
                begin.appendChild(date);
                date.setAttribute("day", String.valueOf(beginTime.getDayOfMonth()));
                date.setAttribute("month", String.valueOf(beginTime.getMonthValue()));
                date.setAttribute("year", String.valueOf(beginTime.getYear()));

                Element time = doc.createElement("time");
                begin.appendChild(time);
                time.setAttribute("hour", String.valueOf(beginTime.getHour()));
                time.setAttribute("minute", String.valueOf(beginTime.getMinute()));

                Element end = doc.createElement("end");
                appointment.appendChild(end);

                date = doc.createElement("date");
                end.appendChild(date);
                date.setAttribute("day", String.valueOf(endTime.getDayOfMonth()));
                date.setAttribute("month", String.valueOf(endTime.getMonthValue()));
                date.setAttribute("year", String.valueOf(endTime.getYear()));

                time = doc.createElement("time");
                end.appendChild(time);
                time.setAttribute("hour", String.valueOf(endTime.getHour()));
                time.setAttribute("minute", String.valueOf(endTime.getMinute()));


                Element description = doc.createElement("description");
                appointment.appendChild(description);
                description.setTextContent(appt.getDescription());

            }
        } catch (DOMException ex ) {
            System.err.println("** DOMException: " + ex);
            System.exit(1);
        }

        // Output to file
        try (PrintWriter pw = new PrintWriter(this.writer)) {
            Source src = new DOMSource(doc);
            Result res = new StreamResult(pw);

            TransformerFactory xFactory = TransformerFactory.newInstance();
            Transformer xform = xFactory.newTransformer();
            xform.setOutputProperty(OutputKeys.INDENT, "yes");
            xform.setOutputProperty(OutputKeys.ENCODING, "us-ascii");
            xform.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, PUBLIC_ID);
            xform.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "apptbook.dtd");


            xform.transform(src, res);

        } catch (TransformerException e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }
}
