package com.conflux.mifosplatform.infrastructure.notifications.exception;

public class NotificationsException extends RuntimeException {

    private final String errorMessage;

    public NotificationsException (final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }
}