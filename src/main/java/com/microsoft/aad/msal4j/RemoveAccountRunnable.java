// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.aad.msal4j;

import java.util.concurrent.CompletionException;

class RemoveAccountRunnable implements Runnable {

    ClientDataHttpHeaders headers;
    ClientApplicationBase clientApplication;
    IAccount account;

    RemoveAccountRunnable
            (ClientApplicationBase clientApplication, IAccount account) {
        this.clientApplication = clientApplication;
        this.headers = new ClientDataHttpHeaders(clientApplication.correlationId());
        this.account = account;
    }

    @Override
    public void run() {
        try {
            InstanceDiscoveryMetadataEntry instanceDiscoveryData =
                    AadInstanceDiscovery.cache.get(clientApplication.authenticationAuthority.host());

            clientApplication.tokenCache.removeAccount
                    (clientApplication.clientId(), account, instanceDiscoveryData.aliases());

        } catch (Exception ex) {
            clientApplication.log.error(
                    LogHelper.createMessage("Execution of " + this.getClass() + " failed.",
                            this.headers.getHeaderCorrelationIdValue()), ex);

            throw new CompletionException(ex);
        }
    }
}
