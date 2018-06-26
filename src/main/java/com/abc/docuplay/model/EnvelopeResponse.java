package com.abc.docuplay.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class EnvelopeResponse {

    public String getEnvelopeId() {
        return envelopeId;
    }

    public void setEnvelopeId(String envelopeId) {
        this.envelopeId = envelopeId;
    }

    private String envelopeId;


}
