package com.br.multicloudecore.awsmodule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@EnableConfigurationProperties
@Configuration
public class AwsModuleApplication {

    public static void main(String[] args) {
        SpringApplication.run(AwsModuleApplication.class, args);
    }

}
