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


/**
 * This class provides configuration for AWS services used by the application.
 */
@Configuration
@Getter
public class AwsConfig {

  /**
   * Creates an instance of {@link AWSSimpleSystemsManagement} to interact with AWS services.
   *
   * @return an instance of {@link AWSSimpleSystemsManagement}
   */
  @Bean
  public AWSSimpleSystemsManagement awsSimpleSystemsManagement() {
    return AWSSimpleSystemsManagementClientBuilder.defaultClient();
  }

  /**
   * Creates an instance of {@link AWSCredentialsProvider} using credentials
   * retrieved from the Parameter Store.
   *
   * @param ssm an instance of {@link AWSSimpleSystemsManagement}
   * @return an instance of {@link AWSCredentialsProvider}
   */
  @Bean
  public AWSCredentialsProvider awsCredentialsProvider(AWSSimpleSystemsManagement ssm) {
    String accessKeyId = getParameter(ssm, "accessKey");
    String secretAccessKey = getParameter(ssm, "secretKey");

    return new AWSStaticCredentialsProvider(
              new BasicAWSCredentials(accessKeyId, secretAccessKey)
      );
  }

  /**
   * Retrieves a parameter value from the Parameter Store.
   *
   * @param ssm  an instance of {@link AWSSimpleSystemsManagement}
   * @param name the name of the parameter to retrieve
   * @return the value of the parameter
   */
  private String getParameter(AWSSimpleSystemsManagement ssm, String name) {
    GetParameterRequest parameterRequest = new GetParameterRequest()
            .withName(name)
            .withWithDecryption(true);
    return ssm.getParameter(parameterRequest).getParameter().getValue();
  }
}
