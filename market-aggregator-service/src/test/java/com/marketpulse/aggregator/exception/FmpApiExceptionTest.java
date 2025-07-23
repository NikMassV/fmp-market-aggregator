package com.marketpulse.aggregator.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FMP API Exception Tests")
class FmpApiExceptionTest {

    private static final String ERROR_MESSAGE = "FMP API error: 500 Internal Server Error";
    private static final String CAUSE_MESSAGE = "Network timeout";

    @Test
    @DisplayName("Should create FmpApiException with message")
    void shouldCreateFmpApiExceptionWithMessage() {
        FmpApiException exception = new FmpApiException(ERROR_MESSAGE);

        assertThat(exception.getMessage()).isEqualTo(ERROR_MESSAGE);
        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("Should create FmpApiException with message and cause")
    void shouldCreateFmpApiExceptionWithMessageAndCause() {
        RuntimeException cause = new RuntimeException(CAUSE_MESSAGE);
        FmpApiException exception = new FmpApiException(ERROR_MESSAGE, cause);

        assertThat(exception.getMessage()).isEqualTo(ERROR_MESSAGE);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getCause().getMessage()).isEqualTo(CAUSE_MESSAGE);
    }

    @Test
    @DisplayName("Should create FmpApiException with null message")
    void shouldCreateFmpApiExceptionWithNullMessage() {
        FmpApiException exception = new FmpApiException(null);

        assertThat(exception.getMessage()).isNull();
        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("Should create FmpApiException with empty message")
    void shouldCreateFmpApiExceptionWithEmptyMessage() {
        FmpApiException exception = new FmpApiException("");

        assertThat(exception.getMessage()).isEmpty();
        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("Should create FmpApiException with blank message")
    void shouldCreateFmpApiExceptionWithBlankMessage() {
        FmpApiException exception = new FmpApiException("   ");

        assertThat(exception.getMessage()).isEqualTo("   ");
        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("Should create FmpApiException with null cause")
    void shouldCreateFmpApiExceptionWithNullCause() {
        FmpApiException exception = new FmpApiException(ERROR_MESSAGE, null);

        assertThat(exception.getMessage()).isEqualTo(ERROR_MESSAGE);
        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("Should have correct HTTP status annotation")
    void shouldHaveCorrectHttpStatusAnnotation() {
        ResponseStatus annotation = FmpApiException.class.getAnnotation(ResponseStatus.class);
        assertThat(annotation).isNotNull();
        assertThat(annotation.value()).isEqualTo(HttpStatus.BAD_GATEWAY);
    }

    @Test
    @DisplayName("Should be instance of RuntimeException")
    void shouldBeInstanceOfRuntimeException() {
        FmpApiException exception = new FmpApiException(ERROR_MESSAGE);

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should have meaningful toString representation")
    void shouldHaveMeaningfulToString() {
        FmpApiException exception = new FmpApiException(ERROR_MESSAGE);
        String toString = exception.toString();
        assertThat(toString)
            .contains(FmpApiException.class.getSimpleName())
            .contains(ERROR_MESSAGE);
    }

    @Test
    @DisplayName("Should handle long error message")
    void shouldHandleLongErrorMessage() {
        String longMessage = "A".repeat(1000);
        FmpApiException exception = new FmpApiException(longMessage);

        assertThat(exception.getMessage()).isEqualTo(longMessage);
    }

    @Test
    @DisplayName("Should handle special characters in error message")
    void shouldHandleSpecialCharactersInErrorMessage() {
        String specialMessage = "FMP API error: @#$%^&*()_+-=[]{}|;':\",./<>?";
        FmpApiException exception = new FmpApiException(specialMessage);

        assertThat(exception.getMessage()).isEqualTo(specialMessage);
    }

    @Test
    @DisplayName("Should handle nested exceptions")
    void shouldHandleNestedExceptions() {
        RuntimeException innerCause = new RuntimeException("Inner error");
        RuntimeException middleCause = new RuntimeException("Middle error", innerCause);
        FmpApiException exception = new FmpApiException(ERROR_MESSAGE, middleCause);

        assertThat(exception.getCause()).isEqualTo(middleCause);
        assertThat(exception.getCause().getCause()).isEqualTo(innerCause);
        assertThat(exception.getCause().getCause().getMessage()).isEqualTo("Inner error");
    }
}
