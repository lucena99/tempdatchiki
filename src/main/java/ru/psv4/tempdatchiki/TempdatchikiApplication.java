package ru.psv4.tempdatchiki;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.core.env.Environment;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
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
        log.info("zone info = " + ZoneId.systemDefault());
        log.info("now datetime = " + ZonedDateTime.now());
        log.info("port = " + env.getProperty("port"));
    }
}
