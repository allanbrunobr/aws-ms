package com.br.multicloudecore.awsmodule.providers;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

import java.util.Map;

@Service
public class DynamicAWSCredentialsProvider implements AWSCredentialsProvider {

    private final VaultTemplate vaultTemplate;
    private final String role = "my-role";

    public DynamicAWSCredentialsProvider(VaultTemplate vaultTemplate) {
        this.vaultTemplate = vaultTemplate;
    }

    @Override
    public AWSCredentials getCredentials() {
        Map<String, Object> awsCredentials = getAwsCredentials();
        String accessKeyId = awsCredentials.get("access_key").toString();
        String secretAccessKey = awsCredentials.get("secret_key").toString();
        String securityToken = null;
        if (awsCredentials.containsKey("security_token") && awsCredentials.get("security_token") != null ){
            securityToken = awsCredentials.get("security_token").toString();
        }
        return new BasicSessionCredentials(accessKeyId, secretAccessKey, securityToken);
    }

    @Override
    public void refresh() {
        // Not needed for dynamic credentials
    }

    private Map<String, Object> getAwsCredentials() {
        VaultResponse response = vaultTemplate.read("aws/creds/" + this.role);
        if (response == null || response.getData() == null) {
            throw new RuntimeException("Failed to retrieve AWS credentials from Vault");
        }
        return response.getData();
    }

}
