package com.abc.docuplay.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class EnvelopeRequest {
    public String getSigner() {
        return signer;
    }

    public void setSigner(String signer) {
        this.signer = signer;
    }

    private String signer;


}
