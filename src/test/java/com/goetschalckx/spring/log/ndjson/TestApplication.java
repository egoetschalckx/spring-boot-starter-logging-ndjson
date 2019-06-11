package com.goetschalckx.spring.log.ndjson;

import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class TestApplication {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(TestApplication.class);

    public static void main(String[] args) {
        new SpringApplicationBuilder(TestApplication.class).run(args);
    }

    @Bean
    public String logTest() {
        log.info("Hello, Computer?");
        return "";
    }

}
