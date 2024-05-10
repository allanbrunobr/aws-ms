package com.br.multicloudecore.awsmodule.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AWSConfig {
    @Bean
    public AWSSimpleSystemsManagement awsSimpleSystemsManagement() {
        return AWSSimpleSystemsManagementClientBuilder.defaultClient();
    }

    @Bean
    public AWSCredentialsProvider awsCredentialsProvider(AWSSimpleSystemsManagement ssm) {
        // Recupera as chaves do Parameter Store
        String accessKeyId = getParameter(ssm, "accessKey");
        String secretAccessKey = getParameter(ssm, "secretKey");

        return new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(accessKeyId, secretAccessKey)
        );
    }

    // Método auxiliar para recuperar um parâmetro do Parameter Store
    private String getParameter(AWSSimpleSystemsManagement ssm, String name) {
        GetParameterRequest parameterRequest = new GetParameterRequest()
                .withName(name)
                .withWithDecryption(true);
        return ssm.getParameter(parameterRequest).getParameter().getValue();
    }
}
