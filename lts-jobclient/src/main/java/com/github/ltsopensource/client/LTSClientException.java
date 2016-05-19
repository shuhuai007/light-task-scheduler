package com.github.ltsopensource.client;

/**
 * Exception thrown by the {@link LTSClient}.
 */
public class LTSClientException extends Exception {

    private String errorCode;

    public LTSClientException() {

    }

    public LTSClientException(String message) {
        super(message);
    }

    public LTSClientException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Return the exception error code.
     *
     * @return the exception error code.
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Return the string representatio of the exception.
     *
     * @return the string representatio of the exception.
     */
    public String toString() {
        if (errorCode == null) {
            return super.getMessage();
        }
        return errorCode + " : " + super.getMessage();
    }
}
