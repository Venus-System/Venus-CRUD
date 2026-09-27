package com.venus.crud.exception;

import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class InvalidStateTransitionDetector {

    private static final Logger log = LoggerFactory.getLogger(InvalidStateTransitionDetector.class);

    private static final String INVALID_STATE_TRANSITION_SQL_STATE = "VE001";

    private InvalidStateTransitionDetector() {
    }

    public static boolean matches(Throwable ex) {
        for (Throwable cause = ex; cause != null; cause = cause.getCause()) {
            if (cause instanceof SQLException sqlException
                    && INVALID_STATE_TRANSITION_SQL_STATE.equals(sqlException.getSQLState())) {
                log.warn("Transicao de status rejeitada pelo banco: {}", sqlException.getMessage());
                return true;
            }
        }
        return false;
    }
}
