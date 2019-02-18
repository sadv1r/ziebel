package ru.sadv1r.sjdbl.core.exception;

import com.siebel.data.SiebelException;

/**
 * Wrapper for {@link SiebelException}, that can define more specific exceptions by
 * {@link SiebelException#getMajorNumber()} and {@link SiebelException#getMinorNumber()}
 */
public class SjdblException extends SiebelException {
    SjdblException(SiebelException e) {
        super(e.getMajorNumber(), e.getMinorNumber(), e.getMessage(), e.getDetailedMessage());
    }

    public SjdblException() {
        super();
    }

    public SjdblException(String message) {
        this();
        super.setMessage(message);
    }

    public SjdblException(String message, Throwable cause) {
        this();
        super.setMessage(message);
        super.initCause(cause);
    }

    public static SjdblException create(SiebelException e) {
        if (e.getMajorNumber() == 16)
            return new SjdblConnectionException(e);
        if (e.getMajorNumber() == 256) {
            switch (e.getMinorNumber()) {
                case 7667856:
                    return new SjdblObjectNotFoundException(e);
                case 7667934:
                    return new SjdblComponentNotFoundException(e);
                case 7668213:
                    return new SjdblSearchSpecException(e);
                case 7668281:
                    return new SjdblAuthException(e);
                case 7733353:
                    return new SjdblSqlException(e);
                default:
                    return new SjdblRequestException(e);
            }
        } else return new SjdblException(e);
    }

    @Override
    public String toString() {
        return getClass().getName() + ":\n" + super.toString();
    }
}