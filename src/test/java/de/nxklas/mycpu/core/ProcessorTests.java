package de.nxklas.mycpu.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/*
    IMMEDIATE(0b00),
    REGISTER(0b01);

    NOP(0x00),
    MOV(0x10),
    HALT(0xFF);
*/

public class ProcessorTests {
    private static final Stream<Arguments> movImmediateToRegisterExecutesCorrectly_args() {
        return Stream.of(
            Arguments.of((byte) 0x00, (byte) 0x10),
            Arguments.of((byte) 0x01, (byte) 0x11),
            Arguments.of((byte) 0x02, (byte) 0x12),
            Arguments.of((byte) 0x03, (byte) 0x13),
            Arguments.of((byte) 0x04, (byte) 0x14),
            Arguments.of((byte) 0x05, (byte) 0x15),
            Arguments.of((byte) 0x06, (byte) 0x16),
            Arguments.of((byte) 0x07, (byte) 0x17),
            Arguments.of((byte) 0x08, (byte) 0x18),
            Arguments.of((byte) 0x09, (byte) 0x19)
        );
    }

    @ParameterizedTest
    @MethodSource("movImmediateToRegisterExecutesCorrectly_args")
    public void movImmediateToRegisterExecutesCorrectly(byte dstRegister, byte srcImmediate) {
        var program = new byte[] {
            0x10, 0x04, dstRegister, srcImmediate
        };
        var processor = new Processor(program);

        processor.execute();
        assertEquals(processor.peekRegister(dstRegister), srcImmediate);
    }

    private static final Stream<Arguments> movRegisterToRegisterExecutesCorrectly_args() {
        return Stream.of(
            Arguments.of((byte) 0x00, (byte) 0x09, (byte) 0x10),
            Arguments.of((byte) 0x01, (byte) 0x08, (byte) 0x11),
            Arguments.of((byte) 0x02, (byte) 0x07, (byte) 0x12),
            Arguments.of((byte) 0x03, (byte) 0x06, (byte) 0x13),
            Arguments.of((byte) 0x04, (byte) 0x05, (byte) 0x14),
            Arguments.of((byte) 0x05, (byte) 0x04, (byte) 0x15),
            Arguments.of((byte) 0x06, (byte) 0x03, (byte) 0x16),
            Arguments.of((byte) 0x07, (byte) 0x02, (byte) 0x17),
            Arguments.of((byte) 0x08, (byte) 0x01, (byte) 0x18),
            Arguments.of((byte) 0x09, (byte) 0x00, (byte) 0x19)
        );
    }

    @ParameterizedTest
    @MethodSource("movRegisterToRegisterExecutesCorrectly_args")
    public void movRegisterToRegisterExecutesCorrectly(byte dstRegister, byte srcRegister, byte immediate) {
        var program = new byte[] {
            0x10, 0x04, srcRegister, immediate,
            0x10, 0x05, dstRegister, srcRegister
        };
        var processor = new Processor(program);

        processor.execute();
        assertEquals(processor.peekRegister(dstRegister), immediate);
    }
}
