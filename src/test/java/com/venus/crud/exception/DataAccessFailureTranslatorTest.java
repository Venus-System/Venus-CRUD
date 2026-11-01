package com.venus.crud.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.hibernate.query.sqm.UnknownPathException;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;

class DataAccessFailureTranslatorTest {

    private static final String ERROR_MESSAGE = "Falha ao consultar marcas";

    @Test
    void invalidSortBecomesBadRequest() {
        UnknownPathException unknownField = new UnknownPathException("Could not resolve attribute 'naoExiste'");
        InvalidDataAccessApiUsageException invalidSort = new InvalidDataAccessApiUsageException("sort",
                new IllegalArgumentException(unknownField));

        RuntimeException translated = DataAccessFailureTranslator.translate(invalidSort, ERROR_MESSAGE);

        assertThat(translated).isInstanceOf(InvalidRequestException.class)
                .hasMessage(DataAccessFailureTranslator.INVALID_SORT_MESSAGE);
    }

    @Test
    void databaseDownBecomesUnavailable() {
        RuntimeException translated = DataAccessFailureTranslator.translate(
                new DataAccessResourceFailureException("sem conexao"), ERROR_MESSAGE);

        assertThat(translated).isInstanceOf(ServiceUnavailableException.class);
    }

    @Test
    void otherErrorStaysUnexpected() {
        RuntimeException translated = DataAccessFailureTranslator.translate(
                new InvalidDataAccessResourceUsageException("sql errado"), ERROR_MESSAGE);

        assertThat(translated).isInstanceOf(DataAccessFailureException.class);
    }
}
