package ru.sadv1r.sjdbl.core.exception;

import com.siebel.data.SiebelException;

/**
 * Major No: 256
 */
public class SjdblRequestException extends SjdblException {
    public SjdblRequestException(SiebelException e) {
        super(e);
    }
}
