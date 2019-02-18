package ru.sadv1r.sjdbl.core.exception;

import com.siebel.data.SiebelException;

public class SjdblConnectionException extends SjdblException {
    public SjdblConnectionException(SiebelException e) {
        super(e);
    }
}
