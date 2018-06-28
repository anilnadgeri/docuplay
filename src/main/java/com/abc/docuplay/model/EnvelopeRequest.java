package com.abc.docuplay.model;

public class EnvelopeRequest {

    EnvelopeRequest(String signer) {
        this.signer = signer;
    }

    public String getSigner() {
        return signer;
    }

    private String signer;
}
