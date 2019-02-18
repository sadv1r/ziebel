package ru.sadv1r.sjdbl.core.exception;

import com.siebel.data.SiebelException;

/**
 * Minor No: 7667856
 */
public class SjdblComponentNotFoundException extends SjdblRequestException {
    public SjdblComponentNotFoundException(SiebelException e) {
        super(e);
    }
}
