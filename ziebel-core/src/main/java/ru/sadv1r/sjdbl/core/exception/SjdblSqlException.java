package ru.sadv1r.sjdbl.core.exception;

import com.siebel.data.SiebelException;

/**
 * Minor No: 7733353
 */
public class SjdblSqlException extends SjdblRequestException {
    public SjdblSqlException(SiebelException e) {
        super(e);
    }
}
