package com.br.multicloudecore.awsmodule.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

import java.util.Map;

@Service
public class VaultService {

    private final VaultTemplate vaultTemplate;

    @Autowired
    public VaultService(VaultTemplate vaultTemplate) {
        this.vaultTemplate = vaultTemplate;
    }

    public Map<String, Object> getAwsCredentials() {
        VaultResponse credentialsResponse = vaultTemplate.read("secret/data/aws");
        Map<String, Object> data = credentialsResponse.getData();
        return (Map<String, Object>) data.get("data");
    }
}
