package com.br.multicloudecore.awsmodule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableConfigurationProperties
@Configuration
@EnableAsync
public class AwsModuleApplication {

    public static void main(String[] args) {
        SpringApplication.run(AwsModuleApplication.class, args);
    }

}
