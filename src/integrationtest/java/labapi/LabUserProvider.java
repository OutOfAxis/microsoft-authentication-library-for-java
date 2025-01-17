//----------------------------------------------------------------------
//
// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
//
//------------------------------------------------------------------------------

package labapi;

import org.testng.util.Strings;
import java.util.HashMap;
import java.util.Map;

public class LabUserProvider {

    private static LabUserProvider instance;

    private final KeyVaultSecretsProvider keyVaultSecretsProvider;
    private final LabService labService;
    private Map<UserQuery, LabResponse> userCache;

    private LabUserProvider(){
        keyVaultSecretsProvider = new KeyVaultSecretsProvider();
        labService = new LabService();
        userCache = new HashMap<>();
    }

    public static synchronized LabUserProvider getInstance(){
        if(instance == null){
            instance = new LabUserProvider();
        }
        return instance;
    }

    public LabResponse getDefaultUser(NationalCloud cloud, boolean useBetaEndpoint) {
        UserQuery query =  new UserQuery.Builder().
                isMamUser(false).
                isMfaUser(false).
                isFederatedUser(false).
                nationalCloud(cloud).
                useBetaEnpoint(useBetaEndpoint).
                build();
        return getLabUser(query);
    }

    public LabResponse getAdfsUser(FederationProvider federationProvider,
                                   boolean federated,
                                   boolean useBetaEndpoint){
        UserQuery query = new UserQuery.Builder().
                isMamUser(false).
                isMfaUser(false).
                isFederatedUser(federated).
                federationProvider(federationProvider).
                useBetaEnpoint(useBetaEndpoint).
                build();
        return getLabUser(query);
    }

    public LabResponse getB2cUser(B2CIdentityProvider b2CIdentityProvider,
                                  boolean useBetaEndpoint){
        UserQuery query = new UserQuery.Builder().
                userType(UserType.B2C).
                b2CIdentityProvider(b2CIdentityProvider).
                useBetaEnpoint(useBetaEndpoint).
                build();
        return getLabUser(query);
    }

    public LabResponse getExternalUser(boolean useBetaEndpoint){
        UserQuery query = new UserQuery.Builder().
                isExternalUser(true).
                isMamUser(false).
                isMfaUser(false).
                isFederatedUser(false).
                useBetaEnpoint(useBetaEndpoint).
                build();
        return getLabUser(query);
    }

    // MSA users are not exposed in lab users API. Have to get them directly from keyvault.
    public LabResponse getMsaUser(){
        String userName =  keyVaultSecretsProvider.getSecret(LabConstants.USER_MSA_USERNAME_URL);
        String password = keyVaultSecretsProvider.getSecret(LabConstants.USER_MSA_PASSWORD_URL);

        LabUser user = new LabUser();
        user.setUpn(userName);
        user.setPassword(password);

        return new LabResponse(LabConstants.MSA_APP_ID, user);
    }

    public LabResponse getLabUser(UserQuery userQuery){
        if(userCache.containsKey(userQuery)){
            return userCache.get(userQuery);
        }
        LabResponse response = labService.getLabResponse(userQuery);
        userCache.put(userQuery, response);
        return response;
    }

    public String getUserPassword(LabUser user){
        if(!Strings.isNullOrEmpty(user.getPassword())){
            return user.getPassword();
        }
        if(Strings.isNullOrEmpty(user.getCredentialUrl())){
            throw new IllegalArgumentException("LabUser credential URL cannot be null");
        }
        try{
            String password = keyVaultSecretsProvider.getSecret(user.getCredentialUrl());
            user.setPassword(password);
            return password;
        } catch (Exception e){
            throw new RuntimeException("Error getting LabUser password: " + e.getMessage());
        }
    }
}
