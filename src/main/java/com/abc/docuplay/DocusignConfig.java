package com.abc.docuplay;

import com.docusign.esign.api.AuthenticationApi;
import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.client.ApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.abc.docuplay.Config.BASE_URL;

@Configuration
public class DocusignConfig {
    @Bean
    public ApiClient apiClient(){
        return new ApiClient(BASE_URL);
    }

    @Bean
    public AuthenticationApi authenticationApi(ApiClient apiClient){
        return new AuthenticationApi(apiClient);
    }

    @Bean
    public EnvelopesApi envelopesApi(ApiClient apiClient){
        return new EnvelopesApi(apiClient);
    }
}
