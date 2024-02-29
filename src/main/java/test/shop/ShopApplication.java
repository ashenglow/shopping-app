package test.shop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@Slf4j
public class ShopApplication {

    @Value("${server.host.api}")
    private String host;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
      log.info("host:" + host);
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(host);
            }
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(ShopApplication.class, args);
    }


}
