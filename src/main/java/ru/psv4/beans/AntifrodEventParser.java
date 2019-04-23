package ru.psv4.beans;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import ru.psv4.dto.DataSubtoalTerminalPayment;
import ru.psv4.dto.DataTerminalPaymentPrinting;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Service
public class AntifrodEventParser {

    private static final Logger log = LogManager.getLogger(AntifrodEventParser.class);

    private static final String EVENT_ID__SUBTOTAL = "2018";
    private static final String EVENT_ID__PAYMENT = "2024";
    private static final String EVENT_ID__RECEIPT_PRINTING = "2025";
    private static final String EVENT_ID__OPERATION_ON_ADDITIONAL_DEVICE = "6033";

    private static final DateTimeFormatter checkdtf = new DateTimeFormatterBuilder()
            .appendPattern("ddMMyyyyHHmmss")
            .appendValue(ChronoField.MILLI_OF_SECOND, 3)
            .toFormatter();

    public Data parseFiles(String folder) {
        Data data = new Data();

        Events events = new Events();

        for (File file : new File(folder).listFiles()) {
            try {
                FileInputStream fin = new FileInputStream(file);
                BufferedReader br = new BufferedReader(new InputStreamReader(fin, "UTF-8"));
                String line = null;
                while (null != (line = br.readLine())) {
                    events.add(new Event(line));
                }
                br.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        events.sort();

        for (Event event : events) {
            if (event.isSubtotal()) {
                Event subtotal = event;
                Event terminalPayment = events.findTerminalPayment(subtotal);
                if (terminalPayment != null) {
                    DataSubtoalTerminalPayment el = new DataSubtoalTerminalPayment();
                    el.station = subtotal.getStation();
                    el.checkNum = subtotal.getCheckNum();
                    el.datetime1 = subtotal.getDatetime();
                    el.datetime2 = terminalPayment.getDatetime();
                    el.clientCard = events.findClientCard(subtotal, terminalPayment);
                    data.add1(el);
                }
            }
        }

        for (Event event : events) {
            if (event.isTerminalPayment()) {
                Event terminalPayment = event;
                Event paymentPrinting = events.findTerminalPayment(terminalPayment);
                if (paymentPrinting != null) {
                    DataTerminalPaymentPrinting el = new DataTerminalPaymentPrinting();
                    el.station = terminalPayment.getStation();
                    el.checkNum = terminalPayment.getCheckNum();
                    el.datetime1 = terminalPayment.getDatetime();
                    el.datetime2 = paymentPrinting.getDatetime();
                    data.add2(el);
                }
            }
        }

        return data;
    }

    private class Events implements Iterable<Event> {

        List<Event> list = new ArrayList<>();

        public void add(Event event) {
            list.add(event);
        }

        public void sort() {
            Collections.sort(list);
        }

        @Override
        public Iterator<Event> iterator() {
            return list.iterator();
        }

        public Event findTerminalPayment(Event subtotal) {
            int index = list.indexOf(subtotal);
            for (int i = index+1; i < list.size(); ++i) {
                Event event = list.get(i);
                if (event.isTerminalPayment()
                        && event.getStation().equals(subtotal.getStation())
                        && event.getCheckNum().equals(subtotal.getCheckNum())) {
                    return event;
                }
                if (Duration.between(subtotal.getDatetime(), event.getDatetime()).toMinutes() > 30) {
                    return null;
                }
            }
            return null;
        }

        public Event findReceiptPrinting(Event terminalPayment) {
            int index = list.indexOf(terminalPayment);
            for (int i = index+1; i < list.size(); ++i) {
                Event event = list.get(i);
                if (event.isReceiptPrinting()
                        && event.getStation().equals(terminalPayment.getStation())
                        && event.getCheckNum().equals(terminalPayment.getCheckNum())) {
                    return event;
                }
                if (Duration.between(terminalPayment.getDatetime(), event.getDatetime()).toMinutes() > 30) {
                    return null;
                }
            }
            return null;
        }

        public String findClientCard(Event subtotal, Event terminalPayment) {
            int i1 = list.indexOf(subtotal);
            int i2 = list.indexOf(terminalPayment);
            for (int i = i1 + 1; i < i2; ++i) {
                Event ev = list.get(i);
                if (ev.isClientCard()) {
                    return ev.getClientCard();
                }
            }
            return null;
        }
    }

    public static class Data {

        public List<DataSubtoalTerminalPayment> data1 = new ArrayList<>();
        public List<DataTerminalPaymentPrinting> data2 = new ArrayList<>();

        public void add1(DataSubtoalTerminalPayment el) {
            data1.add(el);
        }

        public void add2(DataTerminalPaymentPrinting el) {
            data2.add(el);
        }

        public void trace() {
            System.out.println("---DataSubtoalTerminalPayment--- size " + data1.size());
            for (DataSubtoalTerminalPayment el : data1) {
                System.out.println(el.toString());
            }
            System.out.println("---DataTerminalPaymentPrinting--- size " + data2.size());
            for (DataTerminalPaymentPrinting el : data2) {
                System.out.println(el.toString());
            }
        }
    }

    private class Event implements Comparable<Event> {
        private String[] props;

        public Event(String text) {
            props = text.split(";");
        }

        public boolean isSubtotal() {
            return props[2].equals(EVENT_ID__SUBTOTAL);
        }

        public boolean isTerminalPayment() {
            if (props[2].equals(EVENT_ID__PAYMENT)) {
                if (props.length >= 18) {
                    return props[17].equals("2");
                }
            }
            return false;
        }

        public boolean isReceiptPrinting() {
            return props[2].equals(EVENT_ID__RECEIPT_PRINTING);
        }

        public boolean isClientCard() {
            if (props[2].equals(EVENT_ID__OPERATION_ON_ADDITIONAL_DEVICE)) {
                if (props.length >= 14) {
                    String card = props[13];
                    if ((card.startsWith("260") || card.startsWith("264"))
                            && card.length() == 13) {
                        return true;
                    }
                    if (card.startsWith("7789004") && card.length() == 17) {
                        return true;
                    }
                    if ((card.startsWith("7789004")
                            || card.startsWith("7789774")
                            || card.startsWith("7789000")) && card.length() == 23) {
                        return true;
                    }
                }
            }
            return false;
        }

        public String getClientCard() {
            return isClientCard() ? props[13] : null;
        }

        public boolean isSale() {
            if (props.length >= 12) {
                return props[11].equals("1");
            }
            return false;
        }

        public String getCheckNum() {
            if (props.length >= 13) {
                return props[12];
            }
            return "";
        }

        public String getStation() {
            return props[6];
        }

        private LocalDateTime datetime;
        public LocalDateTime getDatetime() {
            if (datetime == null) {
                datetime = LocalDateTime.parse(props[7], checkdtf);
            }
            return datetime;
        }

        @Override
        public int compareTo(Event o) {
            int r = getStation().compareTo(o.getStation());
            if (r != 0) {
                return r;
            } else {
                return getDatetime().compareTo(o.getDatetime());
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(LocalDateTime.parse("27022019224057421", checkdtf));
    }
}
