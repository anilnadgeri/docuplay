package com.abc.docuplay;

import com.abc.docuplay.model.EnvelopeRequest;
import com.abc.docuplay.model.EnvelopeResponse;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.function.Function;

@Component("docuplayFunction")
public class DocuplayFunction implements Function<EnvelopeRequest, EnvelopeResponse> {

    private static final Logger LOG = Logger.getLogger(DocuplayFunction.class);

    private final DocusignService docusignService;

    public DocuplayFunction(DocusignService docusignService) {
        this.docusignService = docusignService;
    }

    @Override
    public EnvelopeResponse apply(EnvelopeRequest envelopeRequest) {
        LOG.info("received request : " + envelopeRequest.getSigner());

        String envelopeId = docusignService.sendDocuments();

        return new EnvelopeResponse(envelopeId);
    }
}
