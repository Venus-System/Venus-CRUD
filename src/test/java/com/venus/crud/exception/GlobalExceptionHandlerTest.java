package com.venus.crud.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.venus.crud.entity.product.Brand;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.data.util.TypeInformation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void unknownSortFieldGives400() {
        PropertyReferenceException unknownField =
                new PropertyReferenceException("naoExiste", TypeInformation.of(Brand.class), List.of());

        ResponseEntity<ErrorResponse> response = handler.handleInvalidSort(unknownField,
                new MockHttpServletRequest("GET", "/api/brands"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().message()).isEqualTo(DataAccessFailureTranslator.INVALID_SORT_MESSAGE);
        assertThat(response.getBody().details()).containsExactly("sort: o campo 'naoExiste' nao existe");
    }

    @Test
    void invalidSortFromQueryGives400() {
        InvalidRequestException invalidSort = new InvalidRequestException(
                DataAccessFailureTranslator.INVALID_SORT_MESSAGE, List.of());

        ResponseEntity<ErrorResponse> response = handler.handleInvalidRequest(invalidSort,
                new MockHttpServletRequest("GET", "/api/brands/search"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
