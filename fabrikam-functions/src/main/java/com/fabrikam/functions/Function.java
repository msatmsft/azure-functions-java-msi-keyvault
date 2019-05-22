package com.fabrikam.functions;

import java.util.*;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.keyvault.KeyVaultClient;
import com.microsoft.azure.keyvault.models.KeyBundle;
import com.microsoft.azure.keyvault.models.SecretBundle;
import com.microsoft.azure.keyvault.webkey.JsonWebKey;
import com.microsoft.azure.AzureEnvironment;
import com.microsoft.azure.credentials.AppServiceMSICredentials;
import com.microsoft.azure.functions.*;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {
    /**
     * This function listens at endpoint "/api/HttpTrigger-Java". Two ways to invoke
     * it using "curl" command in bash: 1. curl -d "HTTP Body" {your
     * host}/api/HttpTrigger-Java&code={your function key} 2. curl "{your
     * host}/api/HttpTrigger-Java?name=HTTP%20Query&code={your function key}"
     * Function Key is not needed when running locally, it is used to invoke
     * function deployed to Azure. More details:
     * https://aka.ms/functions_authorization_keys
     */
    @FunctionName("secret")
    public HttpResponseMessage run(@HttpTrigger(name = "req", methods = { HttpMethod.GET,
            HttpMethod.POST }, authLevel = AuthorizationLevel.FUNCTION) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        String query = request.getQueryParameters().get("version");
        String version = request.getBody().orElse(query);

        AppServiceMSICredentials credentials = new AppServiceMSICredentials(AzureEnvironment.AZURE);
        KeyVaultClient keyVaultClient = new KeyVaultClient(credentials);
        

        if (version == null) {
            SecretBundle result = keyVaultClient.getSecret("https://javafndemokv.vault.azure.net/", "enckey");
            context.getLogger().info("keyvault secret value");
            context.getLogger().info(result.value());
            return request.createResponseBuilder(HttpStatus.OK).body("Secret :" + result.value()).build();

        } else {
            SecretBundle result = keyVaultClient.getSecret("https://javafndemokv.vault.azure.net/", "enckey",version);
            context.getLogger().info("keyvault secret value");
            context.getLogger().info(result.value());
            return request.createResponseBuilder(HttpStatus.OK).body("Version :  " + version + " Secret :" + result.value()).build();
        }
    }
}
