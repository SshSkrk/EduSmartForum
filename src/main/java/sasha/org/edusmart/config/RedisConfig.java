package sasha.org.edusmart.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.net.URI;

@Configuration
public class RedisConfig {

    @Value("${REDISCLOUD_URL:}")
    private String redisUrl; // Heroku Redis URL (empty on local)

    @Bean
    public RedisConnectionFactory redisConnectionFactory() throws Exception {
        if (redisUrl != null && !redisUrl.trim().isEmpty()) {
            URI uri = new URI(redisUrl.trim());
            String host = uri.getHost();
            int port = uri.getPort();

            String password = null;
            String userInfo = uri.getUserInfo();
            if (userInfo != null && userInfo.contains(":")) {
                // userInfo format: "username:password"
                String[] parts = userInfo.split(":", 2);
                if (parts.length == 2) {
                    password = parts[1];
                }
            }

            RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
            if (password != null && !password.isEmpty()) {
                config.setPassword(password);
            }

            return new LettuceConnectionFactory(config);
        }

        // fallback to localhost for local development
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration("localhost", 6379));
    }
}
