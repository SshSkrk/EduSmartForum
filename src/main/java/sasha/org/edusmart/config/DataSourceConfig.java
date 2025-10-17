package sasha.org.edusmart.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.net.URI;


@Configuration
public class DataSourceConfig {

    @Value("${DATABASE_URL:}")
    private String databaseUrl;

    @Bean
    public DataSource dataSource() {
        try {
            if (databaseUrl == null || databaseUrl.isEmpty()) {
                return DataSourceBuilder.create()
                        .url("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE")
                        .username("sa")
                        .password("")
                        .driverClassName("org.h2.Driver")
                        .build();
            }

            URI dbUri = new URI(databaseUrl);
            String[] userInfo = dbUri.getUserInfo().split(":");
            String username = userInfo.length > 0 ? userInfo[0] : "";
            String password = userInfo.length > 1 ? userInfo[1] : "";
            String jdbcUrl = "jdbc:postgresql://" + dbUri.getHost()
                    + ':' + dbUri.getPort()
                    + dbUri.getPath()
                    + "?sslmode=require";

            return DataSourceBuilder.create()
                    .url(jdbcUrl)
                    .username(username)
                    .password(password)
                    .driverClassName("org.postgresql.Driver")
                    .build();
        } catch (Exception e) {
            return DataSourceBuilder.create()
                    .url("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE")
                    .username("sa")
                    .password("")
                    .driverClassName("org.h2.Driver")
                    .build();
        }
    }
}
