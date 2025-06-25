package edu.pdx.cs.joy.whitlock;

import edu.pdx.cs.joy.AppointmentBookParser;
import edu.pdx.cs.joy.ParserException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class XmlParser implements AppointmentBookParser<AppointmentBook> {
    private final InputStream is;
    XmlParser(InputStream is) {
        this.is = is;
    }
    @Override
    public AppointmentBook parse() throws ParserException {
        DocumentBuilderFactory factory = null;
        DocumentBuilder builder = null;
        Document doc = null;
        try {
            AppointmentBookXmlHelper helper = new AppointmentBookXmlHelper();

            factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(true);

            builder = factory.newDocumentBuilder();
            builder.setErrorHandler(helper);
            builder.setEntityResolver(helper);
        } catch (ParserConfigurationException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        AppointmentBook appointmentBook = null;
        try {
            doc = builder.parse(this.is);
            Element root = doc.getDocumentElement();
            NodeList nodeList = root.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (!(node instanceof Element element)) continue;
                switch (element.getNodeName()) {
                    case "owner":
                        String owner = element.getTextContent();
                        if (owner.isBlank()) {
                            throw new ParserException("Missing Owner");
                        }
                        appointmentBook = new AppointmentBook(owner);
                        break;
                    case "appt":
                        if (appointmentBook == null) {
                            throw new ParserException("Appointment Book hasn't been initialized!");
                        }
                        appointmentBook.addAppointment(parseAppointment(element));
                }

            }

        } catch (IOException | SAXException e) {
            throw new ParserException("Error while parsing: " + e.getMessage());
        }
        return appointmentBook;
    }

    private Appointment parseAppointment(Element root) {
        NodeList apptList = root.getChildNodes();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy H:m");
        Appointment appt = null;
        LocalDateTime beginTime = null;
        LocalDateTime endTime = null;
        String description = "";
        for (int i = 0; i < apptList.getLength(); i++) {
            Node apptNode = apptList.item(i);
            if (!(apptNode instanceof Element apptElement)) continue;

            switch (apptElement.getNodeName()) {
                case "begin":
                    beginTime = LocalDateTime.parse(parseTime(apptElement), formatter);
                    break;
                case "end":
                    endTime = LocalDateTime.parse(parseTime(apptElement), formatter);
                    break;
                case "description":
                    description = apptNode.getTextContent();
                    break;
            }
        }

        try {
            appt = new Appointment(description, beginTime, endTime);
        } catch (Appointment.InvalidAppointmentTimeException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        return appt;
    }

    private String parseTime(Element root) {
        NodeList nodeList = root.getChildNodes();
        String date = "";
        String time = "";
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (!(node instanceof Element element)) continue;
            switch (element.getNodeName()) {
                case "date":
                    date = element.getAttribute("day") + "/" + element.getAttribute("month") + "/" + element.getAttribute("year");
                    break;
                case "time":
                    time = element.getAttribute("hour") + ":" + element.getAttribute("minute");
                    break;
            }
        }

        return date + " " + time;
    }
}
