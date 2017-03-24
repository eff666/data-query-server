package com.shuyun.query.exceptions;


public class TagServiceException extends RuntimeException {

    public TagServiceException() {
        super();
    }

    public TagServiceException(String message) {
        super(message);
    }

    public TagServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public TagServiceException(Throwable cause) {
        super(cause);
    }

}
