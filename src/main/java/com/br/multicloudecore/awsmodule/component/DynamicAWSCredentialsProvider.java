package com.br.multicloudecore.awsmodule.component;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
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
        if (cachedCredentials.get() == null || Instant.now().isAfter(credentialsExpirationTime)) {
            refresh();
        }
        return cachedCredentials.get();
    }

    @Scheduled(fixedDelay = 3600000) // Refresh every hour
    public void refresh() {
        try {Map<String, Object> awsCredentials = getAwsCredentials();
            String accessKeyId = (String) awsCredentials.get("access_key");
            String secretAccessKey = (String) awsCredentials.get("secret_key");
            String securityToken = (String) awsCredentials.get("security_token");
            Instant expirationTime = (Instant) awsCredentials.get("expiration");

            AWSCredentials newCredentials = new BasicSessionCredentials(accessKeyId, secretAccessKey, securityToken);
            cachedCredentials.set(newCredentials);
            credentialsExpirationTime = expirationTime;

            LOGGER.info("AWS credentials refreshed successfully");
        } catch (VaultException e) {
            LOGGER.error("Failed to refresh AWS credentials from Vault", e);
        } catch (Exception e) {
            LOGGER.error("Unexpected error while refreshing AWS credentials", e);
        }
    }


    private Map<String, Object> getAwsCredentials() {
        VaultResponse response = vaultTemplate.read("aws/creds/" + this.role);
        if (response == null || response.getData() == null) {
            throw new RuntimeException("Failed to retrieve AWS credentials from Vault");
        }
        String accessKeyId = (String) response.getData().get("access_key");
        String secretAccessKey = (String) response.getData().get("secret_key");
        String securityToken = (String) response.getData().get("security_token");


        if (securityToken != null) {
            return Map.of(
                    "access_key", accessKeyId,
                    "secret_key", secretAccessKey,
                    "security_token", securityToken
            );
        } else {
            return Map.of(
                    "access_key", accessKeyId,
                    "secret_key", secretAccessKey
            );
        }}

}
