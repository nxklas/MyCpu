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
    private static final byte IMM_TO_REG = AccessMode.encode(AccessMode.REGISTER, AccessMode.IMMEDIATE);
    private static final byte REG_TO_REG = AccessMode.encode(AccessMode.REGISTER, AccessMode.REGISTER);

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
            Opcode.MOV.value, IMM_TO_REG, dstRegister, srcImmediate
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
            Opcode.MOV.value, IMM_TO_REG, srcRegister, immediate,
            Opcode.MOV.value, REG_TO_REG, dstRegister, srcRegister
        };
        var processor = new Processor(program);

        processor.execute();
        assertEquals(processor.peekRegister(dstRegister), immediate);
    }

    private static final Stream<Arguments> addOrSubImmedaiteToRegisterCalculatesCorrectly_args() {
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
    @MethodSource("addOrSubImmedaiteToRegisterCalculatesCorrectly_args")
    public void addImmediateToRegisterCaculatesCorrectly(byte dstRegister, byte srcImmediate, byte dstValue) {
        var program = new byte[] {
            Opcode.MOV.value, IMM_TO_REG, dstRegister, dstValue,
            Opcode.ADD.value, IMM_TO_REG, dstRegister, srcImmediate
        };
        var processor = new Processor(program);
        
        processor.execute();
        assertEquals(processor.peekRegister(dstRegister), dstValue + srcImmediate);
    }

    private static final Stream<Arguments> addOrSubRegisterToRegisterCalculatesCorrectly_args() {
        return Stream.of(
            Arguments.of((byte) 0x00, (byte) 0x09, (byte) 0x10, (byte) 0x19),
            Arguments.of((byte) 0x01, (byte) 0x08, (byte) 0x11, (byte) 0x18),
            Arguments.of((byte) 0x02, (byte) 0x07, (byte) 0x12, (byte) 0x17),
            Arguments.of((byte) 0x03, (byte) 0x06, (byte) 0x13, (byte) 0x16),
            Arguments.of((byte) 0x04, (byte) 0x05, (byte) 0x14, (byte) 0x15),
            Arguments.of((byte) 0x05, (byte) 0x04, (byte) 0x15, (byte) 0x14),
            Arguments.of((byte) 0x06, (byte) 0x03, (byte) 0x16, (byte) 0x13),
            Arguments.of((byte) 0x07, (byte) 0x02, (byte) 0x17, (byte) 0x12),
            Arguments.of((byte) 0x08, (byte) 0x01, (byte) 0x18, (byte) 0x11),
            Arguments.of((byte) 0x09, (byte) 0x00, (byte) 0x19, (byte) 0x10)
        );
    }

    @ParameterizedTest
    @MethodSource("addOrSubRegisterToRegisterCalculatesCorrectly_args")
    public void addRegisterToRegisterCaculatesCorrectly(byte dstRegister, byte srcRegister, byte dstValue, byte srcValue) {
        var program = new byte[] {
            Opcode.MOV.value, IMM_TO_REG, dstRegister, dstValue,
            Opcode.MOV.value, IMM_TO_REG, srcRegister, srcValue,
            Opcode.ADD.value, REG_TO_REG, dstRegister, srcRegister
        };
        var processor = new Processor(program);
        
        processor.execute();
        assertEquals(processor.peekRegister(dstRegister), dstValue + srcValue);
    }
    
    @ParameterizedTest
    @MethodSource("addOrSubImmedaiteToRegisterCalculatesCorrectly_args")
    public void subImmediateToRegisterCaculatesCorrectly(byte dstRegister, byte srcImmediate, byte dstValue) {
        var program = new byte[] {
            Opcode.MOV.value, IMM_TO_REG, dstRegister, dstValue,
            Opcode.SUB.value, IMM_TO_REG, dstRegister, srcImmediate
        };
        var processor = new Processor(program);
        
        processor.execute();
        assertEquals(processor.peekRegister(dstRegister), dstValue - srcImmediate);
    }

    @ParameterizedTest
    @MethodSource("addOrSubRegisterToRegisterCalculatesCorrectly_args")
    public void subRegisterToRegisterCaculatesCorrectly(byte dstRegister, byte srcRegister, byte dstValue, byte srcValue) {
        var program = new byte[] {
            Opcode.MOV.value, IMM_TO_REG, dstRegister, dstValue,
            Opcode.MOV.value, IMM_TO_REG, srcRegister, srcValue,
            Opcode.SUB.value, REG_TO_REG, dstRegister, srcRegister
        };
        var processor = new Processor(program);
        
        processor.execute();
        assertEquals(processor.peekRegister(dstRegister), dstValue - srcValue);
    }
}
