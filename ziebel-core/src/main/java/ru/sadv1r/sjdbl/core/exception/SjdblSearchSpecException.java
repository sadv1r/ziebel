package ru.sadv1r.sjdbl.core.exception;

import com.siebel.data.SiebelException;

/**
 * Minor No: 7668213
 */
public class SjdblSearchSpecException extends SjdblRequestException {
    public SjdblSearchSpecException(SiebelException e) {
        super(e);
    }
}
