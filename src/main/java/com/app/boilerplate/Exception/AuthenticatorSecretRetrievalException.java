package com.app.boilerplate.Exception;

public class AuthenticatorSecretRetrievalException extends RuntimeException {
    public AuthenticatorSecretRetrievalException() {super("error.account.authenticator");}
    public AuthenticatorSecretRetrievalException(String message) {super(message);}
}
