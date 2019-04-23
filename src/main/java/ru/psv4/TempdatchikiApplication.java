package ru.psv4;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import ru.psv4.beans.FtpStorage;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@SpringBootApplication
@ServletComponentScan
public class TempdatchikiApplication extends SpringBootServletInitializer {

    private static final Logger log = LogManager.getLogger(TempdatchikiApplication.class);

	public static void main(String[] args) {
	    SpringApplication.run(TempdatchikiApplication.class, args);
	}

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(TempdatchikiApplication.class);
    }

    @Autowired
	public void outputEnvironment(Environment env) {
        log.info("ftp.path = " + env.getProperty("ftp.path"));
        log.info("ftp.host = " + env.getProperty("ftp.host"));
        log.info("ftp.user = " + env.getProperty("ftp.user"));
        log.info("ftp.password = " + env.getProperty("ftp.password"));
        log.info("ftp.port = " + env.getProperty("ftp.port"));
        log.info("zone info = " + ZoneId.systemDefault());
        log.info("now datetime = " + ZonedDateTime.now());
        log.info("server.port = " + env.getProperty("server.port"));
    }

	@Bean
	public FtpStorage getFtpStorage() {
		return new FtpStorage();
	}

}
