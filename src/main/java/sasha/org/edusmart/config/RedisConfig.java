package sasha.org.edusmart.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;

import java.net.URI;

@Configuration
public class RedisConfig {

    @Value("${REDIS_URL:}")
    private String redisUrl;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        if (redisUrl != null && !redisUrl.isEmpty()) {
            try {
                URI redisURI = new URI(redisUrl);
                String host = redisURI.getHost();
                int port = redisURI.getPort();
                String password = redisURI.getUserInfo() != null ? redisURI.getUserInfo().split(":",2)[1] : null;

                RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
                if (password != null) {
                    config.setPassword(password);
                }
                return new LettuceConnectionFactory(config);
            } catch (Exception e) {
                throw new RuntimeException("Invalid REDIS_URL: " + redisUrl, e);
            }
        }
        // fallback to properties
        return new LettuceConnectionFactory();
    }
}

