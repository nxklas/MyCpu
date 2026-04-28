package de.nxklas.mycpu.core;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/*
    IMMEDIATE(0b00),
    REGISTER(0b01);
*/

public final class AccessModeTests {
    private static Stream<Arguments> modesToValue() {
        return Stream.of(
            Arguments.of(AccessMode.IMMEDIATE, AccessMode.IMMEDIATE, (byte) 0x00),
            Arguments.of(AccessMode.IMMEDIATE, AccessMode.REGISTER, (byte) 0x01),

            Arguments.of(AccessMode.REGISTER, AccessMode.IMMEDIATE, (byte) 0x04),
            Arguments.of(AccessMode.REGISTER, AccessMode.REGISTER, (byte) 0x05)
        );
    }

    @ParameterizedTest
    @MethodSource("modesToValue")
    public void encodeComputesCorrectValues(AccessMode dst, AccessMode src, byte expected) {
        assertEquals(AccessMode.encode(dst, src), expected);
    }

    @ParameterizedTest
    @MethodSource("modesToValue")
    public void decodeComputesCorrectValues(AccessMode expectedDst, AccessMode expectedSrc, byte mode) {
        var modes = AccessMode.decode(mode);
        assertAll(
                () -> assertEquals(modes.value1, expectedDst),
                () -> assertEquals(modes.value2, expectedSrc));
    }

    private static Set<AccessMode> validAccessModes() {
        return Arrays.stream(AccessMode.values())
                .map(mode -> mode)
                .collect(Collectors.toSet());
    }

    @Test
    public void fromValueReturnsCorrectModeForGivenValidValue() {
        for (AccessMode mode : validAccessModes())
            assertEquals(mode, AccessMode.fromValue(mode.value));
    }

    private static Set<Byte> validAccessModeValues() {
        return Arrays.stream(AccessMode.values())
                .map(mode -> mode.value)
                .collect(Collectors.toSet());
    }

    @Test
    public void fromValueThrowsForAllGivenInvalidValues() {
        var validValues = validAccessModeValues();
        for (int i = Byte.MIN_VALUE; i <= Byte.MAX_VALUE; i++) {
            final byte value = (byte) i;
            if (!validValues.contains(value))
                assertThrows(IllegalArgumentException.class, () -> AccessMode.fromValue(value),
                        "Argument 'value' was not a supported access mode: " + value);
        }
    }
}
