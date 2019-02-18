package ru.sadv1r.sjdbl.core;

import lombok.experimental.NonFinal;
import ru.sadv1r.sjdbl.core.exception.SjdblException;

import javax.persistence.EntityTransaction;

/**
 * @author sadv1r
 */
public class Transaction implements EntityTransaction {
    @NonFinal
    private TransactionStatus status;
    private ZiebelTransactionService transactionService;

    public Transaction(final ZiebelTransactionService eaiTransactionService) {
        transactionService = eaiTransactionService;
        this.status = TransactionStatus.NOT_ACTIVE;
    }

    @Override
    public void begin() {
        try {
            transactionService.beginTransaction();
            status = TransactionStatus.ACTIVE;
        } catch (SjdblException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public void commit() {
        try {
            transactionService.endTransaction(false);
            status = TransactionStatus.COMMITTED;
        } catch (SjdblException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public void rollback() {
        try {
            transactionService.endTransaction(true);
            status = TransactionStatus.ROLLED_BACK;
        } catch (SjdblException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public void setRollbackOnly() {
        status = TransactionStatus.MARKED_ROLLBACK;
    }

    @Override
    public boolean getRollbackOnly() {
        return TransactionStatus.MARKED_ROLLBACK == status;
    }

    @Override
    public boolean isActive() {
        return TransactionStatus.ACTIVE == status;
    }
}
