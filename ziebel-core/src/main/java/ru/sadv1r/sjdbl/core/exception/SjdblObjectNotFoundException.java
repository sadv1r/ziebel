package ru.sadv1r.sjdbl.core.exception;

import com.siebel.data.SiebelException;

/**
 * Minor No: 7667856
 */
public class SjdblObjectNotFoundException extends SjdblRequestException {
    public SjdblObjectNotFoundException(SiebelException e) {
        super(e);
    }
}
