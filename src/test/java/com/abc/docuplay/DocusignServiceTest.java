package com.abc.docuplay;

import com.docusign.esign.api.AuthenticationApi;
import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.model.EnvelopeSummary;
import com.docusign.esign.model.LoginAccount;
import com.docusign.esign.model.LoginInformation;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DocusignServiceTest {
    @Mock
    RestTemplateBuilder restTemplateBuilder;

    @Mock
    RestTemplate restTemplate;

    @Mock
    Token token;

    @Mock
    ApiClient apiClient;

    @Mock
    AuthenticationApi authenticationApi;

    @Mock
    EnvelopesApi envelopesApi;

    @Test
    public void shouldSendDocumentsWithSingleSignerAndSingleDocument() throws Exception{
        when(restTemplateBuilder.build()).thenReturn(restTemplate);
        when(restTemplate.postForObject(anyString(), any(), any())).thenReturn(token);

        LoginInformation loginInformation = new LoginInformation();
        LoginAccount loginAccount = new LoginAccount();
        loginAccount.setBaseUrl("some_url");
        loginInformation.addLoginAccountsItem(loginAccount);
        when(authenticationApi.login()).thenReturn(loginInformation);

        EnvelopeSummary envelopeSummary = new EnvelopeSummary();
        envelopeSummary.setEnvelopeId("ID100");
        when(envelopesApi.createEnvelope(any(), any())).thenReturn(envelopeSummary);
        DocusignService docusignService = new DocusignService(restTemplateBuilder, apiClient, authenticationApi, envelopesApi);

        String envelopeId = docusignService.sendDocuments();
        Assert.assertThat(envelopeId, equalTo(envelopeSummary.getEnvelopeId()));
    }
}