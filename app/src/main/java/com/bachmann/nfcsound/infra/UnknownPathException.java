package com.bachmann.nfcsound.infra;

class UnknownPathException extends RuntimeException {
    public UnknownPathException () {

    }

    public UnknownPathException(String message) {
        super(message);
    }
}