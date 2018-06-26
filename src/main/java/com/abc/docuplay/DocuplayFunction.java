package com.abc.docuplay;

import com.abc.docuplay.model.EnvelopeRequest;
import com.abc.docuplay.model.EnvelopeResponse;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.function.Function;

@Component("docuplayFunction")
public class DocuplayFunction implements Function<EnvelopeRequest, EnvelopeResponse> {
    @Override
    public EnvelopeResponse apply(EnvelopeRequest envelopeRequest) {
        EnvelopeResponse res = new EnvelopeResponse();
        res.setEnvelopeId("id:" + new Random().nextInt() + envelopeRequest.getSigner());
        return res;
    }
}
