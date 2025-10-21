package sasha.org.edusmart.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.net.URI;

@Configuration
public class RedisConfig {

    @Value("${REDISCLOUD_URL:}")
    private String redisUrl;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() throws Exception {
        if (redisUrl != null && !redisUrl.isBlank()) {
            // Parse the REDISCLOUD_URL: redis://user:password@host:port
            URI uri = new URI(redisUrl);
            String host = uri.getHost();
            int port = uri.getPort();
            String userInfo = uri.getUserInfo();

            RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
            config.setHostName(host);
            config.setPort(port);

            if (userInfo != null && userInfo.contains(":")) {
                String password = userInfo.split(":", 2)[1];
                config.setPassword(RedisPassword.of(password));
            }

            return new LettuceConnectionFactory(config);
        }

        // Local fallback for development
        RedisStandaloneConfiguration localConfig = new RedisStandaloneConfiguration("localhost", 6379);
        return new LettuceConnectionFactory(localConfig);
    }
}
