package ch.wyler.client.exceptions;

import java.io.IOException;

public class OpenRefineException extends IOException {

    public OpenRefineException(final String message) {
        super(message);
    }

    public OpenRefineException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public OpenRefineException(final Throwable cause) {
        super(cause);
    }
}
