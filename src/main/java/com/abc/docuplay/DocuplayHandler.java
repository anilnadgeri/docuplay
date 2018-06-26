package com.abc.docuplay;

import com.abc.docuplay.model.EnvelopeRequest;
import com.abc.docuplay.model.EnvelopeResponse;
import org.springframework.cloud.function.adapter.aws.SpringBootRequestHandler;

public class DocuplayHandler extends SpringBootRequestHandler<EnvelopeRequest, EnvelopeResponse>{

}
