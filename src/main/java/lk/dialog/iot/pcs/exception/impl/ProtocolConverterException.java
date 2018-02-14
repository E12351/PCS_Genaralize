package lk.dialog.iot.pcs.exception.impl;

public class ProtocolConverterException extends Exception {

    private static final long serialVersionUID = 1L;

    private String errorMessage;

    public String getErrorMessage() {
        return errorMessage;
    }

    public ProtocolConverterException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }

    public ProtocolConverterException() {
        super();
    }
}
