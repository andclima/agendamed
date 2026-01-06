package br.com.fiap.mshistorico.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class RestClientConfig {

    @Value("${api.client.url}")
    private String apiUrl;

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
               .baseUrl(apiUrl)
               .build();
    }

}
