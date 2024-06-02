package com.br.multicloudecore.awsmodule.service;

import com.br.multicloudecore.awsmodule.exceptions.AwsCredentialsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

import java.util.Map;
import java.util.Optional;

@Service
public class VaultService {

    private final VaultTemplate vaultTemplate;

    @Autowired
    public VaultService(VaultTemplate vaultTemplate) {
        this.vaultTemplate = vaultTemplate;
    }

    public Map<String, Object> getAwsCredentials() {
        return retrieveCredentialsData();

    }

    private Map<String, Object> retrieveCredentialsData() {
        Optional<VaultResponse> credentialsResponseOptional = Optional.ofNullable(vaultTemplate.read("secret/data/aws"));
        VaultResponse credentialsResponse = credentialsResponseOptional
                .orElseThrow(() -> new RuntimeException("Failed to retrieve AWS credentials from Vault"));

        Map<String, Object> data = Optional.ofNullable(credentialsResponse.getData())
                .orElseThrow(() -> new RuntimeException("No data in Vault response"));

        Object credentialsData = data.get("data");

        if (!(credentialsData instanceof Map)) {
            throw new AwsCredentialsException("Invalid AWS credentials format");
        }

        return (Map<String, Object>) credentialsData;
    }
}
