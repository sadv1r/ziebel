package ru.sadv1r.sjdbl.core.exception;

import com.siebel.data.SiebelException;

/**
 * Minor No: 7668281
 */
public class SjdblAuthException extends SjdblRequestException {
    public SjdblAuthException(SiebelException e) {
        super(e);
    }
}
