package com.abc.docuplay;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.docusign.esign.api.AuthenticationApi;
import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.Configuration;
import com.docusign.esign.model.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.Date;
import java.util.List;

class Token{
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("expires_in")
    private long expiresIn;

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }
}

@Service
public class DocusignService {
    private static final Logger LOG = Logger.getLogger(DocuplayFunction.class);

    private final RestTemplate restTemplate;

    public DocusignService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public String sendDocuments(){
        ApiClient docusignClient = initializeApiClient();
        return createEnvelope(docusignClient).getEnvelopeId();
    }

    private ApiClient initializeApiClient()  {
        String jwtoken = generateJWToken();
        Token token = getAccessToken(jwtoken);
        LOG.info("access token received from Docusign : " + token.getAccessToken());
        ApiClient apiClient = new ApiClient(Config.BASE_URL);
        apiClient.setAccessToken(token.getAccessToken(), System.currentTimeMillis() + token.getExpiresIn());
        return apiClient;
    }

    private Token getAccessToken(String jwtoken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity request = new HttpEntity<Object>(ImmutableMap.of("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer", "assertion", jwtoken));
            return restTemplate.postForObject("https://" + Config.O_AUTH_BASE_URL + Config.O_AUTH_ENDPOINT, request, Token.class);

        }catch (Exception e){
            LOG.error("Error while authenticating " + e.getMessage());
            throw new RuntimeException(e);
        }
    }


    private String generateJWToken(){
        Date issueTime = new Date();
        Algorithm algorithm = Algorithm.RSA256(toPublicKey(Config.PUBLIC_INTEGRATOR_RSA_KEY),
                toPrivateKey(Config.PRIVATE_INTEGRATOR_RSA_KEY));
        String scope = "signature impersonation";

        return JWT.create()
                .withIssuer(Config.INTEGRATOR_KEY)
                .withSubject(Config.IMPERSONATED_USER)
                .withAudience(Config.O_AUTH_BASE_URL)
                .withIssuedAt(issueTime)
                .withExpiresAt(new Date(issueTime.getTime() + Config.EXPIRY_IN_SECONDS*1000))
                .withClaim("scope", scope)
                .sign(algorithm);
    }

    private RSAPrivateKey toPrivateKey(String privateKey) {
        try {
            PemReader pemReader = new PemReader(new StringReader(privateKey));
            byte[] content = pemReader.readPemObject().getContent();

            Security.addProvider(new BouncyCastleProvider());
            KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(content);

            return (RSAPrivateKey)keyFactory.generatePrivate(keySpec);
        }catch (Exception exception){
            LOG.error(exception);
            throw new RuntimeException(exception);
        }
    }

    private RSAPublicKey toPublicKey(String publicKey) {
        try {
            PemReader pemReader = new PemReader(new StringReader(publicKey));
            byte[] content = pemReader.readPemObject().getContent();

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(content);

            return (RSAPublicKey)keyFactory.generatePublic(keySpec);
        }catch (Exception exception){
            LOG.error(exception);
            throw new RuntimeException(exception);
        }
    }

    private String getAccountId(ApiClient apiClient)
    {
        try {
            AuthenticationApi authApi = new AuthenticationApi(apiClient);
            LoginInformation loginInfo = authApi.login();
            // that note user might belong to multiple accounts, here we get first
            String accountId = loginInfo.getLoginAccounts().get(0).getAccountId();
            String baseUrl = loginInfo.getLoginAccounts().get(0).getBaseUrl();
            String[] accountDomain = baseUrl.split("/v2");
            // below code required for production (no effect in demo since all accounts on same domain)
            apiClient.setBasePath(accountDomain[0]);
            Configuration.setDefaultApiClient(apiClient);
            return accountId;
        }
        catch(Exception ex) {
            LOG.error("Error while getting account id: " + ex);
            throw new RuntimeException(ex);
        }
    }

    private EnvelopeSummary createEnvelope(ApiClient docusignClient){
        EnvelopeDefinition envDef = createEnvelopeDefinition();
        try
        {
            EnvelopesApi envelopesApi = new EnvelopesApi(docusignClient);
            String accountId = getAccountId(docusignClient);
            EnvelopeSummary envelopeSummary = envelopesApi.createEnvelope(accountId, envDef);
            LOG.info("EnvelopeSummary: " + envelopeSummary);
            return envelopeSummary;
        }
        catch (com.docusign.esign.client.ApiException ex){
            LOG.error("Exception: " + ex);
            throw new RuntimeException(ex);
        }
    }

    private EnvelopeDefinition createEnvelopeDefinition() {
        // create an envelope that will store the document(s), field(s), and recipient(s)
        EnvelopeDefinition envDef = new EnvelopeDefinition();
        envDef.setEmailSubject("Please sign this document sent from Test");

        Document document = createDocusignDocument();
        envDef.setDocuments(Arrays.asList(document));

        Signer signer = createSigner(document);
        SignHere signHere = createSignhereTab(signer, document);
        List<SignHere> signHereTabs = Arrays.asList(signHere);
        Tabs tabs = new Tabs();
        tabs.setSignHereTabs(signHereTabs);
        signer.setTabs(tabs);

        // add recipients (in this case a single signer) to the envelope
        envDef.setRecipients(new Recipients());
        envDef.getRecipients().setSigners(new ArrayList<Signer>());
        envDef.getRecipients().getSigners().add(signer);

        // send the envelope by setting |status| to "sent". To save as a draft set to "created" instead
        envDef.setStatus("sent");

        return envDef;
    }

    private SignHere createSignhereTab(Signer signer, Document document) {
        // create a |signHere| tab somewhere on the document for the signer to sign
        // here we arbitrarily place it 100 pixels right, 150 pixels down from top
        // left corner of first page of first envelope document
        SignHere signHere = new SignHere();
        signHere.setDocumentId(document.getDocumentId());
        signHere.setPageNumber("1");
        signHere.setRecipientId(signer.getRecipientId());
        signHere.setXPosition("100");
        signHere.setYPosition("150");
        return signHere;
    }

    private Signer createSigner(Document document) {
        // add a recipient to sign the document, identified by name and email we used above
        Signer signer = new Signer();
        signer.setEmail("anilnadgeri@gmail.com");
        signer.setName("Anil Nadgeri");
        signer.setRecipientId(document.getDocumentId());
        return signer;
    }

    private Document createDocusignDocument() {
        Document doc = new Document();
        doc.setDocumentBase64(getDocumentContent());
        doc.setName("TestFile"); // can be different from actual file name
        doc.setFileExtension(".pdf");
        doc.setDocumentId("1");
        return doc;
    }

    private String getDocumentContent() {
        try{
            ClassPathResource resource = new ClassPathResource("sample.pdf");
            byte[] fileBytes = StreamUtils.copyToByteArray(resource.getInputStream());
            return Base64.getEncoder().encodeToString(fileBytes);
        }
        catch (IOException ioExcp){
            LOG.error("Error while reading file " + ioExcp.getMessage());
            throw  new RuntimeException(ioExcp);
        }
    }
}
