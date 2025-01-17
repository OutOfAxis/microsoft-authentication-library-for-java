// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package labapi;

public class AppIdentityProvider {
    KeyVaultSecretsProvider keyVaultSecretsProvider;
    LabService labService;

    public AppIdentityProvider(){
        keyVaultSecretsProvider = new KeyVaultSecretsProvider();
        labService = new LabService();
    }

    public String getDefaultLabId(){
        return keyVaultSecretsProvider.getSecret(LabConstants.APP_ID_URL);
    }

    public String getDefaultLabPassword(){
        return keyVaultSecretsProvider.getSecret(LabConstants.APP_PASSWORD_URL);
    }

    public String getOboPassword(){
        return keyVaultSecretsProvider.getSecret(LabConstants.OBO_APP_PASSWORD_URL);
    }
}
