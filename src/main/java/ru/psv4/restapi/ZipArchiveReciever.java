package ru.psv4.restapi;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.psv4.beans.FtpStorage;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("restapi")
public class ZipArchiveReciever {

    private static final Logger log = LogManager.getLogger(ZipArchiveReciever.class);

    @Autowired
    private FtpStorage ftpStorage;

    @RequestMapping(consumes = "application/gzip", method = RequestMethod.POST)
    public void put(@RequestHeader("Gk-Store") String zavod,
                    @RequestHeader("Message-Id") String messageId,
                    @RequestHeader("Packet-Number") String packetNumber,
                    @RequestBody byte[] data) {
        log.info("Gk-Store = " + String.valueOf(zavod));
        log.info("Message-Id = " + String.valueOf(messageId));
        log.info("Packet-Number = " + String.valueOf(packetNumber));

        LocalDateTime datetime = LocalDateTime.now();
        byte[] textData = extractGZip(data);
        String fileName = DateTimeFormatter.ofPattern("HH-mm").format(datetime) + "_" +
                messageId + "_" +
                packetNumber + ".txt";
        ftpStorage.upload(fileName, datetime.toLocalDate(), textData);
    }

    @RequestMapping(path = "/test")
    public @ResponseBody String test() {
        return "Hello, Stas!";
    }

    private byte[] extractGZip(byte[] data) {
        try {
            BufferedInputStream in = new BufferedInputStream(new ByteArrayInputStream(data));
            GzipCompressorInputStream zIn = new GzipCompressorInputStream(in);
            BufferedReader br = new BufferedReader(new InputStreamReader(zIn, "CP866"));
            ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(
                    byteArrayStream, "UTF-8"));
            String line = null;
            while (null != (line = br.readLine())) {
                wr.write(line);
                wr.newLine();
            }
            br.close();
            wr.close();
            log.info("Extracted gzip");
            return byteArrayStream.toByteArray();
        } catch (IOException e) {
            log.error("Error", e);
            throw new RuntimeException(e);
        }
    }

    public static class ZipArchiveRecieverException extends RuntimeException {
        public ZipArchiveRecieverException(String msg) {
            super(msg);
        }
    }
}
