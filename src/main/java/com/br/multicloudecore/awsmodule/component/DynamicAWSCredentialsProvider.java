package com.br.multicloudecore.awsmodule.component;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

import java.util.Map;
import java.util.Optional;

import static com.br.multicloudecore.util.ConstantsAWSMS.ACCESS_KEY;
import static com.br.multicloudecore.util.ConstantsAWSMS.SECRET_KEY;

@Component
public class DynamicAWSCredentialsProvider implements AWSCredentialsProvider {

    private final VaultTemplate vaultTemplate;

    @Value("${aws.vault.role}")
    private String role;

    public DynamicAWSCredentialsProvider(VaultTemplate vaultTemplate) {

        this.vaultTemplate = vaultTemplate;
    }

   @Override
    public AWSCredentials getCredentials() {
       Map<String, Object> awsCredentials = getAwsCredentials();
       String accessKeyId = (String) awsCredentials.get(ACCESS_KEY);
       String secretAccessKey = (String) awsCredentials.get(SECRET_KEY);

       return new BasicAWSCredentials(accessKeyId, secretAccessKey);
    }

    @Override
    public void refresh() {

    }

    private Map<String, Object> getAwsCredentials() {
        VaultResponse response = vaultTemplate.read("aws/creds/" + this.role);

        Optional<Map<String, Object>> optionalData = Optional.ofNullable(response).map(VaultResponse::getData);
        Map<String, Object> data = optionalData.orElseThrow(() -> new RuntimeException("Failed to retrieve AWS credentials from Vault"));

        String accessKeyId = (String) data.get(ACCESS_KEY);
        String secretAccessKey = (String) data.get(SECRET_KEY);

         return Map.of(
                    "access_key", accessKeyId,
                    "secret_key", secretAccessKey
            );
        }
}