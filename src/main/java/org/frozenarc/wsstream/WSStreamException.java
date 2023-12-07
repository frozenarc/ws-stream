package org.frozenarc.wsstream;

/**
 * Author: mpanchal
 * Date: 2023-12-07 14:13
 */
public class WSStreamException extends Exception {

    public WSStreamException() {
    }

    public WSStreamException(String message) {
        super(message);
    }

    public WSStreamException(String message, Throwable cause) {
        super(message, cause);
    }

    public WSStreamException(Throwable cause) {
        super(cause);
    }

    public WSStreamException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
