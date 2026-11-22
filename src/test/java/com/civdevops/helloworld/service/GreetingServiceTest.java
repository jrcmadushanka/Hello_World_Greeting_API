package com.civdevops.helloworld.service;

import com.civdevops.helloworld.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GreetingServiceTest {

    private final GreetingService service = new GreetingService();

    @ParameterizedTest
    @CsvSource({
            "amith, Hello Amith",
            "Banuka, Hello Banuka",
            "Malith, Hello Malith",
            "m, Hello M",
            "A, Hello A",
            "aMITH, Hello AMITH"
    })
    void shouldReturnGreetingForValidNames(
            String name, String expected) {

        assertEquals(expected, service.greet(name));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Nalin",
            "Zara",
            "n",
            "z",
            "123",
            "!amith",
            "   "
    })

    void shouldRejectInvalidNames(String name) {

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.greet(name)
        );

        assertEquals(
                Constants.INVALID_INPUT,
                exception.getMessage()
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldRejectNullAndEmptyNames(String name) {

        assertThrows(IllegalArgumentException.class, () -> service.greet(name));
    }

    @Test
    void shouldTrimLeadingAndTrailingWhitespace() {

        assertEquals("Hello Amith", service.greet("  amith  "));
    }
}