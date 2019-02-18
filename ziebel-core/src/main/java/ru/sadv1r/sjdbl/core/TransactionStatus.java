package ru.sadv1r.sjdbl.core;

/**
 * @author sadv1r
 */
public enum TransactionStatus {
    /**
     * The transaction has not yet been started.
     */
    NOT_ACTIVE,
    /**
     * The transaction has been started, but not yet completed.
     */
    ACTIVE,
    /**
     * The transaction has been completed successfully.
     */
    COMMITTED,
    /**
     * The transaction has been rolled back.
     */
    ROLLED_BACK,
    /**
     * The transaction has been marked for rollback only.
     */
    MARKED_ROLLBACK;
}
