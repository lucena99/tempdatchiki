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
        URI dbUri = new URI(env.getProperty("dburl"));

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "?sslmode=require";

        DataSourceBuilder b = DataSourceBuilder.create();
        b.driverClassName("org.postgresql.Driver");
        b.url(dbUrl);
        b.username(username);
        b.password(password);
        return b.build();
    }
}
