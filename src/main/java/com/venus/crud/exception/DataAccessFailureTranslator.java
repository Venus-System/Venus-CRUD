package com.venus.crud.exception;

import java.util.List;
import org.hibernate.query.sqm.UnknownPathException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.dao.TransientDataAccessException;

public final class DataAccessFailureTranslator {

    public static final String INVALID_SORT_MESSAGE = "Parametro de ordenacao invalido";
    private static final String INVALID_SORT_DETAIL = "sort: um dos campos de ordenacao nao existe";

    private DataAccessFailureTranslator() {
    }

    public static RuntimeException translate(DataAccessException ex, String errorMessage) {
        if (isInvalidSort(ex)) {
            return new InvalidRequestException(INVALID_SORT_MESSAGE, List.of(INVALID_SORT_DETAIL));
        }
        if (isTemporary(ex)) {
            return new ServiceUnavailableException(errorMessage + ". Tente novamente mais tarde.", ex);
        }
        return new DataAccessFailureException(errorMessage, ex);
    }

    private static boolean isInvalidSort(DataAccessException ex) {
        for (Throwable cause = ex; cause != null; cause = cause.getCause()) {
            if (cause instanceof UnknownPathException) {
                return true;
            }
        }
        return false;
    }

    private static boolean isTemporary(DataAccessException ex) {
        return ex instanceof TransientDataAccessException
                || ex instanceof RecoverableDataAccessException
                || ex instanceof DataAccessResourceFailureException;
    }
}
