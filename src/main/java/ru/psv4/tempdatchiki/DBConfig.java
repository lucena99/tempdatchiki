package ru.psv4.tempdatchiki;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class DBConfig {
    @Bean
    @ConfigurationProperties("spring.datasource.hikari")
    public javax.sql.DataSource getDataSource(Environment env) throws URISyntaxException {
        URI dburl = new URI(env.getProperty("dburl"));
        String username = dburl.getUserInfo().split(":")[0];
        String password = dburl.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:sqlserver://" + dburl.getHost() + ':' + dburl.getPort() + ";" + dburl.getPath().substring(1);
        DataSourceBuilder b = DataSourceBuilder.create();
        b.driverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        b.url(dbUrl);
        b.username(username);
        b.password(password);
        return b.build();
    }
}
