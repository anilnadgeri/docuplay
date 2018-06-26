package com.abc.docuplay;

public class Config {
    public static int EXPIRY_IN_SECONDS = 3600;
    public static String BASE_URL = "https://demo.docusign.net/restapi"; //System.getenv("baseUrl");
    public static String O_AUTH_BASE_URL = "account-d.docusign.com"; //System.getenv("oAuthBaseUrl");
    public static String O_AUTH_ENDPOINT = "/oauth/token"; //v2/oauth2/token  //System.getenv("oAuthEndpoint");

    public static String REDIRECT_URI = "https://docusign.com";
    public static String INTEGRATOR_KEY = "db60f9da-41e9-497c-84fe-ee9cc5c79e27"; //System.getenv("integratorKey");
    public static String IMPERSONATED_USER = "f90d2d07-38c3-4e0a-b408-c4d8be0dd0d5"; //System.getenv("impersonatedUser");
    public static String PUBLIC_INTEGRATOR_RSA_KEY = System.getenv("publicIntegratorRSAKey");
    public static String PRIVATE_INTEGRATOR_RSA_KEY = System.getenv("privateIntegratorRSAKey");
}
