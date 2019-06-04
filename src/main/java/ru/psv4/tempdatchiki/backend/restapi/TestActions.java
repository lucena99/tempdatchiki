package ru.psv4.tempdatchiki.backend.restapi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("test")
public class TestActions {

    private static final Logger log = LogManager.getLogger(TestActions.class);

    @RequestMapping(path = "/pause", method = RequestMethod.GET)
    public String pause3min() {
        int pause = 3*60;
        try {
            TimeUnit.SECONDS.sleep(pause);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return "Time pause " + pause + " sec";
    }
}
