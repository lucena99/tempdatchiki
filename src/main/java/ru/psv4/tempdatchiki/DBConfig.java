package ru.psv4.tempdatchiki;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class DBConfig {
    @Bean
    public javax.sql.DataSource getDataSource(Environment env) throws URISyntaxException {
        URI dburl = new URI(env.getProperty("dburl"));
        String username = dburl.getUserInfo().split(":")[0];
        String password = dburl.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dburl.getHost() + ':' +
                dburl.getPort() + dburl.getPath() + "?sslmode=require";
        DataSourceBuilder b = DataSourceBuilder.create();
        b.driverClassName("org.postgresql.Driver");
        b.url(dbUrl);
        b.username(username);
        b.password(password);
        return b.build();
    }
}