package com.fabrikam.functions;

import java.util.*;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.keyvault.KeyVaultClient;
import com.microsoft.azure.keyvault.models.KeyBundle;
import com.microsoft.azure.keyvault.models.SecretBundle;
import com.microsoft.azure.keyvault.webkey.JsonWebKey;
import com.microsoft.azure.keyvault.webkey.JsonWebKeyType;
import com.microsoft.azure.AzureEnvironment;
import com.microsoft.azure.credentials.AppServiceMSICredentials;
import com.microsoft.azure.functions.*;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {
    /**
     * This function listens at endpoint "/api/secret". Two ways to invoke
     * it using "curl" command in bash: 1. curl -d "HTTP Body" {your
     * host}/api/secret&code={your function key} 2. curl "{your
     * host}/api/secret?name=HTTP%20Query&code={your function key}"
     * Function Key is not needed when running locally, it is used to invoke
     * function deployed to Azure. More details:
     * https://aka.ms/functions_authorization_keys
     */
    @FunctionName("secret")
    public HttpResponseMessage run(@HttpTrigger(name = "req", methods = { HttpMethod.GET,
            HttpMethod.POST }, authLevel = AuthorizationLevel.FUNCTION) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request."); 

        context.getLogger().info(System.getenv("MSI_ENDPOINT"));
        context.getLogger().info(System.getenv("MSI_SECRET"));

        // Parse query parameter
        String query = request.getQueryParameters().get("version");
        String version = request.getBody().orElse(query);

        AppServiceMSICredentials credentials = new AppServiceMSICredentials(AzureEnvironment.AZURE);
        KeyVaultClient keyVaultClient = new KeyVaultClient(credentials);

        
        // Create Key Version
        // for(int x = 1; x < 65000; x = x + 1) {
        //     System.out.print( x );
        //     KeyBundle kb = keyVaultClient.createKey("https://javafndemokv.vault.azure.net/", "enckey",JsonWebKeyType.RSA);
        //     context.getLogger().info(kb.key().toString());
        //  }
        
        

        if (version == null) {
            KeyBundle result = keyVaultClient.getKey("https://javafndemokv.vault.azure.net/", "enckey");
            String k = result.key().toString();
            context.getLogger().info("keyvault Key value");
            context.getLogger().info(k);
            return request.createResponseBuilder(HttpStatus.OK).body("Key :" + k).build();

        } else {
            KeyBundle result = keyVaultClient.getKey("https://javafndemokv.vault.azure.net/", "enckey",version);
            String k = result.key().toString();
            context.getLogger().info("keyvault Key value");
            context.getLogger().info(k);
            return request.createResponseBuilder(HttpStatus.OK).body("Version :  " + version + " Key :" + k).build();
        }
    }
}
