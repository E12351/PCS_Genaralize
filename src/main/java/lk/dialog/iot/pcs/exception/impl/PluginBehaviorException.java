package lk.dialog.iot.pcs.exception.impl;

public class PluginBehaviorException extends Exception {

    private static final long serialVersionUID = 1L;

    private String errorMessage;
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public PluginBehaviorException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }
    
    public PluginBehaviorException() {
        super();
    }

}
