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

    @Override
    public EnvelopeResponse apply(EnvelopeRequest envelopeRequest) {
        EnvelopeResponse res = new EnvelopeResponse();
        LOG.info("received request : " + envelopeRequest.getSigner());
        res.setEnvelopeId("id:" + new Random().nextInt() + envelopeRequest.getSigner());
        return res;
    }
}
