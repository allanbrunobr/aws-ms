package com.br.multicloudecore.awsmodule.component;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.br.multicloudecore.awsmodule.service.AWSS3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.vault.VaultException;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class DynamicAWSCredentialsProvider implements AWSCredentialsProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicAWSCredentialsProvider.class);
    private final VaultTemplate vaultTemplate;
    private final AtomicReference<AWSCredentials> cachedCredentials = new AtomicReference<>();
    private Instant credentialsExpirationTime;

    @Value("${aws.vault.role}")
    private String role;

    public DynamicAWSCredentialsProvider(VaultTemplate vaultTemplate) {

        this.vaultTemplate = vaultTemplate;
    }

   @Override
    public AWSCredentials getCredentials() {
       Map<String, Object> awsCredentials = getAwsCredentials();
       String accessKeyId = (String) awsCredentials.get("access_key");
       String secretAccessKey = (String) awsCredentials.get("secret_key");

       return new BasicAWSCredentials(accessKeyId, secretAccessKey);
    }

    @Override
    public void refresh() {

    }

    private Map<String, Object> getAwsCredentials() {
        VaultResponse response = vaultTemplate.read("aws/creds/" + this.role);
        if (response == null || response.getData() == null) {
            throw new RuntimeException("Failed to retrieve AWS credentials from Vault");
        }
        String accessKeyId = (String) response.getData().get("access_key");
        String secretAccessKey = (String) response.getData().get("secret_key");
         return Map.of(
                    "access_key", accessKeyId,
                    "secret_key", secretAccessKey
            );
        }
}