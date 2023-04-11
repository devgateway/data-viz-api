package org.devgateway.viz.commons.io;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CKANResponse {

    private String help;
    private boolean success;
    private CKANResults result;

    public String getHelp() {
        return help;
    }

    public void setHelp(final String help) {
        this.help = help;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(final boolean success) {
        this.success = success;
    }

    public CKANResults getResult() {
        return result;
    }

    public void setResult(final CKANResults result) {
        this.result = result;
    }
}
