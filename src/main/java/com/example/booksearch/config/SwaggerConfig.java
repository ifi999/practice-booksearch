package com.example.booksearch.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("도서 검색 API")
                        .description("도서 검색 및 관리를 위한 REST API 서비스")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("BookSearch Team")
                                .email("support@booksearch.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("로컬 개발 서버"),
                        new Server().url("https://api.booksearch.com").description("운영 서버")
                ));
    }
}