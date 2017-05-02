package com.github.waiverson.carambola.support;

/**
 * Signals an error in the evaluation of the JavaScript in LetBodyJsHandler.
 *
 * @author xyc
 *
 */
public class JavascriptException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * @param message the exception message.
     */
    public JavascriptException(String message) {
        super(message);
    }
}