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
}
