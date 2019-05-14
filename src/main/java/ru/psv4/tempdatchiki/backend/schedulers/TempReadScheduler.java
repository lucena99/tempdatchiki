package ru.psv4.tempdatchiki.backend.schedulers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.psv4.tempdatchiki.backend.data.Controller;
import ru.psv4.tempdatchiki.backend.service.ControllerService;

import javax.swing.text.NumberFormatter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TempReadScheduler {

    @Autowired
    private ControllerService controllerService;

    private static Logger log = LogManager.getLogger(TempReadScheduler.class);

    @Scheduled(fixedRate = 1000)
    public void tempRead() {
        List<Controller> controllers = controllerService.getList();
        controllers.stream().forEach(c -> tempReadAndNotifyRecipientsIfNeed(c));
    }

    private void tempReadAndNotifyRecipientsIfNeed(Controller controller) {
        log.info(String.format("Read controller %s", controller));
        Map<Integer, Float> tempMap = null;
        try {
            tempMap = getTemp(controller.getUrl());
            tempMap.entrySet().stream().forEach(e -> log.info(e));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private Pattern pattern = Pattern.compile("(?<num>\\d+)#(?<val>-?[0-9]+\\.?[0-9]*)");
    private DecimalFormat floatFormat;
    {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        floatFormat = new DecimalFormat();
        floatFormat.setDecimalFormatSymbols(symbols);
    }
    private Map<Integer, Float> getTemp(String urlToRead) throws IOException, ParseException {
        Map<Integer, Float> map = new LinkedHashMap<>();
        URL url = new URL(urlToRead);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            Matcher m = pattern.matcher(line.trim());
            if (m.find()) {
                Integer num = new Integer(m.group("num"));
                String val = m.group("val");
                if (val != null)
                    map.put(new Integer(m.group("num")), floatFormat.parse(val).floatValue());
            }
        }
        return map;
    }
}
