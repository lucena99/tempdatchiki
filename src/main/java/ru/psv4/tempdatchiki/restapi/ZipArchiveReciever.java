package ru.psv4.tempdatchiki.restapi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("restapi")
public class ZipArchiveReciever {

    private static final Logger log = LogManager.getLogger(ZipArchiveReciever.class);

    @RequestMapping(consumes = "application/gzip", method = RequestMethod.POST)
    public void put(@RequestHeader("Gk-Store") String zavod,
                    @RequestHeader("Message-Id") String messageId,
                    @RequestHeader("Packet-Number") String packetNumber,
                    @RequestBody byte[] data) {
        log.info("Gk-Store = " + String.valueOf(zavod));
        log.info("Message-Id = " + String.valueOf(messageId));
        log.info("Packet-Number = " + String.valueOf(packetNumber));
    }

    @RequestMapping(path = "/test")
    public @ResponseBody String test() {
        return "Hello, Stas!";
    }

    public static class ZipArchiveRecieverException extends RuntimeException {
        public ZipArchiveRecieverException(String msg) {
            super(msg);
        }
    }
}
