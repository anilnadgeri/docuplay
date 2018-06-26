package com.abc.docuplay;

import com.docusign.esign.api.AuthenticationApi;
import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.client.Configuration;
import com.docusign.esign.model.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/docu-play")
public class DocuPlayController {

    private final DocusignService docusignService;

    public DocuPlayController(DocusignService docusignService) {
        this.docusignService = docusignService;
    }


    @PostMapping("/documents")
    public String sendDocuments() throws Exception{
        return docusignService.sendDocuments();
    }

    private ApiClient initializeDocusignClient() throws IOException, ApiException {
        String authServerUrl = "account-d.docusign.com";
        String baseUrl = "https://demo.docusign.net/restapi";

        //account specific settings
        String impersonatedUserID= "f90d2d07-38c3-4e0a-b408-c4d8be0dd0d5";
        String redirectURI = "https://docusign.com";
        String integratorKey = "db60f9da-41e9-497c-84fe-ee9cc5c79e27";


        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(baseUrl);
        apiClient.configureJWTAuthorizationFlow("public.key", "private.key", authServerUrl, integratorKey, impersonatedUserID, 3600);
        String oauthLoginUrl = apiClient.getJWTUri(integratorKey, redirectURI, authServerUrl);
        System.out.print("URL for one time impersonation authorization " + oauthLoginUrl);
        String accessToken = apiClient.getAccessToken();
        System.out.println("Access token " + accessToken);
        return apiClient;
    }
//
//    private String getAccountId(ApiClient apiClient)
//    {
//        try {
//            AuthenticationApi authApi = new AuthenticationApi(apiClient);
//            LoginInformation loginInfo = authApi.login();
//            // that note user might belong to multiple accounts, here we get first
//            String accountId = loginInfo.getLoginAccounts().get(0).getAccountId();
//            String baseUrl = loginInfo.getLoginAccounts().get(0).getBaseUrl();
//            String[] accountDomain = baseUrl.split("/v2");
//            // below code required for production (no effect in demo since all accounts on same domain)
//            apiClient.setBasePath(accountDomain[0]);
//            Configuration.setDefaultApiClient(apiClient);
//            System.out.println("Configuring api client with following base URI: " + accountDomain[0]);
//            return accountId;
//        }
//        catch(Exception ex) {
//            System.out.println("Error: " + ex);
//            return null;
//        }
//    }
//
//    private void createEnvelope(ApiClient docusignClient, String accountId) throws ApiException {
//
//        // create a byte array that will hold our document bytes
//        byte[] fileBytes = null;
//
//        String pathToDocument = "/sample.pdf";
//
//        try
//        {
//            String currentDir = System.getProperty("user.dir");
//
//            // read file from a local directory
//            Path path = Paths.get(currentDir + pathToDocument);
//            fileBytes = Files.readAllBytes(path);
//        }
//        catch (IOException ioExcp)
//        {
//            // TODO: handle error
//            System.out.println("Exception: " + ioExcp);
//            return;
//        }
//
//        // create an envelope that will store the document(s), field(s), and recipient(s)
//        EnvelopeDefinition envDef = new EnvelopeDefinition();
//        envDef.setEmailSubject("Please sign this document sent from Java SDK)");
//
//        // add a document to the envelope
//        Document doc = new Document();
//        String base64Doc = Base64.getEncoder().encodeToString(fileBytes);
//        doc.setDocumentBase64(base64Doc);
//        doc.setName("TestFile"); // can be different from actual file name
//        doc.setFileExtension(".pdf");	// update if different extension!
//        doc.setDocumentId("1");
//
//        List<Document> docs = new ArrayList<Document>();
//        docs.add(doc);
//        envDef.setDocuments(docs);
//
//        // add a recipient to sign the document, identified by name and email we used above
//        Signer signer = new Signer();
//        signer.setEmail("anilnadgeri@gmail.com");
//        signer.setName("Anil Nadgeri");
//        signer.setRecipientId("1");
//
//        // create a |signHere| tab somewhere on the document for the signer to sign
//        // here we arbitrarily place it 100 pixels right, 150 pixels down from top
//        // left corner of first page of first envelope document
//        SignHere signHere = new SignHere();
//        signHere.setDocumentId("1");
//        signHere.setPageNumber("1");
//        signHere.setRecipientId("1");
//        signHere.setXPosition("100");
//        signHere.setYPosition("150");
//
//        // can have multiple tabs, so need to add to envelope as a single element list
//        List<SignHere> signHereTabs = new ArrayList<SignHere>();
//        signHereTabs.add(signHere);
//        Tabs tabs = new Tabs();
//        tabs.setSignHereTabs(signHereTabs);
//        signer.setTabs(tabs);
//
//        // add recipients (in this case a single signer) to the envelope
//        envDef.setRecipients(new Recipients());
//        envDef.getRecipients().setSigners(new ArrayList<Signer>());
//        envDef.getRecipients().getSigners().add(signer);
//
//        // send the envelope by setting |status| to "sent". To save as a draft set to "created" instead
//        envDef.setStatus("sent");
//
//        try
//        {
//            // instantiate a new EnvelopesApi object
//            EnvelopesApi envelopesApi = new EnvelopesApi(docusignClient);
//            // call the createEnvelope() API
//            EnvelopeSummary envelopeSummary = envelopesApi.createEnvelope(accountId, envDef);
//            System.out.println("Envelope has been sent to " + signer.getEmail());
//            System.out.println("EnvelopeSummary: " + envelopeSummary);
//        }
//        catch (com.docusign.esign.client.ApiException ex)
//        {
//            System.out.println("Exception: " + ex);
//        }
//    }
}
